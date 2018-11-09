package org.talend.daikon.dynamic.jvm

object JvmArguments {

  /**
    * Expected to be used somewhere to enable special loggings or operation.
    */
  val debug = "true" == System.getProperty("debug")

  /**
    * Expected to be used somewhere to enable longTimeout.
    */
  val fullStack = "true" == System.getProperty("fullStack")

  /**
    * Expected to be used somewhere to force Full GC at any moments.
    */
  val forceGC = "true" == System.getProperty("forceGC")

}
