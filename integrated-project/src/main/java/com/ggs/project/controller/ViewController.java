package com.ggs.project.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author Starbug
 * @Date 2020/9/5 22:08
 */
@Configuration
public class ViewController implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("back/index.html").setViewName("back/index");
        registry.addViewController("front/index.html").setViewName("front/index");


    }
}
