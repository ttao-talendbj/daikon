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
package org.talend.cqrs.poc.preparation.dataset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@ConditionalOnProperty("dataset.url")
public class DataSetServiceClientImpl implements DataSetServiceClient {

    @Value("${dataset.url}")
    private String dataSetServiceUrl;

    private RestTemplate restTemplate;

    public DataSetServiceClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public DataSet findDataSetById(String id) {
        String url = dataSetServiceUrl + "/" + id;
        ResponseEntity<DataSet> response = this.restTemplate.getForEntity(url, DataSet.class);
        return response.getBody();
    }
}
