package org.assertj.maven.generator;

import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.apache.commons.collections.CollectionUtils.subtract;
import static org.assertj.assertions.generator.AssertionsEntryPointType.BDD;
import static org.assertj.assertions.generator.AssertionsEntryPointType.SOFT;
import static org.assertj.assertions.generator.AssertionsEntryPointType.STANDARD;
import static org.assertj.assertions.generator.util.ClassUtil.collectClasses;
import static org.assertj.core.util.Arrays.isNullOrEmpty;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.maven.plugin.logging.Log;
import org.assertj.assertions.generator.BaseAssertionGenerator;
import org.assertj.assertions.generator.description.ClassDescription;
import org.assertj.assertions.generator.description.converter.ClassToClassDescriptionConverter;
import org.assertj.core.util.VisibleForTesting;

/**
 * Is able to generate AssertJ assertions classes from packages.
 */
public class AssertionsGenerator {

  private static final Pattern INCLUDE_EVERYTHING = Pattern.compile(".*");
  private ClassToClassDescriptionConverter converter;
  private ClassLoader classLoader;
  private BaseAssertionGenerator generator;
  private Pattern[] includePatterns;
  private Pattern[] excludePatterns;
  private Log log;

  public AssertionsGenerator(ClassLoader classLoader) throws FileNotFoundException, IOException {
    this.generator = new BaseAssertionGenerator();
    this.converter = new ClassToClassDescriptionConverter();
    this.classLoader = classLoader;
    this.includePatterns = new Pattern[] { INCLUDE_EVERYTHING };
    this.excludePatterns = new Pattern[0];
  }

  public void setIncludePatterns(String[] includeRegexs) {
    if (isNullOrEmpty(includeRegexs)) {
      includePatterns = new Pattern[] { INCLUDE_EVERYTHING };
      return;
    }
    includePatterns = new Pattern[includeRegexs.length];
    for (int i = 0; i < includeRegexs.length; i++) {
      includePatterns[i] = Pattern.compile(includeRegexs[i]);
    }
  }

  public void setExcludePatterns(String[] excludeRegexs) {
    if (isNullOrEmpty(excludeRegexs)) {
      return;
    }
    excludePatterns = new Pattern[excludeRegexs.length];
    for (int i = 0; i < excludeRegexs.length; i++) {
      excludePatterns[i] = Pattern.compile(excludeRegexs[i]);
    }
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
    report.setInputPackages(packages);
    report.setInputClasses(classesName);
    try {
      Set<Class<?>> classes = collectClasses(classLoader, ArrayUtils.addAll(packages, classesName));
      Set<Class<?>> filteredClasses = removeAssertClasses(classes);
      removeClassesAccordingToIncludeAndExcludePatterns(filteredClasses);
      report.setExcludedClassesFromAssertionGeneration(subtract(classes, filteredClasses));
      report.setDirectoryPathWhereAssertionFilesAreGenerated(destDir);
      if (hierarchical) {
        for (Class<?> clazz : filteredClasses) {
          ClassDescription classDescription = converter.convertToClassDescription(clazz);
          File[] generatedCustomAssertionFiles = generator.generateHierarchicalCustomAssertionFor(classDescription,
                                                                                                  filteredClasses);
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
      File standardAssertionsEntryPointFile = generator.generateAssertionsEntryPointClassFor(classDescriptions,
                                                                                             STANDARD,
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

  private void removeClassesAccordingToIncludeAndExcludePatterns(Set<Class<?>> filteredClasses) {
    for (Iterator<Class<?>> it = filteredClasses.iterator(); it.hasNext();) {
      Class<?> element = it.next();
      if (!isIncluded(element) || isExcluded(element)) it.remove();
    }
  }

  private boolean isIncluded(Class<?> element) {
    String className = element.getName();
    for (Pattern includePattern : includePatterns) {
      if (includePattern.matcher(className).matches()) return true;
    }
    log.debug("Won't generate assertions for " + className + " as it does not match any include regex." );
    return false;
  }

  private boolean isExcluded(Class<?> element) {
    String className = element.getName();
    for (Pattern excludePattern : excludePatterns) {
      if (excludePattern.matcher(className).matches()) {
        log.debug("Won't generate assertions for " + className + " as it matches exclude regex : " + excludePattern);
        return true;
      }
    }
    return false;
  }

  private Set<Class<?>> removeAssertClasses(Set<Class<?>> classList) {
    Set<Class<?>> filteredClassList = newLinkedHashSet();
    for (Class<?> clazz : classList) {
      String classSimpleName = clazz.getSimpleName();
      if (!classSimpleName.endsWith("Assert") && !classSimpleName.endsWith("Assertions")) {
        filteredClassList.add(clazz);
      }
    }
    return filteredClassList;
  }

  @VisibleForTesting
  public void setBaseGenerator(BaseAssertionGenerator generator) {
    this.generator = generator;
  }

  public void setLog(Log log) {
    this.log = log;
  }

}
