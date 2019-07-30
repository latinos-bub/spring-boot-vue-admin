package com.latin.admin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.latin.admin.dao.LoginDao;
import com.latin.admin.enums.ErrorEnum;
import com.latin.admin.service.LoginService;
import com.latin.admin.service.PermissionService;
import com.latin.admin.util.CommonUtils;
import com.latin.admin.util.ConstantUtils;
import org.apache.catalina.security.SecurityUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.shiro.subject.Subject;

import org.apache.shiro.session.Session;

/**
 * @author: util.you.com@gmail.com
 * @date: 2019/7/30 15:37
 * @description:
 * @version: 1.0
 * @className: LoginServiceImpl
 */
public class LoginServiceImpl implements LoginService {


    // auto inject properties
    @Autowired(required = false)
    private LoginDao loginDao;


    @Autowired(required = false)
    private PermissionService permissionService;



    /**
     * @author: util.you.com@gmail.com
     * @param: [jsonObject]
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2019/7/30 15:45
     * @version: 1.0
     * @description: 登录表单提交
     */
    @Override
    public JSONObject authLogin(JSONObject jsonObject) {

        String userName = jsonObject.getString("username");
        String passWord = jsonObject.getString("password");
        JSONObject info = new JSONObject();
        Subject currentUser = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(userName, passWord);
        try {
            currentUser.login(token);
            info.put("result", "success");
        }catch (AuthenticationException e){
            info.put("result", "fail");
        }
        return CommonUtils.successJson(info);
    }





    /**
     * @author: util.you.com@gmail.com
     * @param: [userName, passWord]
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2019/7/30 15:45
     * @version: 1.0
     * @description: 根据用户名和密码查询对应的用户
     */
    @Override
    public JSONObject getUser(String userName, String passWord) {

        return loginDao.getUser(userName, passWord);
    }






    /**
     * @author: util.you.com@gmail.com
     * @param: []
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2019/7/30 15:46
     * @version: 1.0
     * @description: 查询当前登录用户的权限等信息
     */
    @Override
    public JSONObject getInfo() {

        // 从 Session 中获取用于信息
        Session session = SecurityUtils.getSubject().getSession();
        JSONObject userInfo = (JSONObject) session.getAttribute(ConstantUtils.SESSION_USER_INFO);
        String userName = userInfo.getString("username");
        JSONObject info = new JSONObject();
        JSONObject userPermission = permissionService.getUserPermission(userName);
        session.setAttribute(ConstantUtils.SESSION_USER_PERMISSION, userPermission);
        info.put("userPermission", userPermission);
        return CommonUtils.successJson(info);
    }








    /**
     * @author: util.you.com@gmail.com
     * @param: []
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2019/7/30 15:52
     * @version: 1.0
     * @description: 退出登录
     */
    @Override
    public JSONObject logout() {

        try {
            Subject currentUser = SecurityUtils.getSubject();
            currentUser.logout();
        }catch (Exception e){
            return CommonUtils.errorJson(ErrorEnum.E_400);
        }
        return CommonUtils.successJson();
    }
}
