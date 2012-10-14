package org.fest.assertions.maven.generator;

import static org.fest.assertions.generator.util.ClassUtil.collectClasses;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.fest.assertions.generator.BaseAssertionGenerator;
import org.fest.assertions.generator.description.ClassDescription;
import org.fest.assertions.generator.description.converter.ClassToClassDescriptionConverter;

/**
 * Is able to generate Fest assertions classes from packages.
 */
public class AssertionsGenerator {

  private ClassToClassDescriptionConverter converter;
  private ClassLoader classLoader;
  private BaseAssertionGenerator generator;

  public AssertionsGenerator(ClassLoader classLoader) throws FileNotFoundException, IOException {
    this.generator = new BaseAssertionGenerator();
    this.converter = new ClassToClassDescriptionConverter();
    this.classLoader = classLoader;
  }

  public void generateAssertionSources(String[] packages, String destDir) throws Exception {
    generator.setDirectoryWhereAssertionFilesAreGenerated(destDir);
    for (Class<?> clazz : collectClasses(classLoader, packages)) {
      ClassDescription description = converter.convertToClassDescription(clazz);
      generator.generateCustomAssertionFor(description);
    }

  }
}
