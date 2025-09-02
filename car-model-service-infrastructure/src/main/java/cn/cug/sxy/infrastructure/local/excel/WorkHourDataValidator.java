package cn.cug.sxy.infrastructure.local.excel;

import cn.cug.sxy.domain.workhour.model.valobj.WorkHourExcelData;

/**
 * @version 1.0
 * @Date 2025/9/2 13:44
 * @Description 工时数据验证器
 * @Author jerryhotton
 */

public class WorkHourDataValidator {

    /**
     * 验证工时Excel数据
     *
     * @param data 工时数据
     * @return 错误信息，null表示验证通过
     */
    public static String validateWorkHourData(WorkHourExcelData data) {
        // 验证工时代码
        String codeError = ExcelDataValidator.validateRequired(data.getCode(), "工时代码");
        if (codeError != null) {
            return codeError;
        }
        // 验证工时描述
        String descriptionError = ExcelDataValidator.validateRequired(data.getDescription(), "工时描述");
        if (descriptionError != null) {
            return descriptionError;
        }
        // 验证标准工时
        String standardHoursError = ExcelDataValidator.validateNumeric(data.getStandardHours(), "标准工时");
        if (standardHoursError != null) {
            return standardHoursError;
        }
        // 验证步骤顺序
        String stepOrderError = ExcelDataValidator.validateInteger(data.getStepOrder(), "步骤顺序");
        if (stepOrderError != null) {
            return stepOrderError;
        }

        return null; // 验证通过
    }

}
