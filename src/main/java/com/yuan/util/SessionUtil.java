package com.yuan.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ubillion.model.MUsers;

/**
 * 获取session中相关信息公共方法
 * 
 * @author 张红生
 *
 */
public class SessionUtil {

    /**
     * 获取session对象
     * 
     * @return
     */
    public static HttpSession getSession() {
        HttpSession session = null;
        try {
            if (getRequest() != null)
                session = getRequest().getSession();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return session;
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            return attrs.getRequest();
        }
        return null;
    }

    /**
     * 获取当前登陆用户对象信息
     * 
     * @return
     */
    public static MUsers getCurrentUser() {
        MUsers mUsers = null;
        if (getSession() != null) {
            mUsers = (MUsers) getSession().getAttribute("user");
        }
        return mUsers;
    }

}
