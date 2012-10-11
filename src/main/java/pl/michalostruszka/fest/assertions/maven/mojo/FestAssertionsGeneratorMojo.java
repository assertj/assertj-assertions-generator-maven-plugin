package pl.michalostruszka.fest.assertions.maven.mojo;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import pl.michalostruszka.fest.assertions.maven.GeneratorLauncher;

import static org.fest.assertions.generator.util.ClassUtil.collectClasses;

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
   * Destination dir to store generated assertion source files. Defaults to 'target/generated-test-sources/assertions'.
   * Your IDE should be able to pick up files from this location as sources automatically when generated.
   *
   * @parameter default-value="${project.build.directory}/generated-test-sources/assertions"
   */
  public String destDir;

  /**
   * List of packages to generate assertions for.
   * Currently only packages are supported.
   *
   * @parameter
   */
  public String[] packages;

  public void execute() throws MojoExecutionException {
    try {
      createLauncher().runGenerator();
      addDestDirToTestCompilePath();
    } catch (Exception e) {
      throw new MojoExecutionException(e.getMessage());
    }

  }

  private void addDestDirToTestCompilePath() {
    project.addTestCompileSourceRoot(destDir);
  }

  private GeneratorLauncher createLauncher() throws Exception {
    return GeneratorLauncher.create(this);
  }

}
