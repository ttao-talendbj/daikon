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

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.talend.cqrs.poc.dataset.command.create.CreateDataSetCommand;
import org.talend.cqrs.poc.dataset.command.create.DataSetCreatedEvent;
import org.talend.daikon.events.EventMetadataFactory;

import java.util.HashSet;
import java.util.Set;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class DataSet {

    @AggregateIdentifier
    private String id;

    private String name;

    private String description;

    private Set<String> preparations = new HashSet<>();

    @CommandHandler
    public DataSet(CreateDataSetCommand command, EventMetadataFactory eventMetadataFactory) {
        DataSetCreatedEvent event = DataSetCreatedEvent.newBuilder()
                .setMetadata(eventMetadataFactory.createEventMetadataBuilder(command))
                .setId(command.getId())
                .setName(command.getName())
                .setDesc(command.getDescription())
                .build();
        apply(event);
    }

    @EventSourcingHandler
    public void on(DataSetCreatedEvent event) {
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDesc();
    }

}
