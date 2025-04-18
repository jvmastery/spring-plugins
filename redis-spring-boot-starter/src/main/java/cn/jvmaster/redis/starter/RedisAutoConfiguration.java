package cn.jvmaster.redis.starter;

import cn.jvmaster.core.constant.DateTimeFormat;
import cn.jvmaster.core.util.StringUtils;
import cn.jvmaster.redis.generator.CacheKeyGenerator;
import cn.jvmaster.redis.generator.CacheProcessor;
import cn.jvmaster.redis.generator.CacheProcessorManager;
import cn.jvmaster.redis.generator.CacheProcessorManager.CacheProcessorEntity;
import cn.jvmaster.redis.generator.DefaultCacheKeyGenerator;
import cn.jvmaster.redis.generator.processor.DefaultCacheProcessor;
import cn.jvmaster.redis.generator.processor.DefaultHashCacheProcessor;
import cn.jvmaster.redis.generator.processor.DefaultListCacheProcessor;
import cn.jvmaster.redis.generator.processor.DefaultSetCacheProcessor;
import cn.jvmaster.redis.serializer.RedisKeySerializer;
import cn.jvmaster.redis.service.HashRedisOperationService;
import cn.jvmaster.redis.service.ListRedisOperationService;
import cn.jvmaster.redis.service.RedisOperationService;
import cn.jvmaster.redis.service.SetRedisOperationService;
import cn.jvmaster.redis.service.StringRedisOperationService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * redis配置类
 * @author AI
 * @date 2024/11/25 16:09
 * @version 1.0
**/
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisAutoConfiguration {
    private final RedisProperties redisProperties;

    public RedisAutoConfiguration(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean("cacheObjectMapper")
    @ConditionalOnMissingBean(name = "cacheObjectMapper")
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
            // 启用 MapperFeature 选项
            .enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)

            // 反序列化配置
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE)
            .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
            .disable(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS)

            // 序列化配置
            .enable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            // 只序列化非空字段
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            // 设置日期格式
            .defaultDateFormat(new SimpleDateFormat(DateTimeFormat.NORMAL_DATETIME))

            // 启用默认类型信息
            .activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)

            // 注册 Jackson 模块
            .addModule(new ParameterNamesModule())
            .addModule(new JavaTimeModule())
            .addModule(new Jdk8Module())

            .build();
    }

    /**
     * 配置redis template
     * @param redisConnectionFactory redis连接配置
     * @return  redisTemplate
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 配置序列化方式
        redisTemplate.setKeySerializer(new RedisKeySerializer(redisProperties.getPrefix()));
        redisTemplate.setHashKeySerializer(new RedisKeySerializer());

        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(objectMapper());
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);

        return redisTemplate;
    }

    /**
     * 默认缓存key生成规则
     * @return 缓存key默认生成配置
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheKeyGenerator cacheKeyGenerator() {
        return new DefaultCacheKeyGenerator();
    }

    /**
     * string缓存操作类
     * @param redisTemplate redis
     * @return  字符串操作配置服务
     */
    @Bean
    @ConditionalOnMissingBean
    public StringRedisOperationService stringRedisOperationService(RedisTemplate<String, Object> redisTemplate) {
        return new StringRedisOperationService(redisTemplate);
    }

    /**
     * string缓存操作类
     * @param redisTemplate redis
     * @return  字符串操作配置服务
     */
    @Bean
    @ConditionalOnMissingBean
    public HashRedisOperationService hashRedisOperationService(RedisTemplate<String, Object> redisTemplate) {
        return new HashRedisOperationService(redisTemplate);
    }

    /**
     * string缓存操作类
     * @param redisTemplate redis
     * @return  字符串操作配置服务
     */
    @Bean
    @ConditionalOnMissingBean
    public ListRedisOperationService listRedisOperationService(RedisTemplate<String, Object> redisTemplate) {
        return new ListRedisOperationService(redisTemplate);
    }


    /**
     * string缓存操作类
     * @param redisTemplate redis
     * @return  字符串操作配置服务
     */
    @Bean
    @ConditionalOnMissingBean
    public SetRedisOperationService setRedisOperationService(RedisTemplate<String, Object> redisTemplate) {
        return new SetRedisOperationService(redisTemplate);
    }

    /**
     * 缓存操作总方法
     * @param stringRedisOperationService 字符串操作配置服务
     */
    @Bean
    @ConditionalOnMissingBean
    public RedisOperationService redisOperationService(StringRedisOperationService stringRedisOperationService,
                                                        SetRedisOperationService setRedisOperationService,
                                                        ListRedisOperationService listRedisOperationService,
                                                        HashRedisOperationService hashRedisOperationService
    ) {
        return new RedisOperationService(stringRedisOperationService, setRedisOperationService, listRedisOperationService, hashRedisOperationService);
    }

    /**
     * 缓存处理器
     * @param redisTemplate redis
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheProcessorManager cacheProcessorManager(RedisTemplate<String, ?> redisTemplate,
                                                        RedisOperationService redisOperationService,
                                                        List<CacheProcessor> cacheProcessors,
                                                        ApplicationContext applicationContext) {
        List<CacheProcessorEntity> defaultCacheProcessorList = createDefaultProcessor(redisOperationService);
        if (!cacheProcessors.isEmpty()) {
            // 自定义的处理器放在最前面
            defaultCacheProcessorList.addAll(0, cacheProcessors.stream().map(item -> new CacheProcessorEntity(getBeanName(applicationContext, item), item)).toList());
        }

        return new CacheProcessorManager(defaultCacheProcessorList, redisTemplate);
    }

    /**
     * 获取对应的bean name
     * @param applicationContext    上下文环境
     * @param object                对象
     * @return 返回beanName
     */
    private String getBeanName(ApplicationContext applicationContext, Object object) {
        for (String beanName : applicationContext.getBeanNamesForType(object.getClass())) {
            return beanName;
        }

        return null;
    }

    /**
     * 构建默认处理器
     */
    private List<CacheProcessorEntity> createDefaultProcessor(RedisOperationService redisOperationService) {
        List<CacheProcessorEntity> cacheProcessorList = new ArrayList<>();
        cacheProcessorList.add(CacheProcessorManager.build(null, new DefaultListCacheProcessor(redisOperationService.getListRedisOperationService())));
        cacheProcessorList.add(CacheProcessorManager.build(null, new DefaultSetCacheProcessor(redisOperationService.getSetRedisOperationService())));
        cacheProcessorList.add(CacheProcessorManager.build(null, new DefaultHashCacheProcessor(redisOperationService.getHashRedisOperationService())));

        // 默认处理器放在最后一个
        cacheProcessorList.add(CacheProcessorManager.build(null, new DefaultCacheProcessor(redisOperationService.getStringRedisOperationService())));

        return cacheProcessorList;
    }
}
