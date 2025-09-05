package cn.cug.sxy.domain.part.model.entity;

import cn.cug.sxy.domain.part.model.valobj.PartCode;
import cn.cug.sxy.domain.part.model.valobj.PartId;
import cn.cug.sxy.types.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/9/3
 * @Description 备件实体
 * @Author jerryhotton
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartEntity {

    /**
     * 备件ID
     */
    private PartId id;
    /**
     * 备件编码
     */
    private PartCode code;
    /**
     * 备件名称
     */
    private String name;
    /**
     * 状态
     */
    private Status status;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 创建备件
     */
    public static PartEntity create(
            PartCode code,
            String name,
            String creator,
            String remark) {
        // 验证必填字段
        if (code == null) {
            throw new IllegalArgumentException("备件编码不能为空");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("备件名称不能为空");
        }
        if (creator == null || creator.trim().isEmpty()) {
            throw new IllegalArgumentException("创建人不能为空");
        }

        return PartEntity.builder()
                .code(code)
                .name(name)
                .status(Status.ENABLED)
                .creator(creator)
                .remark(remark)
                .build();
    }

    /**
     * 更新备件信息
     */
    public void update(String name, String remark) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (remark != null) {
            this.remark = remark;
        }
    }

    /**
     * 启用备件
     */
    public void enable() {
        this.status = Status.ENABLED;
    }

    /**
     * 禁用备件
     */
    public void disable() {
        this.status = Status.DISABLED;
    }

    /**
     * 删除备件
     */
    public void delete() {
        this.status = Status.DELETED;
    }

}