package com.yuan.util.office;

import com.yuan.util.BaseException;
import com.yuan.util.LogUtil;
import com.yuan.util.constant.PubErrorCode;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.*;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/*
 * 下载EXCEL
 */
public class DownObjectExcelUtil {
	/**
	 * money 格式化金钱
	 * date 格式化日期
	 * rate 格式化话利率
	 * @param response : 响应对象
	 * @param list: 数据，其中放的是map
	 * @param filename ： 下载文件名（例如还款计划.xls)
	 * @param tmptitle: excel的sheet名称 (例如：还款计划）
	 * @param title: 标题，(例如：“序号，还款本金，还款利息”)
	 * @param field: key组合，(例如：“num,amount等”)
	 * @throws IOException
	 * @throws WriteException
	 * @throws BaseException
	 */
	public void downExcel(HttpServletResponse response, List<?> list,
			String filename, String tmptitle, String title, String field,
			int[] width) throws WriteException, IOException, BaseException {
		OutputStream os = null;
		WritableWorkbook wbook = null;
		String freezeLine ="";
		String freezeColumn ="";
		try {
			int listLen = list==null?0:list.size();
			if(listLen!=0){
				JSONObject objj = JSONObject.fromObject(list.get(list.size()-1));
				freezeLine = objj.get("freezeLine")==null?"":objj.get("freezeLine").toString();
				freezeColumn = objj.get("freezeColumn")==null?"":objj.get("freezeColumn").toString();
			}
			os = response.getOutputStream(); // 取得输出流;
			response.reset();// 清空输出流 //给下载的文件命名
			filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
			response.setHeader("Content-disposition", "attachment; filename= "
					+ filename);// 设定输出文件头
			response.setContentType("application/msexcel"); // 定义输出类型
			wbook = Workbook.createWorkbook(os); // 建立excel文件
			WritableSheet wsheet = wbook.createSheet(tmptitle, 0); // sheet名称
			// 设置excel标题
			int charNormal = 10;
			/*
			 * 设置冻结单元格
			 */
			if(!"".equals(freezeLine)&&!"".equals(freezeColumn)){
				wsheet.getSettings().setVerticalFreeze(Integer.parseInt(freezeLine));
				wsheet.getSettings().setHorizontalFreeze(Integer.parseInt(freezeColumn));
				list.remove(list.size()-1);
			}
			WritableFont oneFont = new WritableFont(
			WritableFont.createFont("宋体"), charNormal); // 字体
			WritableCellFormat normalFormat = new WritableCellFormat(oneFont);
			normalFormat.setAlignment(Alignment.CENTRE);
			normalFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			normalFormat.setWrap(true);// 是否换行
			WritableFont wfont = new WritableFont(WritableFont.ARIAL, 16,
					WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);
			WritableCellFormat wcfFC = new WritableCellFormat(wfont);
			wcfFC.setBackground(Colour.AQUA);
			wfont = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD,
					false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			wcfFC = new WritableCellFormat(wfont);
			/*
			 * 设置excel每列的宽度
			 */
			// wsheet.setColumnView(0, 5);
			for (int i = 0; i < width.length; i++) {
				wsheet.setColumnView(i, width[i]);
			}
			//表头字体
			WritableFont titleFont = new WritableFont(
					WritableFont.ARIAL, charNormal, WritableFont.BOLD); // 字体
			//表头标题
			WritableCellFormat titleWcf = new WritableCellFormat(titleFont);
			//把水平对齐方式指定为居中
			titleWcf.setAlignment(Alignment.CENTRE); 
			//把垂直对齐方式指定为居中
			titleWcf.setVerticalAlignment(VerticalAlignment.CENTRE);
			//添加边框：
			titleWcf.setBorder(Border.ALL, BorderLineStyle.THIN);
			//添加背景色：
			titleWcf.setBackground(Colour.GRAY_25);

			// 开始生成标题
			String[] titleList = (title == null ? "" : title).split(",");
			wsheet.addCell(new Label(titleList.length / 2, 0, tmptitle, wcfFC));
			wsheet.addCell(new Label(0, 1, "序号"));
			for (int i = 0; i < titleList.length; i++) {
				wsheet.addCell(new Label(i, 1, titleList[i]));
			}
			WritableCellFormat contentWcf = new WritableCellFormat(oneFont);
			//把垂直对齐方式指定为居中
			contentWcf.setVerticalAlignment(VerticalAlignment.CENTRE);
			contentWcf.setWrap(true);// 是否换行
			
			//人名币格式
			NumberFormat nf = new NumberFormat("¥#,##0.00");//设置数字格式
			WritableCellFormat wcfMoney = new WritableCellFormat(nf); //设置表单格式 
			//把水平对齐方式指定为左对齐
			wcfMoney.setAlignment(Alignment.LEFT); 
			//把垂直对齐方式指定为居中
			wcfMoney.setVerticalAlignment(VerticalAlignment.CENTRE);
			
			//不带符号人名币格式
			NumberFormat noNf = new NumberFormat("#,##0.00");//设置数字格式
			WritableCellFormat noWcfMoney = new WritableCellFormat(noNf); //设置表单格式 
			//把水平对齐方式指定为左对齐
			noWcfMoney.setAlignment(Alignment.LEFT); 
			//把垂直对齐方式指定为居中
			noWcfMoney.setVerticalAlignment(VerticalAlignment.CENTRE);
			
			//日期格式
			DateFormat df = new DateFormat("yyyy/MM/dd"); //添加时间
			WritableCellFormat wcfDF = new WritableCellFormat(df);
			//把水平对齐方式指定为左对齐
			wcfDF.setAlignment(Alignment.LEFT); 
			//把垂直对齐方式指定为居中
			wcfDF.setVerticalAlignment(VerticalAlignment.CENTRE);
			
			/*
			 * 拼接EXCEL数据
			 */
			for (int i = 0; i < list.size(); i++) {
				wsheet.addCell(new Label(0, i + 2, (i + 1) + ""));
				String[] filedList = (field == null ? "" : field).split(",");
				/* 取得map中对应的value值 */
				for (int j = 0; j < filedList.length; j++) {
					JSONObject obj = JSONObject.fromObject(list.get(i));
					String ftext = filedList[j];
					if(ftext.indexOf("^") != -1){
						switch(ftext.split("\\^")[1])
						{
							case "money" :
								String money = (null == obj.get(ftext.split("\\^")[0]) || "" == obj.get(ftext.split("\\^")[0])) ? "0" : obj.get(ftext.split("\\^")[0]).toString();
								double dMoney = Double.parseDouble(money);
								wsheet.addCell(new jxl.write.Number(j, i + 2, dMoney, wcfMoney));
								break;
							case "nosignmoney" :
								String nosignmoney = (null == obj.get(ftext.split("\\^")[0]) || "" == obj.get(ftext.split("\\^")[0])) ? "0" : obj.get(ftext.split("\\^")[0]).toString();
								double dNosignmoney = Double.parseDouble(nosignmoney);
								wsheet.addCell(new jxl.write.Number(j, i + 2, dNosignmoney, noWcfMoney));
								break;
							case "date" :
								SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
								String date = null == obj.get(ftext.split("\\^")[0]) ? "" : obj.get(ftext.split("\\^")[0]).toString();
								if("".equals(date)){
									break;
								}
								Date dDate = sdf.parse(date);
								wsheet.addCell(new DateTime(j, i + 2, dDate,wcfDF));
								break;
							case "rate" :
								String rate = null == obj.get(ftext.split("\\^")[0]) ? "" : obj.get(ftext.split("\\^")[0]).toString()+"%";
								wsheet.addCell(new Label(j, i + 2, rate, contentWcf));
								break;
							case "million" :
								String million = (null == obj.get(ftext.split("\\^")[0]) || "" == obj.get(ftext.split("\\^")[0])) ? "0" : obj.get(ftext.split("\\^")[0]).toString();
								double dMillion = Double.parseDouble(million)/10000;
								wsheet.addCell(new jxl.write.Number(j, i + 2, dMillion, wcfMoney));
								break;
						}
					}else{
						wsheet.addCell(new Label(j, i + 2, null == obj
								.get(filedList[j]) ? "" : obj.get(filedList[j])
								.toString(), contentWcf));
					}
				}
			}
			wbook.write(); // 写入文件
		} catch (Exception e) {
		    throw LogUtil.handerEx(PubErrorCode.PUB_DOWN_EXCEL, "导出EXCEL失败", LogUtil.EMPTY,e);
		} finally {
			if (wbook != null) {
				wbook.close();
			}
			if (os != null) {
				os.close(); // 关闭流
			}
		}
	}
}
