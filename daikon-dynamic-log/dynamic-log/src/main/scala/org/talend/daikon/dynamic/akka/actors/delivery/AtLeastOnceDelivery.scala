package org.talend.daikon.dynamic.akka.actors.delivery

import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorPath, ActorRef, Stash}
import org.talend.daikon.dynamic.akka.messages.RequestReply.{Ack, Request, Retry}
import org.talend.daikon.dynamic.config.ConfigProps
import play.api.Configuration

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * This trait "AtLeastOnceDelivery" is expected to resolve issues relative to delivery, i.e. when temporary network
  * issues occur for a given message, or for other reason a message can't arrive to its target.<br/><br/>
  *
  * At-least-once delivery means that for each message handed to the mechanism potentially multiple attempts are made
  * at delivering it, such that at least one succeeds; again, in more casual terms this means that messages may be
  * duplicated but not lost.<br/>
  * For each message retrieved by a receiver an acknowledgement is sent back to the message sender.<br/>
  * In case this acknowledgement is not received after a certain time frame, the message is resend.<br/>
  * send a message to an other actor, then retry while no acknowledgement has been received by the sender actor.
  * <br/><br/>
  *
  * This trait "AtLeastOnceDelivery" is expected to be implemented both by the "Sender" actor and the "Receiver"
  * actor.<br/><br/>
  *
  * Retries number and frequency are configured by the properties
  * '''`at-least-once-delivery.retry-nr`''' and '''`at-least-once-delivery.retry-delay-ms`'''.<br/><br/>
  *
  * The "sender" actor and the "Receiver" actor must extend the trait
  * [[org.talend.daikon.dynamic.akka.actors.delivery.AtLeastOnceDelivery]].<br/><br/>
  *
  * To send requests to the "Receiver" actor, you have to call the following method:
  * {{{
  *   sendWithRetries_strictOrder(receiverActor, message)
  * }}}
  *
  * The "Sender" actor should implement the call to method '''`ignoreRetryAndAck`''' to avoid that
  * obsolete
  * [[org.talend.daikon.dynamic.akka.messages.RequestReply.Retry]] and
  * [[org.talend.daikon.dynamic.akka.messages.RequestReply.Ack]] messages are
  * logged as warning when the current behavior is your business behavior :
  * {{{
  * override def receive: Receive = ignoreRetryAndAck orElse mySenderBusinessPF ...
  * }}}
  *
  * The "Receiver" actor must implement the call to '''`receiveRequest`''' to receive the messages "Request" incoming
  * from the "Sender" actor:
  * {{{
  * override def receive: Receive = receiveRequest orElse myReceiverBusinessPF ...
  * }}}
  *
  *
  * In case of the current actor is both a "Sender" or a "Receiver" you can combine methods '''`ignoreRetryAndAck`'''
  * and '''`receiveRequest`''':
  * {{{
  * override def receive: Receive = receiveRequest orElse ignoreRetryAndAck orElse mySenderBusinessPF ...
  * }}}
  *
  *
  * Additional code is required in companion of shard "Receiver" actor such as:
  * {{{
  * def extractEntityId: ShardRegion.ExtractEntityId = {
  *     case request@Request(msg, _) =>
  *         val id = extractEntityId(msg)._1
  *         (id, request)
  *     ...
  * }
  *
  * def extractShardId(shardsCount: Int): ShardRegion.ExtractShardId = {
  *     case Request(msg, _) =>
  *         extractShardId(shardsCount)(msg)
  *     ...
  * }
  * }}}
  */
trait AtLeastOnceDelivery extends Actor with Stash with ActorLogging {

  ///////////////////////////////////////////////////////////////////////////////////////////////////
  // Sender
  ///////////////////////////////////////////////////////////////////////////////////////////////////

  private var pendingMessages = scala.collection.mutable.Set[String]()

  private var sequenceNr = UUID.randomUUID().toString

  /**
    * Send a <b>message</b> to the actor <b>receiver</b>, then retries while no acknowledgement has been received from
    * <b>receiver</b>, the number of retries and the delay depends on the application configuration properties
    * 'at-least-once-delivery.retry-delay-ms' and 'at-least-once-delivery.retry-nr'.<br/><br/>
    *
    * Incoming messages are queued in the sender mailbox while no acknowledgement has been received from the receiver,
    * so it results a strict order of messages received by sender and the received messages by receiver.
    *
    * @param message       the message to send
    * @param receiver      the receiver for which the message will be sent
    * @param configuration the Play configuration
    */
  protected def sendWithRetries_strictOrder(receiver: ActorRef, message: Any)(
    implicit configuration: Configuration): Unit = {
    log.debug("sendWithRetries_strictOrder(receiver='{}', message='{}')", receiver, message)
    sequenceNr = UUID.randomUUID().toString
    val request = Request(message, sequenceNr)
    receiver ! request
    val retryNr = 1
    val retry = Retry(receiver, request, retryNr)
    val retryDelayConfig = ConfigProps.getAtLeastOnceDeliveryRetryDelayMs
    val delayDuration = FiniteDuration(retryDelayConfig, TimeUnit.MILLISECONDS)
    context.system.scheduler.scheduleOnce(delayDuration, self, retry)

    if (pendingMessages.isEmpty) {
      context.become(receiveRetry orElse receiveRequest orElse waitingForAck_stashOtherMsgs, discardOld = false)
    }

    pendingMessages += sequenceNr
  }

