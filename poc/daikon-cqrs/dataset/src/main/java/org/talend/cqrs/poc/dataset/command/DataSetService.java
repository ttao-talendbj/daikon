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
package org.talend.cqrs.poc.dataset.command;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.talend.cqrs.poc.dataset.command.create.CreateDataSetCommand;
import org.talend.cqrs.poc.dataset.command.create.CreateDataSetDto;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
public class DataSetService {

    private CommandGateway commandGateway;

    public DataSetService(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> createDataset(@RequestBody CreateDataSetDto dto) {
        final String id = UUID.randomUUID().toString();
        return this.commandGateway.send(new CreateDataSetCommand(id, dto));
    }

}
