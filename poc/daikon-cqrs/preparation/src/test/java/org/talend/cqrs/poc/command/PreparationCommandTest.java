package org.talend.cqrs.poc.command;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.talend.cqrs.poc.preparation.StepAddedEvent;
import org.talend.cqrs.poc.preparation.command.Preparation;
import org.talend.cqrs.poc.preparation.command.create.PreparationCreateCommand;
import org.talend.cqrs.poc.preparation.command.create.PreparationCreatedEvent;
import org.talend.cqrs.poc.preparation.command.create.PreparationUpdatedEvent;
import org.talend.cqrs.poc.preparation.command.steps.StepAddCommand;
import org.talend.cqrs.poc.preparation.command.update.PreparationUpdateCommand;
import org.talend.daikon.events.EventMetadata;
import org.talend.daikon.events.EventMetadataFactory;
import org.talend.daikon.events.RequestContextProvider;

public class PreparationCommandTest {

    private FixtureConfiguration<Preparation> fixture;

    @Mock
    private EventMetadataFactory eventMetadataFactory;

    EventMetadata eventMetadata = new EventMetadata();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        fixture = new AggregateTestFixture<>(Preparation.class).registerInjectableResource(eventMetadataFactory);

        Mockito.when(eventMetadataFactory.createEventMetadataBuilder(Mockito.any(RequestContextProvider.class)))
                .thenReturn(eventMetadata);
    }

    @Test
    public void testPreparationCreatedEvent() {
        fixture.givenNoPriorActivity().when(new PreparationCreateCommand("123", "my Prep", "desc"))
                .expectEvents(new PreparationCreatedEvent(eventMetadata, "123", "my Prep", "desc"));
    }

    @Test
    public void testPreparationStepAddedEvent() {
        fixture.given(new PreparationCreatedEvent(new EventMetadata(), "123", "my Prep", "desc"))
                .when(new StepAddCommand("123", "stepType")).expectEvents(new StepAddedEvent(eventMetadata, "123", "stepType"));
    }

    @Test
    public void testPreparationStepLimitRule() {
        fixture.given(new PreparationCreatedEvent(new EventMetadata(), "123", "my Prep", "desc"),
                new StepAddedEvent(eventMetadata, "123", "foo"), new StepAddedEvent(eventMetadata, "123", "foo"))
                .when(new StepAddCommand("123", "XstepType")).expectException(IllegalArgumentException.class);
    }

    @Test
    public void testPreparationUpdateEvent() {
        fixture.given(new PreparationCreatedEvent(new EventMetadata(), "123", "my Prep", "desc"))
                .when(new PreparationUpdateCommand("123", "my Prep", "desc"))
                .expectEvents(new PreparationUpdatedEvent(eventMetadata, "123", "my Prep", "desc"));
    }

}
