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
package org.talend.cqrs.poc.dataset.query;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DataSetQueryService {

    private DataSetRepository dataSetRepository;

    public DataSetQueryService(DataSetRepository dataSetRepository) {
        this.dataSetRepository = dataSetRepository;
    }

    @GetMapping(path = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataSetQueryObject getDatasetById(@PathVariable("uuid")  String id) {
        return this.dataSetRepository.findOne(id);
    }

    @GetMapping(path = "/")
    public List<DataSetQueryObject> getDataSets() {
        return this.dataSetRepository.findAll();
    }

}
