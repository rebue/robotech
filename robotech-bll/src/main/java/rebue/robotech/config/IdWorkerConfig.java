package rebue.robotech.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
// XXX 启用属性类(也就是注入属性类，如果没有这一行，属性类要另外写注入，如在属性类上加注解@Compenent，或扫描)
@EnableConfigurationProperties(IdWorkerProperties.class)
public class IdWorkerConfig {
}