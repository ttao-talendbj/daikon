package org.talend.daikon.dynamic.akka.actors

import akka.actor.{ActorLogging, Actor}

trait UnhandledExceptionLogging extends Actor with ActorLogging {
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    super.preRestart(reason, message)
    log.error(reason, s"Unhandled exception '${reason.getMessage}' for received message: {}", message)
  }
}
