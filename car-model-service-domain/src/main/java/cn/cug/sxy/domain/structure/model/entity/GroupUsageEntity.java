package cn.cug.sxy.domain.structure.model.entity;

import cn.cug.sxy.domain.structure.model.valobj.Status;
import cn.cug.sxy.domain.structure.model.valobj.UsageId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/4 15:59
 * @Description 统分组用法实体
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupUsageEntity {

    /**
     * 用法ID
     */
    private UsageId id;
    /**
     * 归属系统分组ID
     */
    private Long groupId;
    /**
     * 用法ID（业务ID）
     */
    private Long usageId;
    /**
     * 用法名称
     */
    private String usageName;
    /**
     * 爆炸图URL
     */
    private String explodedViewImg;
    /**
     * 状态
     */
    private Status status;
    /**
     * 创建人
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

    /**
     * 创建用法实体
     *
     * @param groupId         归属系统分组ID
     * @param usageId         用法ID（业务ID）
     * @param usageName       用法名称
     * @param explodedViewImg 爆炸图URL
     * @param creator         创建人
     * @return 用法实体
     */
    public static GroupUsageEntity create(Long groupId, Long usageId, String usageName,
                                          String explodedViewImg, String creator) {
        // 参数校验
        if (groupId == null || groupId <= 0) {
            throw new IllegalArgumentException("系统分组ID不能为空或小于等于0");
        }
        if (usageId == null || usageId <= 0) {
            throw new IllegalArgumentException("用法ID不能为空或小于等于0");
        }
        if (StringUtils.isBlank(usageName)) {
            throw new IllegalArgumentException("用法名称不能为空");
        }
        if (usageName.length() > 128) {
            throw new IllegalArgumentException("用法名称长度不能超过128个字符");
        }
        if (StringUtils.isBlank(creator)) {
            throw new IllegalArgumentException("创建人不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        return GroupUsageEntity.builder()
                .groupId(groupId)
                .usageId(usageId)
                .usageName(usageName)
                .explodedViewImg(explodedViewImg)
                .status(Status.ENABLED)
                .creator(creator)
                .createdTime(now)
                .updatedTime(now)
                .build();
    }

    /**
     * 更新用法信息
     *
     * @param usageName       用法名称
     * @param explodedViewImg 爆炸图URL
     */
    public void update(String usageName, String explodedViewImg) {
        if (StringUtils.isNotBlank(usageName)) {
            if (usageName.length() > 128) {
                throw new IllegalArgumentException("用法名称长度不能超过128个字符");
            }
            this.usageName = usageName;
        }
        this.explodedViewImg = explodedViewImg;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 启用用法
     */
    public void enable() {
        this.status = Status.ENABLED;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 禁用用法
     */
    public void disable() {
        this.status = Status.DISABLED;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 更新状态
     *
     * @param status 状态
     */
    public void updateStatus(Status status) {
        if (status != null) {
            this.status = status;
            this.updatedTime = LocalDateTime.now();
        }
    }

}
