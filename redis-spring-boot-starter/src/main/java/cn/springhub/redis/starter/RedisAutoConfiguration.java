package cn.springhub.redis.starter;

import cn.springhub.base.util.StringUtils;
import cn.springhub.redis.generator.CacheKeyGenerator;
import cn.springhub.redis.generator.CacheProcessor;
import cn.springhub.redis.generator.CacheProcessorManager;
import cn.springhub.redis.generator.CacheProcessorManager.CacheProcessorEntity;
import cn.springhub.redis.generator.DefaultCacheKeyGenerator;
import cn.springhub.redis.generator.processor.DefaultCacheProcessor;
import cn.springhub.redis.generator.processor.DefaultHashCacheProcessor;
import cn.springhub.redis.generator.processor.DefaultListCacheProcessor;
import cn.springhub.redis.generator.processor.DefaultSetCacheProcessor;
import cn.springhub.redis.serializer.RedisKeySerializer;
import cn.springhub.redis.service.HashRedisOperationService;
import cn.springhub.redis.service.ListRedisOperationService;
import cn.springhub.redis.service.RedisOperationService;
import cn.springhub.redis.service.SetRedisOperationService;
import cn.springhub.redis.service.StringRedisOperationService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    /**
     * 配置redis template
     * @param redisConnectionFactory redis连接配置
     * @return  redisTemplate
     */
    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    @ConditionalOnMissingBean
    public RedisTemplate<String, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 配置序列化方式
        redisTemplate.setKeySerializer(new RedisKeySerializer(redisProperties.getPrefix()));
        redisTemplate.setHashKeySerializer(new RedisKeySerializer());

        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(StringUtils.getObjectMapper());
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
