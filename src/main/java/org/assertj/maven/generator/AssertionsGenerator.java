package org.assertj.maven.generator;

import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.apache.commons.collections.CollectionUtils.subtract;
import static org.assertj.assertions.generator.AssertionsEntryPointType.BDD;
import static org.assertj.assertions.generator.AssertionsEntryPointType.SOFT;
import static org.assertj.assertions.generator.AssertionsEntryPointType.STANDARD;
import static org.assertj.assertions.generator.util.ClassUtil.collectClasses;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.assertj.assertions.generator.BaseAssertionGenerator;
import org.assertj.assertions.generator.description.ClassDescription;
import org.assertj.assertions.generator.description.converter.ClassToClassDescriptionConverter;
import org.assertj.core.util.VisibleForTesting;

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

  /**
   * Generates custom assertions for classes in given packages with the Assertions class entry point in given
   * destination dir.
   * 
   * @param packages the packages containing the classes we want to generate Assert classes for.
   * @param classesName the packages containing the classes we want to generate Assert classes for.
   * @param destDir the base directory where the classes are going to be generated.
   * @param entryPointFilePackage the package of the assertions entry point class, may be <code>null</code>.
   * @throws IOException if the files can't be generated
   */
  @SuppressWarnings("unchecked")
  public AssertionsGeneratorReport generateAssertionsFor(String[] packages, String[] classesName, String destDir,
                                                         String entryPointFilePackage, boolean hierarchical) {
    generator.setDirectoryWhereAssertionFilesAreGenerated(destDir);
    Set<ClassDescription> classDescriptions = new HashSet<ClassDescription>();
    AssertionsGeneratorReport report = new AssertionsGeneratorReport();
    try {
      Set<Class<?>> classes = collectClasses(classLoader, ArrayUtils.addAll(packages, classesName));
      Set<Class<?>> filteredClasses = removeAssertClasses(classes);
      report.setExcludedClassesFromAssertionGeneration(subtract(classes, filteredClasses));
      report.setInputPackages(packages);
      report.setInputClasses(classesName);
      report.setDirectoryPathWhereAssertionFilesAreGenerated(destDir);
      if (hierarchical) {
        for (Class<?> clazz : filteredClasses) {
          ClassDescription classDescription = converter.convertToClassDescription(clazz);
          File[] generatedCustomAssertionFiles = generator.generateHierarchicalCustomAssertionFor(classDescription, filteredClasses);
          report.addGeneratedAssertionFile(generatedCustomAssertionFiles[0]);
          report.addGeneratedAssertionFile(generatedCustomAssertionFiles[1]);
          classDescriptions.add(classDescription);
        }
      } else {
        for (Class<?> clazz : filteredClasses) {
          ClassDescription classDescription = converter.convertToClassDescription(clazz);
          File generatedCustomAssertionFile = generator.generateCustomAssertionFor(classDescription);
          report.addGeneratedAssertionFile(generatedCustomAssertionFile);
          classDescriptions.add(classDescription);
        }
      }
      File standardAssertionsEntryPointFile = generator.generateAssertionsEntryPointClassFor(classDescriptions, STANDARD,
                                                                                             entryPointFilePackage);
      report.setAssertionsEntryPointFile(standardAssertionsEntryPointFile);
      File softAssertionsEntryPointFile = generator.generateAssertionsEntryPointClassFor(classDescriptions, SOFT,
                                                                                         entryPointFilePackage);
      report.setSoftAssertionsEntryPointFile(softAssertionsEntryPointFile);
      File bddAssertionsEntryPointFile = generator.generateAssertionsEntryPointClassFor(classDescriptions, BDD,
                                                                                        entryPointFilePackage);
      report.setBddAssertionsEntryPointFile(bddAssertionsEntryPointFile);
    } catch (Exception e) {
      report.setException(e);
    }
    return report;
  }

  private Set<Class<?>> removeAssertClasses(Set<Class<?>> classList) {
    Set<Class<?>> filteredClassList = newLinkedHashSet();
    for (Class<?> clazz : classList) {
      if (!clazz.getSimpleName().endsWith("Assert")) {
        filteredClassList.add(clazz);
      }
    }
    return filteredClassList;
  }

  @VisibleForTesting
  public void setBaseGenerator(BaseAssertionGenerator generator) {
    this.generator = generator;
  }

}
