# Common Java Library

A library which is the core of our projects and can be used for other cool projects as well.

---
## Integration into own projects

Include the library in your own project via [maven](https://maven.apache.org/) or [gradle](https://gradle.org/).
Completed builds can be imported via our [repository](https://repo.dotspace.dev/repository/space-public/).

### Repository (Gradle): 
```
maven {
  name 'dotSpace'
  url 'https://repo.dotspace.dev/repository/space-public/'
}
```
### Repository (Maven):
```
<repository>
  <id>dotSpace</id>
  <name>dotSpace Public Repo</name>
  <url>https://repo.dotspace.dev/repository/space-public/</url>
</repository>
```
### Dependency (Gradle):
```
implementation 'dev.dotspace:common:1.0.6'
```
### Dependency (Maven):
```
<dependency>
  <groupId>dev.dotspace</groupId>
  <artifactId>common</artifactId>
  <version>1.0.5</version>
</dependency>
```
---

