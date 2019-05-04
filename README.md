# 基于数据库的表单登陆

## 基于username，password的认证方式，都会使用UsernamePasswordFilter来处理，以下内容，都是基于此过滤器的认证流程

### maven依赖
```xml
    
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-lang3</artifactId>
			<version>3.8.1</version>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<version>1.16.22</version>
		</dependency>
	</dependencies>
```
### 配置类
```java
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
    @Override
        protected void configure(HttpSecurity http) throws Exception {
    
            //根据需求来配置
    
            http
                    .addFilterBefore(ValidateCodeFilter(), UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()
                    .regexMatchers(
                            "/authentication/require",
                            webSecurityProperties.getLoginPage(),
                            "/code/image").permitAll()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/authentication/require")
                    .loginProcessingUrl("/auth/form")
                    .defaultSuccessUrl("/")
                    .successHandler(dbAuthenticationSuccessHandler())
                    .failureHandler(dbAuthenticationFailureHandler())
                    .and()
                    .csrf().disable()
            ;
    
        }
}
```
### 处理用户信息的获取逻辑UserDetailService
```java
@Slf4j
public class DbUserDetailService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("用户登陆验证：{}", username);
        User user = userMapper.findUserByUsername(username);
        if (user == null)
            return null;
        List<GrantedAuthority> roles = AuthorityUtils.createAuthorityList("admin");
        return new DbUserDetails(user.getId(), user.getUsername(), user.getPassword(), roles);
    }
}
```
### 处理用户校验的逻辑

```java
public class DbUserDetails extends User{

    @Getter
    private Long uid;

    public DbUserDetails(Long id,String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.uid = id;
    }

    public DbUserDetails(Long uid,String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.uid = uid;
    }

}
```

### 自定义登陆页面
```java
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(ValidateCodeFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()//请求认证
                .regexMatchers(
                        "/authentication/require",//处理认证的url
                        webSecurityProperties.getLoginPage(),//登陆的html页面
                        "/code/image"/*获取验证码图片的url*/).permitAll()//允许
                .anyRequest()//任何
                .authenticated()//需要认证
                .and()
                .formLogin()//使用表单登陆
                .loginPage("/authentication/require")//需要认证就转到/authentication/require；它在决定下一步如何处理
                .loginProcessingUrl("/auth/form")//表单提交的url,提交的数据被UsernamePasswordToken接收
                .defaultSuccessUrl("/")
                .successHandler(dbAuthenticationSuccessHandler())
                .failureHandler(dbAuthenticationFailureHandler())
                .and()
                .csrf().disable()
        ;

    }
```

### 自定义登陆失败处理器
```java
public class DbAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebSecurityProperties webSecurityProperties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        //根据登陆的处理方式，选择返回json，还是重定向
        if (webSecurityProperties.getLoginType().equals(LoginType.JSON)){
            httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            httpServletResponse.setContentType("application/json;charset=utf-8");
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(new SimpleExceptionResponse(e)));
        }else{
            super.onAuthenticationFailure(httpServletRequest,httpServletResponse,e);
        }
    }
}
```
### 自定义登录成功处理器

```java
public class DbAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebSecurityProperties webSecurityProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        if (webSecurityProperties.getLoginType().equals(LoginType.JSON)){
            httpServletResponse.setContentType("application/json;charset=utf-8");
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(authentication));
        }else {
            super.onAuthenticationSuccess(httpServletRequest,httpServletResponse,authentication);
        }
    }
}
```
### 自定义认证Controller

