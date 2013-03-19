package org.assertj.maven.testdata2;

import java.util.List;

import org.assertj.maven.testdata1.Address;


public class Employee {

  private String name;
  private List<Address> addresses;
  private boolean active;

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

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
  
}
