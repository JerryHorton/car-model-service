package cn.cug.sxy.infrastructure.local.excel;

import cn.cug.sxy.domain.part.model.entity.PartBindHourExcelData;

/**
 * @version 1.0
 * @Date 2025/9/3
 * @Description 备件工时数据验证器
 * @Author jerryhotton
 */
public class PartHourDataValidator {

    /**
     * 验证备件工时Excel数据
     *
     * @param data 备件工时数据
     * @return 错误信息，null表示验证通过
     */
    public static String validatePartHourData(PartBindHourExcelData data) {
        // 验证备件编码
        String partCodeError = ExcelDataValidator.validateRequired(data.getPartCode(), "备件编码");
        if (partCodeError != null) {
            return partCodeError;
        }

        // 验证工时代码
        String hourCodeError = ExcelDataValidator.validateRequired(data.getHourCode(), "工时代码");
        if (hourCodeError != null) {
            return hourCodeError;
        }

        return null; // 验证通过
    }

}