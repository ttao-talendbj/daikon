package org.talend.daikon.dynamic.context

import akka.actor.ActorSystem
import akka.testkit.TestKitBase
import com.typesafe.config.{Config, ConfigFactory}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import org.talend.daikon.dynamic.SecretForTests
import org.talend.daikon.dynamic.model.Constants
import play.api.{Application, Configuration}
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceableModule}

import scala.reflect.ClassTag
import scala.util.Random

/**
  * Default context providing a Play [[play.api.Application]] and its [[play.api.inject.Injector]]
  */
trait DefaultContext {

  /**
    * A Play [[play.api.Application]]
    */
  val application: Application

  /**
    * @tparam T the type to retrieve from the injector
    * @return the instance of [[T]]
    */
  def inject[T: ClassTag]: T = application.injector.instanceOf[T]

  def buildConfigMap(configProperties: String): Map[String, _] = {
    val config = ConfigFactory.parseString(configProperties)
    Configuration
      .apply(config)
      .entrySet
      .map(t => t._1 -> t._2)
      .toMap
  }

}

/**
  * Default context providing an implicit [[ActorSystem]] and mix in the [[DefaultContext]]
  */
trait DefaultContextKit extends DefaultContext with SecretForTests with TestKitBase {

  import DefaultContextKit._

  implicit lazy val system: ActorSystem = ActorSystem("DefaultContext", testConfig)

  /** The example tenant ID that this has been configured for. */
  final val MyTenantId = DefaultContextKit.MyTenantId

  def testConfig: Config = {
    val defaultConfig = ConfigFactory.load()
    var config = DefaultContextKit.config
      .withFallback(defaultConfig)

    // Since akka-persistence configurations for dataset and datastreams are now separated, we have to simulate
    // distinct configurations for the inmem plugin when needed for the tests.
    if (defaultConfig.hasPath(INMEMORY_JOURNAL)) {
      config = config
        .withValue(
          s"$INMEMORY_JOURNAL-${Constants.DATASET}",
          defaultConfig.getValue(INMEMORY_JOURNAL)
        )
        .withValue(
          s"$INMEMORY_JOURNAL-${Constants.DATASTREAMS}",
          defaultConfig.getValue(INMEMORY_JOURNAL)
        )
    }
    if (defaultConfig.hasPath(INMEMORY_SNAPSHOT_STORE)) {
      config = config
        .withValue(
          s"$INMEMORY_SNAPSHOT_STORE-${Constants.DATASET}",
          defaultConfig.getValue(INMEMORY_SNAPSHOT_STORE)
        )
        .withValue(
          s"$INMEMORY_SNAPSHOT_STORE-${Constants.DATASTREAMS}",
          defaultConfig.getValue(INMEMORY_SNAPSHOT_STORE)
        )
    }
    config
  }
}

object DefaultContextKit {

  val INMEMORY_JOURNAL = "inmemory-journal"
  val INMEMORY_SNAPSHOT_STORE = "inmemory-snapshot-store"

  final val MyTenantId = "myTenantId"

  /**
    * Represents the default [[com.typesafe.config.Config]] used to instantiate the [[DefaultContextKit]]
    * [[ActorSystem]]
    */
  // TODO: extract datastreams config to DatastreamsContextKit
  val config: Config =
    ConfigFactory.parseString(
      s"""
      akka.persistence {
        journal.plugin = "inmemory-journal"
        snapshot-store.plugin = "inmemory-snapshot-store"
      }
      jdbc-journal-dataset-$MyTenantId {
        class = "akka.persistence.journal.inmem.InmemJournal"
        plugin-dispatcher = "akka.actor.default-dispatcher"
      }
      jdbc-snapshot-store-dataset-$MyTenantId {
        class = "akka.persistence.snapshot.local.LocalSnapshotStore"
        dir = "/tmp/snapshots-${Random.nextInt()}"
        plugin-dispatcher = "akka.persistence.dispatchers.default-plugin-dispatcher"
        stream-dispatcher = "akka.persistence.dispatchers.default-stream-dispatcher"
        max-load-attempts = 2
      }
      jdbc-journal-datastreams-$MyTenantId {
        class = "akka.persistence.journal.inmem.InmemJournal"
        plugin-dispatcher = "akka.actor.default-dispatcher"
      }
      jdbc-snapshot-store-datastreams-$MyTenantId {
        class = "akka.persistence.snapshot.local.LocalSnapshotStore"
        dir = "/tmp/snapshots-${Random.nextInt()}"
        plugin-dispatcher = "akka.persistence.dispatchers.default-plugin-dispatcher"
        stream-dispatcher = "akka.persistence.dispatchers.default-stream-dispatcher"
        max-load-attempts = 2
      }
      akka {
      #  loglevel = DEBUG
      #  actor {
      #    debug {
      #    # enable function of LoggingReceive, which is to log any received message at
      #    # DEBUG level
      #    receive = on
      #    }
      #  }
      }
      """
    )
}

/**
  * Common context kit
  *
  * @param overrideModule a list of [[GuiceableModule]] to override in the application
  */
class CommonServiceContextKit(overrideModule: GuiceableModule*)
  extends DefaultContextKit
  with Mockito
  with org.specs2.matcher.MustThrownExpectations
  with Scope {

  def getDefaultConfigMap: Map[String, Any] = Map[String, Any](
    "bridge-to-actor.long_timeout_sec" -> 5,
    "stream.submitting.timeout.sec" -> 5,
    "stream.killing.timeout.sec" -> 5
  )

  def getAdditonalConfigMap: Map[String, Any] = Map[String, Any]()

  override lazy val application: Application = new GuiceApplicationBuilder()
    .overrides(overrideModule: _*)
    .configure(getDefaultConfigMap ++ getAdditonalConfigMap)
    .build()

}

/**
  * Common context
  *
  * @param overrideModule a list of [[GuiceableModule]] to override in the application
  */
class CommonServiceContext(overrideModule: GuiceableModule*)
  extends DefaultContext
  with Mockito
  with org.specs2.matcher.MustThrownExpectations
  with Scope {

  def getDefaultConfigMap: Map[String, Any] = Map[String, Any](
    "bridge-to-actor.long_timeout_sec" -> 5
  )

  def getAdditonalConfigMap: Map[String, Any] = Map[String, Any]()

  override lazy val application: Application = new GuiceApplicationBuilder()
    .overrides(overrideModule: _*)
    .configure(getDefaultConfigMap ++ getAdditonalConfigMap)
    .build()
}
