package org.talend.daikon.dynamic

/**
  * Trait used to disable check of environment for tests
  */
trait SecretForTests {
  // set the property to pass the tests
  System.setProperty("OSS_PRODUCT_SECRET", "TFD123")
}
