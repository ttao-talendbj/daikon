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
package org.talend.cqrs.poc.dataset.command.create;

import org.talend.daikon.events.RequestContext;
import org.talend.daikon.events.RequestContextProvider;

import java.util.HashMap;
import java.util.Map;


public class CreateDataSetCommand extends CreateDataSetDto implements RequestContextProvider {

    private String id;

    public CreateDataSetCommand(String id, CreateDataSetDto dto) {
        super(dto.getName(), dto.getDescription());
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public RequestContext getRequestContext() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("toto" ,"tutu");
        return new RequestContext("", "", "", parameters);
    }
}
