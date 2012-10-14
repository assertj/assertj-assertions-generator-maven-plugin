Maven plugin for fest-assertion-generator
==

This is maven plugin for really useful tool [fest-assertion-generator](https://github.com/joel-costigliola/fest-assertion-generator) (Big thanks to [Michal Ostruszka](https://github.com/mostr) for its major contribution).

It allows to generate custom fest assertions classes via maven, either with command line or from every IDE that supports maven.

By default it generates source files to `target/generated-test-sources/assertions` as per maven convention.

Configuration
--

You need to have FEST Assertions as dependency in `pom.xml` e.g (version 2.x is required)

```xml
<dependency>
    <groupId>org.easytesting</groupId>
    <artifactId>fest-assert-core</artifactId>
    <version>2.0M8</version>
</dependency>
```

To generate custom assertions add the following plugin to your `pom.xml` section

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

`packages` list configuration element is required in order to generate assertions for classes in given packages. 

You can also specfy non-standard destination directory for assertion files using `targetDir` configuration element e.g.

<configuration>
```xml
    <packages>
        <param>your.first.package</param>
        <param>your.second.package</param>
        ...
    </packages>
    <targetDir>YOUR_NON_STANDARD_DIR</targetDir>
</configuration>
```
