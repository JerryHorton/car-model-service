package cn.cug.sxy.infrastructure.local.excel;

import cn.cug.sxy.domain.part.model.entity.UsageBindPartExcelData;

/**
 * @version 1.0
 * @Date 2025/9/5 09:54
 * @Description 用法备件数验证器
 * @Author jerryhotton
 */

public class UsagePartDataValidator {

    /**
     * 验证用法备件数据
     *
     * @param data 用法备件数据
     * @return 错误信息，null表示验证通过
     */
    public static String validateUsagePartData(UsageBindPartExcelData data) {
        // 验证备件编码
        String codeError = ExcelDataValidator.validateRequired(data.getPartCode(), "备件编码");
        if (codeError != null) {
            return codeError;
        }
        // 验证数量
        String quantityError = ExcelDataValidator.validateInteger(data.getCount(), "数量");
        if (quantityError != null) {
            return quantityError;
        }

        return null; // 验证通过
    }

}
