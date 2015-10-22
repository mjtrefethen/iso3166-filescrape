package com.egoshard.iso3166;

public class Region implements Named {

  public long identity;
  public long categoryId;
  public String categoryName;
  public String name;
  public String code;
  public String parentCode;
  public String countryCode;

  public Region() {
  }

  @Override
  public String getShortName() {
    return name;
  }

  @Override
  public String toString() {
    return "Region{" +
        "identity=" + identity +
        ", categoryId=" + categoryId +
        ", categoryName='" + categoryName + '\'' +
        ", name='" + name + '\'' +
        ", code='" + code + '\'' +
        ", parentCode='" + parentCode + '\'' +
        ", countryCode='" + countryCode + '\'' +
        '}';
  }
}
