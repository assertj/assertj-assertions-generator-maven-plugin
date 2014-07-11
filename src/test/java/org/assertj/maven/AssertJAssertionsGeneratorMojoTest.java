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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import org.assertj.assertions.generator.BaseAssertionGenerator;
import org.assertj.assertions.generator.description.ClassDescription;
import org.assertj.maven.generator.AssertionsGenerator;
import org.assertj.maven.test.Employee;
import org.assertj.maven.test2.adress.Address;

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
  public void executing_plugin_with_classes_and_packages_parameter_only_should_pass() throws Exception {
    assertjAssertionsGeneratorMojo.packages = array("org.assertj.maven.test", "org.assertj.maven.test2");
    assertjAssertionsGeneratorMojo.classes = array("org.assertj.maven.test.Employee");
    List<String> classes = newArrayList(Employee.class.getName(), Address.class.getName());
    when(mavenProject.getCompileClasspathElements()).thenReturn(classes);

    assertjAssertionsGeneratorMojo.execute();

    // check that expected assertions file exist (we don't check the content we suppose the generator works).
    assertThat(assertionsFileFor(Employee.class)).exists();
    assertThat(assertionsFileFor(Address.class)).exists();
    assertThat(assertionsEntryPointFile("Assertions.java")).exists();
    assertThat(assertionsEntryPointFile("BddAssertions.java")).exists();
    assertThat(assertionsEntryPointFile("SoftAssertions.java")).exists();
  }

  @Test
  public void executing_plugin_with_hierarchical_option_should_generate_hierarchical_assertions() throws Exception {
    assertjAssertionsGeneratorMojo.packages = array("org.assertj.maven.test", "org.assertj.maven.test2");
    assertjAssertionsGeneratorMojo.classes = array("org.assertj.maven.test.Employee");
    assertjAssertionsGeneratorMojo.hierarchical = true;
    List<String> classes = newArrayList(Employee.class.getName(), Address.class.getName());
    when(mavenProject.getCompileClasspathElements()).thenReturn(classes);

    assertjAssertionsGeneratorMojo.execute();

    // check that expected assertions file exist (we don't check the content we suppose the generator works).
    assertThat(assertionsFileFor(Employee.class)).exists();
    assertThat(abstractAssertionsFileFor(Employee.class)).exists();
    assertThat(assertionsFileFor(Address.class)).exists();
    assertThat(abstractAssertionsFileFor(Address.class)).exists();
    assertThat(assertionsEntryPointFile("Assertions.java")).exists();
    assertThat(assertionsEntryPointFile("BddAssertions.java")).exists();
    assertThat(assertionsEntryPointFile("SoftAssertions.java")).exists();
  }
  
  @Test
  public void shoud_not_generate_assertions_for_assert_classes() throws Exception {
    assertjAssertionsGeneratorMojo.classes = array("org.assertj.maven.test.MyAssert");
    assertjAssertionsGeneratorMojo.packages = array("some.package");
    assertjAssertionsGeneratorMojo.hierarchical = true;
    assertjAssertionsGeneratorMojo.execute();
    assertThat(assertionsFileFor("org.assertj.maven.test.MyAssertAssert")).doesNotExist();
  }
  
  @Test
  public void executing_plugin_with_classes_parameter_only_should_pass() throws Exception {
    assertjAssertionsGeneratorMojo.classes = array("org.assertj.maven.test.Employee", "org.assertj.maven.test2.adress.Address");
    List<String> classes = newArrayList(Address.class.getName());
    assertjAssertionsGeneratorMojo.hierarchical = true;
    when(mavenProject.getCompileClasspathElements()).thenReturn(classes);

    assertjAssertionsGeneratorMojo.execute();

    // check that expected assertions file exist (we don't check the content we suppose the generator works).
    assertThat(assertionsFileFor(Employee.class)).exists();
    assertThat(assertionsFileFor(Address.class)).exists();
    assertThat(assertionsEntryPointFile("Assertions.java")).exists();
    assertThat(assertionsEntryPointFile("BddAssertions.java")).exists();
    assertThat(assertionsEntryPointFile("SoftAssertions.java")).exists();
  }
  
  @Test
  public void executing_plugin_with_custom_entry_point_class_package_should_pass() throws Exception {
    assertjAssertionsGeneratorMojo.classes = array("org.assertj.maven.test.Employee");
    assertjAssertionsGeneratorMojo.entryPointClassPackage = "my.custom.pkg";
    List<String> classes = newArrayList(Employee.class.getName(), Address.class.getName());
    when(mavenProject.getCompileClasspathElements()).thenReturn(classes);

    assertjAssertionsGeneratorMojo.execute();

    assertThat(assertionsFileFor(Employee.class)).exists();
    assertThat(assertionsEntryPointFileWithCustomPackage()).exists();
  }

  @Test
  public void executing_plugin_with_fake_package_should_not_generate_anything() throws Exception {
    assertjAssertionsGeneratorMojo.packages = array("fakepackage");
    List<String> classes = newArrayList();
    when(mavenProject.getCompileClasspathElements()).thenReturn(classes);

    assertjAssertionsGeneratorMojo.execute();

    assertThat(temporaryFolder.getRoot().list()).isEmpty();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void executing_plugin_with_error_should_be_reported_in_generator_report() throws Exception {
    assertjAssertionsGeneratorMojo.classes = array("org.assertj.maven.test.Employee");
    when(mavenProject.getCompileClasspathElements()).thenReturn(newArrayList(Employee.class.getName()));
    // let's throws an IOException when generating custom assertions
    AssertionsGenerator generator = new AssertionsGenerator(Thread.currentThread().getContextClassLoader());
    BaseAssertionGenerator baseGenerator = mock(BaseAssertionGenerator.class);
    generator.setBaseGenerator(baseGenerator);
    when(baseGenerator.generateCustomAssertionFor(Mockito.any(ClassDescription.class))).thenThrow(IOException.class);
    assertjAssertionsGeneratorMojo.executeWithAssertionGenerator(generator);

    assertThat(temporaryFolder.getRoot().list()).isEmpty();
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
    return new File(temporaryFolder.getRoot(), basePathName(clazz) + "Assert.java");
  }

  private File assertionsFileFor(String className) throws IOException {
    return new File(temporaryFolder.getRoot(), className.replace('.', File.separatorChar)+".java");
  }
  
  private File abstractAssertionsFileFor(Class<?> clazz) throws IOException {
    return new File(temporaryFolder.getRoot(), basePathName("Abstract", clazz) + "Assert.java");
  }

  private static String basePathName(Class<?> clazz) {
    return basePathName("", clazz);
  }
  private static String basePathName(String prefix, Class<?> clazz) {
    return clazz.getPackage().getName().replace('.', File.separatorChar) + File.separator + prefix + clazz.getSimpleName();
  }

  private File assertionsEntryPointFile(String simpleName) throws IOException {
    return new File(temporaryFolder.getRoot(), "org.assertj.maven.test".replace('.', File.separatorChar) + File.separator + simpleName);
  }

  private File assertionsEntryPointFileWithCustomPackage() throws IOException {
    return new File(temporaryFolder.getRoot(), "my.custom.pkg".replace('.', File.separatorChar) + File.separator + "Assertions.java");
  }
}
