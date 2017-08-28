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
package org.talend.cqrs.poc;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.talend.cqrs.poc.preparation.dataset.DataSetServiceClient;

/**
 * Created by bguillon on 03/07/2017.
 */
@Configuration
public class MockDataSetServiceClientConfiguration {

    @Bean
    public DataSetServiceClient dataSetServiceClient() {
        return Mockito.mock(DataSetServiceClient.class);
    }

}
