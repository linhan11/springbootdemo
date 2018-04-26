package jp.co.saison.tvc.springbootdemo.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/chat").setViewName("chat");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/users").setViewName("users/index");
        registry.addViewController("/login").setViewName("login");
    }

}
