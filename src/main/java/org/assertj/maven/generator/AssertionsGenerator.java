package org.assertj.maven.generator;

import static org.assertj.assertions.generator.util.ClassUtil.collectClasses;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
  private Set<ClassDescription> classDescriptions;
  private File assertionsEntryPointFile;

  public AssertionsGenerator(ClassLoader classLoader) throws FileNotFoundException, IOException {
    this.generator = new BaseAssertionGenerator();
    this.converter = new ClassToClassDescriptionConverter();
    this.classLoader = classLoader;
    this.classDescriptions = new HashSet<ClassDescription>();
  }

  /**
   * Generates custom assertions for classes in given packages with the Assertions class entry point in given
   * destination dir.
   * 
   * @param packages the packages containing the classes we want to generate Assert classes for.
   * @param destDir the base directory where the classes are going to be generated.
   * @throws IOException if the files can't be generated 
   */
  public void generateAssertionSources(String[] packages, String destDir) throws IOException {
    generator.setDirectoryWhereAssertionFilesAreGenerated(destDir);
    classDescriptions = new HashSet<ClassDescription>();
    for (Class<?> clazz : collectClasses(classLoader, packages)) {
      ClassDescription classDescription = converter.convertToClassDescription(clazz);
      generator.generateCustomAssertionFor(classDescription);
      classDescriptions.add(classDescription);
    }
    assertionsEntryPointFile = generator.generateAssertionsEntryPointFor(classDescriptions);
  }
  
  public Set<ClassDescription> getClassDescriptionsOfGeneratedAssertionsClass() {
    return classDescriptions;
  }
  
  public File getAssertionsEntryPointFile() {
    return assertionsEntryPointFile;
  }
}
