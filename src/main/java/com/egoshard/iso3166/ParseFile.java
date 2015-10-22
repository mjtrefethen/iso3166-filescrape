package com.egoshard.iso3166;

import java.io.File;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public class ParseFile {

  private static final String SUMMARY_CLASS = "div.core-view-summary";
  private static final String SUMMARY_LINE = "div.core-view-line";
  private static final String SUMMARY_NAME = "div.core-view-field-name";
  private static final String SUMMARY_VALUE = "div.core-view-field-value";
  private static final String DIVISION_ID = "country-subdivisions";
  private static final String TBODY = "tbody";
  private static final long COUNTRY_ID = 1;
  private static final String COUNTRY_TYPE = "Country";

  private HashMap<String, RegionType> typeHashMap = new HashMap<>();
  private HashMap<String, Country> countryHashMap = new HashMap<>();
  private HashMap<String, Region> regionHashMap = new HashMap<>();

  private JdbcTemplate jdbcTemplate;

  public void setDataSource(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public void parse(String path) {

    Document htmlFile;
    try {
      File[] files = new File(path).listFiles(
          (dir, name) -> {
            return name.endsWith(".html");
          });
      for (File file : files) {
        if (file.isFile()) {
          htmlFile = Jsoup.parse(file, "UTF-8");
          // Get Country
          String currentCountryCode = null;
          Element element = htmlFile.select(SUMMARY_CLASS).first();
          Country country = new Country();
          for (Element line : element.select(SUMMARY_LINE)) {
            String name = line.select(SUMMARY_NAME).first().text();
            String value = line.select(SUMMARY_VALUE).first().text();
            switch (name) {
              case "Alpha-2 code":
                country.Code2 = value;
                currentCountryCode = value;
                break;
              case "Short name":
                country.ShortName = value;
                break;
              case "Short name lower case":
                country.ShortNameLC = value.replace("*", "");
                break;
              case "Full name":
                country.FullName = value.replace("*", "");
                break;
              case "Alpha-3 code":
                country.Code3 = value;
                break;
              case "Numeric code":
                country.NumCode = value;
                break;
              default:
            }
          }
          countryHashMap.put(country.Code2, country);
          // Get Divisions
          Element divisions = htmlFile.getElementById(DIVISION_ID);
          Element table = divisions.select(TBODY).first();
          for (Element row : table.select("tr")) {
            Elements cells = row.select("td");
            if (cells.size() > 6) {
              throw new Exception("Cell count greater than expected.");
            }
            Element[] cellArray = cells.toArray(new Element[6]);
            Region region = new Region();
            for (int x = 0; x < 6; x++) {
              if (x == 0) {
                // Get type name add to list.
                String regionTypeName = capitalize(cellArray[x].text());
                if (!typeHashMap.containsKey(regionTypeName)) {
                  RegionType regionType = new RegionType(capitalize(regionTypeName));
                  typeHashMap.put(regionTypeName, regionType);
                }
                region.categoryName = regionTypeName;
              }
              if (x == 1) {
                // Get region code
                region.code = cellArray[x].text().replace("*", "");
              }
              if (x == 2) {
                // Get region name
                region.name = cellArray[x].text();
              }
              if (x == 5) {
                // Get region parent
                String parentCode = cellArray[x].text();
                if (!parentCode.isEmpty()) {
                  region.parentCode = parentCode;
                }
              }
            }
            if (currentCountryCode == null || currentCountryCode.isEmpty()) {
              throw new Exception("Country code is null or empty.");
            }
            region.countryCode = currentCountryCode;
            if (!regionHashMap.containsKey(region.code)) {
              regionHashMap.put(region.code, region);
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public void save() {

    // Sort list, add country to top, save types, put ids into type map
    List<RegionType> regionTypeList = new ArrayList(typeHashMap.values());
    Collections.sort(regionTypeList, (object1, object2) -> object1.getShortName().compareTo(object2.getShortName()));
    regionTypeList.add(0, new RegionType(COUNTRY_ID, COUNTRY_TYPE));
    // Add country to list after sort.
    typeHashMap.put(COUNTRY_TYPE, new RegionType(COUNTRY_ID, COUNTRY_TYPE));
    // Save
    for (RegionType regionType : regionTypeList) {
      regionType.identity = insertRegionType(regionType);
    }
    // Save countries put ids into country map
    List<Country> countryList = new ArrayList(countryHashMap.values());
    Collections.sort(countryList, (object1, object2) -> object1.getShortName().compareTo(object2.getShortName()));
    for (Country country : countryList) {
      country.identity = insertCountry(country);
    }
    // Save parent regions put ids into region map
    List<Region> regionList = new ArrayList(regionHashMap.values());
    Collections.sort(regionList, (object1, object2) -> object1.getShortName().compareTo(object2.getShortName()));
    for (Region region : regionList) {
      if (region.parentCode == null || region.parentCode.isEmpty()) {
        region.identity = insertRegion(region);
      }
    }
    // Save child regions
    for (Region region : regionList) {
      if (region.parentCode != null && !region.parentCode.isEmpty()) {
        region.identity = insertRegion(region);
      }
    }

    // Save Region path to root, TODO really brute force, this could be SO better.
    regionList = new ArrayList(regionHashMap.values());
    Collections.sort(regionList, (object1, object2) -> object1.getShortName().compareTo(object2.getShortName()));
    for (Region region : regionList) {
      if (!region.categoryName.equals(COUNTRY_TYPE)) {
        // region is not a country and so has a parent list. Country is the current top of the list.
        if (region.parentCode == null || region.parentCode.isEmpty()) {
          // if region has no parent, record country as parent node @ level 1 and leaf region as 2
          insertNodePath(region.identity, countryHashMap.get(region.countryCode).identity, 1);
          insertNodePath(region.identity, region.identity, 2);
        } else {
          // if region has a parent, record country as parent node @ level 1, parent region as 2 and leaf region as 3
          insertNodePath(region.identity, countryHashMap.get(region.countryCode).identity, 1);
          insertNodePath(region.identity, regionHashMap.get(region.parentCode).identity, 2);
          insertNodePath(region.identity, region.identity, 3);
        }
      }
    }

  }

  private void insertNodePath(long regionId, long nodeId, long level) {

    final String INSERT_SQL = "insert into cd_region_path (region_rid, node_rid, node_level) values (?, ?, ?)";

    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(INSERT_SQL, new Object[]{regionId, nodeId, level});

  }

  private long insertRegionType(RegionType type) {

    final String INSERT_SQL = "insert into cd_region_type (record_key, name) values (?, ?)";

    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
          PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"record_id"});
          ps.setString(1, UUID.randomUUID().toString());
          ps.setString(2, type.name);
          return ps;
        },
        keyHolder);

    return keyHolder.getKey().longValue();

  }

  private long insertCountry(Country country) {

    final String INSERT_SQL = "insert into cd_region (record_key, region_type_rid, name, name_lc, name_full, name_common, code_alpha, code_alpha_alt, code_numeric) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
          PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"record_id"});
          ps.setString(1, UUID.randomUUID().toString());
          ps.setLong(2, 1L);
          ps.setString(3, country.ShortName);
          ps.setString(4, country.ShortNameLC);
          ps.setString(5, country.FullName);
          ps.setString(6, getCommonName(country.Code2, country.ShortNameLC)); // populate common, to be edited after first run manually. Do not execute this program twice, run SQL import instead.
          ps.setString(7, country.Code2);
          ps.setString(8, country.Code3);
          ps.setString(9, country.NumCode);
          return ps;
        },
        keyHolder);

    return keyHolder.getKey().longValue();

  }

  private long insertRegion(Region region) {

    final String INSERT_SQL = "insert into cd_region (record_key, region_type_rid, name, name_common, code_alpha, parent_region_rid, active_flag) values (?, ?, ?, ?, ?, ?, ?)";

    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
          PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"record_id"});
          ps.setString(1, UUID.randomUUID().toString());
          ps.setLong(2, typeHashMap.get(region.categoryName).identity);
          ps.setString(3, region.name);
          ps.setString(4, getCommonName(region.code, region.getShortName()));
          ps.setString(5, region.code);
          if (region.parentCode == null || region.parentCode.isEmpty()) {
            // No parent == region directly under country.
            try {
              ps.setLong(6, countryHashMap.get(region.countryCode).identity);
            } catch (Exception e) {
              e.printStackTrace();
            }
          } else {
            // Subregion
            ps.setLong(6, regionHashMap.get(region.parentCode).identity);
          }
          ps.setBoolean(7, getActiveFlag(region.code));
          return ps;
        },
        keyHolder);

    return keyHolder.getKey().longValue();

  }

  protected static boolean getActiveFlag(String regionCode) {

    switch (regionCode) {
      case "US-AS":
        return false;
      case "CN-92":
        return false;
      case "NL-BQ1":
        return false;
      case "NL-CW":
        return false;
      case "FR-GP":
        return false;
      case "US-GU":
        return false;
      case "FR-GF":
        return false;
      case "NO-22":
        return false;
      case "FR-RE":
        return false;
      case "FR-MQ":
        return false;
      case "FR-YT":
        return false;
      case "US-MP":
        return false;
      case "FR-NC":
        return false;
      case "FR-PF":
        return false;
      case "US-PR":
        return false;
      case "NL-BQ2":
        return false;
      case "FR-BL":
        return false;
      case "FR-MF":
        return false;
      case "FR-PM":
        return false;
      case "NL-BQ3":
        return false;
      case "NL-SX":
        return false;
      case "NL-AW":
        return false;
      case "GB-ENG":
        return false;
      case "GB-SCT":
        return false;
      case "GB-WLS":
        return false;
      case "NO-21":
        return false;
      case "CN-71":
        return false;
      case "FR-TF":
        return false;
      case "US-UM":
        return false;
      case "US-VI":
        return false;
      case "FR-WF":
        return false;
      default:
        return true;
    }

  }

  protected static String getCommonName(String regionCode, String shortName) {

    String commonName = shortName.replaceAll("\\(.*?\\)", "").trim();
    commonName = commonName.replaceAll("\\[.*?\\]", "").trim();
    commonName = commonName.replace("†", "").trim();
    switch (regionCode) {
      case "VG":
        commonName = "British Virgin Islands";
        break;
      case "CD":
        commonName = "Democratic Republic of the Congo";
        break;
      case "FK":
        commonName = "Falkland Islands";
        break;
      case "FM":
        commonName = "Federated States of Micronesia";
        break;
      case "LA":
        commonName = "Laos";
        break;
      case "KP":
        commonName = "North Korea";
        break;
      case "CG":
        commonName = "Republic of the Congo";
        break;
      case "KR":
        commonName = "South Korea";
        break;
      case "VI":
        commonName = "U.S. Virgin Islands";
        break;
      case "GB":
        commonName = "United Kingdom";
        break;
      case "CC":
        commonName = "Cocos (Keeling) Islands";
        break;
      case "GB-SCB":
        commonName = "Scottish Borders";
      default:
    }
    return commonName;

  }

  private String capitalize(String data) {
    return data.substring(0, 1).toUpperCase() + data.substring(1);
  }

  protected static void sortList(List<Named> list) {
    list.sort((object1, object2) -> object1.getShortName().compareTo(object2.getShortName()));
  }

}

