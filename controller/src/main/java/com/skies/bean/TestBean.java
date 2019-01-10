package com.skies.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 测试bean
 */
@Data
@ApiModel
public class TestBean {
    @ApiModelProperty(value="名称",dataType="String",name="name",example="oKong")
    @NotBlank(message = "名称不能为空")
    private String name;
    @ApiModelProperty(value="年龄",dataType="Integer",name="age",example="45")
    private Integer age;
}