  private def waitingForAck_stashOtherMsgs: Receive = {
    case Ack(sequenceNrAck) =>
      log.debug("Ack({})", sequenceNrAck)
      removeFromPending(sequenceNrAck)

    case _ =>
      stash()

  }

  private def receiveRetry(implicit configuration: Configuration): Receive = {
    case retry @ Retry(receiver, request, retryNr) =>
      if (pendingMessages.contains(request.sequenceNrDelivery)) {
        log.debug("receiveRetry retry='{}'", retry)
        receiver ! request
        val retryNumber = retryNr + 1
        val retryNrConfig = ConfigProps.getAtLeastOnceDeliveryRetryNr
        if (retryNumber <= retryNrConfig) {
          val newRetry = retry.copy(retryNr = retryNumber)
          val retryDelayConfig = ConfigProps.getAtLeastOnceDeliveryRetryDelayMs
          val delayDuration = FiniteDuration(retryDelayConfig, TimeUnit.MILLISECONDS)
          context.system.scheduler.scheduleOnce(delayDuration, self, newRetry)
          log.debug("Retry rescheduled with sequenceNr {} : {}", request.sequenceNrDelivery, newRetry)
        } else {
          removeFromPending(request.sequenceNrDelivery)
          log.error("Not able to deliver the following message to {}: {}", receiver, request.msg)
        }
      } else {
        log.debug("Retry ignored because sequenceNr {} is no more pending: {}", request.sequenceNrDelivery, retry)
      }
  }

  private def removeFromPending(sequenceNrAck: String): Unit = {
    pendingMessages -= sequenceNrAck
    if (pendingMessages.isEmpty) {
      context.unbecome()
      unstashAll()
    }
  }

  /**
    * Ignore [[org.talend.daikon.dynamic.akka.messages.RequestReply.Retry]]
    * and [[org.talend.daikon.dynamic.akka.messages.RequestReply.Ack]] messages.
    * To avoid the obsolete messages are
    * logged as warning, you should use this method in your receive.
    *
    * @return the [[Receive]]
    */
  protected def ignoreRetryAndAck: Receive = ignoreRetry orElse ignoreAck

  /**
    * Ignore [[org.talend.daikon.dynamic.akka.messages.RequestReply.Retry]] messages.
    * To avoid the obsolete messages are
    * logged as warning, you should use this method in your receive.
    *
    * @return the [[Receive]]
    */
  protected def ignoreRetry: Receive = {
    case retry @ Retry(_, request, _) =>
      log.debug("Retry with sequenceNr {} sent by {} ignored: {}", request.sequenceNrDelivery, sender(), retry)
  }

  /**
    * Ignore [[org.talend.daikon.dynamic.akka.messages.RequestReply.Ack]] messages.
    * To avoid the obsolete messages are
    * logged as warning, you should use this method in your receive.
    *
    * @return the [[Receive]]
    */
  protected def ignoreAck: Receive = {
    case Ack(sequenceNrAck) =>
      log.debug("Ack with sequenceNr {} sent by {} ignored", sequenceNrAck, sender())
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////////
  // Receiver
  ///////////////////////////////////////////////////////////////////////////////////////////////////

  private var committedMessages = scala.collection.mutable.Map[CommittedMessageKey, Long]()

  /**
    * Receive the [[org.talend.daikon.dynamic.akka.messages.RequestReply.Request]] embedding the business message and
    * the sequence number of the message.
    *
    * @param configuration the Play configuration to the retry number and the period duration of retry
    * @return the [[Receive]]
    */
  def receiveRequest(implicit configuration: Configuration): Receive = {
    case request @ Request(message, sequenceNrDelivery) =>
      val _sender = sender()
      val key = buildKey(_sender, request)
      // send Ack whatever message has already been committed, because Ack could not be received previously by Sender
      _sender ! Ack(sequenceNrDelivery)
      if (!committedMessages.contains(key)) {
        log.debug(s"Self forward $message. Key: $key")
        cleanCommittedMessages
        committedMessages += (key -> System.currentTimeMillis())
        self forward message
      } else {
        log.warning(s"Did not self forward $message. Key: $key")
      }
  }

  private def buildKey(actor: ActorRef, request: Request): CommittedMessageKey =
    CommittedMessageKey(actor.path, request.sequenceNrDelivery)

  private def cleanCommittedMessages(implicit configuration: Configuration) = {
    val currentTime = System.currentTimeMillis()
    val retryNrConfig = ConfigProps.getAtLeastOnceDeliveryRetryNr
    val retryDelayConfig = ConfigProps.getAtLeastOnceDeliveryRetryDelayMs
    committedMessages.retain((_, timestamp) => timestamp + (retryNrConfig + 1) * retryDelayConfig > currentTime)
  }

}

case class CommittedMessageKey(actorPath: ActorPath, sequenceNrDelivery: String)
