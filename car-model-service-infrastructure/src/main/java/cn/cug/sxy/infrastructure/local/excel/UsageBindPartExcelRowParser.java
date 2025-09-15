package cn.cug.sxy.infrastructure.local.excel;

import cn.cug.sxy.domain.part.model.entity.UsageBindPartExcelData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;

/**
 * @version 1.0
 * @Date 2025/9/5 09:49
 * @Description 用法绑定备件Excel行解析器
 * @Author jerryhotton
 */

@Slf4j
public class UsageBindPartExcelRowParser implements ExcelRowParser<UsageBindPartExcelData> {

    @Override
    public UsageBindPartExcelData parseRow(Row row, int rowNumber) {
        try {
            // 读取第一列数据
            String partCode = ExcelUtils.getCellValueAsString(row.getCell(0));
            // 读取第二列数据
            String count = ExcelUtils.getCellValueAsString(row.getCell(1));
            if (StringUtils.isEmpty(partCode) || StringUtils.isEmpty(count)) {
                return null;
            }

            return UsageBindPartExcelData.builder()
                    .partCode(partCode)
                    .count(count)
                    .build();
        } catch (Exception e) {
            log.warn("读取第{}行数据失败: {}", rowNumber, e.getMessage());
            return null;
        }

    }

    @Override
    public int getColumnCount() {
        return 2;
    }

}
