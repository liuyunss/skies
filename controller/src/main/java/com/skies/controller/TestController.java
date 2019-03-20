package com.skies.controller;

import com.skies.bean.TestBean;
import com.skies.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试Controller
 */
@RestController
@RequestMapping("/test")
@Slf4j
@PropertySource(value = "classpath:filepath.properties")
@Api(value = "测试接口", tags = "TestController", description = "测试接口相关")
public class TestController {
    @Value("${hive.path.ip}")
    private String ipPath;
    @Value("${hive.path.port}")
    private Integer portPath;


    /**
     * 打招呼测试
     * @return
     */
    @RequestMapping(value = "/hello",  method = {RequestMethod.GET,RequestMethod.POST } )
    @ApiOperation(tags = "打招呼测试接口", value = "进行打招呼户测试", notes = "请求返回测试bean" )
    public TestBean hello(){
        TestBean testBean = new TestBean();
        testBean.setName("zhangsan");
        testBean.setAge(portPath);
        log.info("times:"+ DateUtils.getNowTime(DateUtils.DateFormatUnit.DATE_TIME));
        log.info("testBean:"+testBean.toString());
        return testBean;
    }

}
