package com.liuzd.soft.config;

import com.liuzd.soft.interceptor.MyWebInterceptor;
import com.liuzd.soft.service.impl.LoginServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 */
@Configuration
@AutoConfigureAfter({DynamicDataSourceConfig.class})
public class InterceptorConfig implements WebMvcConfigurer {

    private final DynamicDataSource dynamicDataSource;
    private final LoginServiceImpl loginServiceImpl;

    public InterceptorConfig(DynamicDataSource dynamicDataSource, LoginServiceImpl loginServiceImpl) {
        this.dynamicDataSource = dynamicDataSource;
        this.loginServiceImpl = loginServiceImpl;
    }

    /**
     * 配置拦截的请求，与不拦截的请求
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //addPathPatterns拦截的路径
        String[] addPathPatterns = {"/**"};
        //excludePathPatterns排除的路径
        String[] excludePathPatterns = {"/minio/**"};

        MyWebInterceptor myWebInterceptor = new MyWebInterceptor();
        myWebInterceptor.setDynamicDataSource(dynamicDataSource);
        myWebInterceptor.setLoginServiceImpl(loginServiceImpl);
        registry.addInterceptor(myWebInterceptor)
                .addPathPatterns(addPathPatterns)
                .excludePathPatterns(excludePathPatterns);

    }


}
