package com.tracy.agent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author tracy.
 * @create 2018-06-04 18:34
 **/
@Configuration
@EnableWebMvc
public class AppConfig extends WebMvcConfigurerAdapter {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        GsonHttpMessageConverter msgConverter = new GsonHttpMessageConverter();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        msgConverter.setGson(gson);
        converters.add(msgConverter);
    }
}
