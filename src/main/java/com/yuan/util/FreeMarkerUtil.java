package com.yuan.util;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;

public class FreeMarkerUtil {

    public static TemplateHashModel useStaticPackage(String packageName) {
        TemplateHashModel fileStatics = null;
        try {
            BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
            TemplateHashModel staticModels = wrapper.getStaticModels();
            fileStatics = (TemplateHashModel) staticModels.get(packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileStatics;
    }
}
