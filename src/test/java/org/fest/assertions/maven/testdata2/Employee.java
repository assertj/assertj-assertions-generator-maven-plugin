package org.fest.assertions.maven.testdata2;

import java.util.List;

import org.fest.assertions.maven.testdata1.Address;

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
