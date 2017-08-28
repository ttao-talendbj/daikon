package org.talend.cqrs.poc.dataset.query;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import org.talend.cqrs.poc.dataset.command.create.DataSetCreatedEvent;

/**
 * listen to all Preparation events and store/update the model in the local DB tuned to the query API.
 */
@Component
public class DataSetQueryObjectUpdater {

    private DataSetRepository repo;

    public DataSetQueryObjectUpdater(DataSetRepository repo) {
        this.repo = repo;
    }

    @EventHandler
    public void on(DataSetCreatedEvent dce) {
        DataSetQueryObject entity = new DataSetQueryObject();
        entity.setId(dce.getId());
        entity.setName(dce.getName());
        entity.setDescription(dce.getDesc());
        repo.save(entity);
    }


}
