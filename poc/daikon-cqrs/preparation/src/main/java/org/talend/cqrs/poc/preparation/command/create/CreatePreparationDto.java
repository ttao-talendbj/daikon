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
package org.talend.cqrs.poc.preparation.command.create;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatePreparationDto {

    private String name;

    private String description;

    private String dataSetId;

    @JsonCreator
    public CreatePreparationDto(@JsonProperty("name") String name, @JsonProperty("description") String description,
                                @JsonProperty("dataSetId") String dataSetId) {
        this.name = name;
        this.description = description;
        this.dataSetId = dataSetId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDataSetId() {
        return dataSetId;
    }
}
