package org.talend.daikon.dynamic.config

import java.util.concurrent.TimeUnit

import org.talend.daikon.dynamic.util.ConfigUtil
import play.api.Configuration

import scala.concurrent.duration.{Duration, FiniteDuration}

object ConfigProps {

  val DEFAULT_TIMEOUT = 5

  val DEFAULT_MEDIUM_TIMEOUT = 10

  val DEFAULT_LONG_TIMEOUT = 60

  val DEFAULT_AT_LEAST_ONCE_DELIVERY_RETRY_NR = 10

  val DEFAULT_AT_LEAST_ONCE_DELIVERY_RETRY_DELAY_MS = 2000

  val DEFAULT_KAFKA_TOPICS_WAITING_TIME_DELAY_MS = 1000

  val DEFAULT_SHARDS_COUNT = 100

  def getBridgeToActorTimeoutSec(implicit config: Configuration): Long = {
    config.getOptional[Long]("bridge-to-actor.timeout_sec").getOrElse(DEFAULT_TIMEOUT)
  }

  /**
    * Returns a timeout for long synchronous calls. This timeout is retrieved from the application configuration.
    *
    * @param config the [[Configuration]] to get the timeout from
    * @return the timeout configuration
    */
  def getBridgeToActorLongTimeoutSec(implicit config: Configuration): Long = {
    config.getOptional[Long]("bridge-to-actor.long_timeout_sec").getOrElse(DEFAULT_LONG_TIMEOUT)
  }

  /**
    * Returns the timeout expected to be used when fetching the sample. This timeout is retrieved from the application
    * configuration.
    *
    * @param config the [[Configuration]] to get the timeout from
    * @return the timeout configuration
    */
  def getBridgeToActorFetchSampleTimeoutSec(implicit config: Configuration): Long = {
    config.getOptional[Long]("bridge-to-actor.fetch-sample_long_timeout_sec").getOrElse(DEFAULT_LONG_TIMEOUT)
  }

  def getJournalActorHandleEventTimeoutSec(implicit config: Configuration): Long = {
    config.getOptional[Long]("journal-actor.handle-event_timeout_sec").getOrElse(DEFAULT_TIMEOUT)
  }

  /**
    * Timeout for the import pipeline processing.
    *
    * @param config the Play [[Configuration]]
    * @return the timeout configuration
    */
  def getImportTimeoutSec(implicit config: Configuration): Long = {
    config.getOptional[Long]("stream.import.timeout.sec").getOrElse(DEFAULT_MEDIUM_TIMEOUT)
  }

  /**
    * Time to wait between the subscription on a kafka topics and when we can assure read the first message from it.
    *
    * @param config the Play [[Configuration]]
    * @return the timeout configuration
    */
  def getImportKafkaWait(implicit config: Configuration): Long = {
    config.getOptional[Long]("kafka.topics.waiting.time").getOrElse(DEFAULT_KAFKA_TOPICS_WAITING_TIME_DELAY_MS)
  }

  /**
    * Return the Kafka bootstrap servers
    *
    * @param config the Play [[Configuration]]
    * @return a string containg something like "host1:port1,host2:port2"
    */
  def getKafkaBootstrapServers(implicit config: Configuration): String = {
    ConfigUtil.getStringOrFail(config, "kafka.bootstrapServers")
  }

  /**
    * Return the number of retries when using the at least once delivery guaranty with trait
    * [[org.talend.daikon.dynamic.akka.actors.delivery.AtLeastOnceDelivery]].
    *
    * @param config the Play [[Configuration]]
    * @return a number
    */
  def getAtLeastOnceDeliveryRetryNr(implicit config: Configuration): Int = {
    config.getOptional[Int]("at-least-once-delivery.retry-nr").getOrElse(DEFAULT_AT_LEAST_ONCE_DELIVERY_RETRY_NR)
  }

  /**
    * Return the delay between each retry when using the at least once delivery guaranty with trait
    * [[org.talend.daikon.dynamic.akka.actors.delivery.AtLeastOnceDelivery]].
    *
    * @param config the Play [[Configuration]]
    * @return the number of millisec
    */
  def getAtLeastOnceDeliveryRetryDelayMs(implicit config: Configuration): Long = {
    config
      .getOptional[Long]("at-least-once-delivery.retry-delay-ms")
      .getOrElse(DEFAULT_AT_LEAST_ONCE_DELIVERY_RETRY_DELAY_MS)
  }

  /**
    * Return the shards count used to extract the shard id.
    *
    * @param config the Play [[Configuration]]
    * @return the shards count
    */
  def getShardsCount(implicit config: Configuration): Int = {
    config.getOptional[Int]("shard-extractor.shards-count").getOrElse(DEFAULT_SHARDS_COUNT)
  }

  /**
    * Return the akka receive timeout as a [[Duration]].
    * This timeout is equal to "akka.receive.timeout" or 60 seconds by default
    * @param config the Play [[Configuration]]
    * @return the akka receive timeout
    */
  def getAkkaReceiveTimeout(implicit config: Configuration): Duration = {
    val timeoutInSec: Long = config.getOptional[Long]("akka.receive.timeout").getOrElse(DEFAULT_LONG_TIMEOUT)
    FiniteDuration(timeoutInSec, TimeUnit.SECONDS)
  }
}
