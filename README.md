xo-tinkerpop-blueprints
=======================

[TinkerPop Blueprints](https://github.com/tinkerpop/blueprints/wiki) binding for [eXtendend Objects (XO)](https://github.com/buschmais/extended-objects).

Maven Users
-----------

Our artifacts are published to the Maven Central repository and can be found under the ``com.smb-tec.xo`` groupId.

You'll need to add the following dependency in your builds (and Maven will automatically include the additional transitive dependencies to TinkerPop Blueprints for you). It currently uses the TinkerPop Blueprints API 2.6.0 version:

    <dependency>
      <groupId>com.smb-tec.xo</groupId>
      <artifactId>xo-tinkerpop-blueprints</artifactId>
      <version>0.0.4-SNAPSHOT</version>
    </dependency>

There are a lot of different Blueprints-enabled graph backends. See the [TinkerPop Blueprints Wiki](https://github.com/tinkerpop/blueprints/wiki) for a (more or less) complete list of implementations. Note that you have to add a dependency to the Blueprints-enabled graph backend of your choice to your applications ``pom.xml``.


|         | xo-tinkerpop-blueprints|extended-objects (XO)|Tinkerpop Blueprints|
|---------|------------------------|---------------------|--------------------|
| Version | 0.0.2                  | 0.4.0               | 2.5.0              |
| Version | 0.0.3                  | 0.4.5               | 2.6.0              |
| Version | 0.0.4-SNAPSHOT         | 0.5.0-SNAPSHOT      | 2.6.0              |

Quick Start
-----------

```java

@Vertex
public interface Person {
    String getName();
    void setName(String name);
}

@Vertex
public interface Student extends Person {
    Long getRegNumber();
    void setRegNumber(Long regNumber);
}

// persist an entity
xoManager.currentTransaction().begin();
Student student = xoManager.create(Student.class);
student.setRegNumber(123456L);
student.setName("John Doe");
xoManager.currentTransaction().commit();

// find an entity and modify attributes
xoManager.currentTransaction().begin();
Person johnDoe = xoManager.find("John Doe", Person.class);
johnDoe.setName("John Doe Jr.");
xoManager.currentTransaction().commit();

//
xoManager.currentTransaction().begin();

xoManager.currentTransaction().commit();
```


Getting Help
------------

Please visit the project wiki for the latest information : [https://github.com/BluWings/xo-tinkerpop-blueprints/wiki](https://github.com/BluWings/xo-tinkerpop-blueprints/wiki)

If you are new to eXtended Objects (XO) please visit [eXtended Objects ()](https://github.com/buschmais/extended-objects).

License
-------

``xo-tinkerpop-blueprints`` is contributed under Apache License 2.0.

Build
-----

Start the Maven build on command line

    mvn clean package

Continuous Build
----------------

[![Build Status](https://secure.travis-ci.org/SMB-TEC/xo-tinkerpop-blueprints.png)](http://travis-ci.org/SMB-TEC/xo-tinkerpop-blueprints)