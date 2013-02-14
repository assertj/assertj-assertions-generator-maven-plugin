package org.fest.assertions.maven;

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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import org.fest.assertions.maven.generator.AssertionsGenerator;

/**
 * Generates custom FEST assertions files for provided packages
 * 
 * @goal generate-assertions
 * @phase generate-test-sources
 * @requiresDependencyResolution compile+runtime
 */
public class FestAssertionsGeneratorMojo extends AbstractMojo {

  /**
   * Current maven project
   * 
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  public MavenProject project;

  /**
   * Destination dir to store generated assertion source files. Defaults to
   * 'target/generated-test-sources/fest-assertions'.<br>
   * Your IDE should be able to pick up files from this location as sources automatically when generated.
   * 
   * @parameter default-value="${project.build.directory}/generated-test-sources/fest-assertions"
   */
  public String targetDir;

  /**
   * List of packages to generate assertions for. Currently only packages are supported.
   * 
   * @parameter
   */
  public String[] packages;

  public void execute() throws MojoExecutionException {
    try {
      newAssertionGenerator().generateAssertionSources(packages, targetDir);
      logExecution();
      project.addTestCompileSourceRoot(targetDir);
    } catch (Exception e) {
      throw new MojoExecutionException(e.getMessage());
    }
  }

  private void logExecution() {
    getLog().info("About to generate Fest assertions for classes in following packages and subpackages : ");
    for (String pack : packages) {
      getLog().info("- " + pack);
    }
    getLog().info(" ");
    getLog().info("Fest assertions classes have been generated in : " + targetDir);
  }

  private AssertionsGenerator newAssertionGenerator() throws Exception {
    return new AssertionsGenerator(getProjectClassLoader());
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

}
