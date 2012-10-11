package pl.michalostruszka.fest.assertions.maven.generator;

import org.fest.assertions.generator.util.ClassUtil;

import java.util.List;

public class PackageScanner {

  private ClassLoader classLoader;

  public PackageScanner(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  public List<Class<?>>loadClassesFor(String[] packages) {
    try {
      return ClassUtil.collectClasses(classLoader, packages);
    } catch (Exception e) {
      throw new RuntimeException("Exception while loading classes from packages: " + packages, e);
    }
  }

}
