package org.assertj.maven;

import org.apache.maven.plugin.logging.Log;

public class NoLog implements Log {
  @Override
  public boolean isDebugEnabled() {
    return false;
  }

  @Override
  public void debug(CharSequence content) {
    // do nothing
  }

  @Override
  public void debug(CharSequence content, Throwable error) {
    // do nothing
  }

  @Override
  public void debug(Throwable error) {
    // do nothing
  }

  @Override
  public boolean isInfoEnabled() {
    return false;
  }

  @Override
  public void info(CharSequence content) {
    // do nothing
  }

  @Override
  public void info(CharSequence content, Throwable error) {
    // do nothing
  }

  @Override
  public void info(Throwable error) {
    // do nothing
  }

  @Override
  public boolean isWarnEnabled() {
    return false;
  }

  @Override
  public void warn(CharSequence content) {
    // do nothing
  }

  @Override
  public void warn(CharSequence content, Throwable error) {
    // do nothing
  }

  @Override
  public void warn(Throwable error) {
    // do nothing
  }

  @Override
  public boolean isErrorEnabled() {
    return false;
  }

  @Override
  public void error(CharSequence content) {
    // do nothing
  }

  @Override
  public void error(CharSequence content, Throwable error) {
    // do nothing
  }

  @Override
  public void error(Throwable error) {
    // do nothing
  }
}
