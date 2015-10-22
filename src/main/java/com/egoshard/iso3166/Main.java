package com.egoshard.iso3166;

import javax.sql.DataSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 *
 */
@SpringBootApplication()
@ComponentScan("com.egoshard")
@PropertySource({"classpath:application.properties"})
public class Main implements CommandLineRunner {

  public static void main(String[] args) {

    String path = args[0];

    ConfigurableApplicationContext context = SpringApplication.run(Main.class);
    DataSource dataSource = context.getBean("dataSource", DataSource.class);

    ParseFile parseFile = new ParseFile();
    parseFile.setDataSource(dataSource);
    parseFile.parse(path);
    parseFile.save();

  }

  @Override
  public void run(String... strings) throws Exception {

  }

}