package org.talend.daikon.dynamic.config

import play.api.libs.json.JsString

/**
  * Catalog of JSON keys used in the UI Spec
  */
trait UiSpecKey {

  // The name of the key that wraps the TCOMP data
  val tcompKey = "properties"

  // The name of the "properties" key in a JsonSchema
  val jsonSchemaPropertiesKey = "properties"

  // The name of the key that wraps the data in a TCOMP Ui Spec
  val uiSpecPropertiesKey = "properties"

  // The name of the key that wraps the properties in form data parameter
  val dataPropertiesKey = "properties"

  val uiSpecJsonSchemaKey = "jsonSchema"

  val uiSpecJsonSchemaRequiredKey = "required"

  val uiSpecUiSchemaKey = "uiSchema"

  val uiSpecUiOrder = "ui:order"

  val uiSpecUiAutoFocus = "ui:autofocus"

  val uiSpecUiColumnName = "columnName"

  val uiSpecUiDatalist = "datalist"

  val dataPropertyDatasetId = "datasetId"

  val datasetInfoKey = "datasetInfo"

  val uiSpecUiWidget = "ui:widget"

  val dependenciesKey = "dependencies"

  val withUiSpec = "withUiSpec"

  val advancedUiSpec = "advanced"

  val typologyKey = "typology"

  val executionEngineKey = "executionEngine"

  val datastoreDefinitionSourceKey = "inputCompName"

  val datastoreDefinitionSinkKey = "outputCompName"

  val defaultPassword = "<Not Available>"

  val defaultPasswordJS = JsString(defaultPassword)

  val passwordKey = "password"

  val hiddenKey = "hidden"

}
