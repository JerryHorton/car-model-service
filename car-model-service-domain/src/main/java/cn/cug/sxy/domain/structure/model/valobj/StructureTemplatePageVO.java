package cn.cug.sxy.domain.structure.model.valobj;

import cn.cug.sxy.domain.structure.model.entity.StructureTemplateEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @version 1.0
 * @Date 2025/7/29 17:39
 * @Description
 * @Author jerryhotton
 */

@Getter
@Builder
public class StructureTemplatePageVO {

    /**
     * 原始模版数据
     */
    private List<StructureTemplateEntity> templates;
    /**
     * 总条数
     */
    private Long total;
    /**
     * 总页数
     */
    private Integer totalPages;
    /**
     * 当前页码
     */
    private Integer currentPage;

}
