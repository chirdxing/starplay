package com.star.common.service;

import com.star.common.entity.User;

/**
 * 用户账号相关逻辑层接口
 * @date 2020年1月7日
 * @version 1.0
 */
public interface IUserService {

	/**
	 * 创建新的账号
	 * @param user
	 */
	int save(User user);
	
	/**
	 * 根据用户UID更新用户信息
	 * @param user
	 */
	void updateByUid(User user);
	
	/**
	 * 修改账号的基本信息
	 * @param uid
	 * @param entity
	 */
	void updateBaseInfo(Integer uid, User entity);
	
	/**
	 * 修改账号密码
	 * @param uid
	 * @param password 加密后的新密码
	 */
	void updatePassword(Integer uid, String password);
	
	/**
	 * 修改账号的状态
	 * @param uid
	 * @param state 账号状态
	 */
	void updateState(Integer uid, Short state);
	
	/**
	 * 修改绑定的邮箱
	 * @param uid
	 * @param email
	 */
	void updateEmail(Integer uid, String email);
	
	/**
	 * 修改绑定的手机号码
	 * @param uid
	 * @param tel
	 */
	void updateTel(Integer uid, String tel);
	
	/**
	 * 更新最后一次登录时间为当前时间
	 * @param uid
	 */
	void updateLastLoginTime(Integer uid);
	
	/**
	 * 根据账号ID查询账号
	 * @param uid
	 * @return
	 */
	User findByUid(Integer uid);
	
	/**
	 * 根据登录信息查询账号是否存在
	 * @param accountName 登录账号名
	 * @param password 加密后的登录密码
	 * @return
	 */
	User findByLoginInfo(String accountName, String password);
	
	/**
	 * 根据openid查找手机用户，对应accountName
	 * @param openid 
	 * @return
	 */
	User findByOpenid(String openid);
	
	/**
	 * 判断安全邮箱是否已经被使用
	 * @param email
	 * @return
	 */
	boolean isEmailExists(String email);
	
	/**
	 * 判断安全手机号码是否已经被使用
	 * @param tel
	 * @return
	 */
	boolean isTelExists(String tel);
	
	/**
	 * 判断登录账号名是否已经被使用
	 * @param accountName
	 * @return
	 */
	boolean isAccountNameExists(String accountName);
	
}
