package rebue.robotech.beansearch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class BeanSearcherConfig {
    @Bean
    public EnumToByteConvertor enumToByteConvertor() {
        return new EnumToByteConvertor();
    }
}
