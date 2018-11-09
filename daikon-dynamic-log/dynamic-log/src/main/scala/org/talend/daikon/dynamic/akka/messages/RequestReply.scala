package org.talend.daikon.dynamic.akka.messages

import akka.actor.ActorRef

object RequestReply {

  /**
    * The request sent to a [[org.talend.daikon.dynamic.akka.actors.delivery.AtLeastOnceDelivery]]
    *
    * @param msg                the embedd business message
    * @param sequenceNrDelivery the sequence number used to store already commited message at receiver and used by the
    *                           sender to ignore messages with an unexpected sequence number.
    *                           A new sequence number is generated for each new business message.
    */
  case class Request(msg: Any, sequenceNrDelivery: String)

  /**
    * A retry message emnedding the request sent from the sender to itself according to the configured retry number and
    * the configured retry period.
    *
    * @param receiver the receiver we want to sent the request at next retry
    * @param request  the request we want to send to receiver
    * @param retryNr  the current retry number, inremented at each retry
    */
  case class Retry(receiver: ActorRef, request: Request, retryNr: Int)

  /**
    * Message sent by receiver to sender when the Request has been received by receiver
    *
    * @param sequenceNrAck the sequence number of request message
    */
  case class Ack(sequenceNrAck: String)

  /**
    * The wrapping class which contains the business message with the targets for a potential reply.
    *
    * @param msg            the wrapped business message
    * @param originalSender the original sender, this is the final target for a reply
    * @param proxySender    the proxy sender, the is the intermediate target for a reply
    */
  case class MsgFrom(msg: Any, originalSender: ActorRef, proxySender: ActorRef)

  case class Reply[+VALUE](data: VALUE)

  /**
    * The wrapping class which contains the value to reply to the final target.
    *
    * @param data        the wrapped business message
    * @param finalTarget the final target (e.g.: temporary controller actor) after sending to the current target
    *                    (e.g.: proxySender)
    */
  case class ReplyTo(data: Any, finalTarget: ActorRef)

}
