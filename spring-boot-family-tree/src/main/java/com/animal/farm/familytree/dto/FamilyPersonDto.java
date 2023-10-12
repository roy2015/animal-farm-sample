package com.animal.farm.familytree.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author guojun
 * @date 2023/10/11 15:16
 */

@Data
@Accessors(chain = true)
public class FamilyPersonDto {
  private Long id;

  private String code;

  private String name;

  private String mark;

  private String parentCode;

  private String parentName;

  private Integer sort;

  private String remark;
}
