# Content service - MongoDB Journal
http://www.talend.com


![alt text](https://www.talend.com/wp-content/uploads/2016/07/talend-logo.png "Talend")

This module contains support code storage journal using MongoDB as persistence layer.

## Usage

1. Include the following dependencies in your module:
```xml
<dependency>
   <groupId>org.talend.daikon</groupId>
   <artifactId>content-service-journal-mongodb</artifactId>
   <version>latest daikon version</version>
</dependency>
```
Doing so will bring all needed dependencies.

2. No additional configuration is needed. Please note code supports MongoDB multi-tenancy.