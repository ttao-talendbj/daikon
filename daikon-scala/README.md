# Daikon scala libraries

This module contains helpful common Scala code and libraries used at Talend

## scala-parent: Scala maven integration

The scala-parent module is a pom artifact containing common Maven configuration for Scala.

It contains appropriate Scala compiler setup and base dependencies.

In order to create a new Scala maven module, simply use the following Maven pom template:

```
<?xml version="1.0"
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.talend.daikon.scala</groupId>
        <artifactId>scala-parent</artifactId>
        <version>0.24.0-SNAPSHOT</version>
        <relativePath>../scala-parent</relativePath>
    </parent>
    [...]
</project>
```

It will compile Scala code located in src/main/scala and Scala tests located in src/test/scala

It configures the Maven surefire plugin to run *Spec classes (specs2 style). This configuration requires Specification classes are annotated with

```
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MyClassSpec extends Specification
```

It uses Scala 2.11 and defines the `scala-binary-version` Maven property to refer to the `2.11` constant later in artifactId

## scala-play2-dependencies: Play2 and akka dependency management

The scala-play2-dependencies module is a pom artifact defining the required dependencies to run a web app using the [Play2 Framework](https://www.playframework.com/) with akka and [Slick](http://slick.lightbend.com/).

It enherits from scala-parent (see above).

To use this module simply import it as follows in a Maven project:

```
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.talend.daikon.scala</groupId>
            <artifactId>scala-play2-dependencies</artifactId>
            <version>0.24.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## multi-tenancy-jdbc

Common code to configure the akka persistence and slick to be multi-tenant (ie isolate storage per tenant).


