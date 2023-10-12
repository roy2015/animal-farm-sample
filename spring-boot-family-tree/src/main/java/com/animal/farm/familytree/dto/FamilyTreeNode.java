package com.animal.farm.familytree.dto;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author guojun
 * @date 2023/10/11 16:22
 */
@Data
@Accessors(chain = true)
public class FamilyTreeNode {

  private Long id;

  private String code;

  private String name;

  private String mark;

  private String parentCode;

//  private String parentName;

  private Integer sort;

//  private String remark;

//  private String value;

  private List<FamilyTreeNode> children;

}
