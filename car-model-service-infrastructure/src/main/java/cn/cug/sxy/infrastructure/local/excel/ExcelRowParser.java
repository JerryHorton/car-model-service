package cn.cug.sxy.infrastructure.local.excel;

import org.apache.poi.ss.usermodel.Row;

/**
 * @version 1.0
 * @Date 2025/9/2 11:09
 * @Description Excel行解析器接口
 * @Author jerryhotton
 */

public interface ExcelRowParser<T> {

    /**
     * 解析Excel行数据
     *
     * @param row       Excel行
     * @param rowNumber 行号（从1开始）
     * @return 解析后的数据对象
     */
    T parseRow(Row row, int rowNumber);

    /**
     * 获取需要检查的列数（用于空行判断）
     *
     * @return 列数
     */
    int getColumnCount();

}
