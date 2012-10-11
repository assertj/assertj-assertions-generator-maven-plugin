package pl.michalostruszka.fest.assertions.maven.generator;

import edu.emory.mathcs.backport.java.util.Arrays;
import pl.michalostruszka.fest.assertions.maven.generator.AssertionsGenerator;
import pl.michalostruszka.fest.assertions.maven.generator.PackageScanner;
import pl.michalostruszka.fest.assertions.maven.testdata.Address;
import pl.michalostruszka.fest.assertions.maven.testdata.Employee;
import org.fest.assertions.generator.BaseAssertionGenerator;
import org.fest.assertions.generator.description.ClassDescription;
import org.fest.assertions.generator.description.TypeName;
import org.fest.assertions.generator.description.converter.ClassToClassDescriptionConverter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

// TODO: refactor this test to make it more readable

public class AssertionsGeneratorTest {

  public static final String DEST_DIR = "/target-directory";
  public static final String[] PACKAGES_LIST = new String[]{"com.example.package"};
  public static final List<Class<?>> LOADED_CLASSES = Arrays.asList(new Class<?>[]{Employee.class, Address.class});
  public static final Map<Class<?>, ClassDescription> CLASS_TO_DESCRIPTION_MAP = classToDescriptionMap(LOADED_CLASSES);

  private BaseAssertionGenerator coreGeneratorMock;
  private ClassToClassDescriptionConverter converterMock;
  private AssertionsGenerator generator;
  private PackageScanner packageScannerMock;

  private static Map<Class<?>, ClassDescription> classToDescriptionMap(List<Class<?>> classes) {
    Map<Class<?>, ClassDescription> map = new HashMap<Class<?>, ClassDescription>(classes.size());
    for (Class<?> clazz : classes) {
      map.put(clazz, new ClassDescription(new TypeName(clazz)));
    }
    return map;
  }

  @Before
  public void setUp() throws Exception {
    coreGeneratorMock = mock(BaseAssertionGenerator.class);
    converterMock = mock(ClassToClassDescriptionConverter.class);
    packageScannerMock = mock(PackageScanner.class);
    generator = new AssertionsGenerator(coreGeneratorMock, converterMock, packageScannerMock);
  }

  @Test
  public void shouldCreateDescriptionForEveryClassOnList() throws Exception {
    when(packageScannerMock.loadClassesFor(PACKAGES_LIST)).thenReturn(LOADED_CLASSES);
    when(converterMock.convertToClassDescription(any(Class.class))).thenAnswer(new Answer<Object>() {
      @Override
      public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
        Class<?> classProvided = (Class<?>) invocationOnMock.getArguments()[0];
        return CLASS_TO_DESCRIPTION_MAP.get(classProvided);
      }
    });

    generator.generateAssertionSources(PACKAGES_LIST, DEST_DIR);

    for (Class<?> clazz  : LOADED_CLASSES) {
      verify(coreGeneratorMock).generateCustomAssertionFor(CLASS_TO_DESCRIPTION_MAP.get(clazz));
    }
  }

  @Test
  public void shouldConfigureTargetDirForGenerator() throws Exception {
    generator.generateAssertionSources(PACKAGES_LIST, DEST_DIR);
    verify(coreGeneratorMock).setDirectoryWhereAssertionFilesAreGenerated(DEST_DIR);
  }


}
