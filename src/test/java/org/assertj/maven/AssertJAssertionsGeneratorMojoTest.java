package org.assertj.maven;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.assertj.core.util.Arrays.array;
import static org.assertj.core.util.Lists.newArrayList;
import static org.assertj.maven.AssertJAssertionsGeneratorMojo.shouldHaveNonEmptyPackagesOrClasses;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.assertj.maven.testdata1.Address;
import org.assertj.maven.testdata2.Employee;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class AssertJAssertionsGeneratorMojoTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private AssertJAssertionsGeneratorMojo assertjAssertionsGeneratorMojo;
  private MavenProject mavenProject;

  @Before
  public void setUp() throws Exception {
    mavenProject = mock(MavenProject.class);
    assertjAssertionsGeneratorMojo = new AssertJAssertionsGeneratorMojo();
    assertjAssertionsGeneratorMojo.project = mavenProject;
    assertjAssertionsGeneratorMojo.targetDir = temporaryFolder.getRoot().getAbsolutePath();
  }

  @Test
  public void testExecute() throws Exception {
    assertjAssertionsGeneratorMojo.packages = array("org.assertj.maven.testdata1");
    assertjAssertionsGeneratorMojo.classes = array("org.assertj.maven.testdata2.Employee");
    List<String> classes = newArrayList(Employee.class.getName(), Address.class.getName());
    when(mavenProject.getRuntimeClasspathElements()).thenReturn(classes);

    assertjAssertionsGeneratorMojo.execute();

    // check that expected assertions file exist (we don't check the content we suppose the generator works).
    assertThat(assertionsFileFor(Employee.class)).exists();
    assertThat(assertionsFileFor(Address.class)).exists();
  }

  @Test
  public void should_fail_if_packages_and_classes_parameters_are_null() throws Exception {
    try {
      assertjAssertionsGeneratorMojo.execute();
      failBecauseExceptionWasNotThrown(MojoFailureException.class);
    } catch (MojoFailureException e) {
      assertThat(e).hasMessage(shouldHaveNonEmptyPackagesOrClasses());
    }
  }

  private File assertionsFileFor(Class<?> clazz) throws IOException {
    return temporaryFolder.newFile(clazz.getPackage().getName().replace('.', File.separatorChar) + File.separator
        + clazz.getSimpleName() + "Assert.java");
  }
}
