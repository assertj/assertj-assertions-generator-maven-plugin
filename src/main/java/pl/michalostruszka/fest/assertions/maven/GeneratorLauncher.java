package pl.michalostruszka.fest.assertions.maven;

import org.fest.assertions.generator.BaseAssertionGenerator;
import org.fest.assertions.generator.description.converter.ClassToClassDescriptionConverter;
import pl.michalostruszka.fest.assertions.maven.generator.AssertionsGenerator;
import pl.michalostruszka.fest.assertions.maven.generator.MavenTargetDirClassLoaderFactory;
import pl.michalostruszka.fest.assertions.maven.generator.PackageScanner;
import pl.michalostruszka.fest.assertions.maven.mojo.FestAssertionsGeneratorMojo;

public class GeneratorLauncher {
  private AssertionsGenerator generator;
  private FestAssertionsGeneratorMojo mojo;

  public GeneratorLauncher(AssertionsGenerator generator, FestAssertionsGeneratorMojo mojo) {
    this.generator = generator;
    this.mojo = mojo;
  }

  public void runGenerator() throws Exception {
    generator.generateAssertionSources(mojo.packages, mojo.destDir);
  }

  /**
   * Poor man's dependency injection :)
   * Wires all default instances in object graph. Used as entry point to invoke generator in mojo.
   * @param mojo
   * @return fully configured launcher as entry point for generating assertions
   * @throws Exception
   */
  public static GeneratorLauncher create(FestAssertionsGeneratorMojo mojo) throws Exception {
    BaseAssertionGenerator assertionGenerator = new BaseAssertionGenerator();
    ClassToClassDescriptionConverter converter = new ClassToClassDescriptionConverter();
    ClassLoader classLoader = MavenTargetDirClassLoaderFactory.createFor(mojo);
    PackageScanner scanner = new PackageScanner(classLoader);
    AssertionsGenerator generator = new AssertionsGenerator(assertionGenerator, converter, scanner);
    return new GeneratorLauncher(generator, mojo);
  }

}
