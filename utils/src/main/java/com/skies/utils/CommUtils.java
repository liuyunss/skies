package com.skies.utils;
/**
 * 公共变量存放工具
 * @author Admin
 *
 */
public class CommUtils {
	public static final int SUCCODE = 200;//请求成功
	public static final int REPEATCODE = 300;//数据已存在
	public static final int REDIRECTCODE = 303;//请求重定向
	public static final int ERRCODE = 500;//服务器内部错误，无法完成请求
	public static final int LOGCODE = 401;//请求要求用户的身份认证
	public static final int NULLCODE = 404;//请求结果为空
	public static final int AUTHORIZEDCODE = 403;//没有权限
	public static final int GRAMMARERRCODE = 400;//客户端请求语法错误
}
