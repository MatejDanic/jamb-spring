package matej.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration Class that implements the {@link WebMvcConfigurer} interface
 *
 * @author MatejDanic
 * @version 1.0
 * @since 2020-08-20
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    
    /**
	 * This method overrides the addCorsMappings super method and makes POST, GET, PUT and DELETE METHODS available to the cors registry.
	 *
	 * @param registry the cors registry
	 */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowCredentials(true)               
        .allowedHeaders("Authorization", "Cache-Control", "Content-Type", "Accept", "X-Requested-With", "Access-Control-Allow-Origin", "Access-Control-Allow-Headers", "Origin")
        .exposedHeaders("Access-Control-Expose-Headers", "Authorization", "Cache-Control", "Content-Type", "Access-Control-Allow-Origin", "Access-Control-Allow-Headers", "Origin")
        .allowedMethods("POST", "GET", "PUT", "DELETE");
    }
}