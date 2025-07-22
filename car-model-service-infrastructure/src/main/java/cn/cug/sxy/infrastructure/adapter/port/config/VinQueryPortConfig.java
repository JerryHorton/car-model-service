package cn.cug.sxy.infrastructure.adapter.port.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @Date 2025/7/22 17:55
 * @Description VIN码查询适配端口配置
 * @Author jerryhotton
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "vin.query")
public class VinQueryPortConfig {

    /**
     * 是否启用VIN码查询功能
     */
    private boolean enabled = true;
    /**
     * 默认适配端口名称
     */
    private String defaultPort;
    /**
     * 适配器配置
     */
    private Map<String, PortConfig> ports = new HashMap<>();

    /**
     * 适配端口配置
     */
    @Data
    public static class PortConfig {

        /**
         * 是否启用该适配器
         */
        private boolean enabled = true;
        /**
         * 适配器优先级，值越小优先级越高
         */
        private int priority = 100;
        /**
         * 适配端口基础URL
         */
        private String baseUrl;

    }

}
