# Talend Daikon events

The purpose of this module is to centralize common code and best practice on events management and asynchronous 
inter-process communication.

## Common events model

The [events-model](events-model) sub-module defines a common events metadata model. This model relies on 
[Apache Avro](https://avro.apache.org/) as serialization framework and the model it-self is defined as a set of 
independent Avro schema. It assumes that consuming services will model their events using Avro as well.

All Avro schema defined here will use the same namespace: `org.talend.daikon.events`. Consuming services can use
their own specific namespace(s).

### Common metadata

The first Avro schema to start with is [EventMetadata.avsc](events-model/src/main/avro/EventMetadata.avsc). It contains
base metadata that should be applicable to most use cases.

+ id: a mandatory event identifier. The model does not assume any particular format of id.
+ timestamp: a mandatory timestamp: number of milliseconds since Epoch (UTC) 
+ issuer: a mandatory definition of the service that issued this event
   + service: the name of the service that created this event
   + version: the version of the service that created this event
+ userId: if event was generated after user interactions, this field will contain the unique identifier
of the user that made it. If no user, default value is `system`.
+ 

### Entity change metadata
 

## How to use the common events model

As Avro does not support inheritance, consuming services will use composition to include base metadata in their own 
events model.





 
 





