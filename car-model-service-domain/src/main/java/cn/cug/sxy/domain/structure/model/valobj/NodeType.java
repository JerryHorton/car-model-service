package cn.cug.sxy.domain.structure.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @version 1.0
 * @Date 2025/7/28 15:35
 * @Description
 * @Author jerryhotton
 */

@Getter
@AllArgsConstructor
public enum NodeType {

    CATEGORY("CATEGORY", "系统大类"),
    GROUP("GROUP", "系统分组"),
    USAGE("USAGE", "用法");

    private final String code;
    private final String info;

    public static NodeType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (NodeType nodeType : NodeType.values()) {
            if (nodeType.getCode().equals(code)) {
                return nodeType;
            }
        }
        throw new IllegalArgumentException("未知的节点类型编码: " + code);
    }

}
