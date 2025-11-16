package rebue.robotech.clone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class CloneConfig {
    @Bean
    public EnumConverter enumConverter() {
        return new EnumConverter();
    }

    @Bean
    public PostGisConverter postGisConverter() {
        return new PostGisConverter();
    }
}