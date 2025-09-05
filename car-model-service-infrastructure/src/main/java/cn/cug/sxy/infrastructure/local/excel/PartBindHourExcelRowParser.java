package cn.cug.sxy.infrastructure.local.excel;

import cn.cug.sxy.domain.part.model.entity.PartBindHourExcelData;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;

/**
 * @version 1.0
 * @Date 2025/9/3 14:42
 * @Description 备件绑定工时Excel解析器
 * @Author jerryhotton
 */

@Slf4j
public class PartBindHourExcelRowParser implements ExcelRowParser<PartBindHourExcelData> {

    @Override
    public PartBindHourExcelData parseRow(Row row, int rowNumber) {
        try {
            // 读取第一列数据：备件编码
            String partCode = ExcelUtils.getCellValueAsString(row.getCell(0));
            // 读取第二列数据：工时代码
            String hourCode = ExcelUtils.getCellValueAsString(row.getCell(1));
            // 如果备件编码或工时代码为空，跳过该行
            if (partCode == null || partCode.trim().isEmpty() ||
                    hourCode == null || hourCode.trim().isEmpty()) {
                return null;
            }

            return PartBindHourExcelData.builder()
                    .partCode(partCode.trim())
                    .hourCode(hourCode.trim())
                    .build();
        } catch (Exception e) {
            log.warn("读取第{}行数据失败: {}", rowNumber, e.getMessage());
            return null;
        }
    }

    @Override
    public int getColumnCount() {
        return 2; // 需要读取两列（备件编码和工时代码）
    }

}
