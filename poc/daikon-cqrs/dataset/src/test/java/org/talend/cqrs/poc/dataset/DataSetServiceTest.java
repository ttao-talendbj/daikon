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
package org.talend.cqrs.poc.dataset;

import com.jayway.restassured.RestAssured;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.talend.cqrs.poc.dataset.command.create.CreateDataSetDto;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DataSetApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DataSetServiceTest {

    @Value("${local.server.port}")
    private int port;

    @Before
    public void setUpBeforeTest() throws Exception {
        RestAssured.port = port;
    }

    @Test
    public void createDataSet() throws Exception {
        CreateDataSetDto dto = new CreateDataSetDto("dataSet1", "This is my dataset 1");
        String id = RestAssured.given().content(dto).contentType(MediaType.APPLICATION_JSON_VALUE).post("/")
                .then()
                .statusCode(HttpStatus.OK.value()).extract().body().asString();
        Assert.assertFalse(StringUtils.isEmpty(id));
    }


}
