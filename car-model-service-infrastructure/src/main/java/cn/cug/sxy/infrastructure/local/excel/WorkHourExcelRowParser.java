package cn.cug.sxy.infrastructure.local.excel;

import cn.cug.sxy.domain.workhour.model.valobj.WorkHourExcelData;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;

/**
 * @version 1.0
 * @Date 2025/9/2 11:45
 * @Description
 * @Author jerryhotton
 */

@Slf4j
public class WorkHourExcelRowParser implements ExcelRowParser<WorkHourExcelData> {

    @Override
    public WorkHourExcelData parseRow(Row row, int rowNumber) {
        try {
            // 读取各列数据
            String code = ExcelUtils.getCellValueAsString(row.getCell(0));           // A列：工时代码
            String description = ExcelUtils.getCellValueAsString(row.getCell(1));    // B列：工时描述
            String standardHours = ExcelUtils.getCellValueAsString(row.getCell(2));  // C列：标准工时
            String stepOrder = ExcelUtils.getCellValueAsString(row.getCell(3));      // D列：步骤顺序
            // 如果工时代码为空，跳过该行
            if (code == null || code.trim().isEmpty()) {
                return null;
            }

            return WorkHourExcelData.builder()
                    .code(code.trim())
                    .description(description != null ? description.trim() : "")
                    .standardHours(standardHours != null ? standardHours.trim() : "")
                    .stepOrder(stepOrder != null ? stepOrder.trim() : "")
                    .build();
        } catch (Exception e) {
            log.warn("读取第{}行数据失败: {}", rowNumber, e.getMessage());
            return null;
        }
    }

    @Override
    public int getColumnCount() {
        return 4; // 工时数据有4列
    }

}
