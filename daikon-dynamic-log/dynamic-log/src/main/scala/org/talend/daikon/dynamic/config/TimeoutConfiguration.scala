package org.talend.daikon.dynamic.config

import org.talend.daikon.dynamic.jvm.JvmArguments

import scala.concurrent.duration.{FiniteDuration, _}

/**
  * Exposes default timeout configuration depending on whether the application is started in debug mode or not.
  */
trait TimeoutConfiguration {

  val LONG_TIME = 1.day

  val SHORT_TIME = 10.seconds

  val finiteTimeout: FiniteDuration = if (JvmArguments.debug) LONG_TIME else SHORT_TIME

  val finiteTimeoutWithRetry: FiniteDuration = SHORT_TIME

  val maxRetry: Int = 10
}
