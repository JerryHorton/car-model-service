package cn.cug.sxy.infrastructure.local.excel;

import cn.cug.sxy.types.enums.ResponseCode;
import cn.cug.sxy.types.exception.AppException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;

/**
 * @version 1.0
 * @Date 2025/9/2 13:39
 * @Description 通用Excel数据验证器
 * @Author jerryhotton
 */

public class ExcelDataValidator {

    /**
     * 验证Excel数据列表
     *
     * @param dataList  数据列表
     * @param validator 验证函数
     * @param <T>       数据类型
     * @throws AppException 验证失败异常
     */
    public static <T> void validateDataList(List<T> dataList, Function<T, String> validator) {
        if (dataList == null || dataList.isEmpty()) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "Excel文件中没有有效数据");
        }
        for (int i = 0; i < dataList.size(); i++) {
            T data = dataList.get(i);
            String errorMessage = validator.apply(data);
            if (StringUtils.isNotBlank(errorMessage)) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),
                        String.format("第%d行数据验证失败: %s", i + 2, errorMessage)); // +2因为跳过了标题行
            }
        }
    }

    /**
     * 验证字符串字段不为空
     *
     * @param value     字段值
     * @param fieldName 字段名称
     * @return 错误信息，null表示验证通过
     */
    public static String validateRequired(String value, String fieldName) {
        if (StringUtils.isBlank(value)) {
            return fieldName + "不能为空";
        }
        return null;
    }

    /**
     * 验证数字字段
     *
     * @param value     字段值
     * @param fieldName 字段名称
     * @return 错误信息，null表示验证通过
     */
    public static String validateNumeric(String value, String fieldName) {
        String requiredError = validateRequired(value, fieldName);
        if (requiredError != null) {
            return requiredError;
        }
        try {
            Double.parseDouble(value);
            return null;
        } catch (NumberFormatException e) {
            return fieldName + "必须是有效的数字";
        }
    }

    /**
     * 验证整数字段
     *
     * @param value     字段值
     * @param fieldName 字段名称
     * @return 错误信息，null表示验证通过
     */
    public static String validateInteger(String value, String fieldName) {
        String requiredError = validateRequired(value, fieldName);
        if (requiredError != null) {
            return requiredError;
        }
        try {
            Integer.parseInt(value);
            return null;
        } catch (NumberFormatException e) {
            return fieldName + "必须是有效的整数";
        }
    }

}
