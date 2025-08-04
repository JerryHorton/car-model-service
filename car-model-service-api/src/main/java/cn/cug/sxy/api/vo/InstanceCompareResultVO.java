package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/31 16:29
 * @Description 比较结果VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstanceCompareResultVO {

    /**
     * 新增的节点
     */
    private List<InstanceNodeDiffVO> added;
    /**
     * 删除的节点
     */
    private List<InstanceNodeDiffVO> removed;
    /**
     * 修改的节点
     */
    private List<InstanceNodeDiffVO> modified;
    /**
     * 新增的节点数量
     */
    private Integer totalAdded;
    /**
     * 删除的节点数量
     */
    private Integer totalRemoved;
    /**
     * 修改的节点数量
     */
    private Integer totalModified;

}
