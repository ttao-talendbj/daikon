package org.talend.daikon.dynamic.util

import com.typesafe.config.{Config, ConfigFactory}
import org.talend.daikon.dynamic.config.ConfigurationServiceLike
import org.talend.daikon.dynamic.persistence.postgresql.config.DatabaseConfigProviderLike
import play.api.Configuration

object ConfigUtil {

  // TODO : Current workaround waiting for multi tenancy
  val playConfiguration: Configuration = Configuration(ConfigFactory.load())

  val defaultTenantName = "default-tenant.name"

  /**
    * Gets the [[String]] value of a given key from a [[Configuration]] if it exists. Else, throw a [[RuntimeException]]
    *
    * @param configuration the [[Configuration]] to search in
    * @param key           the key to find in the [[Configuration]]
    * @return the [[String]] value of the given key
    */
  def getStringOrFail(configuration: Configuration, key: String): String = {
    configuration
      .getOptional[String](key)
      .getOrElse(
        throw new RuntimeException(s"The key '$key' is undefined in the application configuration.")
      )
  }

  def getTenantConfiguration(configurationService: ConfigurationServiceLike, tenantId: String): Configuration = {
    configurationService.getConfigurationOrFail(tenantId)
  }

  def getTenantConfig(databaseConfigProvider: DatabaseConfigProviderLike, tenantId: String): Config = {
    databaseConfigProvider.getConfig(tenantId)
  }

  /**
    * Gets the [[String]] value of a given key from a [[Configuration]] if it exists. Else, return the default value
    * given in parameter
    *
    * @param configuration the [[Configuration]] to search in
    * @param key           the key to find in the [[Configuration]]
    * @param defaultValue  the default value if the key is not found in the configuration file
    * @return the [[String]] value of the given key
    */
  def getStringOrElse(configuration: Configuration, key: String)(defaultValue: String): String = {
    configuration.getOptional[String](key).getOrElse(defaultValue)
  }
}
