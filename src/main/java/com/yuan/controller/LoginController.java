package com.yuan.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.yuan.model.Users;
import com.yuan.service.LoginService;
import com.yuan.util.*;
import com.yuan.util.constant.BussErrorCode;
import com.yuan.util.constant.PubErrorCode;
import com.yuan.util.http.HttpUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;


import net.sf.json.JSONObject;

/**
 * 登陆控制器。
 * 
 * @version $Revision$ $Date$
 * @since 3.0.0
 */
@RestController
@RequestMapping("/com/yuan/service/login/")
public class LoginController {

    @Resource
    private LoginService loginService;

    
    /**登陆，成功跳转首页，失败跳转错误页面
     * @param request
     * @return
     * @throws BaseException
     */
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView login(HttpServletRequest request) throws BaseException {
        ModelAndView mav = new ModelAndView("redirect:/service/login/error");
        String userId = "";
        JSONObject bodyJson = new JSONObject();
        try {
            userId = request.getParameter("userId");
            String userPwd = request.getParameter("userPwd");
            bodyJson.put("userId", userId);
            bodyJson.put("userPwd", userPwd);
            JSONObject userJson = HttpUtil.sendHttpRequest(bodyJson);
            Users user = (Users) JSONUtil.jsonToBean(userJson, Users.class);
            String userNo = user.getUserId();
            if (!"".equals(userNo)) {
                String userValid = user.getUserValid();
                if (!"0".equals(userValid)) { //有效的用户
                    mav = new ModelAndView("redirect:/service/login/index");
                    HttpSession session = getSession();
                    session.setAttribute("user", user);
//                    session.setAttribute("resList", resList);//权限下可操作的资源
                }
                String firstLogin = user.getFirstLogin();
                if (!"1".equals(firstLogin)) { //第一次登陆
                    mav = new ModelAndView("/updloginpassword");
                    HttpSession session = getSession();
                    session.setAttribute("user", user);
                }
            }
           return mav;
        } catch (Exception e) {
            throw new BaseException(PubErrorCode.ERROR_LOGIN_CONTROL, "根据登录用户名:" + userId + ",查询用户信息Controller失败！", e);
        }
    }
    /**
     * 修改登录密码
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "loginpassword")
    public ModelAndView loginPassword(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = getSession();
        ModelAndView mav = new ModelAndView("/updloginpassword");
        Users user = (Users) session.getAttribute("user");
        mav.addObject("users", user);
        return mav;
    }

    @RequestMapping(value = "lackaccess")
    public ModelAndView lackaccess(HttpServletRequest reqeust) {
        ModelAndView modelAndView = new ModelAndView("template/lackaccessf");
        return modelAndView;
    }

    @SuppressWarnings("static-access")
    @RequestMapping(value = "loginout")
    public void logOut(HttpServletRequest reqeust, HttpServletResponse response) throws IOException {
        SessionUtil session = new SessionUtil();
        session.getSession().invalidate();
        response.sendRedirect(reqeust.getContextPath() + "");
    }

    @RequestMapping(value = "error")
    public ModelAndView errorPage(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("error/error");
        return mv;

    }

    public static HttpSession getSession() {
        HttpSession session = null;
        try {
            session = getRequest().getSession();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return session;
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }

    /**
     * iframe
     */
    @RequestMapping(value = "index")
    public ModelAndView turnIframePage(HttpServletRequest request) throws BaseException {
        ModelAndView mav = new ModelAndView("index");
        return mav;
    }

    /**
     * 首页
     */
    @RequestMapping(value = "firtPage")
    public ModelAndView turnFirstPage(HttpServletRequest request) throws BaseException {
        ModelAndView mav = new ModelAndView("firtPageDemo");
        return mav;
    }
    
    /**
     * 列表
     */
    @RequestMapping(value = "custList")
    public ModelAndView queryCustList(HttpServletRequest request) throws BaseException {
        ModelAndView mav = new ModelAndView("tablesDemo");
        return mav;
    }

    /**
     * 表单
     */
    @RequestMapping(value = "addForm")
    public ModelAndView addForm(HttpServletRequest request) throws BaseException {
        ModelAndView mav = new ModelAndView("formDemo");
        return mav;
    }
    
    /**
	 * 修改登录密码
	 * 
	 * @param request
	 * @return
	 * @throws BaseException
	 */
	@RequestMapping(value = "updateloginpassword")
	public int updateLoginPassword(HttpServletRequest request) throws BaseException {
		int updateFlag = 0;
		JavaUtil ju = new JavaUtil();
		Map<String, Object> map = ju.getMap(request);
		JSONObject bodyJson = new JSONObject();
		try {
			Users user = (Users) SessionUtil.getSession().getAttribute("user");
			String newPassword = map.getOrDefault("newPassword","").toString();
			String oldPassword = map.getOrDefault("oldPassword","").toString();
			   String userId =user.getUserId();
	            bodyJson.put("userNo", userId);
	            bodyJson.put("oldPassword", oldPassword);
	            bodyJson.put("newPassword", newPassword);
	            //TODO 校验并修改
	            //0：修改成功，1：新老密码不一致，2：旧密码或者用户号错误
//	            String updateFlagString = userJson.getString("updateFlag");
//	            updateFlag = Integer.parseInt(updateFlagString);
		} catch (Exception e) {
			throw LogUtil.handerEx(BussErrorCode.ERROR_MANAGE_USER_F8, "修改登录密码Controller失败,参数为："+map, LogUtil.ERROR, e);
		}
		return updateFlag;
	}
}
