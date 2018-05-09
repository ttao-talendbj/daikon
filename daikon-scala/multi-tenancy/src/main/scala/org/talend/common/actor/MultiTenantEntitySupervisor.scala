package org.talend.common.actor

import akka.actor.{ Actor, ActorLogging, ActorRef, Props, ReceiveTimeout, Stash }
import akka.cluster.sharding.ShardRegion.Passivate
import akka.pattern.pipe
import com.typesafe.config.Config
import org.talend.common.configuration.ConfigurationService

import scala.concurrent.{ ExecutionContext, Future }

object MultiTenantEntitySupervisor {

  sealed trait ConfigurationMessage

  case class ConfigurationSuccess(originator: ActorRef, command: Any, config: Config) extends ConfigurationMessage

  case class ConfigurationFailure(originator: ActorRef, command: Any, reason: String) extends ConfigurationMessage

  case object StopCommand

}

trait MultiTenantEntitySupervisor[TENANT_ID] extends ActorLogging {
  this: Actor with Stash with ActorLogging =>

  import MultiTenantEntitySupervisor._

  def application: String

  def extractTenantIdFrom(command: Any): TENANT_ID

  def configurationService: ConfigurationService[TENANT_ID]

  def childProps(command: Any, config: Config): Props

  def handleError(originator: ActorRef, command: Any, reason: String): Unit

  implicit val ec: ExecutionContext

  override def receive: Receive = waitingForInitialMessage

  def waitingForInitialMessage: Receive = {
    case command =>
      fetchConfiguration(sender(), command) pipeTo self
      context become waitingForConfiguration
  }

  def waitingForConfiguration: Receive = {
    case ConfigurationSuccess(originator, command, config) =>
      unstashAll()
      val child = context.actorOf(childProps(command, config))
      child.tell(command, originator)
      context become configured(child)
    case ConfigurationFailure(originator, command, reason) =>
      handleError(originator, command, reason)
      context.parent ! Passivate(StopCommand)
    case StopCommand =>
      context stop self
    case _ =>
      stash()
  }

  def configured(child: ActorRef): Receive = {
    case ReceiveTimeout =>
      context.parent ! Passivate(StopCommand)
    case StopCommand =>
      context stop self
    case command =>
      child forward command
  }

  def fetchConfiguration(originator: ActorRef, command: Any): Future[ConfigurationMessage] = {
    val tenantId = extractTenantIdFrom(command)
    log.debug("fetch configuration for tenant {}", tenantId)
    configurationService.forTenant(tenantId, application) map {
      case Right(config) =>
        ConfigurationSuccess(originator, command, config)
      case Left(reason) =>
        log.error("could not process command {}; tenant-specific configuration could not be retrieved because {}", command, reason)
        ConfigurationFailure(originator, command, reason)
    } recover {
      case exception =>
        log.error(exception, "could not process command {}; tenant-specific configuration could not be retrieved", command)
        ConfigurationFailure(originator, command, exception.getMessage)
    }
  }

}
