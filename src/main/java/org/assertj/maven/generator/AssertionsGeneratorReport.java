package org.assertj.maven.generator;

import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class AssertionsGeneratorReport {

  private static final String INDENT = "- ";
  private static final String SECTION_START = "--- ";
  private static final String SECTION_END = " ---\n";
  private String directoryPathWhereAssertionFilesAreGenerated;
  private List<String> generatedCustomAssertionFileNames;
  private File assertionsEntryPointFile;
  private File softAssertionsEntryPointFile;
  private File bddAssertionsEntryPointFile;
  private String[] inputPackages;
  private String[] inputClasses;
  private Exception exception;

  public AssertionsGeneratorReport() {
    generatedCustomAssertionFileNames = new ArrayList<String>();
    directoryPathWhereAssertionFilesAreGenerated = "no directory set";
  }

  public void setDirectoryPathWhereAssertionFilesAreGenerated(String directory) {
    this.directoryPathWhereAssertionFilesAreGenerated = directory;
  }

  public void addGeneratedAssertionFile(File generatedCustomAssertionFile) {
    generatedCustomAssertionFileNames.add(generatedCustomAssertionFile.getName());
  }

  public String getReportContent() {
    StringBuilder reportBuilder = new StringBuilder("\n");
    reportBuilder.append("\n");
    reportBuilder.append("====================================\n");
    reportBuilder.append("AssertJ assertions generation report\n");
    reportBuilder.append("====================================\n");
    buildGeneratorParametersReport(reportBuilder);
    reportBuilder.append("\n");
    reportBuilder.append(SECTION_START).append("Generator results").append(SECTION_END);
    if (generationError()) {
      buildGeneratorReportError(reportBuilder);
    } else if (nothingGenerated()) {
      buildGeneratorReportWhenNothingWasGenerated(reportBuilder);
    } else {
      buildGeneratorReportSuccess(reportBuilder);
    }
    return reportBuilder.toString();
  }

  /**
   * @param reportBuilder
   */
  private void buildGeneratorReportSuccess(StringBuilder reportBuilder) {
    reportBuilder.append("\n");
    reportBuilder.append("Directory where custom assertions files have been generated :\n");
    reportBuilder.append(INDENT).append(directoryPathWhereAssertionFilesAreGenerated).append("\n");
    reportBuilder.append("\n");
    reportBuilder.append("Custom assertions files generated :\n");
    for (String fileName : generatedCustomAssertionFileNames) {
      reportBuilder.append(INDENT).append(fileName).append("\n");
    }
    reportBuilder.append("\n");
    reportBuilder.append("Assertions entry point class has been generated in file:\n");
    reportBuilder.append(INDENT).append(assertionsEntryPointFile.getAbsolutePath()).append("\n");
    reportBuilder.append("\n");
    reportBuilder.append("Soft Assertions entry point class has been generated in file:\n");
    reportBuilder.append(INDENT).append(softAssertionsEntryPointFile.getAbsolutePath()).append("\n");
    reportBuilder.append("\n");
    reportBuilder.append("BDD Assertions entry point class has been generated in file:\n");
    reportBuilder.append(INDENT).append(bddAssertionsEntryPointFile.getAbsolutePath()).append("\n");
  }

  /**
   * @param reportBuilder
   */
  private void buildGeneratorReportWhenNothingWasGenerated(StringBuilder reportBuilder) {
    reportBuilder.append("\n");
    reportBuilder.append("No assertions generated as no classes have been found from given classes/packages.\n");
    if (isNotEmpty(inputClasses)) {
      reportBuilder.append(INDENT).append("Given classes : ").append(Arrays.toString(inputClasses));
    }
    if (isNotEmpty(inputPackages)) {
      reportBuilder.append(INDENT).append("Given packages : ").append(Arrays.toString(inputPackages));
    }
  }

  /**
   * @param reportBuilder
   */
  private void buildGeneratorReportError(StringBuilder reportBuilder) {
    reportBuilder.append("\n");
    reportBuilder.append("Assertions failed with error : ").append(exception.getMessage());
    if (isNotEmpty(inputClasses)) {
      reportBuilder.append(INDENT).append("Given classes were : ").append(Arrays.toString(inputClasses));
    }
    if (isNotEmpty(inputPackages)) {
      reportBuilder.append(INDENT).append("Given packages were : ").append(Arrays.toString(inputPackages));
    }
    reportBuilder.append("Full error stack : ").append(ExceptionUtils.getStackTrace(exception));
  }

  /**
   * @param reportBuilder
   */
  private void buildGeneratorParametersReport(StringBuilder reportBuilder) {
    reportBuilder.append("\n");
    reportBuilder.append(SECTION_START).append("Generator input parameters").append(SECTION_END);
    reportBuilder.append("\n");
    if (isNotEmpty(inputPackages)) {
      reportBuilder.append("Generating AssertJ assertions for classes in following packages and subpackages:\n");
      for (String inputPackage : inputPackages) {
        reportBuilder.append(INDENT).append(inputPackage).append("\n");
      }
    }
    if (isNotEmpty(inputClasses)) {
      if (isNotEmpty(inputPackages)) {
        reportBuilder.append("\n");
      }
      reportBuilder.append("Generating AssertJ assertions for classes:\n");
      for (String inputClass : inputClasses) {
        reportBuilder.append(INDENT).append(inputClass).append("\n");
      }
    }
  }

  /**
   * @return
   */
  private boolean generationError() {
    return exception != null;
  }

  /**
   * @return
   */
  private boolean nothingGenerated() {
    return generatedCustomAssertionFileNames.isEmpty();
  }

  public void setAssertionsEntryPointFile(File assertionsEntryPointFile) {
    this.assertionsEntryPointFile = assertionsEntryPointFile;
  }

  public void setSoftAssertionsEntryPointFile(File softAssertionsEntryPointFile) {
    this.softAssertionsEntryPointFile = softAssertionsEntryPointFile;
  }

  public void setInputPackages(String[] packages) {
    this.inputPackages = packages;
  }

  public void setInputClasses(String[] classes) {
    this.inputClasses = classes;
  }

  public void setException(Exception exception) {
    this.exception = exception;
  }

  public void setBddAssertionsEntryPointFile(final File bddAssertionsEntryPointFile) {
    this.bddAssertionsEntryPointFile = bddAssertionsEntryPointFile;
  }
}
