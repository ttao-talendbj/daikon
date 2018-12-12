package org.talend.daikon.spring;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.ReflectionUtils.doWithFields;
import static org.springframework.util.ReflectionUtils.makeAccessible;
import static org.springframework.util.ReflectionUtils.setField;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultOperations;

/**
 * This {@link BeanPostProcessor} processes beans with {@link SensitiveValue} fields and inject tenant-prefixed
 * configuration keys.
 */
@Component
@ConditionalOnBean(VaultOperations.class)
public class SensitiveValuePostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        final VaultOperations environment = applicationContext.getBean(VaultOperations.class);
        doWithFields(bean.getClass(), //
                field -> {
                    final SensitiveValue annotation = findAnnotation(field, SensitiveValue.class);

                    if (field.getType().isAssignableFrom(SecuredValue.class)) {
                        final Object value = new SecuredValue(environment, annotation.value());
                        makeAccessible(field);
                        setField(field, bean, value);
                    } else {
                        throw new IllegalArgumentException(
                                "Field '" + field.getName() + "' is not typed as " + SecuredValue.class.getName());
                    }
                }, //
                f -> findAnnotation(f, SensitiveValue.class) != null //
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
