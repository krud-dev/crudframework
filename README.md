# CRUD Framework

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/dev.krud.crudframework/crud-framework-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/dev.krud.crudframework/crud-framework)
[![CircleCI](https://circleci.com/gh/krud-dev/crud-framework.svg?style=shield)](https://circleci.com/gh/krud-dev/crud-framework)


The CRUD Framework is a Spring-powered framework intended to simplify and expand on CRUD operations in Spring, currently
supporting both MongoDB(Via Spring Data) and JPA.


## Compatibility

The CRUD Framework is currently compatible with Spring Boot 2.6.7

## Getting started

### Dependencies

Only one connector is required, but it is possible for multiple connectors to work in tandem with eachother.

#### JPA/Hibernate5 Connector

Maven:

```xml

<dependency>
    <groupId>dev.krud.crudframework</groupId>
    <artifactId>crud-framework-hibernate5-connector</artifactId>
    <version>0.23.0</version>
</dependency>
```

Gradle:

```kotlin
implementation("dev.krud.crudframework:crud-framework-hibernate5-connector:0.9.0")
```

### MongoDB Connector

```xml

<dependency>
    <groupId>dev.krud.crudframework</groupId>
    <artifactId>crud-framework-mongo-connector</artifactId>
    <version>0.23.0</version>
</dependency>
```

Gradle:

```kotlin
implementation("dev.krud.crudframework:crud-framework-mongo-connector:0.9.0")
```


### Web

Contains useful utilities and classes for web operations

```xml

<dependency>
    <groupId>dev.krud.crudframework</groupId>
    <artifactId>crud-framework-web</artifactId>
    <version>0.23.0</version>
</dependency>
```

Gradle:

```kotlin
implementation("dev.krud.crudframework:crud-framework-web:0.3.3")
```

### Operation

To activate the CRUD Framework, add the activation annotations for your chosen connectors to a configuration class;

| Connector   | Annotation
|-------------|------------------|
| hibernate5 | `@EnableJpaCrud`
| mongo      | `@EnableMongoCrud`

Once activated, the `CrudHandler` bean can be wired and used.

## License

CRUD Framework is released under the MIT license. For more information visit `LICENSE.md`
