package org.talend.common.actor

import akka.persistence.{PersistenceIdentity, RuntimePluginConfig}


trait MultiTenantPersistenceIdentity extends PersistenceIdentity with RuntimePluginConfig {

  def plugin: PersistentPluginConfiguration

  override def journalPluginId = plugin.journalPluginId

  override def snapshotPluginId = plugin.snapshotPluginId

  override def journalPluginConfig = plugin.journalPluginConfig

  override def snapshotPluginConfig = plugin.snapshotPluginConfig

}
