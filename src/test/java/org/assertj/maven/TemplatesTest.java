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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.assertj.assertions.generator.Template;
import org.junit.Before;
import org.junit.Test;

public class TemplatesTest {

  private Log log;
  private Templates templates;

  @Before
  public void setup() {
    templates = new Templates();
    log = mock(Log.class);
  }

  @Test
  public void should_load_user_template() {
    // GIVEN
    List<Template> list = new ArrayList<>();
    String templateFilename = "my_has_assertion_template.txt";
    templates.templateDirectory = "target/test-classes/";
    // WHEN
    templates.loadUserTemplate(templateFilename, Template.Type.HAS, "my has template", list, log);
    // THEN
    assertThat(list).hasSize(1);
    assertThat(list.get(0).getContent()).isNotEmpty();
  }

  @Test
  public void should_log_loading_failure_and_move_one() {
    // GIVEN
    List<Template> list = new ArrayList<>();
    templates.templateDirectory = "target/test-classes/";
    // WHEN
    templates.loadUserTemplate("unknown", Template.Type.HAS, "my has template", list, log);
    // THEN
    assertThat(list).isEmpty();
    verify(log).warn(any(CharSequence.class));
  }
}
