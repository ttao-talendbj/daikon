package org.talend.daikon.spring.configuration;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.ReflectionUtils.doWithFields;
import static org.springframework.util.ReflectionUtils.makeAccessible;
import static org.springframework.util.ReflectionUtils.setField;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.PropertyPlaceholderHelper;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.daikon.multitenant.core.Tenant;

/**
 * This {@link BeanPostProcessor} processes beans with {@link TenantValue} fields and inject tenant-prefixed
 * configuration keys.
 */
@Component
public class TenantValuePostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantValuePostProcessor.class);

    private static final PropertyPlaceholderHelper HELPER = new PropertyPlaceholderHelper("${", "}", ":", false);

    private static final Map<Class, Boolean> checkedClasses = new HashMap<>();

    private ApplicationContext applicationContext;

    private void assertValidBean(Object bean, String beanName) {
        final boolean isValid = checkedClasses.computeIfAbsent(bean.getClass(), clazz -> {
            final AtomicBoolean checkResult = new AtomicBoolean(true);
            doWithFields(bean.getClass(), //
                    field -> {
                        if (!applicationContext.isPrototype(beanName)) {
                            checkResult.set(false);
                        }
                    }, //
                    f -> findAnnotation(f, TenantValue.class) != null //
            );
            return checkResult.get();
        });
        if (!isValid) {
            throw new IllegalArgumentException("Bean '" + beanName + "' is not a prototype scope.");
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        assertValidBean(bean, beanName);

        final Environment environment = applicationContext.getBean(Environment.class);
        doWithFields(bean.getClass(), //
                field -> {
                    final TenantValue annotation = findAnnotation(field, TenantValue.class);
                    final Object value = HELPER.replacePlaceholders(annotation.value(), placeholderName -> {
                        final Tenant tenant = TenancyContextHolder.getContext().getTenant();
                        final String tenantId = String.valueOf(tenant.getIdentity());
                        LOGGER.debug("Looking for configuration key '{}' for tenant '{}'", placeholderName, tenantId);

                        final String configurationKey = tenantId + "/" + placeholderName;
                        LOGGER.debug("Resolved tenant configuration key '{}'", configurationKey);

                        return environment.getProperty(configurationKey);
                    });

                    makeAccessible(field);
                    setField(field, bean, value);
                }, //
                f -> findAnnotation(f, TenantValue.class) != null //
        );
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
