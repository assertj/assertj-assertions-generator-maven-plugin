package pl.michalostruszka.fest.assertions.maven.testdata;

import java.util.List;

/**
 * Author: michal
 */
public class Employee {

  private String name;
  private List<Address> addresses;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Address> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<Address> addresses) {
    this.addresses = addresses;
  }
}
