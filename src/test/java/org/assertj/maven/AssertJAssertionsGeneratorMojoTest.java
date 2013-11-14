package org.assertj.maven;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Arrays.array;
import static org.assertj.core.util.Lists.newArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import org.assertj.maven.test.Employee;
import org.assertj.maven.test2.adress.Address;

public class AssertJAssertionsGeneratorMojoTest {

  private static final String TARGET_DIR = "." + File.separator + "target" + File.separator;

  private AssertJAssertionsGeneratorMojo assertjAssertionsGeneratorMojo;
  private MavenProject mavenProject;

  @Before
  public void setUp() throws Exception {
    mavenProject = mock(MavenProject.class);
    assertjAssertionsGeneratorMojo = new AssertJAssertionsGeneratorMojo();
    assertjAssertionsGeneratorMojo.project = mavenProject;
    assertjAssertionsGeneratorMojo.packages = array("org.assertj.maven.test", "org.assertj.maven.test2");
    assertjAssertionsGeneratorMojo.targetDir = TARGET_DIR;
  }

  @Test
  public void testExecute() throws Exception {
    List<String> classes = newArrayList(Employee.class.getName(), Address.class.getName());
    when(mavenProject.getRuntimeClasspathElements()).thenReturn(classes);

    assertjAssertionsGeneratorMojo.execute();

    // check that expected assertions file exist (we don't check the content we suppose the generator works).
    assertThat(assertionsFileFor(Employee.class)).exists();
    assertThat(assertionsFileFor(Address.class)).exists();
    assertThat(assertionsEntryPointFile()).exists();
  }

  private static File assertionsFileFor(Class<?> clazz) {
    return new File(basePathName(clazz) + "Assert.java");
  }

  private static String basePathName(Class<?> clazz) {
    String basePathname = TARGET_DIR + clazz.getPackage().getName().replace('.', File.separatorChar) + File.separator
        + clazz.getSimpleName();
    return basePathname;
  }

  private static File assertionsEntryPointFile() {
    return new File(TARGET_DIR + "org.assertj.maven.test".replace('.', File.separatorChar) + File.separator
        + "Assertions.java");
  }

}
