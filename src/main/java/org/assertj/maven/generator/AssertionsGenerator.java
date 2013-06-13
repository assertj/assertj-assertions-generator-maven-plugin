package org.assertj.maven.generator;

import static org.assertj.assertions.generator.util.ClassUtil.collectClasses;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.assertj.assertions.generator.BaseAssertionGenerator;
import org.assertj.assertions.generator.description.ClassDescription;
import org.assertj.assertions.generator.description.converter.ClassToClassDescriptionConverter;

/**
 * Is able to generate AssertJ assertions classes from packages.
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

  public void generateAssertionSources(String[] packagesOrClasses, String destDir) throws Exception {
    generator.setDirectoryWhereAssertionFilesAreGenerated(destDir);
    for (Class<?> clazz : collectClasses(classLoader, packagesOrClasses)) {
      ClassDescription description = converter.convertToClassDescription(clazz);
      generator.generateCustomAssertionFor(description);
    }

  }
}
