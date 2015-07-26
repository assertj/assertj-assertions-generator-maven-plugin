/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2015 the original author or authors.
 */
package org.assertj.maven;

import static java.lang.String.format;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_TEST_SOURCES;
import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;
import static org.assertj.assertions.generator.AssertionsEntryPointType.BDD;
import static org.assertj.assertions.generator.AssertionsEntryPointType.JUNIT_SOFT;
import static org.assertj.assertions.generator.AssertionsEntryPointType.SOFT;
import static org.assertj.assertions.generator.AssertionsEntryPointType.STANDARD;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.assertj.core.util.VisibleForTesting;
import org.assertj.maven.generator.AssertionsGenerator;
import org.assertj.maven.generator.AssertionsGeneratorReport;

/**
 * Generates custom AssertJ assertions (*Assert) for all given classes and classes of given packages.
 */
@Mojo(name = "generate-assertions",
    defaultPhase = GENERATE_TEST_SOURCES, requiresDependencyResolution = TEST, requiresProject = true)
public class AssertJAssertionsGeneratorMojo extends AbstractMojo {

  private static final String[] INCLUDE_ALL_CLASSES = { ".*" };

  /**
   * Current maven project
   */
  @Parameter(property = "project", required = true, readonly = true)
  public MavenProject project;

  /**
   * Destination dir to store generated assertion source files. <br>
   * Defaults to 'target/generated-test-sources/assertj-assertions'.<br>
   * Your IDE should be able to pick up files from this location as sources automatically when generated.
   */
  @Parameter(defaultValue = "${project.build.directory}/generated-test-sources/assertj-assertions", property = "assertj.targetDir")
  public String targetDir;

  /**
   * List of packages to generate assertions for.
   */
  @Parameter(property = "assertj.packages")
  public String[] packages;

  /**
   * List of classes to generate assertions for.
   */
  @Parameter(property = "assertj.classes")
  public String[] classes;

  /**
   * Generated assertions are limited to classes matching one of the given regular expressions, default is to include
   * all classes.
   */
  @Parameter(property = "assertj.includes")
  public String[] includes = INCLUDE_ALL_CLASSES;

  /**
   * If class macthes one of the given regex, no assertions will be generated for it, default is not to exclude
   * anything.
   */
  @Parameter(property = "assertj.excludes")
  public String[] excludes = new String[0];

  /**
   * Flag specifying whether to generate hierarchical assertions. The default is false.
   */
  @Parameter(defaultValue = "false", property = "assertj.hierarchical")
  public boolean hierarchical;

  /**
   * An optional package name for the Assertions entry point class. If omitted, the package will be determined
   * heuristically from the generated assertions.
   */
  @Parameter(property = "assertj.entryPointClassPackage")
  public String entryPointClassPackage;

  /**
   * Skip generating classes, handy way to disable the plugin.
   */
  @Parameter(property = "assertj.skip")
  public boolean skip = false;

  /**
   * Generate Assertions entry point class.
   */
  @Parameter(property = "assertj.generate.Assertions")
  public boolean generateAssertions = true;

  /**
   * Generate generating BDD Assertions entry point class.
   */
  @Parameter(property = "assertj.generate.BddAssertions")
  public boolean generateBddAssertions = true;

  /**
   * Generate generating JUnit Soft Assertions entry point class.
   */
  @Parameter(property = "assertj.generate.JUnitSoftAssertions")
  public boolean generateJUnitSoftAssertions = true;

  /**
   * Generate generating Soft Assertions entry point class.
   */
  @Parameter(property = "assertj.generate.SoftAssertions")
  public boolean generateSoftAssertions = true;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
	if (skip) {
	  getLog().info("Assertions generator is disabled as 'skip' option is true.");
	  return;
	}
	failIfMojoParametersAreMissing();
	try {
	  ClassLoader projectClassLoader = getProjectClassLoader();
	  AssertionsGenerator assertionGenerator = new AssertionsGenerator(projectClassLoader);
	  assertionGenerator.setIncludePatterns(includes);
	  assertionGenerator.setExcludePatterns(excludes);
	  if (generateAssertions) assertionGenerator.enableEntryPointClassesGenerationFor(STANDARD);
	  if (generateBddAssertions) assertionGenerator.enableEntryPointClassesGenerationFor(BDD);
	  if (generateSoftAssertions) assertionGenerator.enableEntryPointClassesGenerationFor(SOFT);
	  if (generateJUnitSoftAssertions) {
		if (junitFoundBy(projectClassLoader)) assertionGenerator.enableEntryPointClassesGenerationFor(JUNIT_SOFT);
		else getLog().info("JUnit not found in project classpath => JUnitSoftAssertions entry point class won't be generated.");
	  }
	  assertionGenerator.setLog(getLog());
	  executeWithAssertionGenerator(assertionGenerator);
	} catch (Exception e) {
	  throw new MojoExecutionException(e.getMessage(), e);
	}
  }

  @VisibleForTesting
  AssertionsGeneratorReport executeWithAssertionGenerator(AssertionsGenerator assertionGenerator) {
	if (classes == null) classes = new String[0];
	AssertionsGeneratorReport generatorReport = assertionGenerator.generateAssertionsFor(packages, classes, targetDir,
	                                                                                     entryPointClassPackage,
	                                                                                     hierarchical);
	getLog().info(generatorReport.getReportContent());
	project.addTestCompileSourceRoot(targetDir);
	return generatorReport;
  }

  private void failIfMojoParametersAreMissing() throws MojoFailureException {
	if (isEmpty(packages) && isEmpty(classes)) {
	  throw new MojoFailureException(shouldHaveNonEmptyPackagesOrClasses());
	}
  }

  @SuppressWarnings("unchecked")
  private ClassLoader getProjectClassLoader() throws DependencyResolutionRequiredException, MalformedURLException {
	List<String> classpathElements = new ArrayList<String>(project.getCompileClasspathElements());
	classpathElements.addAll(project.getTestClasspathElements());
	List<URL> classpathElementUrls = new ArrayList<URL>(classpathElements.size());
	for (int i = 0; i < classpathElements.size(); i++) {
	  classpathElementUrls.add(new File(classpathElements.get(i)).toURI().toURL());
	}
	return new URLClassLoader(classpathElementUrls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
  }

  @VisibleForTesting
  static String shouldHaveNonEmptyPackagesOrClasses() {
	return format("Parameter 'packages' or 'classes' must be set to generate assertions.%n[Help] https://github.com/joel-costigliola/assertj-assertions-generator-maven-plugin");
  }

  private boolean junitFoundBy(ClassLoader projectClassLoader) {
	try {
	  Class.forName("org.junit.Rule", false, projectClassLoader);
	  return true;
	} catch (ClassNotFoundException e) {
	  return false;
	}
  }

}
