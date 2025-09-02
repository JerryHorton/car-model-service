package cn.cug.sxy.domain.usage.model.entity;

import cn.cug.sxy.domain.usage.model.valobj.UsageId;
import cn.cug.sxy.types.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/5 19:43
 * @Description 用法实体
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsageEntity {

    /**
     * 用法ID
     */
    private UsageId id;
    /**
     * 用法名称
     */
    private String usageName;
    /**
     * 爆炸图URL
     */
    private String explodedViewImg;
    /**
     * 下载URL
     */
    private String downloadUrl;
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
     * 创建用法
     *
     * @param usageName       用法名称
     * @param explodedViewImg 爆炸图URL
     * @param creator         创建人
     * @return 用法实体
     */
    public static UsageEntity create(String usageName, String explodedViewImg, String creator) {
        return UsageEntity.builder()
                .usageName(usageName)
                .explodedViewImg(explodedViewImg)
                .status(Status.ENABLED)
                .creator(creator)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    /**
     * 更新用法
     *
     * @param usageName       用法名称
     * @param explodedViewImg 爆炸图URL
     */
    public void update(String usageName, String explodedViewImg) {
        this.usageName = usageName;
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
     * 删除用法
     */
    public void delete() {
        this.status = Status.DELETED;
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 检查是否可用
     *
     * @return 是否可用
     */
    public boolean isEnabled() {
        return Status.ENABLED.equals(this.status);
    }

}
