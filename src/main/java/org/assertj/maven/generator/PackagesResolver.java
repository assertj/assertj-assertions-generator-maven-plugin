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
 * Copyright 2012-2017 the original author or authors.
 */
package org.assertj.maven.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.assertj.maven.NoLog;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class PackagesResolver {
  private final ClassLoader classloader;
  private final Collection<String> inputPackages;
  private final Log logger;

  public PackagesResolver(final ClassLoader classloader, final String[] inputPackages,
                          final Log logger) {
    super();
    this.classloader = classloader;
    this.inputPackages = (inputPackages == null || inputPackages.length == 0)
        ? new LinkedHashSet<String>()
        : new LinkedHashSet<String>(Arrays.asList(inputPackages));
    this.logger = (logger == null) ? new NoLog() : logger;
  }

  public String[] getPackages() {
    final String[] resolvedPackages;
    if (!hasWildcards())
    {
      resolvedPackages = inputPackages.toArray(new String[inputPackages.size()]);
    } else {
      final Collection<String> resolvedPackagesList = new LinkedHashSet<>();
      final Collection<Pattern> patterns = getPatterns();

      try {
        for (final ClassInfo classInfo : ClassPath.from(classloader).getTopLevelClasses()) {
          if (matches(classInfo, patterns)) {
            resolvedPackagesList.add(classInfo.getPackageName());
          }
        }
      } catch (IOException e) {
        logger.warn("fail to detect packages dynamically", e);
        resolvedPackagesList.addAll(inputPackages);
      }

      resolvedPackages = resolvedPackagesList.toArray(new String[resolvedPackagesList.size()]);
    }
    
    logger.info("resolved packages to generate assertions: " + Arrays.toString(resolvedPackages));
    return resolvedPackages;
  }

  protected boolean hasWildcards() {
    boolean result = false;

    if (inputPackages != null && !inputPackages.isEmpty()) {
      for (final String inputPackage : inputPackages) {
        result = result || StringUtils.contains(inputPackage, "*");
      }
    }

    return result;
  }

  protected Collection<Pattern> getPatterns() {
    final List<Pattern> patterns = new ArrayList<>();

    for (final String inputPackage : inputPackages) {
      String pattern = StringUtils.replace(inputPackage, ".", "\\.");
      pattern = StringUtils.replace(pattern, "*", "(.*)");

      if (!StringUtils.endsWith(pattern, "(.*)")) {
        pattern += "(.*)";
      }

      try {
        patterns.add(Pattern.compile(pattern));
      } catch (PatternSyntaxException e) {
        logger.warn("fail to select packages with the following java regular expression -> " + pattern + " (" + inputPackage
                    + ") because: ", e);
      }
    }

    return patterns;
  }

  protected boolean matches(final ClassInfo classInfo, final Collection<Pattern> patterns) {
    boolean match = false;

    for (final Pattern pattern : patterns) {
      match = match || pattern.matcher(classInfo.getPackageName()).matches();
      if (match) {
        break;
      }
    }

    return match;
  }
}
