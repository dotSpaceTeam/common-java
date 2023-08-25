<h1 align="center">Common Java Library</h1> <br>

<div align="center">

![Java Version](https://img.shields.io/badge/Java-17-important?style=for-the-badge&logo=java)
![GitHub](https://img.shields.io/github/license/dotSpaceTeam/common-java?style=for-the-badge)
[![Support Server](https://img.shields.io/discord/678733739504697375.svg?color=7289da&label=dotSpace%20Dev&logo=discord&style=for-the-badge)](https://discord.gg/mFfDMAEFWE)
![Stars](https://img.shields.io/github/stars/dotSpaceTeam/common-java?style=for-the-badge)

A library which is the core of our projects and can be used for other cool projects as well.

</div>

---
### Building your own copy

- To build and execute the jar **Java 17** is recommended.
- Execute ``gradlew build`` to run test and build.

---
### Integration into own projects

Include the library in your own project via [maven](https://maven.apache.org/) or [gradle](https://gradle.org/).
Completed builds can be imported via our [repository](https://repo.dotspace.dev/repository/space-public/).

> Gradle:

```
//Repository
maven {
  name 'dotSpace'
  url 'https://repo.dotspace.dev/repository/space-public/'
}

//Dependency
implementation 'dev.dotspace:common:1.0.8'
```

> Maven:
```
//Repository
<repository>
  <id>dotSpace</id>
  <name>dotSpace Public Repo</name>
  <url>https://repo.dotspace.dev/repository/space-public/</url>
</repository>

//Dependency
<dependency>
  <groupId>dev.dotspace</groupId>
  <artifactId>common</artifactId>
  <version>1.0.8</version>
</dependency>
```
---

