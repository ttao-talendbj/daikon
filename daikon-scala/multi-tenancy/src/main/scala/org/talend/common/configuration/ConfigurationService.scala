package org.talend.common.configuration

import com.typesafe.config.Config

import scala.concurrent.Future

trait ConfigurationService[TENANT_ID] {

  def forTenant(tenantId: TENANT_ID, application: String): Future[Either[String, Config]]

}
