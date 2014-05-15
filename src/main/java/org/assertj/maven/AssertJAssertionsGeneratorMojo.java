package org.assertj.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

import static java.lang.String.format;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;
import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_TEST_SOURCES;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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
@Mojo(name = "generate-assertions", defaultPhase = GENERATE_TEST_SOURCES, requiresDependencyResolution = COMPILE_PLUS_RUNTIME, requiresProject = true)
public class AssertJAssertionsGeneratorMojo extends AbstractMojo {

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
   * An optional package name for the Assertions entry point class. If omitted, the package will be determined
   * heuristically from the generated assertions.
   */
  @Parameter(property = "assertj.entryPointClassPackage")
  public String entryPointClassPackage;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    failIfMojoParametersAreMissing();
    try {
      executeWithAssertionGenerator(new AssertionsGenerator(getProjectClassLoader()));
    } catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  @VisibleForTesting
  void executeWithAssertionGenerator(AssertionsGenerator assertionGenerator) {
    AssertionsGeneratorReport generatorReport = assertionGenerator.generateAssertionsFor(packages, classes, targetDir,
        entryPointClassPackage);
    getLog().info(generatorReport.getReportContent());
    project.addTestCompileSourceRoot(targetDir);
  }

  private void failIfMojoParametersAreMissing() throws MojoFailureException {
    if (isEmpty(packages) && isEmpty(classes)) {
      throw new MojoFailureException(shouldHaveNonEmptyPackagesOrClasses());
    }
  }

  private ClassLoader getProjectClassLoader() throws DependencyResolutionRequiredException, MalformedURLException {
    @SuppressWarnings("unchecked")
    List<String> runtimeClasspathElements = project.getRuntimeClasspathElements();
    URL[] runtimeUrls = new URL[runtimeClasspathElements.size()];
    for (int i = 0; i < runtimeClasspathElements.size(); i++) {
      runtimeUrls[i] = new File(runtimeClasspathElements.get(i)).toURI().toURL();
    }
    return new URLClassLoader(runtimeUrls, Thread.currentThread().getContextClassLoader());
  }

  @VisibleForTesting
  static String shouldHaveNonEmptyPackagesOrClasses() {
    return format("Parameter 'packages' or 'classes' must be set to generate assertions.%n[Help] https://github.com/joel-costigliola/assertj-assertions-generator-maven-plugin");
  }
}
