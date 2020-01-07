package com.star.common.entity;

import java.io.Serializable;
import java.util.Date;


/**
 * 用户表，用于系统用户登录和注册，账号状态变更，账号安全绑定等
 * @date 2020年1月7日
 * @version 1.0
 */
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	
	// 唯一标记，所有账号类型的统一账号ID，全局唯一
	private Integer uid;
	
	private String openid;
	
	// 登录账号名，全局唯一，可以为手机号、邮箱等（补充：删除操作不作硬删除故账号名依然占位），手机用户则为授权openid
	private String accountName;
	
	// 登录密码（MD5加密）
	private String password;
	
	/**
	 * 账号类型
	 */
	private Short type;
	
	/**
	 * 账号状态
	 */
	private Short state;
	
	// 用户真实姓名
	private String userName;
	
	// 性别，true表示男，false表示女
	private Boolean sex;
	
	// 头像
	private String headimg;
	
	// 通讯地址
	private String address;

	// 账号关联邮箱
	private String email;
	
	// 账号关联的手机号码
	private String tel;
	
	// 账号创建时间
	private Date createTime;
	
	// 最后一次访问时间
	private Date lastLoginTime;
	
	// 备注
	private String remark;

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Short getType() {
		return type;
	}

	public void setType(Short type) {
		this.type = type;
	}

	public Short getState() {
		return state;
	}

	public void setState(Short state) {
		this.state = state;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Boolean getSex() {
		return sex;
	}

	public void setSex(Boolean sex) {
		this.sex = sex;
	}

	public String getHeadimg() {
		return headimg;
	}

	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}
