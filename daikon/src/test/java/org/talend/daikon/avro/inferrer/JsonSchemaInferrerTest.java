// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.avro.inferrer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.io.IOException;
import java.util.List;

import org.apache.avro.Schema;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test {@link JsonSchemaInferrer}
 */
public class JsonSchemaInferrerTest {

    private final JsonSchemaInferrer jsonSchemaInferrer = JsonSchemaInferrer.createJsonSchemaInferrer();

    private final String simpleJson = "{\"a\": {\"b\": \"b1\"}, \"d\": \"d1\"}";

    private final String arrayJson = "{\"a\": [{\"b\": \"b1\"}, {\"b\": \"b2\"}]}";

    private final String nullJson = "{\"a\": null}";

    private final String intJson = "{\"a\": 100}";

    private final String doubleJson = "{\"a\": 100.1}";

    private final String simpleBooleanJson = "{\"a\": {\"b\": false}, \"d\": true}";

    private final String arrayBooleanJson = "{\"a\": [{\"b\": true}, {\"b\": false}]}";

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#simpleJson}
     *
     * Expected fields:
     * [{"name":"a","type":{"type":"record","fields":[{"name":"b","type":["string","null"]}]}},{"name":"d","type":["string","null"]}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsSimpleJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(simpleJson);

        List<Schema.Field> fieldList = jsonSchemaInferrer.getFields(jsonNode);
        assertThat(fieldList, hasSize(2));

        // Check `a` field
        Schema.Field fieldA = fieldList.get(0);

        // "name":"a"
        assertThat(fieldA.name(), is(equalTo("a")));

        // Check `a` field type content
        // "type":{"type":"record","fields":[{"name":"b","type":["string","null"]}]}

        // "type":"record"
        assertThat(fieldA.schema().getType().getName(), is(equalTo("record")));

        // "fields":[{"name":"b","type":["null","string"]}]
        List<Schema.Field> fieldATypeFields = fieldA.schema().getFields();

        // Check `b` field
        Schema.Field fieldB = fieldATypeFields.get(0);

        // "name":"b"
        assertThat(fieldB.name(), is(equalTo("b")));

        // Check `b` field type content
        // "type":["string","null"]
        Schema.Type fieldBType = fieldB.schema().getType();
        assertThat(fieldBType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaBTypeItems = fieldB.schema().getTypes();
        assertThat(fieldSchemaBTypeItems.get(0).getName(), is(equalTo("string")));
        assertThat(fieldSchemaBTypeItems.get(1).getName(), is(equalTo("null")));

        // Check `d` field
        Schema.Field fieldD = fieldList.get(1);

        // "name":"d"
        assertThat(fieldD.name(), is(equalTo("d")));

        // Check `d` field type content
        // "type":["string","null"]
        Schema.Type fieldDType = fieldD.schema().getType();
        assertThat(fieldDType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaDTypeItems = fieldD.schema().getTypes();
        assertThat(fieldSchemaDTypeItems.get(0).getName(), is(equalTo("string")));
        assertThat(fieldSchemaDTypeItems.get(1).getName(), is(equalTo("null")));
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#arrayJson}
     *
     * Expected fields:
     * [{"name":"a","type":{"type":"array","items":{"type":"record","fields":[{"name":"b","type":["string","null"]}]}}}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsArrayJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(arrayJson);

        List<Schema.Field> fieldList = jsonSchemaInferrer.getFields(jsonNode);
        assertThat(fieldList, hasSize(1));

        // Check `a` field
        Schema.Field fieldA = fieldList.get(0);

        // "name":"a"
        assertThat(fieldA.name(), is(equalTo("a")));

        // Check `a` field type content
        // "type":{"type":"array","items":{"type":"record","fields":[{"name":"b","type":["string","null"]}]}}

        // "type":"array"
        assertThat(fieldA.schema().getType().getName(), is(equalTo("array")));

        List<Schema.Field> fieldAArray = fieldA.schema().getElementType().getFields();

        // Check `b` field
        Schema.Field fieldB = fieldAArray.get(0);

        // "name":"b"
        assertThat(fieldB.name(), is(equalTo("b")));

        // Check `b` field type content
        // "type":["string","null"]
        Schema.Type fieldBType = fieldB.schema().getType();
        assertThat(fieldBType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaBTypeItems = fieldB.schema().getTypes();
        assertThat(fieldSchemaBTypeItems.get(0).getName(), is(equalTo("string")));
        assertThat(fieldSchemaBTypeItems.get(1).getName(), is(equalTo("null")));
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#nullJson}
     *
     * Expected fields:
     * [{"name":"a","type":["string","null"]}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsNullJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(nullJson);

        List<Schema.Field> fieldList = jsonSchemaInferrer.getFields(jsonNode);
        assertThat(fieldList, hasSize(1));

        // Check `a` field
        Schema.Field fieldA = fieldList.get(0);

        // "name":"a"
        assertThat(fieldA.name(), is(equalTo("a")));

        // Check `a` field type content
        // "type":["string","null"]
        Schema.Type fieldAType = fieldA.schema().getType();
        assertThat(fieldAType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaATypeItems = fieldA.schema().getTypes();
        assertThat(fieldSchemaATypeItems.get(0).getName(), is(equalTo("string")));
        assertThat(fieldSchemaATypeItems.get(1).getName(), is(equalTo("null")));
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#intJson}
     *
     * Expected fields:
     * [{"name":"a","type":["int","null"]}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsIntJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(intJson);

        List<Schema.Field> fieldList = jsonSchemaInferrer.getFields(jsonNode);
        assertThat(fieldList, hasSize(1));

        // Check `a` field
        Schema.Field fieldA = fieldList.get(0);

        // "name":"a"
        assertThat(fieldA.name(), is(equalTo("a")));

        // Check `a` field type content
        // "type":["int","null"]
        Schema.Type fieldAType = fieldA.schema().getType();
        assertThat(fieldAType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaATypeItems = fieldA.schema().getTypes();
        assertThat(fieldSchemaATypeItems.get(0).getName(), is(equalTo("int")));
        assertThat(fieldSchemaATypeItems.get(1).getName(), is(equalTo("null")));
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#doubleJson}
     *
     * Expected fields:
     * [{"name":"a","type":["double","null"]}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsDoubleJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(doubleJson);

        List<Schema.Field> fieldList = jsonSchemaInferrer.getFields(jsonNode);
        assertThat(fieldList, hasSize(1));

        // Check `a` field
        Schema.Field fieldA = fieldList.get(0);

        // "name":"a"
        assertThat(fieldA.name(), is(equalTo("a")));

        // Check `a` field type content
        // "type":["double","null"]
        Schema.Type fieldAType = fieldA.schema().getType();
        assertThat(fieldAType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaATypeItems = fieldA.schema().getTypes();
        assertThat(fieldSchemaATypeItems.get(0).getName(), is(equalTo("double")));
        assertThat(fieldSchemaATypeItems.get(1).getName(), is(equalTo("null")));
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#simpleBooleanJson}
     *
     * Expected fields:
     * [{"name":"a","type":{"type":"record","fields":[{"name":"b","type":["boolean","null"]}]}},{"name":"d","type":["boolean","null"]}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsSimpleBooleanJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(simpleBooleanJson);

        List<Schema.Field> fieldList = jsonSchemaInferrer.getFields(jsonNode);
        assertThat(fieldList, hasSize(2));

        // Check `a` field
        Schema.Field fieldA = fieldList.get(0);

        // "name":"a"
        assertThat(fieldA.name(), is(equalTo("a")));

        // Check `a` field type content
        // "type":{"type":"record","fields":[{"name":"b","type":["boolean","null"]}]}

        // "type":"record"
        assertThat(fieldA.schema().getType().getName(), is(equalTo("record")));

        // "fields":[{"name":"b","type":["boolean","null"]}]
        List<Schema.Field> fieldATypeFields = fieldA.schema().getFields();

        // Check `b` field
        Schema.Field fieldB = fieldATypeFields.get(0);

        // "name":"b"
        assertThat(fieldB.name(), is(equalTo("b")));

        // Check `b` field type content
        // "type":["string","null"]
        Schema.Type fieldBType = fieldB.schema().getType();
        assertThat(fieldBType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaBTypeItems = fieldB.schema().getTypes();
        assertThat(fieldSchemaBTypeItems.get(0).getName(), is(equalTo("boolean")));
        assertThat(fieldSchemaBTypeItems.get(1).getName(), is(equalTo("null")));

        // Check `d` field
        Schema.Field fieldD = fieldList.get(1);

        // "name":"d"
        assertThat(fieldD.name(), is(equalTo("d")));

        // Check `d` field type content
        // "type":["boolean","null"]
        Schema.Type fieldDType = fieldD.schema().getType();
        assertThat(fieldDType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaDTypeItems = fieldD.schema().getTypes();
        assertThat(fieldSchemaDTypeItems.get(0).getName(), is(equalTo("boolean")));
        assertThat(fieldSchemaDTypeItems.get(1).getName(), is(equalTo("null")));
    }

    /**
     * Test {@link JsonSchemaInferrer#getFields(JsonNode)}
     *
     * Get fields of the input record: {@link JsonSchemaInferrerTest#arrayBooleanJson}
     *
     * Expected fields:
     * [{"name":"a","type":{"type":"array","items":{"type":"record","fields":[{"name":"b","type":["boolean","null"]}]}}}]
     *
     * @throws IOException
     */
    @Test
    public void testGetFieldsArrayBooleanJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(arrayBooleanJson);

        List<Schema.Field> fieldList = jsonSchemaInferrer.getFields(jsonNode);
        assertThat(fieldList, hasSize(1));

        // Check `a` field
        Schema.Field fieldA = fieldList.get(0);

        // "name":"a"
        assertThat(fieldA.name(), is(equalTo("a")));

        // Check `a` field type content
        // "type":{"type":"array","items":{"type":"record","fields":[{"name":"b","type":["boolean","null"]}]}}

        // "type":"array"
        assertThat(fieldA.schema().getType().getName(), is(equalTo("array")));

        List<Schema.Field> fieldAArray = fieldA.schema().getElementType().getFields();

        // Check `b` field
        Schema.Field fieldB = fieldAArray.get(0);

        // "name":"b"
        assertThat(fieldB.name(), is(equalTo("b")));

        // Check `b` field type content
        // "type":["boolean","null"]
        Schema.Type fieldBType = fieldB.schema().getType();
        assertThat(fieldBType.getName(), is(equalTo("union")));
        List<Schema> fieldSchemaBTypeItems = fieldB.schema().getTypes();
        assertThat(fieldSchemaBTypeItems.get(0).getName(), is(equalTo("boolean")));
        assertThat(fieldSchemaBTypeItems.get(1).getName(), is(equalTo("null")));
    }

    /**
     * Test {@link JsonSchemaInferrer#inferSchema(String)}
     *
     * Convert the input record: {@link JsonSchemaInferrerTest#simpleJson} to avro schema
     *
     * Expected avro schema:
     * {"type":"record","name":"outer_record","namespace":"org.talend",
     * "fields":[{"name":"a","type":{"type":"record","fields":[{"name":"b","type":["string","null"]}]}},
     * {"name":"d","type":["string","null"]}]}
     *
     * @throws IOException
     */
    @Test
    public void testInferSchema() throws IOException {

        Schema schema = jsonSchemaInferrer.inferSchema(simpleJson);
        List<Schema.Field> fieldList = schema.getFields();
        assertThat(fieldList, hasSize(2));

        // Get `a` field schema and check its type
        Schema.Field fieldA = fieldList.get(0);
        Schema schemaA = fieldA.schema();
        assertThat(schemaA.getType().getName(), is(equalTo("record")));

        List<Schema.Field> fieldsA = schemaA.getFields();

        // Check `b` field schema
        Schema.Field fieldB = fieldsA.get(0);
        Schema schemaB = fieldB.schema();
        assertThat(schemaB.getType().getName(), is(equalTo("union")));
        List<Schema> typesB = schemaB.getTypes();
        assertThat(typesB.get(0).getName(), is(equalTo("string")));
        assertThat(typesB.get(1).getName(), is(equalTo("null")));

        // Check `d` field schema
        Schema.Field fieldD = fieldList.get(1);
        Schema schemaD = fieldD.schema();
        assertThat(schemaD.getType().getName(), is(equalTo("union")));
        List<Schema> typesD = schemaD.getTypes();
        assertThat(typesD.get(0).getName(), is(equalTo("string")));
        assertThat(typesD.get(1).getName(), is(equalTo("null")));
    }
}
