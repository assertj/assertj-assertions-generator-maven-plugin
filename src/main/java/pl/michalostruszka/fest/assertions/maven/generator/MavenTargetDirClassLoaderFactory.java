package pl.michalostruszka.fest.assertions.maven.generator;

import pl.michalostruszka.fest.assertions.maven.mojo.FestAssertionsGeneratorMojo;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;


public class MavenTargetDirClassLoaderFactory  {

  // TODO: think on better way of handling this class loader creation

  public static URLClassLoader createFor(FestAssertionsGeneratorMojo mojo) throws Exception {
    List runtimeClasspathElements = mojo.project.getRuntimeClasspathElements();
    URL[] runtimeUrls = new URL[runtimeClasspathElements.size()];
    for (int i = 0; i < runtimeClasspathElements.size(); i++) {
      String element = (String) runtimeClasspathElements.get(i);
      runtimeUrls[i] = new File(element).toURI().toURL();
    }
    return new URLClassLoader(runtimeUrls, Thread.currentThread().getContextClassLoader());
  }
}
