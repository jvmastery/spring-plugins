# 系统安全模块

## 使用
通过`@EnableSecurity`注解开启系统安全模块配置。
```
@EnableSecurity
@Configuration
public class SecurityConfiguration {

}
```

## 配置用户信息获取
为了满足通过数据库获取用户信息，提供一个`UserDetailsService`的实现类。
```
@Component
@AllArgsConstructor
public class RedisUserDetailsService implements UserDetailsService {

    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("未找到对应的用户：" + username);
        }

        return new AuthorizationUser(user.getId(), user.getUsername(), user.getPassword(), new ArrayList<>());
    }
}
```

## 配置客户端密钥信息
```
@Component
@AllArgsConstructor
public class RedisRegisteredClientRepository implements RegisteredClientRepository {

    private ClientService clientService;

    @Override
    public void save(RegisteredClient registeredClient) {

    }

    @Override
    public RegisteredClient findById(String id) {
        return buildClient(clientService.findById(id));
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return buildClient(clientService.findByClientKey(clientId));
    }

    /**
     * 构建权限对象
     * @param client
     * @return
     */
    public RegisteredClient buildClient(Client client) {
        if (client == null) {
            return null;
        }

        return RegisteredClient.withId(String.valueOf(client.getId()))
                .clientId(client.getClientKey())
                .clientSecret(client.getClientSecret())
                .clientAuthenticationMethods(item -> item.addAll(ClientAuthenticationMethodBuilder.getMethod(client.getAuthenticationMethod())))
                .authorizationGrantTypes(item -> item.addAll(AuthorizationGrantTypeBuilder.getTypes(client.getGrantType())))
                .redirectUris(item -> {
                    if(!StringUtils.hasLength(client.getRedirectUri())) {
                        return;
                    }

                    item.addAll(Arrays.stream(client.getRedirectUri().split(VariableConstant.WORD_SEPARATOR)).toList());
                })
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
//               使用opaqueToken
                .tokenSettings(TokenSettings
                        .builder()
                        .accessTokenFormat(OAuth2TokenFormat.REFERENCE)
                        .accessTokenTimeToLive(Duration.ofSeconds(client.getTokenExpireSecond()))
                        .refreshTokenTimeToLive(Duration.ofSeconds(client.getRefreshTokenExpireSecond()))
                        .build()
                )
//                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();
    }
}
```

## 开放接口配置
在接口上标注 `@OpenApi` 注解标明改接口为开放接口，可以不通过认证直接访问。为了保证安全性，开放接口必须设置开放对应的ip。如：
```
@RequestMapping("user/info")
@OpenApi(ips = "*")
public User getUserInfo() {
    return null;
}
```
ips对应改接口开放的ip地址。可以使用通配符`*`，表示所有ip地址。  
除了单个指定外，系统支持自定义配置开放接口，只需注入一个`OpenIpCustomizer`即可。
```
@Bean
public OpenIpCustomizer openIpCustomizer() {
    return (request, handlerMethod) -> false;
}
```

注意：如果没有任何的开放ip匹配成功，即使设置了注解，对应的接口也仍需进行认证。