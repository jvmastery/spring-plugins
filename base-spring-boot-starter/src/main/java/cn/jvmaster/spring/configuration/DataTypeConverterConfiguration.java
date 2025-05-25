package cn.jvmaster.spring.configuration;

import cn.jvmaster.core.util.DateUtils;
import cn.jvmaster.core.util.StringUtils;
import cn.jvmaster.spring.converter.StringToListConverter;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.converter.Converter;

/**
 * 全局类型转换配置
 * @author AI
 * @date 2025/5/21 14:37
 * @version 1.0
**/
@Configuration
public class DataTypeConverterConfiguration {

    /**
     * 字符串类型转换为日期类型
     */
    @Bean
    public Converter<String, Date> dateConverter() {
        return new Converter<String, Date>() {
            @Override
            public Date convert(String source) {
                return DateUtils.convert(source);
            }
        };
    }
}
