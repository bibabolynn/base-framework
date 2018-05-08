package com.yuan.util.office;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ubillion.util.JavaUtil;
import com.ubillion.util.LogUtil;
import com.ubillion.util.SysParamUtil;
import com.ubillion.util.TranFailException;
import com.ubillion.util.constant.Constants;
import com.ubillion.util.constant.PubErrorCode;

import freemarker.template.Configuration;
import freemarker.template.Template;

/*
 * 根据指定路径下模板，填充数据，生成word存储到指定路径下
 */

public class WordTemplateUtil {	
	/**
     * @param templatePath : 模板所在路径
     * @param templateName: 模板名称，不带扩展名
     * @param dataMap: 模板数据存放地
     * @param saveFilePath: 文件存储路径
     * @param saveFileName : 文件存储名称
     * @param out
     * @throws TranFailException
     */
    public void write(String templatePath, String templateName, Map<String, Object> dataMap, String saveFilePath,String saveFileName) throws TranFailException {
        Writer out = null;
        try {
            JavaUtil.createDirectory(saveFilePath); // 创建文件路径
            out = new OutputStreamWriter(new FileOutputStream(saveFilePath + saveFileName), "UTF-8"); // 生成文件的保存路径
            Template t = getTemplate(templatePath, templateName);
            t.process(dataMap, out);
        } catch (Exception e) {
            throw LogUtil.handerEx(PubErrorCode.PUB_TEMP_BUILD,saveFilePath + "路径下文件为：" + saveFileName+ ",生成文件失败", LogUtil.EMPTY,e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                throw new TranFailException(PubErrorCode.PUB_CLOSE_TEMP, "关闭流失败");
            }
        }
    }
	/**
	 * 加载指定路径下的模板成为Template模板
	 * 
	 * @param templatePath : 模板所在路径
	 * @param templateName: 模板名称，不带扩展名
	 * @return Template;
	 * @throws TranFailException
	 */
	private Template getTemplate(String templatePath, String templateName) throws TranFailException {
		try {
		    Configuration configuration = new Configuration();
            configuration.setDefaultEncoding("UTF-8");
			configuration.setClassForTemplateLoading(this.getClass(), templatePath); // 加载模板所在路径
			Template t = configuration.getTemplate(templateName + ".ftl"); // 加载指定模板
			t.setEncoding("UTF-8");
			return t;
		} catch (Exception e) {
		    throw LogUtil.handerEx(PubErrorCode.PUB_LOAD_TEMP, "加载路径为：" + templatePath + ",模板名为："+ templateName + "失败", LogUtil.EMPTY,e);
		}
	}

	/*
	 * 测试方法
	 */
	public static void main(String[] args) throws TranFailException {
		Map<String, Object> map = new HashMap<String, Object>();
		WordTemplateUtil wordUtil = new WordTemplateUtil();
		map.put("phone", "13681144423");
		try {
			String filePath = SysParamUtil.getSysParValue("IMAGE_ROOT") + JavaUtil.DateToString(new Date(), "yyyyMMdd")+ "/";
			wordUtil.write(Constants.TEMPLATE_PATH, "word", map, filePath, "test.doc");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}