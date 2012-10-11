package pl.michalostruszka.fest.assertions.maven;

import pl.michalostruszka.fest.assertions.maven.generator.AssertionsGenerator;
import pl.michalostruszka.fest.assertions.maven.mojo.FestAssertionsGeneratorMojo;
import pl.michalostruszka.fest.assertions.maven.mojo.MojoBuilder;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GeneratorLauncherTest {

  private static final String[] CONFIGURED_PACKAGES = new String[]{"pl.michalostruszka.fest"};
  private static final String CONFIGURED_DEST_DIR = "/dummy-target-directory";

  @Test
  public void shouldInvokeGeneratorWithMojoParameters() throws Exception {
    FestAssertionsGeneratorMojo mojo = new MojoBuilder().withPackages(CONFIGURED_PACKAGES).withDestDir(CONFIGURED_DEST_DIR).build();
    AssertionsGenerator generator = mock(AssertionsGenerator.class);

    new GeneratorLauncher(generator, mojo).runGenerator();

    verify(generator).generateAssertionSources(CONFIGURED_PACKAGES, CONFIGURED_DEST_DIR);
  }

}
