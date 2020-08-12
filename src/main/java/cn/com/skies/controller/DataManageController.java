package cn.com.skies.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 数据处理controller层
 * @Author sunmeng
 * @Date 2020/07/28
 */

@RestController
@RequestMapping("/dataManage")
@Slf4j
@Api(value = "dataMnnageController", description = "数据处理controller层")
public class DataManageController {

    //修改名称还未对门牌表做修改
    @RequestMapping(value = "/updateJlxData",method = {RequestMethod.POST, RequestMethod.GET})
    @ApiOperation( value = "街路巷数据维护", notes = "街路巷数据维护")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "demo", value = "演示参数",  dataType = "String"),
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "请求成功"),
            @ApiResponse(code = 404, message = "无结果数据"),
            @ApiResponse(code = 500, message = "服务器内部错误，无法完成请求") })
    public String updateJlxData(@RequestParam(value = "demo",required = false,defaultValue = "1")String demo){

        return "success";
    }
}
