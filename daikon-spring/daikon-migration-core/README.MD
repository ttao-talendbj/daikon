# On-the-fly document migrations

This module aims to address migration issues when Java bean to read persisted objects differ too much and database scripts / down time migrations are needed.

For example, first version of bean Person is:
```java
class Person {
    public int id;
    public String name;
}
``` 
and second version becomes:
````java
class Person {
    public UUID uuid;
    public IPersonalInformation information; // IPersonalInformation is an interface
}
````
Data created (in a MongoDB database, as example, but could very well be JSON serialized objects) in version 1 is no longer comptatible with version 2.

This modules aims to solve this issue in the following manner: beans can declare migration paths from a version to another **and** get the underlying serialized object to achieve this. So migration from version 1 to version 2 can use underlying technology and access _raw_ data (a DBObject for MongoDB, a JSONNode for a JSON stream).

In order to achieve this, there's some requirements:
* Bean **must** declare a version field: this also migration code to detect what migration(s) should be applied.
* Version field must be editable: once migration(s) is/are applied, code needs to update bean's version to prevent new possibly unwanted migrations.

But:
* Version field is highly configurable: in fact, only requirement of this library is to give a **way** to extract a version from a bean.

## Declaring a migration

There are 2 ways to declare migrations:
1. An implementation of `Migration` as inner class in a bean
1. A registration to `MigrationRegister`.

Code in migration aggregates both inner classes and registrations, so applications may use both.

### Inner class
Current way to declare migration is to add `@MigrationRules` in the bean:

````java
public class A {

    private String myNewField = "...";

    // Getters and setters...

    @MigrationRule(version = "2.0.0")
    public static class Rule_200 implements Migration<ObjectNode, A> {

        @Override
        public A apply(ObjectNode source, A a) {
            a.setMyNewField(String.valueOf(source.get("oldField").asText()));
            return a;
        }
    }
}
````
In this example, when an instance of `A` from version 1 is read by a runtime in version 2, the rule `Rule_200` will be called.
When calling:
```java
class Main {
    public static void main(String... args) {
        ObjectNode jsonObject = ... // omitted
        Function<ObjectNode, String> version = ... // a way to extract version from JSON object
        A instanceOfA = new A();
        
        A migratedInstance = Migrations.migrate(jsonObject, version, instanceOfA);
    }
}
```

### Registration

Same as inner classes, create a migration implementation:
```java
@MigrationRule(version = "2.0.0")
public class Rule_200 implements Migration<ObjectNode, A> {

    @Override
    public A apply(ObjectNode source, A a) { 
        a.setMyNewField(String.valueOf(source.get("oldField").asText()));
        return a;
    }
}
```

Then use `MigrationRegister`:
```java
class Main {
    public static void main(String... args) {
        ObjectNode jsonObject = ... // omitted
        Function<ObjectNode, String> version = ... // a way to extract version from JSON object
        A instanceOfA = new A();
        
        MigrationRegister.register(A.class, Rule_200.class); // Important since A no longer has inner class.
        A migratedInstance = Migrations.migrate(jsonObject, version, instanceOfA);
    }
}
```