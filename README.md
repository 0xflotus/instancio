<img src="https://i.imgur.com/937nevX.png" alt="Instancio" width="250"/> [![Maven Central](https://img.shields.io/maven-central/v/org.instancio/instancio-core.svg)](https://search.maven.org/artifact/org.instancio/instancio-core/)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=instancio_instancio&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=instancio_instancio)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=instancio_instancio&metric=coverage)](https://sonarcloud.io/summary/new_code?id=instancio_instancio)

---

# What is it?

Instancio is a Java library that automatically creates and populates objects for your unit tests.

Instead of manually setting up test data:

```java
Address address  = new Address();
address.setStreet("street");
address.setCity("city");
//...

Person person = new Person();
person.setFirstName("first-name");
person.setLastName("last-name");
person.setAge(22);
person.setGender(Gender.MALE);
person.setAddress(address);
//...
```

You can simply do the following:

```java
Person person = Instancio.create(Person.class);
```

This one-liner returns a fully-populated person, including nested objects and collections.
The object is populated with random data that can be reproduced in case of test failure.

### What else can Instancio do?

1. Create collections of objects:

```java
List<Person> persons = Instancio.ofList(Person.class).size(10).create();
```

2. Create streams of objects:

```java
Stream<Person> persons = Instancio.stream(Person.class);
```

3. Create generic types:

```java
Pair<List<Foo>, List<Bar>> pairOfLists = Instancio.create(new TypeToken<Pair<List<Foo>, List<Bar>>>() {});
```

4. Customise generated values:

```java
Person person = Instancio.of(Person.class)
    .generate(field(Person::getDateOfBirth), gen -> gen.temporal().localDate().past())
    .generate(field(Phone::getAreaCode), gen -> gen.oneOf("604", "778"))
    .generate(field(Phone::getNumber), gen -> gen.text().pattern("#d#d#d-#d#d-#d#d"))
    .subtype(all(AbstractAddress.class), AddressImpl.class)
    .supply(all(LocalDateTime.class), () -> LocalDateTime.now())
    .onComplete(all(Person.class), (Person p) -> p.setName(p.getGender() == Gender.MALE ? "John" : "Jane"))
    .create();
```

5. Create reusable templates (Models) of objects:

```java
Model<Person> simpsons = Instancio.of(Person.class)
    .set(field(Person::getLastName), "Simpson")
    .set(field(Address::getCity), "Springfield")
    .generate(field(Person::getAge), gen -> gen.ints().range(40, 50))
    .toModel();

Person homer = Instancio.of(simpsons)
    .set(field(Person::getFirstName), "Homer")
    .set(all(Gender.class), Gender.MALE)
    .create();

Person marge = Instancio.of(simpsons)
    .set(field(Person::getFirstName), "Marge")
    .set(all(Gender.class), Gender.FEMALE)
    .create();
```

## Main Features

- Fully reproducible data in case of test failures.
- Support for generics, `record` and `sealed` classes.
- Support for defining custom generators.
- Flexible configuration options.
- `InstancioExtension` for Junit 5 `@ExtendWith`.

## Documentation

- [User guide](https://www.instancio.org/user-guide)
- [Javadocs](https://javadoc.io/doc/org.instancio/instancio-core/latest/)

## Quickstart

[Instancio Quickstart](https://github.com/instancio/instancio-quickstart) is the best way to get started.
It is a sample (Maven) project that provides an overview of all the main features.

```sh
git clone https://github.com/instancio/instancio-quickstart.git
```

## Latest Release

Version `2.3.0` is now available.
A summary of new features is availabe in the [release notes](https://github.com/instancio/instancio/discussions/331).

# Maven coordinates

If you have JUnit 5 on the classpath, use the `instancio-junit` dependency.

```xml
<dependency>
    <groupId>org.instancio</groupId>
    <artifactId>instancio-junit</artifactId>
    <version>2.3.0</version>
    <scope>test</scope>
</dependency>
```

To use Instancio with JUnit 4, TestNG, or standalone, use `instancio-core`:

```xml
<dependency>
    <groupId>org.instancio</groupId>
    <artifactId>instancio-core</artifactId>
    <version>2.3.0</version>
    <scope>test</scope>
</dependency>
```

# Feedback

Feedback and bug reports are greatly appreciated. Please submit an
[issue](https://github.com/instancio/instancio/issues) to report a bug,
or if you have a question or a suggestion.

# Special thanks to

JetBrains for supporting this project with their [Open Source Licenses](https://www.jetbrains.com/opensource).

<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg" width="150px">


