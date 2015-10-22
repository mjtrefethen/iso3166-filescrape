package com.egoshard.iso3166;

public class Country implements Named {

  public long identity;
  public String Code2;
  public String Code3;
  public String ShortName;     // shortname
  public String ShortNameLC;
  public String FullName;
  public String NumCode;

  public Country() {
  }

  @Override
  public String getShortName() {
    return ShortName;
  }

}
