package cn.cug.sxy.domain.system.model.entity;

import cn.cug.sxy.domain.system.model.valobj.CategoryId;
import cn.cug.sxy.domain.system.model.valobj.GroupCode;
import cn.cug.sxy.domain.system.model.valobj.GroupId;
import cn.cug.sxy.domain.system.model.valobj.GroupStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/7/28 08:46
 * @Description 系统分组实体
 * @Author jerryhotton
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemGroupEntity {

    /**
     * 系统分组ID
     */
    private GroupId groupId;
    /**
     * 归属系统大类ID
     */
    private CategoryId categoryId;
    /**
     * 系统分组编码（值对象）
     */
    private GroupCode groupCode;
    /**
     * 系统分组名称
     */
    private String groupName;
    /**
     * 系统分组英文名称
     */
    private String groupNameEn;
    /**
     * 排序顺序，用于界面展示排序
     */
    private Integer sortOrder;
    /**
     * 状态：启用、禁用、删除
     */
    private GroupStatus status;
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

    public static SystemGroupEntity create(CategoryId categoryId, GroupCode groupCode, String groupName,
                                           String groupNameEn, Integer sortOrder, String creator) {
        return SystemGroupEntity.builder()
                .categoryId(categoryId)
                .groupCode(groupCode)
                .groupName(groupName)
                .groupNameEn(groupNameEn)
                .sortOrder(sortOrder != null ? sortOrder : 0)
                .status(GroupStatus.ENABLED)
                .creator(creator)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    public void update(CategoryId categoryId, GroupCode groupCode, String groupName, String groupNameEn, Integer sortOrder) {
        this.categoryId = categoryId;
        this.groupCode = groupCode;
        this.groupName = groupName;
        this.groupNameEn = groupNameEn;
        if (sortOrder != null) {
            this.sortOrder = sortOrder;
        }
        this.updatedTime = LocalDateTime.now();
    }

    public void updateGroupCode(GroupCode groupCode) {
        this.groupCode = groupCode;
        this.updatedTime = LocalDateTime.now();
    }

    public void updateStatus(GroupStatus status) {
        this.status = status;
        this.updatedTime = LocalDateTime.now();
    }

    public void disable() {
        this.status = GroupStatus.DISABLED;
        this.updatedTime = LocalDateTime.now();
    }

    public void enable() {
        this.status = GroupStatus.ENABLED;
        this.updatedTime = LocalDateTime.now();
    }

}
