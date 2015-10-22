package com.egoshard.iso3166;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ParseFileUnitTest {

  @Test
  public void sortListTest() {

    List<Named> list = new ArrayList<>();
    list.add(new RegionType("Bob"));
    list.add(new RegionType("Alice"));
    list.add(new RegionType("Sarah"));
    ParseFile.sortList(list);

    assertTrue(list.get(0).getShortName().equals("Alice"));
    assertTrue(list.get(1).getShortName().equals("Bob"));
    assertTrue(list.get(2).getShortName().equals("Sarah"));

  }

  @Test
  public void commonNameTest() {

    assertEquals("Test Country", ParseFile.getCommonName("Test Country (the)"));

  }

}
