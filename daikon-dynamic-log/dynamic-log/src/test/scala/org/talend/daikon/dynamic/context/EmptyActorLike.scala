package org.talend.daikon.dynamic.context

import akka.actor.{Actor, Props}

/**
  * Catalog of empty [[Actor]]s and their [[Props]] used in tests
  */
trait EmptyActorLike {

  val props = Props(new EmptyActor)

  val propsWithType = Props(new TypedEmptyActor)

  class EmptyActor extends Actor {
    override def receive: Receive = {
      case _ =>
    }
  }

  class TypedEmptyActor[T] extends Actor {
    override def receive: Receive = {
      case _ =>
    }
  }

}
