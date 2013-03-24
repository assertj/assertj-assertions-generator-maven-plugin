Maven plugin for assertj-assertions-generator
==

This plugin can generate [AssertJ assertions](https://github.com/joel-costigliola/assertj-core) for your own classes via maven (it is based on [assertj-assertions-generator](https://github.com/joel-costigliola/assertj-assertions-generator)).

Let's say that you have a `Player` class with `name` and `team` attributes, the plugin is able to create a `PlayerAssert` assertions class with `hasName` and `hasTeam` assertions, to write code like :

```java
assertThat(mvp).hasName("Lebron James").hasTeam("Miami Heat");
```

The plugin can be launched with command `mvn generate-test-sources` (or simply `mvn test`) or with any IDE that supports maven.
By default, it generates the assertions source files in `target/generated-test-sources/assertions` as per maven convention (but this can be changed - see below).

**Big thanks** to [Michal Ostruszka](https://github.com/mostr) for its major contribution on this plugin.

Releases
--

The first release 1.0.0 will be available at the end of March.

Configuration
--

You need to have AssertJ Core as a dependency in `pom.xml` :

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

To generate custom assertions, add the following plugin to your `pom.xml` build/plugins section and execute the command :
```
mvn assertj:generate-assertions
```

xml snippet to add to your pom :

```xml
<plugin>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-assertions-generator-maven-plugin</artifactId>
    <version>1.0.0</version>
    <configuration>
        <packages>
            <param>your.first.package</param>
            <param>your.second.package</param>
            ...
        </packages>
    </configuration>
</plugin>
```

`packages` configuration element is required so that the plugin knows where to find classes you want to generate assertions for.

You can also specify non-standard destination directory for assertion files using `targetDir` configuration element e.g.

```xml
<configuration>
    <packages>
        <param>your.first.package</param>
        <param>your.second.package</param>
        ...
    </packages>
    <targetDir>YOUR_NON_STANDARD_DIR</targetDir>
</configuration>
```

To generate assertions classes at every build, add an `<executions>` section as shown below :

```xml
<plugin>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-assertions-generator-maven-plugin</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <goals>
                <goal>generate-assertions</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <packages>
            <param>your.first.package</param>
            <param>your.second.package</param>
            ...
        </packages>
    </configuration>
</plugin>
```

Have good assertions !
