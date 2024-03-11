package rebue.robotech.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "idworker")
public class IdWorkerProperties {
    /**
     * 节点ID二进制的位数
     */
    private Integer nodeIdBits = 5;

    /**
     * 服务列表
     */
    Map<String, Svc> svces = new LinkedHashMap<>();

    @Data
    public static class Svc {
        /**
         * 节点ID二进制的位数
         */
        private Integer nodeIdBits;
    }
}
