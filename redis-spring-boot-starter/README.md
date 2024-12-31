# 项目介绍
redis-spring-boot-starter是一个封装了redis常用操作的starter，为redis常用的使用场景提供简易的api，减少代码中对redis的操作。

# 使用方式
## 依赖
```
<dependency>
    <groupId>cn.springhub</groupId>
    <artifactId>redis-spring-boot-starter</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>
```

## 使用
通过 `@EnableRedis` 来启用redis常用操作。
```
@SpringBootApplication
@EnableRedis
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

# 应用
## 1、分布式锁
使用`RedisOperationService`的lock方法，来进行加锁操作。
```
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class Test {
    @Autowired
    private RedisOperationService redisOperationService;
    
    @org.junit.Test
    public void test() throws Exception {
        // 1、简单使用，如果已经存在锁的情况，则返回null。否则执行回调函数
        redisOperationService.lock("lock", () -> {
            // do something...
    
            return null;
        });
    
        // 2、指定重试次数，如果锁已经存在，则会等待1秒后，进行一次重试，直到达到指定次数。如果已经达到指定次数，上个操作还没执行完，则返回null
        redisOperationService.lock("lock", () -> {
           return null;
        }, 5);
    
        // 3、指定锁存在的时间，默认情况下锁存在的时间为30秒
        redisOperationService.lock("lock", () -> {
            return null;
        }, 5, Duration.ofHours(1L));
    
        // 4、指定是否抛出异常，前面几个默认情况下，如果锁还存在，则返回null，如果指定抛出异常，如果锁存在，则会以异常的方式抛出
        redisOperationService.lock("lock", () -> {
            return null;
        }, 5, Duration.ofHours(1L), false);
    }
}
```
上面的方法是主动调用锁的方式，同时也可以使用 `@Lock` 注解来直接对需要加锁的方法进行加锁。注意：该方法需要为spring容器的方法，按照普通方式调用方法，注解无效。
```
@Component
public class Test {

    @Lock
    public void testLock() {
        // 加锁操作
    }
}

// 测试
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class Test {
    @Autowired
    private cn.boruisoft.Test test;
    
    @org.junit.Test
    public void test() throws Exception {
    
        test.testLock();
    }
}
```
对于Lock注解，它有3个属性：name、lockExistSeconds、retryTimes，分别是锁的名称、锁存在的时间（秒）、重试次数，3个属性的含义和主动调用方式含义一致。其中锁名定义在见下面 `缓存名称定义` 。

## 2、缓存
### 1、@Cache 添加缓存
通过在方法上添加 `@Cache` 注解，标示该方法是一个缓存方法。
```
@Cache(name = "cacheName")
public void test() throws Exception {
    System.out.println("进入test,.....");
}

/**
 * 通过expire指定缓存有效时间
 */
@Cache(name = "cacheName1", expire = 3600L)
public List<String> testList() {
    System.out.println("进入testList,.....");
    List<String> list = new ArrayList<String>();
    list.add("1");
    list.add("2");
    return list;
}

@Cache
public Set<String> testSet() {
    System.out.println("进入testSet.....");
    Set<String> list = new HashSet<>();
    list.add("3");
    list.add("4");
    return list;
}

@Cache
public Map<String, Object> testMap() {
    System.out.println("进入testMap.....");
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("1", "2");
    map.put("3", "4");
    map.put("5", "6");
    return map;
}
```
通过name属性指定缓存的名称；expire指定缓存有效时间（秒），默认为30天，缓存实际有效时间为指定时间再加上100以内的随机数，防止缓存雪崩。

在执行 `@Cache` 注解的方法时，会根据缓存名称查询是否存在缓存，如果存在，则直接返回存储的缓存内容，如果不存在，则会执行方法，获取方法的返回值，
然后根据方法返回的类型，将缓存添加到不同类型中，对应关系如下：

| java类型 | redis类型 |
|--------|---------|
| List   | list    |
| Set    | set     |
| Map    | hash    |        
| 其他     | string  |

### 2、更新缓存
默认情况下，`@Cache` 注解会在缓存存在的情况下，直接从缓存中获取内容，而不再调用方法。如果需要指定方法仅为更新方法（即不管缓存存不存在，
都调用方法，然后更新缓存），可以通过设置mode来实现。
```
/**
 * 通过设置mode来表示方法为更新方法
 * 不管缓存存不存在，都会调用方法，然后更新缓存
 */
