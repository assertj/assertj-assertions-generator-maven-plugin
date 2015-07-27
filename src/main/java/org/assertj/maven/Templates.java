/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2015 the original author or authors.
 */
package org.assertj.maven;

import static org.apache.commons.io.Charsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.assertj.assertions.generator.Template.Type.ABSTRACT_ASSERT_CLASS;
import static org.assertj.assertions.generator.Template.Type.ASSERTIONS_ENTRY_POINT_CLASS;
import static org.assertj.assertions.generator.Template.Type.ASSERTION_ENTRY_POINT;
import static org.assertj.assertions.generator.Template.Type.ASSERT_CLASS;
import static org.assertj.assertions.generator.Template.Type.BDD_ASSERTIONS_ENTRY_POINT_CLASS;
import static org.assertj.assertions.generator.Template.Type.BDD_ENTRY_POINT_METHOD_ASSERTION;
import static org.assertj.assertions.generator.Template.Type.HAS;
import static org.assertj.assertions.generator.Template.Type.HAS_FOR_ARRAY;
import static org.assertj.assertions.generator.Template.Type.HAS_FOR_ITERABLE;
import static org.assertj.assertions.generator.Template.Type.HAS_FOR_PRIMITIVE;
import static org.assertj.assertions.generator.Template.Type.HAS_FOR_REAL_NUMBER;
import static org.assertj.assertions.generator.Template.Type.HIERARCHICAL_ASSERT_CLASS;
import static org.assertj.assertions.generator.Template.Type.IS;
import static org.assertj.assertions.generator.Template.Type.JUNIT_SOFT_ASSERTIONS_ENTRY_POINT_CLASS;
import static org.assertj.assertions.generator.Template.Type.SOFT_ASSERTIONS_ENTRY_POINT_CLASS;
import static org.assertj.assertions.generator.Template.Type.SOFT_ENTRY_POINT_METHOD_ASSERTION;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.assertj.assertions.generator.Template;
import org.assertj.core.util.VisibleForTesting;
import org.assertj.maven.generator.AssertionsGeneratorReport;

public class Templates {

  public String templateDirectory;
  // assertion class templates
  public String assertionClassTemplate;
  public String hierarchicalAssertionConcreteClassTemplate;
  public String hierarchicalAssertionAbstractClassTemplate;
  // assertion method templates
  public String hasAssertionTemplate;
  public String isAssertionTemplate;
  public String arrayHasElementsAssertionTemplate;
  public String iterableHasElementsAssertionTemplate;
  public String primitiveHasAssertionTemplate;
  public String realNumberHasAssertionTemplate;
  // entry point templates
  public String assertionsEntryPointClassTemplate;
  public String assertionEntryPointMethodTemplate;
  public String softEntryPointAssertionClassTemplate;
  public String junitSoftEntryPointAssertionClassTemplate;
  public String softEntryPointAssertionMethodTemplate;
  public String bddEntryPointAssertionClassTemplate;
  public String bddEntryPointAssertionMethodTemplate;

  public List<Template> getTemplates(AssertionsGeneratorReport report) {
    // resolve user templates directory
    if (templateDirectory == null) templateDirectory = "./";
    // load any templates overridden by the user
    List<Template> userTemplates = new ArrayList<>();
    // @format:off
    // assertion class templates
    loadUserTemplate(assertionClassTemplate, ASSERT_CLASS, "'class assertions'", userTemplates, report);
    loadUserTemplate(hierarchicalAssertionConcreteClassTemplate, HIERARCHICAL_ASSERT_CLASS, "'hierarchical concrete class assertions'", userTemplates, report);
    loadUserTemplate(hierarchicalAssertionAbstractClassTemplate, ABSTRACT_ASSERT_CLASS, "'hierarchical abstract class assertions'", userTemplates, report);
    // assertion method templates
    loadUserTemplate(hasAssertionTemplate, HAS, "'has assertions'", userTemplates, report);
    loadUserTemplate(isAssertionTemplate, IS, "'is assertions'", userTemplates, report);
    loadUserTemplate(arrayHasElementsAssertionTemplate, HAS_FOR_ARRAY, "'array has assertions'", userTemplates, report);
    loadUserTemplate(iterableHasElementsAssertionTemplate, HAS_FOR_ITERABLE, "'iterable has assertions'", userTemplates, report);
    loadUserTemplate(primitiveHasAssertionTemplate, HAS_FOR_PRIMITIVE, "'primitive type has assertions'", userTemplates, report);
    loadUserTemplate(realNumberHasAssertionTemplate, HAS_FOR_REAL_NUMBER, "'real number has assertions'", userTemplates, report);
    // entry point templates
    loadUserTemplate(assertionsEntryPointClassTemplate,ASSERTIONS_ENTRY_POINT_CLASS, "'assertions entry point class'", userTemplates, report);
    loadUserTemplate(assertionEntryPointMethodTemplate,ASSERTION_ENTRY_POINT,  "'assertions entry point method'", userTemplates, report);
    loadUserTemplate(softEntryPointAssertionClassTemplate, SOFT_ASSERTIONS_ENTRY_POINT_CLASS, "'soft assertions entry point class'", userTemplates, report);
    loadUserTemplate(junitSoftEntryPointAssertionClassTemplate, JUNIT_SOFT_ASSERTIONS_ENTRY_POINT_CLASS, "'junit soft assertions entry point class'", userTemplates, report);
    loadUserTemplate(softEntryPointAssertionMethodTemplate, SOFT_ENTRY_POINT_METHOD_ASSERTION, "'soft assertions entry point method'", userTemplates, report);
    loadUserTemplate(bddEntryPointAssertionClassTemplate, BDD_ASSERTIONS_ENTRY_POINT_CLASS, "'BDD assertions entry point class'", userTemplates, report);
    loadUserTemplate(bddEntryPointAssertionMethodTemplate, BDD_ENTRY_POINT_METHOD_ASSERTION, "'BDD assertions entry point method'", userTemplates, report);
    // @format:on
    return userTemplates;
  }

  @VisibleForTesting
  void loadUserTemplate(String userTemplate, Template.Type type, String templateDescription,
                        List<Template> userTemplates, AssertionsGeneratorReport report) {
    if (userTemplate != null) {
      try {
        File templateFile = new File(templateDirectory, userTemplate);
        String templateContent = readFileToString(templateFile, UTF_8);
        userTemplates.add(new Template(type, templateContent));
        report.registerUserTemplate("Using custom template for " + templateDescription + " loaded from "
                                    + templateDirectory + userTemplate);
      } catch (Exception e) {
        // best effort : if we can't read user template, use the default one.
        report.registerUserTemplate("Use default " + templateDescription
                 + " assertion template as we failed to to read user template from "
                 + templateDirectory + userTemplate);
      }
    }
  }
}
