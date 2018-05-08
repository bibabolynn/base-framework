package com.yuan.util.office;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yuan.util.BaseException;
import com.yuan.util.JavaUtil;
import com.yuan.util.LogUtil;
import com.yuan.util.constant.PubErrorCode;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelOperate {
    /**读取excel，返回每行的信息集合
     * 备注：文件格式为：xlsx
     * @param file ： excel文件
     * @param ignoreRows：读取数据忽略的行数[从1开始]
     * @param isTitle:是否读取标题【暂不支持，预留】
     * @return Map<Integer,List<Map<Integer, String>>>
     * 备注：第一个Integer：是sheet所在的位置【从1开始】
     *      Map<Integer, String>：是其sheet的行信息，其中的Integer某行是列数【从1开始】，String是某行列值
     * @throws BaseException
     */
    public static Map<Integer,List<Map<Integer, String>>> getDataXlsx(File file, int ignoreRows, boolean isTitle)throws BaseException {
        BufferedInputStream buffIn = null;
        Map<Integer,List<Map<Integer, String>>> dataMap = new HashMap<Integer, List<Map<Integer,String>>>();
        try{
            /*
             * 1、验证文件类型是否为excel
             */
            if(file!=null){
                int excelType = readExcelType(file.getPath());
                if(excelType==0){
                    return dataMap;
                }
            }else{
                return dataMap;
            }
            /*
             * 2、获取excel的sheet个数，挨个sheet读取数据
             */
            buffIn = new BufferedInputStream(new FileInputStream(file));
            XSSFWorkbook wb = new XSSFWorkbook(buffIn);// 打开XSSFWorkbook
            int sheetNum = wb.getNumberOfSheets(); //获取sheet个数
            for (int sheetIndex = 0; sheetIndex < sheetNum; sheetIndex++) {
                /*
                 * 3、获取sheet的所有行数
                 * 备注：从忽略的行数开始，到结束
                 */
                List<Map<Integer, String>> result = new ArrayList<Map<Integer, String>>(); //用于存放sheet的行数信息
                XSSFSheet st = wb.getSheetAt(sheetIndex); //获取sheet
                int rowNum = st.getLastRowNum();
                ignoreRows = ignoreRows>0?(ignoreRows-1):0;
                for (int rowline = ignoreRows; rowline <= rowNum; rowline++) {
                    /*
                     * 4、获取每行的所有列，循环所有列
                     */
                    XSSFRow row = st.getRow(rowline);
                    int columnSize = row.getLastCellNum();
                    Map<Integer,String> cellMap = new HashMap<Integer, String>();
                    for (int columnline = 0; columnline < columnSize; columnline++) {
                        XSSFCell cell = row.getCell(columnline); //获取某一列数据
                        String value = getCellValue(cell); //获取某一列值
                        cellMap.put((columnline+1), value); //列数作为key，列值作为value
                    }
                    result.add(cellMap);
                }
                dataMap.put((sheetIndex+1), result);
            }
        }catch(Exception e){
            throw LogUtil.handerEx(PubErrorCode.PUB_EXCEL_ANALYSIS, "解析excel失败", LogUtil.EMPTY, e);
        }finally{
            if(buffIn!=null){
                try {
                    buffIn.close();
                } catch (Exception e) {
                    throw LogUtil.handerEx(PubErrorCode.PUB_EXCEL_ANALYSIS, "关闭excel工具类流失败", LogUtil.EMPTY, e);
                }
            }
        }
        return dataMap;
    }
    
    /**获取excel某一列的值，根据类类型不同取值不同
     * @param cell:excel的一列对象
     * @return
     * @throws BaseException
     */
    public static String getCellValue(XSSFCell cell) throws BaseException{
        String value = "";
        try{
            if (cell != null) {
                /*
                 *  根据不同数据类型，取值方式不同
                 *  CELL_TYPE_NUMERIC 数值型 0
                    CELL_TYPE_STRING 字符串型 1
                    CELL_TYPE_FORMULA 公式型 2
                    CELL_TYPE_BLANK 空值 3
                    CELL_TYPE_BOOLEAN 布尔型 4
                    CELL_TYPE_ERROR 错误 5
                 */
                switch (cell.getCellType()) {
                    case XSSFCell.CELL_TYPE_NUMERIC: //0：数值
                        if (HSSFDateUtil.isCellDateFormatted(cell)) {
                            Date date = cell.getDateCellValue();
                            if (date != null) {
                                value = new SimpleDateFormat("yyyyMMdd").format(date);
                            } else {
                                value = "";
                            }
                        } else {
                        	DecimalFormat format = new DecimalFormat("#.#######");
                            value = format.format(cell.getNumericCellValue());
                        }
                        break;
                    case XSSFCell.CELL_TYPE_STRING: //1:字符串
                        value = cell.getStringCellValue();
                        break;
                    case XSSFCell.CELL_TYPE_FORMULA: //2:公式
                        // 导入时如果为公式生成的数据则无值
                        if (!cell.getStringCellValue().equals("")) {
                            value = cell.getStringCellValue();
                        } else {
                            value = cell.getNumericCellValue() + "";
                        }
                        break;
                    case XSSFCell.CELL_TYPE_BOOLEAN://4:布尔值
                        value = (cell.getBooleanCellValue() == true ? "Y" : "N");
                        break;
                    default:
                        value = "";
                        break;
                }
                rightTrim(value);
            }
        }catch(Exception e){
            throw LogUtil.handerEx(PubErrorCode.PUB_EXCEL_ROW, "读取excel的列值失败", LogUtil.EMPTY, e);
        }
        return value;
    }
	/**
	 * 去掉字符串右边的空格
	 * @param str 要处理的字符串
	 * @return 处理后的字符串
	 */
	public static String rightTrim(String str) {
		if (str == null) {
			return "";
		}
		int length = str.length();
		for (int i = length - 1; i >= 0; i--) {
			if (str.charAt(i) != 0x20) {
				break;
			}
			length--;
		}
		return str.substring(0, length);
	}
	/**获取文件扩展名
	 * @param path
	 * @return int
	 * 备注：0为非excel，1：为xls，2：xlsx
	 */
	public static int readExcelType(String path) {
        if (path == null || "".equals(path)) {
            return 0;
        } else {
            String postfix = JavaUtil.getPostfix(path);
            if (!"".equals(postfix)) {
                if ("xls".equals(postfix)) {
                    return 1;
                } else if ("xlsx".equals(postfix)) {
                    return 2;
                }
            } else {
                return 0;
            }
        }
        return 0;
    }
	
	/**根据文件路径获取扩展名
	 * @param path
	 * @return
	 */
	public static String getPostfix(String path) {
	    if (path == null || "".equals(path.trim())) {
	       return "";
	    }
	    if (path.contains(".")) {
	       return path.substring(path.lastIndexOf(".") + 1,path.length());
	    }
	   return "";
	}
}