@Cache(mode = Mode.UPDATE_ONLY)
public int testPut() {
    System.out.println("进入testPut,.....");
    return RandomUtils.random(100);
}
```

### 3、删除缓存
`@CacheRemove` 注解可以删除缓存数据，该注解标注的方法，在调用后，会删除指定缓存名称的缓存。如果将allEntries设置为true，那么则会删除所有包含缓存名称的key。

在这里如果没有指定缓存的name，那么该方法会议当前类className为缓存名称进行全部删除。 

```
/**
 * 删除指定的key
 */
@CacheRemove(name = "Test::testPut")
public void remove() {
    System.out.println("删除缓存");
}

/**
 * 批量删除key
 */
@CacheRemove(name = "Test", allEntries = true)
public void removeAll(String name) {
    System.out.println("清除缓存：" + name);
}
```

### 4、缓存名称定义
对于 `@Cache`、`@CacheRemove` 注解都需要有缓存名称，在没有指定的情况下，系统会自动生成一个缓存名称，其他 `@Cache` 注解为`className + methodName + hashCode(parmas)`，
`@CacheRemove`为`className`。

同时也可以通过name属性，来自定义缓存的名称。自定义名称可以是一个字符串（`不能包含#`），也可以是一个springel表达式。现在主要说明一下springel表达式的定义方式。

定义缓存名称表达式时，有以下几个属性：
- root：当前类对象
- method：当前方法Method对象
- params: 当前方法的实际参数数组
- 实际参数名称

下面通过实际的例子来理解:
```
/**
 * 定义普通字符串名称
 * 实际缓存名称为：cacheName
 */
@Cache(name = "cacheName")
public void test() throws Exception {
    System.out.println("进入test,.....");
}

/**
 * 定义：类名
 * 实际缓存名称为：cn.springhub.Test
 */
@Cache(name = "#target.name")
public void test1() throws Exception {
    System.out.println("进入test,.....");
}

/**
 * 定义：方法名称
 * 实际缓存名称为：test2
 */
@Cache(name = "#method.name")
public void test2() throws Exception {
    System.out.println("进入test,.....");
}

/**
 * 定义：根据参数数组
 * 调用：test.test3("user");
 * 实际缓存名称为： user
 */
@Cache(name = "#params[0]")
public void test3(String name) throws Exception {
    System.out.println("进入test,.....");
}

/**
 * 定义：根据参数来
 * 调用：test.test4("user1");
 * 实际缓存名称为： user1
 */
@Cache(name = "#name")
public void test4(String name) throws Exception {
    System.out.println("进入test,.....");
}

/**
 * 定义：拼接
 * test.test5("user2");
 * 实际缓存名称为： cacheuser2
 */
@Cache(name = "'cache' + #name")
public void test5(String name) throws Exception {
    System.out.println("进入test,.....");
}
```

### 5、自定义缓存解析器
默认情况下，缓存都有各自的解析器方法，但同时我们可以通过自定义缓存解析器来达到修改解析器的目的，如果需要增加自定义解析器，需要实现`cn.springhub.redis.generator.CacheProcessor`接口。

自定义解析器只需要实现接口，然后将类添加到spring容器中即可。
```
@Component
public class CustomCacheProcessor implements CacheProcessor {

    private RedisTemplate<String, Object> redisTemplate;

    public CustomCacheProcessor(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 获取缓存方法
     * @param cacheName 缓存名称
     * @param cache     缓存注解
     */
    @Override
    public Object get(String cacheName, Cache cache) {
        return redisTemplate.opsForValue().get(cacheName);
    }

    /**
     * 保存缓存的方法
     * @param cacheName 缓存名称
     * @param value 当前方法获取到的值
     * @param cache 缓存注解
     */
    @Override
    public void save(String cacheName, Object value, Cache cache) {
        redisTemplate.opsForValue().set(cacheName, value);
    }
}
```
解析器通过上述方法即可定义完成。然后我们在使用`@Cache`注解的时候，只需要指定下`resolver`属性即可使用对应的解析器，resolver的值为注入到容器内的解析器的名称。
```
@Cache(resolver = "customCacheProcessor")
public Set<String> testSet() {
    System.out.println("进入testSet.....");
    Set<String> list = new HashSet<>();
    list.add("3");
    list.add("4");
    return list;
}
```
**全局解析器的定义**
上面的方法介绍的是独立解析器，如果我们想要将这个方法设置为全局的解析器，我们可以实现CacheProcessor接口的方法，
```
default boolean support(Object target, MethodSignature method, Object[] args) {
    return false;
}
```
由于自定义解析器的优先级总是高于默认解析器，因此仅需让support方法返回true即可。

这里需要注意的一点是，系统在寻找解析器的时候，只要找到了一个满足的解析器，就不会继续找其他的，因此如果自定义全局解析器，需要注意support返回的判定。


