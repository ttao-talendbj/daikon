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
package org.talend.daikon.avro.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.util.ArrayList;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericRecord;
import org.junit.Test;

/**
 * Test {@link JsonGenericRecordConverter}
 */
public class JsonGenericRecordConverterTest {

    private final Schema inputSchemaStrB = SchemaBuilder.record("b").fields().name("b").type().optional().stringType()
            .endRecord();

    private final Schema inputSchemaIntB = SchemaBuilder.record("b").fields().name("b").type().optional().intType().endRecord();

    private final Schema inputSchemaBoolB = SchemaBuilder.record("b").fields().name("b").type().optional().booleanType()
            .endRecord();

    private final Schema inputSimpleSchema = SchemaBuilder.record("inputSimpleRow").fields().name("a").type(inputSchemaStrB)
            .noDefault().name("d").type().optional().stringType().endRecord();

    private final Schema inputArraySchema = SchemaBuilder.record("inputArrayRow").fields().name("a")
            .type(SchemaBuilder.array().items(inputSchemaStrB)).noDefault().endRecord();

    private final Schema inputNullSchema = SchemaBuilder.record("inputNull").fields().name("a").type().nullType().nullDefault()
            .endRecord();

    private final Schema inputIntSchema = SchemaBuilder.record("inputIntRow").fields().name("a").type(inputSchemaIntB).noDefault()
            .name("d").type().optional().intType().endRecord();

    private final Schema inputBoolSchema = SchemaBuilder.record("inputBoolRow").fields().name("a").type(inputSchemaBoolB)
            .noDefault().name("d").type().optional().booleanType().endRecord();

    private final Schema inputArrayBoolSchema = SchemaBuilder.record("inputArrayBoolRow").fields().name("a")
            .type(SchemaBuilder.array().items(inputSchemaBoolB)).noDefault().endRecord();

    private final String simpleJson = "{\"a\": {\"b\": \"b1\"}, \"d\": \"d1\"}";

    private final String arrayJson = "{\"a\": [{\"b\": \"b1\"}, {\"b\": \"b2\"}]}";

    private final String nullJson = "{\"a\": null}";

    private final String intJson = "{\"a\": {\"b\": 10}, \"d\": 11}";

    private final String simpleBooleanJson = "{\"a\": {\"b\": false}, \"d\": true}";

    private final String arrayBooleanJson = "{\"a\": [{\"b\": true}, {\"b\": false}]}";

    private JsonGenericRecordConverter jsonGenericRecordConverter;

