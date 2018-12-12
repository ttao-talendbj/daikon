package org.talend.daikon.spring.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Like a {@link org.springframework.beans.factory.annotation.Value} annotated field, this annotation indicates the
 * annotated field receives a value that is tenant dependent.
 * </p>
 * <p>
 * Annotation has 2 effects:
 * <ul>
 *     <li>Bean <b>must</b> have <i>prototype</i> scope (singleton scope is not allowed).</li>
 *     <li>A tenancy context must be available at the time the bean is created.</li>
 * </ul>
 * </p>
 * @see org.talend.daikon.multitenant.context.TenancyContextHolder
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface TenantValue {

    /**
     * The actual value expression: e.g. "#{systemProperties.myProp}".
     *
     * @return The configuration property to be retrieved.
     */
    String value();
}
