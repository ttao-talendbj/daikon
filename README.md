# Talend Daikon

[![Join the chat at https://gitter.im/Talend/daikon](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/Talend/daikon?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)  [![Codacy Badge](https://api.codacy.com/project/badge/Grade/ad1dea086b9140e8b9be7635fe2762ee)](https://www.codacy.com/app/Talend/daikon?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=Talend/daikon&amp;utm_campaign=Badge_Grade)

http://www.talend.com


![Talend](https://www.talend.com/wp-content/uploads/2016/07/talend-logo.png "Talend")


## Contents

This repository contains commonly used classes that are expected to be shared across all Talend products.
here is the folder description:

_Modules_                                               |_Description_                             
-------------------------------------------------------|------------------------------------------
[daikon](daikon)                                        |*The core library with a restricted set of dependencies. It contains Avro, i18n, Properties, UI-specs, Exception, Sandbox classloader handling classes among things* 
[daikon-content-service](daikon-content-service)        |*Spring Abstraction for handling content resources on multiple filesystem like local or S3*
[daikon-logging](daikon-logging)                        |*Json Layout for all loggers to used for cloud projects and it's associated documentation*
[daikon-service](daikon-service)                        |*Provides spring services utilities such as abstracting the access to local or remote service and Enuciate documentation generation*
[daikon-spring](daikon-spring)                          |*Spring specific classes that can be shared by projects, like multitenant for mongoDB or @RequiresAuthority for simple permission check*
[daikon-tql](daikon-tql)                                |*Talend Query Language, simple query language for java and javascript with a MongoDB implementation*
[poc](poc)                                              |*module use to store experiments and POCs like the CQRS one*
[reporting](reporting)                                  |*Internal module used to aggregate jacoco converage reports and send them to codacy during the deploy phase*


## Release TQL Javascript module

To publish on npm repository the TQL module on a release of daikon use the maven profile npm.

## Support

You can ask for help on our [forum](https://community.talend.com/).


## Contributing

We welcome contributions of all kinds from anyone.

Using the [Talend bugtracker](https://jira.talendforge.org/projects/TDKN) is the best channel for bug reports and feature requests. Use [GitHub](https://github.com/Talend/daikon) to submit pull requests.

For code formatting, please use the configuration file and setup for Eclipse or IntelliJ that you find here: https://github.com/Talend/tools/tree/master/tools-java-formatter


## License

Copyright (c) 2006-2017 Talend

Licensed under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0.txt)

## Name origin

The Daikon is a root vegetable and since this project is supposed to be the root of many projects we thought it was a fun and appropriate name.
