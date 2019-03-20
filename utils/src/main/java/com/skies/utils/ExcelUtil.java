package com.skies.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by SKIES on 2018/8/20.
 */
public class ExcelUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * @param @param  is
     * @param @param  excelFileName
     * @param @return
     * @param @throws IOException
     * @return Workbook
     * @throws
     * @Title: createWorkbook
     * @Description: 判断excel文件后缀名，生成不同的workbook
     */
    public static Workbook createWorkbook(InputStream is, String excelFileName) throws IOException {
        if (excelFileName.endsWith(".xls")) {
            return new HSSFWorkbook(is);
        } else if (excelFileName.endsWith(".xlsx")) {
            return new XSSFWorkbook(is);
        }
        return null;
    }

    /**
     * @param @param  workbook
     * @param @param  sheetIndex
     * @param @return
     * @return Sheet
     * @throws
     * @Title: getSheet
     * @Description: 根据sheet索引号获取对应的sheet
     */
    public static Sheet getSheet(Workbook workbook, int sheetIndex) {
        return workbook.getSheetAt(0);
    }

    /**
     * 将上传的excel转成list
     *
     * @param multipartFile //直接上传的文件格式
     * @param excelFileName // 上传excel名字
     * @return
     */
    public static List<JSONObject> importDataFromExcel(MultipartFile multipartFile, String excelFileName) {
        List<JSONObject> List = new ArrayList<JSONObject>();
        InputStream is = null;
        JSONObject json = new JSONObject(true);
        try {
            is = multipartFile.getInputStream();
            //创建工作簿
            Workbook workbook = createWorkbook(is, excelFileName);
            //创建工作表sheet
            Sheet sheet = getSheet(workbook, 0);
            //获取sheet中数据的行数
            int rows = sheet.getPhysicalNumberOfRows();
            //获取表头单元格个数
            int cells = sheet.getRow(0).getPhysicalNumberOfCells();
            for (int i = 0; i < rows; i++) {
                //第一行为标题栏，从第二行开始取数据
                Row row = sheet.getRow(i);
                JSONObject jsonObject = new JSONObject();
                int index = 0;
                while (index < cells) {
                    Cell cell = row.getCell(index);
                    if (null == cell) {
                        cell = row.createCell(index);
                    }
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    String value = null == cell.getStringCellValue() ? "" : cell.getStringCellValue();
                    jsonObject.put(index + "", value);
                    index++;
                }
                List.add(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                //关闭流
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return List;
    }

    /**
     * @param @param  object
     * @param @return
     * @return boolean
     * @throws
     * @Title: isHasValues
     * @Description: 判断一个对象所有属性是否有值，如果一个属性有值(分空)，则返回true
     */
    public static Boolean isHasValues(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        Boolean flag = false;
        for (int i = 0; i < fields.length; i++) {
            String fieldName = fields[i].getName();
            String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method getMethod;
            try {
                getMethod = object.getClass().getMethod(methodName);
                Object obj = getMethod.invoke(object);
                if (null != obj && !"".equals(obj)) {
                    flag = true;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }


    /**
     * 导出
     *
     * @param request
     * @param response
     * @param list
     * @param head
     * @param title
     * @throws IOException
     */
    public static void export(HttpServletRequest request, HttpServletResponse response, List<Map<String, Object>> list,
                              String head, String title, int numRow, String width) throws IOException {
        String[] heads = head.split(",");
        HSSFWorkbook workbook = new HSSFWorkbook();

        //生成一个表格
        HSSFSheet sheet = workbook.createSheet(title);
        //设置表格默认列宽15个字节
        if(width==null||"".equals(width)){
            sheet.setDefaultColumnWidth(20);
        }
        //生成一个样式
        HSSFCellStyle style = getCellStyle(workbook);
        //生成一个字体
        HSSFFont font = getFont(workbook);
        //把字体应用到当前样式
        style.setFont(font);
        //第一行合并
        if(numRow==1){
            Row row = sheet.createRow((short) 0);
            Cell cell = row.createCell((short) 0);
            cell.setCellValue(title);
            //合并单元格
            sheet.addMergedRegion(new CellRangeAddress(0,0,0,heads.length-1));
        }else if(numRow == 0){

        }

        //生成表格标题
        HSSFRow row = sheet.createRow(numRow);
        row.setHeight((short) 300);
        HSSFCell cell = null;
        int i = 0;
        String[] split = width.split(",");
        style.setWrapText(true);
        for (String key : heads) {
//          sheet.setColumnWidth(split[i]);
            cell = row.createCell(i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(key);
            cell.setCellValue(text);
            i++;
        }
        if (list.size() > 0) {
            Map<String, Object> stringObjectMap = list.get(0);
            Set<String> strings = stringObjectMap.keySet();
            //将数据放入sheet中
            for (int m = 0+numRow; m < list.size(); m++) {
                row = sheet.createRow(m + 1);
                Map<String, Object> stringObjectMap1 = list.get(m);
                int j = 0;
                for (String key : strings) {
                    cell = row.createCell(j);
                    String value = "";
                    try {
                        value = stringObjectMap1.get(key).toString();
                    } catch (Exception e) {

                    }
                    if (null == value)
                        value = "";
                    cell.setCellValue(value.toString());
                    j++;
                }
            }
        }
//        System.out.println(System.currentTimeMillis()+"=="+list.size());
//        response.setContentType("application/x-download;charset=UTF-8");// // 指定文件的保存类型。
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");// // 指定文件的保存类型。
//        response.setCharacterEncoding("ISO-8859-1");
        response.setCharacterEncoding("UTF-8");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String fileName = title + sdf.format(date);
        response.setHeader("content-disposition", "attachment;filename=" + new String(fileName.getBytes("gb2312"), "ISO8859-1") + ".xls");
        try {
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出excel
     *
     * @param request
     * @param response
     * @param header
     * @param title
     * @throws IOException
     */
    public static void exportDataToExcel(HttpServletRequest request, HttpServletResponse response, String[] header, String title) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        //生成一个表格
        HSSFSheet sheet = workbook.createSheet("模板");
        //设置表格默认列宽15个字节
        sheet.setDefaultColumnWidth(20);
        //生成一个样式
        HSSFCellStyle style = getCellStyle(workbook);
        //生成一个字体
        HSSFFont font = getFont(workbook);
        //把字体应用到当前样式
        style.setFont(font);
        //生成表格标题
        HSSFRow row = sheet.createRow(0);
        row.setHeight((short) 300);
        HSSFCell cell = null;
        int i = 0;
        style.setWrapText(true);
        for (String key : header) {
            cell = row.createCell(i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(key);
            cell.setCellValue(text);
            i++;
        }
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        // // 指定文件的保存类型。
        //        response.setCharacterEncoding("ISO-8859-1");
        response.setCharacterEncoding("UTF-8");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String fileName = title + "模板" + sdf.format(date);
        //导致中文消失
        //response.setHeader("Content-Disposition", "attachment;filename="
        //               + fileName + ".xls");
        //正常显示
        response.setHeader("content-disposition", "attachment;filename=" + new String(fileName.getBytes("gb2312"), "ISO8859-1") + ".xls");
        try {
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param @param  workbook
     * @param @return
     * @return HSSFCellStyle
     * @throws
     * @Title: getCellStyle
     * @Description: 获取单元格格式
     */
    public static HSSFCellStyle getCellStyle(HSSFWorkbook workbook) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setLeftBorderColor(HSSFCellStyle.BORDER_THIN);
        style.setRightBorderColor(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setWrapText(true);
        return style;
    }

    /**
     * @param @param  workbook
     * @param @return
     * @return HSSFFont
     * @throws
     * @Title: getFont
     * @Description: 生成字体样式
     */
    public static HSSFFont getFont(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.WHITE.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        return font;
    }

    public static Boolean isIE(HttpServletRequest request) {
        return request.getHeader("USER-AGENT").toLowerCase().indexOf("msie") > 0 ? true : false;
    }

    public static String getRandom() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSS");
        String str = sdf.format(date).toString() + (int) (Math.random() * 10000);
        return str;
    }

    /**
     * @Title: importDataFromExcel
     * @Description: 将sheet中的数据保存到list中，
     * 1、调用此方法时，vo的属性个数必须和excel文件每行数据的列数相同且一一对应，vo的所有属性都为String
     * 2、在action调用此方法时，需声明
     *     private File excelFile;上传的文件
     *     private String excelFileName;原始文件的文件名
     * 3、页面的file控件name需对应File的文件名
     * @param @param vo javaBean
     * @param @param is 输入流
     * @param @param excelFileName
     * @param @return
     * @return List<Object>
     * @throws
     */
    public static List<Object> importDataFromExcel(Object vo, InputStream is, String excelFileName){
        List<Object> list = new ArrayList<Object>();
        try {
            //创建工作簿
            Workbook workbook = createWorkbook(is, excelFileName);
            //创建工作表sheet
            Sheet sheet = getSheet(workbook, 0);
            //获取sheet中数据的行数
            int rows = sheet.getPhysicalNumberOfRows();
            //获取表头单元格个数
            int cells = sheet.getRow(0).getPhysicalNumberOfCells();
            //利用反射，给JavaBean的属性进行赋值
            Field[] fields = vo.getClass().getDeclaredFields();
            Object temp = vo.getClass().getConstructor(new Class[]{}).newInstance(new Object[]{});//存放导入的数据
            for (int i = 1; i < rows; i++) {//第一行为标题栏，从第二行开始取数据
                Row row = sheet.getRow(i);
                int index = 0;
                while (index < cells) {
                    Cell cell = row.getCell(index);
                    if (null == cell) {
                        cell = row.createCell(index);
                    }
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    String value = null == cell.getStringCellValue()?"":cell.getStringCellValue();

                    Field field = fields[index];
                    String fieldName = field.getName();
                    String methodName = "set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
                    Method setMethod = vo.getClass().getMethod(methodName, new Class[]{String.class});
                    //setMethod.invoke(vo, new Object[]{value});
                    setMethod.invoke(temp, new Object[]{value});
                    index++;
                }
                //if (isHasValues(vo)) {//判断对象属性是否有值
                if (isHasValues(temp)) {//判断对象属性是否有值
                    //list.add(vo);
                    //vo.getClass().getConstructor(new Class[]{}).newInstance(new Object[]{});//重新创建一个vo对象,经测试无法重建新的对象
                    list.add(temp);
                    temp = vo.getClass().getConstructor(new Class[]{}).newInstance(new Object[]{});
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            e.printStackTrace();
        }finally{
            try {
                is.close();//关闭流
            } catch (Exception e) {
                logger.info(e.getMessage());
                e.printStackTrace();
            }
        }
        return list;

    }
}
