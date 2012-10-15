Maven plugin for fest-assertion-generator
==

This plugin can generate [Fest assertions](https://github.com/alexruiz/fest-assert-2.x/wiki) for your own classes via maven (it is based on [fest-assertion-generator](https://github.com/joel-costigliola/fest-assertion-generator/wiki)).

Let's say that you have a `Player` class with `name` and `team` attributes, the plugin is able to create a `PlayerAssert` assertions class with `hasName` and `hasTeam` assertions, to write code like :

```java
assertThat(mvp).hasName("Lebron James").hasTeam("Miami Heat");
```

The plugin can be launched with command `mvn generate-test-sources` (or simply `mvn test`) or with any IDE that supports maven.
By default, it generates the assertions source files in `target/generated-test-sources/assertions` as per maven convention (but this can be changed - see below).

**Big thanks** to [Michal Ostruszka](https://github.com/mostr) for its major contribution on this plugin.

Configuration
--

You need to have FEST Assertions as a dependency in `pom.xml` e.g (version 2.x is required)

```xml
<dependency>
    <groupId>org.easytesting</groupId>
    <artifactId>fest-assert-core</artifactId>
    <version>2.0M8</version>
    <scope>test</scope>
</dependency>
```

To generate custom assertions, add the following plugin to your `pom.xml` build/plugins section

```xml
<plugin>
    <groupId>org.easytesting</groupId>
    <artifactId>maven-fest-assertion-generator-plugin</artifactId>
    <version>1.0</version>
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

`packages` configuration element is required in order to generate assertions for classes located in the specified packages. 

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

Have good assertions !
