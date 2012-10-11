package pl.michalostruszka.fest.assertions.maven.generator;

import org.fest.assertions.api.Assertions;
import org.junit.Test;

import java.util.List;

public class PackageScannerTest {

  private static final Class<PackageScannerTest> CURRENT_CLASS = PackageScannerTest.class;
  private static final String[] CURRENT_PACKAGE = new String[]{PackageScannerTest.class.getPackage().getName()};

  @Test
  public void shouldLoadClassesUsingClassLoaderProvided() throws Exception {
    PackageScanner scanner = new PackageScanner(currentClassLoader());
    List<Class<?>> classesLoaded = scanner.loadClassesFor(CURRENT_PACKAGE);
    Assertions.assertThat(classesLoaded).contains(CURRENT_CLASS);
  }

  private ClassLoader currentClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }

}
