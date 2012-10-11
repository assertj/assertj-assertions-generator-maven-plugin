package pl.michalostruszka.fest.assertions.maven.generator;

import org.fest.assertions.generator.BaseAssertionGenerator;
import org.fest.assertions.generator.description.ClassDescription;
import org.fest.assertions.generator.description.converter.ClassToClassDescriptionConverter;

public class AssertionsGenerator {
  private ClassToClassDescriptionConverter converter;
  private PackageScanner packageScanner;
  private BaseAssertionGenerator generator;

  public AssertionsGenerator(BaseAssertionGenerator generator, ClassToClassDescriptionConverter converter, PackageScanner packageScanner) {
    this.generator = generator;
    this.converter = converter;
    this.packageScanner = packageScanner;
  }

  public void generateAssertionSources(String[] packages, String destDir) throws Exception {
    generator.setDirectoryWhereAssertionFilesAreGenerated(destDir);
    for (Class<?> clazz: packageScanner.loadClassesFor(packages)) {
      ClassDescription description = converter.convertToClassDescription(clazz);
      generator.generateCustomAssertionFor(description);
    }

  }
}
