package org.fest.assertions.maven;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.util.Arrays.array;
import static org.fest.util.Lists.newArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import org.fest.assertions.maven.testdata1.Address;
import org.fest.assertions.maven.testdata2.Employee;

public class FestAssertionsGeneratorMojoTest {

  private static final String TARGET_DIR = "./target/";

  private FestAssertionsGeneratorMojo festAssertionsGeneratorMojo;
  private MavenProject mavenProject;

  @Before
  public void setUp() throws Exception {
    mavenProject = mock(MavenProject.class);
    festAssertionsGeneratorMojo = new FestAssertionsGeneratorMojo();
    festAssertionsGeneratorMojo.project = mavenProject;
    festAssertionsGeneratorMojo.packages = array("org.fest.assertions.maven.testdata1",
        "org.fest.assertions.maven.testdata2");
    festAssertionsGeneratorMojo.targetDir = TARGET_DIR;
  }

  @Test
  public void testExecute() throws Exception {
    List<String> classes = newArrayList(Employee.class.getName(), Address.class.getName());
    when(mavenProject.getRuntimeClasspathElements()).thenReturn(classes);

    festAssertionsGeneratorMojo.execute();

    // check that expected assertions file exist (we don't check the content we suppose the generator works).
    assertThat(assertionsFileFor(Employee.class)).exists();
    assertThat(assertionsFileFor(Address.class)).exists();

  }

  private static File assertionsFileFor(Class<?> clazz) {
    return new File(TARGET_DIR + clazz.getPackage().getName().replace('.', File.separatorChar) + File.separator
        + clazz.getSimpleName() + "Assert.java");
  }

}
