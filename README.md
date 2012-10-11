Maven plugin for fest-assertion-generator
==

####This is still work in progress project. If you want to use this plugin, please clone this repo, build, and deploy this plugin to your repository. I hope to publish it in Maven Central repo soon.  

This is maven plugin for really useful tool [fest-assertion-generator](https://github.com/joel-costigliola/fest-assertion-generator). I described this tool [here](http://michalostruszka.pl/blog/2012/10/07/generate-custom-fest-assertion-classes-with-one-shot).  
It allows to generate custom fest assertions via maven, so it is possible to use it from every IDE that supports maven, or just generate them from command line using maven (instead of original command-line version of this tool).

By default it generates source files to `target/generated-test-sources/assertions` as per maven convention.

Usage prerequisities (for now)
--

In order for original generator tool to work wrapped in maven plugin there are some changes introduced that are not yet merged with original version. So if you want to use this plugin now, you need to use [my fork of generator](https://github.com/mostr/fest-assertion-generator), build it and install/deploy in your repo.  

Configuration
--

You need to have FEST Assertions as dependency in `pom.xml` e.g (version 2.x is required)

    <dependency>
        <groupId>org.easytesting</groupId>
        <artifactId>fest-assert-core</artifactId>
        <version>2.0M8</version>
    </dependency>

To generate custom assertions add the following plugin to your `pom.xml` section

    <plugin>
        <groupId>org.easytesting</groupId>
        <artifactId>maven-fest-assert-gen-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
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

`packages` list configuration element is required in order to generate assertions for classes in given packages. 

You can also specfy non-standard destination directory for assertion files using `destDir` configuration element e.g.

    <configuration>
        <packages>
            <param>your.first.package</param>
            <param>your.second.package</param>
            ...
        </packages>
        <destDir>YOUR_NON_STANDARD_DIR</destDir>
    </configuration>
        



