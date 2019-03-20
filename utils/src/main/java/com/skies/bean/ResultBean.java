package com.skies.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Admin on 2019-1-27.
 */
@Data
@ApiModel
public class ResultBean implements Serializable {
    /**
     * 状态码
     */
    @ApiModelProperty(value="状态码",dataType="int",name="stateCode",required = true,example="200")
    int stateCode;
    /**
     * 消息
     */
    @ApiModelProperty(value="消息",dataType="String",name="message",required = false,example="")
    String message;
    /**
     * 额外数据
     */
    @ApiModelProperty(value="数据",dataType="Object",name="data",required = false,example="")
    Object data;
}
