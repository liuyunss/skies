package cn.com.skies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Description 交通运行监测调度中心展示
 * @Date 2020/7/28
 * @Created by sunmeng
 */

@SpringBootApplication
@EnableTransactionManagement
@ServletComponentScan
@EnableScheduling
public class SkiesSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkiesSpringBootApplication.class, args);
	}

}
