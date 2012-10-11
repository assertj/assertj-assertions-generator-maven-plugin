package pl.michalostruszka.fest.assertions.maven.mojo;

public class MojoBuilder {

  private FestAssertionsGeneratorMojo mojo = new FestAssertionsGeneratorMojo();

  public MojoBuilder withPackages(String... packages) {
    mojo.packages = packages;
    return this;
  }

  public MojoBuilder withDestDir(String destDir) {
    mojo.destDir = destDir;
    return this;
  }

  public FestAssertionsGeneratorMojo build() {
    return mojo;
  }
}
