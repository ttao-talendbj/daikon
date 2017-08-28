package org.talend.cqrs.poc.preparation.command.create;

import org.talend.daikon.events.RequestContext;
import org.talend.daikon.events.RequestContextProvider;

import java.util.HashMap;
import java.util.UUID;

public class CreatePreparationCommand extends CreatePreparationDto implements RequestContextProvider {

    private String requestId = UUID.randomUUID().toString();

    private String id;

    public CreatePreparationCommand(String id, CreatePreparationDto dto) {
        super(dto.getName(), dto.getDescription(), dto.getDataSetId());
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public RequestContext getRequestContext() {
        return new RequestContext(requestId, null, "CreatePreparationCommand", new HashMap<>());
    }
}
