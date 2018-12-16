package org.talend.daikon.spring.mongo.migration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates a migration class (implements {@link Migration}).
 */
@Target({ ElementType.TYPE, ElementType.TYPE_PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface MigrationRule {

    /**
     * <p>
     * Indicates a version the migration applies to. The following decisions are made based on migration's version (M)
     * and the version in the source object (O).
     * </p>
     * <p>
     * If:
     * <ul>
     * <li>O <= M, migration is applied. In this case O was created before M existed, so O is not up to date for being
     * read using M (or versions that follow M).</li>
     * <li>O > M, migration is not applied. In this case, it expected O already has the changes M would have
     * introduced.</li>
     * </ul>
     * </p>
     * 
     * @return A version string this migration applies to.
     */
    String version();

}
