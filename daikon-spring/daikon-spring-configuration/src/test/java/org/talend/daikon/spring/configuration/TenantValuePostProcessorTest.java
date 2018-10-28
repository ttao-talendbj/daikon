package org.talend.daikon.spring.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.talend.daikon.multitenant.context.TenancyContext;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.daikon.multitenant.provider.DefaultTenant;

@RunWith(MockitoJUnitRunner.class)
public class TenantValuePostProcessorTest {

    private TenantValuePostProcessor processor;

    @Before
    public void setUp() {
        processor = new TenantValuePostProcessor();

        final ApplicationContext context = mock(ApplicationContext.class);
        processor.setApplicationContext(context);

        final Environment environment = mock(Environment.class);
        when(context.getBean(eq(Environment.class))).thenReturn(environment);

        when(environment.getProperty("tenant-1234.value")).thenReturn("Tenant value");

        final TenancyContext tenancyContext = TenancyContextHolder.createEmptyContext();
        tenancyContext.setTenant(new DefaultTenant("tenant-1234", null));
        TenancyContextHolder.setContext(tenancyContext);

        when(context.isPrototype("validBean")).thenReturn(true);
        when(context.isPrototype("invalidBean")).thenReturn(false);
    }

    @Test
    public void shouldInjectTenantValue() {
        // given
        final ValidBean bean = new ValidBean();

        // when
        processor.postProcessAfterInitialization(bean, "validBean");

        // then
        assertEquals("Tenant value", bean.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotInjectTenantValue() {
        // given
        final InvalidBean bean = new InvalidBean();

        // when
        processor.postProcessAfterInitialization(bean, "invalidBean");
    }

    @Test
    public void shouldNotModifyInBeforeInitialization() {
        // when
        final ValidBean bean = new ValidBean();
        Object beforeInit = processor.postProcessBeforeInitialization(bean, "validName");

        // then
        assertSame(beforeInit, bean);
    }

    private static class ValidBean {

        @TenantValue("${value}")
        private String value;

        String getValue() {
            return value;
        }
    }

    private static class InvalidBean {

        @TenantValue("${value}")
        private String value;

        String getValue() {
            return value;
        }
    }


}