package cn.cug.sxy.api.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/31 16:27
 * @Description 点差异VO
 * @Author jerryhotton
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstanceNodeDiffVO implements Serializable {

    /**
     * 节点编码
     */
    private String nodeCode;
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 节点类型
     */
    private String nodeType;
    /**
     * 差异类型：added, removed, modified
     */
    private String diffType;
    /**
     * 变更的字段列表，仅当diffType为modified时有值
     */
    private List<String> changedFields;

}
