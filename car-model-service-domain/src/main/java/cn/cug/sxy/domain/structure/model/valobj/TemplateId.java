package cn.cug.sxy.domain.structure.model.valobj;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @version 1.0
 * @Date 2025/7/28 15:26
 * @Description 车型结构树模板ID值对象
 * @Author jerryhotton
 */

@Getter
@ToString
@EqualsAndHashCode
public class TemplateId {

    private final Long id;

    public TemplateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Template ID cannot be null or less than or equal to 0");
        }
        this.id = id;
    }

}
