package com.lijun.config;

import com.lijun.common.JacksonObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /*
    * 扩展mvc的消息转换器-->将对象转换成json对象(扩展)
    * */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();

        //设置扩展的消息转换器对象，mvc默认底层用jackson将对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());

        //将扩展的消息转换器对象追加到mvc框架当中，并且设置优先使用扩展的消息转换器
        converters.add(0,messageConverter);
    }
}
