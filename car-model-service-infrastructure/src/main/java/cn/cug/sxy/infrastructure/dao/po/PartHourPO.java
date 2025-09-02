package cn.cug.sxy.infrastructure.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @version 1.0
 * @Date 2025/8/27 14:04
 * @Description 备件工时持久化对象
 * @Author jerryhotton
 */

@Data
public class PartHourPO {

    /**
     * 自增ID
     */
    private Long id;
    /**
     * 备件ID
     */
    private Long partId;
    /**
     * 工时ID
     */
    private Long hourId;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

}
