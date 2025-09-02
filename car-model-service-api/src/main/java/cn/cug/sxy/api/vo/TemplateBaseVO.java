package cn.cug.sxy.api.vo;

import com.auth0.jwt.JWTCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/7/29 16:39
 * @Description 模板基本信息VO
 * @Author jerryhotton
 */

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class TemplateBaseVO implements Serializable {

    /**
     * 模板ID
     */
    private Long id;
    /**
     * 模板编码
     */
    private String templateCode;
    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 模板描述
     */
    private String templateDesc;
    /**
     * 模板版本
     */
    private String version;
    /**
     * 状态
     */
    private String status;
    /**
     * 创建者
     */
    private String creator;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

}
