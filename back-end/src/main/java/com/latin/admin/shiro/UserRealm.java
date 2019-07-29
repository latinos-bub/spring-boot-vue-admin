package com.latin.admin.shiro;

import com.alibaba.fastjson.JSONObject;
import com.latin.admin.service.LoginService;
import com.latin.admin.util.ConstantUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.shiro.session.Session;

import java.util.Collection;

/**
 * @author: util.you.com@gmail.com
 * @date: 2019/7/29 16:19
 * @description: 自定义 Realm
 * @version: 1.0
 * @className: UserRealm
 */
public class UserRealm extends AuthorizingRealm {


    private Logger logger = LoggerFactory.getLogger(UserRealm.class);

    // auto inject properties
    @Autowired
    private LoginService loginService;


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        Session session = SecurityUtils.getSubject().getSession();
        // 查询用户的权限
        JSONObject permission = (JSONObject) session.getAttribute(ConstantUtils.SESSION_USER_PERMISSION);
        logger.info("permission 的值为: " + permission);
        logger.info("本用户权限为: " + permission.get("permissionList"));
        // 为当前用户设置角色和权限
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addStringPermissions((Collection<String>) permission.get("permissionList"));

        return authorizationInfo;
    }






    /**
     * @author: util.you.com@gmail.com
     * @param: [authenticationToken]
     * @return: org.apache.shiro.authc.AuthenticationInfo
     * @date: 2019/7/29 16:37
     * @version: 1.0
     * @description: 验证当前登录的 Subject
     * LoginController.login() 方法中执行 Subject.login() 时 执行此方法
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        String loginName = (String) authenticationToken.getPrincipal();
        // 获取用户密码
        String passWord = new String((char[]) authenticationToken.getCredentials());
        JSONObject user = loginService.getUser(loginName, passWord);
        if (user == null){
            // 没找到账号
            throw new UnknownAccountException();
        }
        // 交给 AuthenticatingRealm 使用 CredentialsMatcher 进行密码匹配
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user.getString("username"),
                user.getString("password"),
//                采用明文访问时，不需要第三个参数
//                ByteSource.Util.bytes("salt"), salt = username+ salt,
                getName()
        );
        // session 中不需要保存密码
        user.remove("password");
        // 将 用户信息 放入 session 中
        SecurityUtils.getSubject().getSession().setAttribute(ConstantUtils.SESSION_USER_INFO, user);
        return authenticationInfo;
    }
}