    /**
     * Test {@link JsonGenericRecordConverter#getSchema()}
     */
    @Test
    public void testGetSchema() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter(inputSimpleSchema);
        assertThat(inputSimpleSchema, is(equalTo(jsonGenericRecordConverter.getSchema())));
    }

    /**
     * Test {@link JsonGenericRecordConverter#getDatumClass()}
     */
    @Test
    public void testGetDatumClass() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter();
        assertThat(jsonGenericRecordConverter.getDatumClass(), is(equalTo(String.class)));
    }

    /**
     * Test {@link JsonGenericRecordConverter#convertToDatum(GenericRecord)}
     */
    @Test
    public void testConvertToDatum() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter(inputSimpleSchema);
        GenericRecord record = jsonGenericRecordConverter.convertToAvro(simpleJson);
        String jsonConverted = jsonGenericRecordConverter.convertToDatum(record);
        assertThat(simpleJson, is(equalTo(jsonConverted)));
    }

    /**
     * Test {@link JsonGenericRecordConverter#convertToAvro(String)}
     *
     * Get Avro Generic Record and check its nested fields values.
     *
     * Input record: {@link JsonGenericRecordConverterTest#simpleJson}
     *
     * @throws Exception
     */
    @Test
    public void testConvertToAvroSimpleJson() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter(inputSimpleSchema);

        // Get Avro Generic Record
        GenericRecord outputRecord = jsonGenericRecordConverter.convertToAvro(simpleJson);

        // Get `a` field
        GenericRecord recordA = (GenericRecord) outputRecord.get(0);

        // Check `b` field value
        assertThat((String) recordA.get("b"), is(equalTo("b1")));

        // Check `d` field value
        assertThat((String) outputRecord.get(1), is(equalTo("d1")));
    }

    /**
     * Test {@link JsonGenericRecordConverter#convertToAvro(String)}
     *
     * Get Avro Generic Record and check its nested fields values.
     *
     * Input record: {@link JsonGenericRecordConverterTest#arrayJson}
     *
     * @throws Exception
     */
    @Test
    public void testConvertToAvroArrayJson() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter(inputArraySchema);

        // Get Avro Generic Record
        GenericRecord outputRecord = jsonGenericRecordConverter.convertToAvro(arrayJson);

        // Get `a` array field
        ArrayList<GenericRecord> arrayRecordA = (ArrayList<GenericRecord>) outputRecord.get(0);

        // Check that `a` array field contains two records
        assertThat(arrayRecordA, hasSize(2));

        // Check `b` field values
        GenericRecord recordB1 = arrayRecordA.get(0);
        GenericRecord recordB2 = arrayRecordA.get(1);
        assertThat((String) recordB1.get("b"), is(equalTo("b1")));
        assertThat((String) recordB2.get("b"), is(equalTo("b2")));
    }

    /**
     * Test {@link JsonGenericRecordConverter#convertToAvro(String)}
     *
     * Get Avro Generic Record and check its nested fields values.
     *
     * Input record: {@link JsonGenericRecordConverterTest#nullJson}
     *
     * @throws Exception
     */
    @Test
    public void testConvertToAvroNullJson() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter(inputNullSchema);

        // Get Avro Generic Record
        GenericRecord outputRecord = jsonGenericRecordConverter.convertToAvro(nullJson);

        // Check that `a` field is null
        assertThat(outputRecord.get("a"), is(equalTo(null)));
    }

    /**
     * Test {@link JsonGenericRecordConverter#convertToAvro(String)}
     *
     * Get Avro Generic Record and check its nested fields values.
     *
     * Input record: {@link JsonGenericRecordConverterTest#intJson}
     *
     * @throws Exception
     */
    @Test
    public void testConvertToAvroIntJson() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter(inputIntSchema);

        // Get Avro Generic Record
        GenericRecord outputRecord = jsonGenericRecordConverter.convertToAvro(intJson);

        // Get `a` field
        GenericRecord recordA = (GenericRecord) outputRecord.get(0);

        // Check `b` field value
        assertThat((int) recordA.get("b"), is(equalTo(10)));

        // Check `d` field value
        assertThat((int) outputRecord.get(1), is(equalTo(11)));
    }

    /**
     * Test {@link JsonGenericRecordConverter#convertToAvro(String)}
     *
     * Get Avro Generic Record and check its nested fields values.
     *
     * Input record: {@link JsonGenericRecordConverterTest#simpleBooleanJson}
     *
     * @throws Exception
     */
    @Test
    public void testConvertToAvroSimpleBooleanJson() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter(inputBoolSchema);

        // Get Avro Generic Record
        GenericRecord outputRecord = jsonGenericRecordConverter.convertToAvro(simpleBooleanJson);

        // Get `a` field
        GenericRecord recordA = (GenericRecord) outputRecord.get("a");

        // Check `b` field value
        assertThat((boolean) recordA.get("b"), is(equalTo(false)));

        // Check `d` field value
        assertThat((boolean) outputRecord.get("d"), is(equalTo(true)));
    }

    /**
     * Test {@link JsonGenericRecordConverter#convertToAvro(String)}
     *
     * Get Avro Generic Record and check its nested fields values.
     *
     * Input record: {@link JsonGenericRecordConverterTest#arrayBooleanJson}
     *
     * @throws Exception
     */
    @Test
    public void testConvertToAvroArrayBooleanJson() {
        jsonGenericRecordConverter = new JsonGenericRecordConverter(inputArrayBoolSchema);

        // Get Avro Generic Record
        GenericRecord outputRecord = jsonGenericRecordConverter.convertToAvro(arrayBooleanJson);

        // Get `a` array field
        ArrayList<GenericRecord> arrayRecordA = (ArrayList<GenericRecord>) outputRecord.get("a");

        // Check that `a` array field contains two records
        assertThat(arrayRecordA, hasSize(2));

        // Check `b` field values
        GenericRecord recordB1 = arrayRecordA.get(0);
        GenericRecord recordB2 = arrayRecordA.get(1);
        assertThat((boolean) recordB1.get("b"), is(equalTo(true)));
        assertThat((boolean) recordB2.get("b"), is(equalTo(false)));
    }
}
