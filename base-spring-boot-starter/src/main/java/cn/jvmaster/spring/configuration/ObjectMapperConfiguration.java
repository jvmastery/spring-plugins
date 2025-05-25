package cn.jvmaster.spring.configuration;

import cn.jvmaster.core.constant.Constant;
import cn.jvmaster.core.serializer.EnumSerializer;
import cn.jvmaster.core.serializer.LongToStringSerializer;
import cn.jvmaster.spring.annotation.JsonIgnoreForHttp;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.text.SimpleDateFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * 接口objectmapper配置
 * @author AI
 * @date 2024/8/7 22:08
 */
@Configuration
public class ObjectMapperConfiguration {

    @Bean
    @Primary
    @SuppressWarnings({"unchecked", "rawtypes"})
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        SimpleModule enumModule = new SimpleModule();
        Class<Constant<?>> enumClass = (Class<Constant<?>>) (Class<?>) Constant.class;
        enumModule.addSerializer(enumClass, new EnumSerializer());
        enumModule.addSerializer(Long.class, new LongToStringSerializer());

        return builder
            .createXmlMapper(false)
            .annotationIntrospector(new HttpIgnoreIntrospector())
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
            .modules(new ParameterNamesModule(), new Jdk8Module(), new JavaTimeModule(), enumModule)
            .build();
    }
}

/**
 * 添加忽略@JsonIgnoreForHttp 注解标注的字段
 */
class HttpIgnoreIntrospector extends JacksonAnnotationIntrospector {
    @Override
    public boolean hasIgnoreMarker(AnnotatedMember m) {
        // 如果字段上有 @JsonIgnoreForHttp 注解，则忽略
        if (m.hasAnnotation(JsonIgnoreForHttp.class)) {
            return true;
        }

        return super.hasIgnoreMarker(m);
    }
}