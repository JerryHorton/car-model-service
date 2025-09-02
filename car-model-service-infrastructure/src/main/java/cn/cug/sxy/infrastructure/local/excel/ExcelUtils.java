package cn.cug.sxy.infrastructure.local.excel;

import cn.cug.sxy.domain.workhour.model.valobj.WorkHourExcelData;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Date 2025/9/1 17:21
 * @Description Excel工具类
 * @Author jerryhotton
 */

@Slf4j
public class ExcelUtils {


    /**
     * 通用Excel读取方法 - 模板方法
     *
     * @param file      Excel文件
     * @param rowParser 行解析器
     * @param <T>       数据类型
     * @return 数据列表
     * @throws IOException 读取异常
     */
    public static <T> List<T> readExcel(MultipartFile file, ExcelRowParser<T> rowParser) throws IOException {
        List<T> dataList = new ArrayList<>();
        Workbook workbook = createWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        // 跳过标题行，从第二行开始读取数据
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            // 检查是否为空行
            if (isEmptyRow(row, rowParser.getColumnCount())) {
                continue;
            }
            T data = rowParser.parseRow(row, rowIndex + 1);
            if (data != null) {
                dataList.add(data);
            }
        }
        workbook.close();
        log.info("成功读取Excel文件，共{}条记录", dataList.size());
        return dataList;
    }

    /**
     * 创建Workbook对象
     *
     * @param file Excel文件
     * @return Workbook对象
     * @throws IOException 创建异常
     */
    private static Workbook createWorkbook(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.endsWith(".xlsx")) {
            return new XSSFWorkbook(file.getInputStream());
        } else if (fileName != null && fileName.endsWith(".xls")) {
            return new HSSFWorkbook(file.getInputStream());
        } else {
            throw new IllegalArgumentException("不支持的文件格式，只支持.xlsx和.xls文件");
        }
    }

    /**
     * 检查是否为空行
     *
     * @param row         Excel行
     * @param columnCount 需要检查的列数
     * @return 是否为空行
     */
    private static boolean isEmptyRow(Row row, int columnCount) {
        if (row == null) {
            return true;
        }
        for (int i = 0; i < columnCount; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && !getCellValueAsString(cell).trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * 获取单元格值作为字符串
     *
     * @param cell Excel单元格
     * @return 字符串值
     */
    public static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // 避免科学计数法，转换为整数或保留小数
                    double value = cell.getNumericCellValue();
                    if (value == (long) value) {
                        return String.valueOf((long) value);
                    } else {
                        return String.valueOf(value);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getStringCellValue();
                }
            default:
                return null;
        }
    }

}