```java
@RestController
@Slf4j
public class WebSecurityController {

    private RequestCache requestCache = new HttpSessionRequestCache();

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();


    @Autowired
    private WebSecurityProperties webSecurityProperties;

    /**
     * 当需要身份认证时，跳转到这里
     * 如果是login请求就返回登陆页
     * 其他的就发送提醒返回状态吗401 HttpStatus.UNAUTHORIZED
     * @param request
     * @param response
     */
    @RequestMapping("authentication/require")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public SimpleResponse requireAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SavedRequest savedRequest = requestCache.getRequest(request,response);
        if (savedRequest!=null){
            String target = savedRequest.getRedirectUrl();
            log.info("引发跳转的请求是{}",target);
            String path = StringUtils.substringAfterLast(target, "/");
            if (StringUtils.equalsIgnoreCase("login",path)
                    || StringUtils.equalsIgnoreCase("login.html",path)){
                redirectStrategy.sendRedirect(request,response,webSecurityProperties.getLoginPage());
            }
        }

        return new SimpleResponse("访问的服务需要身份认证,请引导用户到登陆页");

    }
}

```

### 图形验证码
####### 发送
  * 创建图形验证码
  * 记录验证码到某处（本地是session）
  * 响应发送图片
 
```java
@RestController
public class ValidateCodeController {

    public static final String SESSION_KEY="SESSION_KEY_IMAGE";

    @Autowired
    private VerifyCodeUtil imgGenUtil;

    @Autowired
    private ImageProperty imageProperty;

    @GetMapping("code/image")
    public void createCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //得到图形验证码
        ImageCode imageCode = createImageCode(request);
        //写入session
        fillSession(request,SESSION_KEY,imageCode);
        // 设置返回内容
        response.setContentType("image/jpg");
        //图片写入响应
        ImageIO.write(imageCode.getImage(),"JPEG", response.getOutputStream());
    }

    private void fillSession(HttpServletRequest request, String sessionKey, Object obj) {
        request.getSession().setAttribute(sessionKey,obj);
    }

    private ImageCode createImageCode(HttpServletRequest request) {

        //创建对象
        String s = RandomStringUtils.randomAlphanumeric(imageProperty.getCharAmt());
        BufferedImage image = imgGenUtil.getVeriCodeImg(s);

        return new ImageCode(image,s,imageProperty.getExpireIn());
    }
}
```

####### 验证图形验证码

    * 拦截表单请求
    * 处理验证码信息，若正确放行，错误抛出异常并返回

```java
public class ValidateCodeFilter extends OncePerRequestFilter {
    @Setter
    @Getter
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private ImageProperty imageProperty;

    /**
     * 处理 请求
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //处理表单登陆请求
        if (StringUtils.equals("/auth/form",StringUtils.substringAfter(request.getRequestURI(),request.getContextPath()))
                && StringUtils.equalsIgnoreCase(request.getMethod(),"post")){
            try{
                validate(request);
            }catch (ValidateCodeException e){
                authenticationFailureHandler.onAuthenticationFailure(request,response,e);
                return;
            }

        }
            filterChain.doFilter(request,response);
    }

    /**
     * 从session中取出验证码来验证传入的验证码
     * @param request
     * @throws ValidateCodeException
     */
    private void validate(HttpServletRequest request) throws ValidateCodeException {
        HttpSession session = request.getSession();
        //取出图片
        ImageCode image = (ImageCode) session.getAttribute(ValidateCodeController.SESSION_KEY);
        //取出表单的验证码
        String vcode = request.getParameter(imageProperty.getInputImgName());

        if (vcode==null)
            throw new ValidateCodeException("验证码不存在");

        if (StringUtils.isBlank(vcode))
            throw new ValidateCodeException("验证码不能为空");

        if (image.isExpired()){
            session.removeAttribute(ValidateCodeController.SESSION_KEY);
            throw new ValidateCodeException("验证码已过期");
        }

        if (!StringUtils.equalsIgnoreCase(vcode,image.getCode()))
            throw new ValidateCodeException("验证码不匹配");

        session.removeAttribute(ValidateCodeController.SESSION_KEY);
    }
}
```
##### 将此过滤器添加到UsernamePasswordFilter过滤器的前边

```java
        http
                .addFilterBefore(ValidateCodeFilter(), UsernamePasswordAuthenticationFilter.class)
```





