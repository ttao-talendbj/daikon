package org.talend.daikon.dynamic.model

import javax.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class Constants @Inject()(configuration: Configuration) {

  val applicationName: String = configuration
    .getOptional[String]("application.name")
    .getOrElse("!!!application.name not found!!!")

}

object Constants {
  val DATASET = "dataset"
  val DATASTREAMS = "datastreams"
}
