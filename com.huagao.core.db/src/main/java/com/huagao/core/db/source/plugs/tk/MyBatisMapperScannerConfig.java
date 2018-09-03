package com.huagao.core.db.source.plugs.tk;

import com.huagao.core.db.mapper.GenericMapper;
import com.huagao.core.db.source.DataSourceConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import tk.mybatis.spring.mapper.MapperScannerConfigurer;

import java.util.Properties;

@Configuration
@PropertySource("classpath:db.properties")
@AutoConfigureAfter({ DataSourceConfig.class })
public class MyBatisMapperScannerConfig {

	@Bean(name = "mapperScannerConfigurer")
	public MapperScannerConfigurer mapperScannerConfigurer() {
		String sessionFacBeanName = "sqlSessionFactory";
		String tkMappers = GenericMapper.class.getName();
		String basePackage = "com.huagao.tongshan.dao";
		String tkNotEmpty = "false";
		String tkIdentity = "MYSQL";
		return this.buildMapperScannerConfigurer(sessionFacBeanName, basePackage, tkMappers, tkNotEmpty, tkIdentity);
	}

	private MapperScannerConfigurer buildMapperScannerConfigurer(String sessionFacBeanName, String basePackage, String tkMappers, String tkNotEmpty, String tkIdentity) {
		MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
		mapperScannerConfigurer.setSqlSessionFactoryBeanName(sessionFacBeanName);
		mapperScannerConfigurer.setBasePackage(basePackage);

		Properties properties = new Properties();
		// 这里要特别注意，不要把MyMapper放到 basePackage 中，也就是不能同其他Mapper一样被扫描到。
		properties.setProperty("mappers", tkMappers);
		properties.setProperty("notEmpty", tkNotEmpty);
		properties.setProperty("IDENTITY", tkIdentity);
		mapperScannerConfigurer.setProperties(properties);

		return mapperScannerConfigurer;
	}
}
