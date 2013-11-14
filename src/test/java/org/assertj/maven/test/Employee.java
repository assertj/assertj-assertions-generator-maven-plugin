package org.assertj.maven.test;

import java.util.List;

import org.assertj.maven.test2.adress.Address;


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
