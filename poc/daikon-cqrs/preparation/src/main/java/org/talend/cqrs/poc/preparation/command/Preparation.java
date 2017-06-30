package org.talend.cqrs.poc.preparation.command;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.util.Assert;
import org.talend.cqrs.poc.preparation.StepAddedEvent;
import org.talend.cqrs.poc.preparation.command.create.PreparationCreateCommand;
import org.talend.cqrs.poc.preparation.command.create.PreparationCreatedEvent;
import org.talend.cqrs.poc.preparation.command.create.PreparationUpdatedEvent;
import org.talend.cqrs.poc.preparation.command.steps.StepAddCommand;
import org.talend.cqrs.poc.preparation.command.update.PreparationUpdateCommand;
import org.talend.daikon.events.EventMetadata;
import org.talend.daikon.events.EventMetadataFactory;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@Aggregate
public class Preparation {

    // the aggregate state is reconstructed by the framework using the history for a given @AggregateIdentifier using
    // event sourcing

    @AggregateIdentifier
    private String id;

    private int NbOfSteps;

    public Preparation() {
    }

    @CommandHandler
    public Preparation(PreparationCreateCommand preparationCC, EventMetadataFactory eventMetadataFactory) {
        // check things
        Assert.hasLength(preparationCC.getName());
        // event sourcing here. Never change the state of the aggregator in commandhandlers, do so in Event handlers,
        // see @EvenSourcingHandler methods below.
        EventMetadata eventMetadata = eventMetadataFactory.createEventMetadataBuilder(preparationCC);
        apply(new PreparationCreatedEvent(eventMetadata, preparationCC.getId(), preparationCC.getName(),
                preparationCC.getDesc()));
    }

    @EventSourcingHandler
    public void on(PreparationCreatedEvent dce) {
        this.id = dce.getId();
    }

    @CommandHandler
    public void stepAdd(StepAddCommand stepAddC, EventMetadataFactory eventMetadataFactory) {
        if (NbOfSteps >= 2) {
            throw new IllegalArgumentException();
        }
        EventMetadata eventMetadata = eventMetadataFactory.createEventMetadataBuilder(stepAddC);
        apply(new StepAddedEvent(eventMetadata, id, stepAddC.getStepType()));
    }

    @EventSourcingHandler
    public void on(StepAddedEvent sae) {
        NbOfSteps++;
    }

    @CommandHandler
    public void update(PreparationUpdateCommand preparationUC, EventMetadataFactory eventMetadataFactory) {
        EventMetadata eventMetadata = eventMetadataFactory.createEventMetadataBuilder(preparationUC);
        apply(new PreparationUpdatedEvent(eventMetadata, id, preparationUC.getName(), preparationUC.getDesc()));
    }

}
