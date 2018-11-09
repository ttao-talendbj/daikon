package org.talend.daikon.dynamic.persistence.postgresql.config

import com.typesafe.config.{Config, ConfigFactory}
import play.api.Logger

trait DatabaseConfigProviderLike {

  /**
    * Retrieves a [[Config]] that contains the database configuration related with the provided tenant ID.
    *
    * @param tenantId The tenant ID to retrieve the database configuration for
    * @return the relevant database configuration
    */
  def getConfig(tenantId: String): Config
}

/**
  * OSS implementation of the [[DatabaseConfigProviderLike]] trait, without any tenancy awareness.
  */
class DatabaseConfigProviderOss extends DatabaseConfigProviderLike {

  val logger = Logger(this.getClass)

  override def getConfig(tenantId: String): Config = {
    ConfigFactory.load()
  }
}
