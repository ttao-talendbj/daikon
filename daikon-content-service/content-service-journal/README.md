# Content service - Journal
http://www.talend.com


![alt text](https://www.talend.com/wp-content/uploads/2016/07/talend-logo.png "Talend")

This module contains support code storage journal.

## Usage

1. Include the following dependencies in your module:
```xml
<dependency>
   <groupId>org.talend.daikon</groupId>
   <artifactId>content-service-journal</artifactId>
   <version>latest daikon version</version>
</dependency>
```
Doing so will bring all needed dependencies.

2. In your configuration, add the following line:
```properties
content-service.journalized=true
```

3. You also need to add a journal implementation, like MongoDB as an example:

```xml
<dependency>
   <groupId>org.talend.daikon</groupId>
   <artifactId>content-service-journal-mongo</artifactId>
   <version>latest daikon version</version>
</dependency>
```

A journal implementation is required to complete journal storage support.