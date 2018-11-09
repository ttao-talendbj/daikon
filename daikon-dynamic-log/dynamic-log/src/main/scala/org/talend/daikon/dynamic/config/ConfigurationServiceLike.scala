package org.talend.daikon.dynamic.config

import javax.inject.Inject
import com.typesafe.config.ConfigObject
import org.talend.daikon.dynamic.model.Constants
import play.api.Configuration
import play.api.cache.SyncCacheApi
import play.api.libs.json.JsObject

trait ConfigurationServiceLike {

  /**
    * Retrieves a precise configuration property related with a given tenant.
    *
    * @param tenantId                    the tenant ID
    * @param propertyName                the configuration property to retrieve
    * @param decryptConfigServicePayload whether to decrypt the TPSVC config service payload or not
    * @return the requested configuration property value, if found
    */
  def getConfigurationProperty(tenantId: String,
                               propertyName: String,
                               decryptConfigServicePayload: Boolean = false): Option[String]

  /**
    * Retrieves the whole configuration related with a given tenant.
    *
    * @param tenantId                    the tenant ID
    * @param decryptConfigServicePayload whether to decrypt the TPSVC config service payload or not
    * @return the whole configuration of the tenant, if found
    */
  def getConfiguration(tenantId: String, decryptConfigServicePayload: Boolean = false): Option[Configuration]

  /**
    * Retrieves the whole configuration related with a given tenant.
    *
    * @param tenantId                    the tenant ID
    * @param decryptConfigServicePayload whether to decrypt the TPSVC config service payload or not
    * @return the whole configuration of the tenant, if found, otherwise fail
    */
  def getConfigurationOrFail(tenantId: String, decryptConfigServicePayload: Boolean = false): Configuration

  /**
    * Update tenant configuration
    *
    * @param tenantId                    tenant ID
    * @param tenantConfiguration         tenant configuration
    * @param encryptConfigServicePayload whether to encrypt the config payload
    */
  def updateTenantConfiguration(tenantId: String,
                                tenantConfiguration: JsObject,
                                encryptConfigServicePayload: Boolean = false): Unit
}

/**
  * OSS implementation of the [[ConfigurationServiceLike]] trait, relying on plain [[Configuration]]
  * objects without any tenancy awareness.
  */
case class ConfigurationServiceOss @Inject()(configuration: Configuration, cache: SyncCacheApi)
  extends ConfigurationServiceLike {

  override def getConfigurationProperty(tenantId: String,
                                        propertyName: String,
                                        decryptConfigServicePayload: Boolean = false): Option[String] =
    getConfigurationOrFail(tenantId).getOptional[String](propertyName)

  override def getConfiguration(tenantId: String, decryptConfigServicePayload: Boolean = false): Option[Configuration] =
    Some(
      cache.getOrElseUpdate[Configuration](tenantId)(
        ConfigurationService.toTenantConfiguration(configuration, tenantId)))

  override def getConfigurationOrFail(tenantId: String, decryptConfigServicePayload: Boolean = false): Configuration =
    cache.getOrElseUpdate[Configuration](tenantId)(ConfigurationService.toTenantConfiguration(configuration, tenantId))

  override def updateTenantConfiguration(tenantId: String,
                                         tenantConfiguration: JsObject,
                                         encryptConfigServicePayload: Boolean = false): Unit = {
    // In OSS this implementation is not required
  }

}

object ConfigurationService {

  val JOURNAL_PLUGIN_PATH = "akka.persistence.journal.plugin"
  val SNAPSHOT_STORE_PLUGIN_PATH = "akka.persistence.snapshot-store.plugin"

  // Prefixes for Akka persistence configuration keys
  val DEFAULT_JOURNAL_PLUGIN = "jdbc-journal"
  val DEFAULT_SNAPSHOT_STORE_PLUGIN = "jdbc-snapshot-store"
  val DEFAULT_READ_JOURNAL_PLUGIN = "jdbc-read-journal"

  private[config] def findPath(configuration: Configuration, path: String, defaultValue: String): String = {
    configuration
      .getOptional[String](path)
      .getOrElse(defaultValue)
  }

  /**
    * Transforms a [[Configuration]] into a tenant [[Configuration]] by injecting the akka persistence parameters.
    * The list of injected parameters is bounded:
    * - akka.persistence.journal.plugin -> {journalName}-{appName}-{tenantId}
    * - akka.persistence.snapshot-store.plugin -> {snapshotStoreName}-{appName}-{tenantId}
    * - {journalName}-{appName}-{tenantId}
    * - {snapshotStoreName}-{appName}-{tenantId}
    * - jdbc-read-journal-{appName}-{tenantId}
    *
    * {appName} being the application identifier, such as dataset or datastreams. Akka persistence configuration
    * is not the same between those applications since they should use their own database setup so separated
    * configuration keys are needed.
    *
    * @param configuration the [[Configuration]] to transform
    * @param tenantId      the tenant identifier
    * @return a tenant aware [[Configuration]]
    */
  def toTenantConfiguration(configuration: Configuration, tenantId: String): Configuration = {
    configuration ++
      configureAkkaPersistence(configuration, tenantId, Constants.DATASET) ++
      configureAkkaPersistence(configuration, tenantId, Constants.DATASTREAMS)
  }

  private def configureAkkaPersistence(
    configuration: Configuration,
    tenantId: String,
    appName: String
  ): Configuration = {
    // Common Akka persistence configuration keys prefixes
    val journalPluginPrefix = findPath(configuration, JOURNAL_PLUGIN_PATH, DEFAULT_JOURNAL_PLUGIN)
    val snapshotStorePluginPrefix = findPath(configuration, SNAPSHOT_STORE_PLUGIN_PATH, DEFAULT_SNAPSHOT_STORE_PLUGIN)
    val readJournalPluginPrefix = DEFAULT_READ_JOURNAL_PLUGIN

    // Akka persistence configuration keys for ...
    // journal
    val journalPluginApp = s"$journalPluginPrefix-$appName"
    val tenantJournalPluginApp = s"$journalPluginApp-$tenantId"
    // snapshot-store
    val snapshotStorePluginApp = s"$snapshotStorePluginPrefix-$appName"
    val tenantSnapshotStorePluginApp = s"$snapshotStorePluginApp-$tenantId"
    // read-journal
    val readJournalPluginApp = s"$readJournalPluginPrefix-$appName"
    val tenantReadJournalPluginApp = s"$readJournalPluginApp-$tenantId"

    // Akka persistence configuration values
    val journalPluginConfigurationApp = configuration.get[ConfigObject](journalPluginApp)
    val snapshotStorePluginConfigurationApp = configuration.get[ConfigObject](snapshotStorePluginApp)
    val readJournalPluginConfigurationApp = configuration.get[ConfigObject](readJournalPluginApp)

    // Configure Akka persistence for the current application and the current tenant
    Configuration(tenantJournalPluginApp -> journalPluginConfigurationApp) ++
      Configuration(tenantSnapshotStorePluginApp -> snapshotStorePluginConfigurationApp) ++
      Configuration(tenantReadJournalPluginApp -> readJournalPluginConfigurationApp)
  }

}
