Multi tenancy support (for Spring data MongoDB)
================================================

This module provides support for multi tenancy over Spring Data MongoDB:
* Automatically wraps the previously existing MongoDbFactory to support multi-tenancy.
* Allow to have the following information per tenant: database name, credentials and authentication database (MongoDb apparently authorizes to use another database name for authorization).

It does **not** allow to have a MongoDB host per tenant. It allows you to have single MongoDB database per tenant. 

*TODO*
* A careful look at how connection pool & cie performs.

Known limitations
-----------------

* You can't change SSL keys per tenant when connecting to MongoDB.

Using multi tenancy support
---------------------------

1) Include artifact in your application:


    <dependency>
        <groupId>org.talend.daikon</groupId>
        <artifactId>daikon-spring-mongo</artifactId>
        <version>0.16.0-SNAPSHOT</version>
    </dependency>

2) Make sure, `org.talend.daikon` in included in the Spring component scan


    @ComponentScan(basePackages = { "org.talend.myapp", "org.talend.daikon" })

3) Include property in your application configuration:


    multi-tenancy.mongodb.active=true
    
Note: property defaults to `false`, so your application configuration to explicitly enable multi tenancy support.

4) Add a `org.talend.daikon.spring.mongo.TenantInformationProvider` implementation in your application and expose it as a Spring Bean. For example:


    @Bean
    public TenantInformationProvider tenantProvider() {
        return new TenantInformationProvider() {
            @Override
            public String getDatabaseName() {
                // Fetch database name
            }

            @Override
            public UserCredentials getCredentials() {
                // Fetch credentials when establishing a connection
            }

            @Override
            public String getAuthenticationDatabase() {
                // Database for authentication may differ from content database.
            }
        };
    }

If you don't add this bean to your context, you will have expected startup error such as:

    ***************************
    APPLICATION FAILED TO START
    ***************************
    
    Description:
    
    Field [...] required a bean of type 'org.talend.daikon.spring.mongo.TenantInformationProvider' that could not be found.

5) On startup of your application, you should see:


    09:41:53.236 [main] INFO  o.t.d.s.m.MultiTenancyMongoDbConfiguration - Enable MongoDB multi tenancy support 'class org.springframework.data.mongodb.core.SimpleMongoDbFactory' (defaultMongoDbFactory)...


6) And it's done, all the `MongoTemplate` or `MongoRepository` will work with multi tenancy without changing a line of code in them.

Troubleshoot
------------

**Spring complains about a missing MongoClient**

You *need* to expose a MongoClient as a bean. The default MongoDbFactory should be created that way:

    /**
     * @return the mongodb factory bean
     */
    @Bean
    public MongoDbFactory mongoDbFactory(MongoClient client) throws UnknownHostException {
        return new SimpleMongoDbFactory(client, database);
    }

The mongo client is not created in the method that creates the MongoDbFactory instance but injected. This is important since the multi tenancy code for MongoDb expects a mongo client bean in the context.

How it works
-----------
In `org.talend.daikon.spring.mongo.MultiTenancyMongoDbConfiguration` a `BeanPostProcessor` will wrap existing `MongoDBFactory` and intercept all calls to retrieve a MongoDB database. Information given my implementation of `TenantInformationProvider` do the rest.

Pitfalls
--------

Be aware the implementation of `org.talend.daikon.spring.mongo.TenantInformationProvider` is very frequently called (depending on your usage of MongoDB). Very fast response times all of the methods in implementation is therefore very important.

