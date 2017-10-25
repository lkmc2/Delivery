package com.limin.delivery.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences工具类（用于储存当前登陆的用户名）
 */

public class ShareUtils {
    private static final String SHARE_NAME = "share_name"; //当前存储工具的名字
    private static final String LOGIN_USER_NAME = "user_name"; //当前登陆的用户名


    /**
     * 添加当前登陆的用户
     * @param mContext 上下文
     * @param username 关键字
     */
    public static void addCurrentLoginUser(Context mContext, String username) {
        getSP(mContext).edit().putString(LOGIN_USER_NAME, username).apply(); //存入已登录的用户名
    }

    /**
     * 删除当前登陆的用户
     * @param mContext 上下文
     */
    public static void deleteLoginUser(Context mContext) {
        getSP(mContext).edit().putString(LOGIN_USER_NAME, "").apply(); //将登陆的用户名清空
    }

    /**
     * 获取当前登陆的用户名
     * @param mContext 上下文
     */
    public static String getLoginUserName(Context mContext) {
        return getSP(mContext).getString(LOGIN_USER_NAME, ""); //未登录时用户名为空字符串
    }

    /**
     * 检查是否有用户登陆
     * @param mContext 上下文
     * @return 是否有用户登陆
     */
    public static boolean checkIsHaveLogin(Context mContext) {
        //获取已登录的用户名
        String loginName = getLoginUserName(mContext);

        return !"".equals(loginName); //判断登陆的用户名是否为空字符串
    }

    /**
     * 返回一个根据上下文生成的SharedPreferences
     * @param mContext 上下文
     * @return 根据上下文生成的SharedPreferences
     */
    private static SharedPreferences getSP(Context mContext) {
        return mContext.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE);
    }
}
