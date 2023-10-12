package com.animal.farm.familytree.dto.request;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author guojun
 * @date 2023/10/12 11:03
 */
@ApiModel("新增家谱人员dto")
@Data
@Accessors(chain = true)
public class AddFamilyPersonDto {

//  private Long id;

  @ApiModelProperty(value = "code",required = true)
  @NotNull(message = "code不能为空")
  @NotEmpty(message = "code不能为空字符串")
  private String code;

  @ApiModelProperty(value = "name", required = true)
  @NotNull(message = "name不能为空")
  @NotEmpty(message = "name不能为空字符串")
  private String name;

  @ApiModelProperty("字， 可以写小名备注")
  private String mark;

  @ApiModelProperty(value = "父亲code", required = true)
  @NotNull(message = "父亲code不能为空")
  @NotEmpty(message = "父亲code不能为空字符串")
  private String parentCode;

  @ApiModelProperty(value = "父亲名字，可空，系统自己查询出来赋值")
  private String parentName;

  @ApiModelProperty(value = "排序字段，可空，系统自己计算，依次编号")
  private Integer sort;

  @ApiModelProperty(value = "备注")
  private String remark;
}
