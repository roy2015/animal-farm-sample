package com.animal.farm.familytree;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author guojun
 * @date 2023/10/11 14:32
 */
@MapperScan("com.animal.farm.familytree.orm.mapper")
@EnableTransactionManagement
@SpringBootApplication
public class FamilyTreeApplication {
  public static void main(String[] args) {
    SpringApplication.run(FamilyTreeApplication.class, args);
  }
}
