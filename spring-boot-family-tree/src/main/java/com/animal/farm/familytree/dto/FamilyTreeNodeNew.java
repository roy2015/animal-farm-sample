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
public class FamilyTreeNodeNew {
//  {id: 100, name: 'flare', value: 123, pid: 0},

  private Long id;


  private String name;

  private String value;

  private Long pid;


}
