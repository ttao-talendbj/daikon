package org.talend.cqrs.poc.preparation.command.create;

import org.talend.daikon.events.RequestContext;
import org.talend.daikon.events.RequestContextProvider;

import java.util.HashMap;
import java.util.UUID;

public class PreparationCreateCommand implements RequestContextProvider {

    private String requestId = UUID.randomUUID().toString();

    private String id;

    private String name;

    private String desc;

    public PreparationCreateCommand(String id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public RequestContext getRequestContext() {
        return new RequestContext(requestId, null, "PreparationCreateCommand", new HashMap<>());
    }
}
