package com.egoshard.iso3166;

public class RegionType implements Named {

  public long identity;
  public String name;

  public RegionType(String name) {
    this.name = name;
  }

  public RegionType(long identity, String name) {
    this.identity = identity;
    this.name = name;
  }

  @Override
  public String getShortName() {
    return name;
  }
}
