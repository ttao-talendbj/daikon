package org.talend.cqrs.poc.preparation.command;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.talend.cqrs.poc.preparation.command.create.CreatePreparationCommand;
import org.talend.cqrs.poc.preparation.command.create.CreatePreparationDto;
import org.talend.cqrs.poc.preparation.command.steps.StepAddCommand;
import org.talend.cqrs.poc.preparation.command.update.PreparationUpdateCommand;

@RestController
public class PreparationCommandController {

    private CommandGateway commandGateway;

    public PreparationCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> createPreparation(@RequestBody CreatePreparationDto dto) {
        String preparationId = UUID.randomUUID().toString();
        return commandGateway.send(new CreatePreparationCommand(preparationId, dto));
    }

    @PostMapping("/step/{id}")
    public CompletableFuture<String> addStep(@PathVariable String id, @RequestParam(name = "type") String stepType) {
        return commandGateway.send(new StepAddCommand(id, stepType));
    }

    @PostMapping("/{id}")
    public CompletableFuture<String> updatePreparation(@PathVariable String id, @RequestParam(name = "name") String name,
            @RequestParam(name = "desc", defaultValue = "") String desc) {
        return commandGateway.send(new PreparationUpdateCommand(id, name, desc));
    }

}
