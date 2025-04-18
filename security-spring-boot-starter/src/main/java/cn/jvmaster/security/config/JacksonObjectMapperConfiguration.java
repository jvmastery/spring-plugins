package cn.jvmaster.security.config;

import cn.jvmaster.security.mixin.AuthorizationGrantTypeMixin;
import cn.jvmaster.security.serializer.PersistentRememberMeTokenDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

/**
 * json配置
 * 添加spring security相关的模块，防止反序列化错误
 * @author AI
 * @date 2024/3/12 21:50
 */
@Configuration
public class JacksonObjectMapperConfiguration {

    private final ObjectMapper objectMapper;

    public JacksonObjectMapperConfiguration(@Qualifier("cacheObjectMapper") ObjectMapper cacheObjectMapper) {
        this.objectMapper = cacheObjectMapper;
    }

    @PostConstruct
    public void config() {
        objectMapper.addMixIn(AuthorizationGrantType.class, AuthorizationGrantTypeMixin.class);

        ClassLoader classLoader = JacksonObjectMapperConfiguration.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);

        this.objectMapper.registerModules(securityModules);
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());

        // 无默认构造函数反序列化
        SimpleModule module = new SimpleModule();
        module.addDeserializer(PersistentRememberMeToken.class, new PersistentRememberMeTokenDeserializer());
        this.objectMapper.registerModule(module);
    }
}
