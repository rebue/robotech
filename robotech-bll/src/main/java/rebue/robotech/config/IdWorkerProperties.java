package rebue.robotech.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rebue")
public class IdWorkerProperties {
    /**
     * idworker
     * 如果以"nodeId:"开头，且值为0~31，则是指定nodeId;
     * 如果以"auto"开头，则由zookeeper自动分配nodeId，nodeIdBits默认为5
     * 如果以"auto:"开头，则由zookeeper自动分配nodeId，"auto:"后面跟nodeIdBits的值
     * 如果不设置，则不使用zookeeper来计算id(仅用于开发或单机模式中)
     */
    private String idworker;
}
