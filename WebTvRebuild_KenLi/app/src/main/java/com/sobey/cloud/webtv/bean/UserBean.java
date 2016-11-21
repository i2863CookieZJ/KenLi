package com.sobey.cloud.webtv.bean;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 用户实体 Created by lazy on 15/1/28.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(value = { "status", "error_code", "error_msg", "R" })
public class UserBean implements Serializable {
	/**
	 * 账号ID
	 */
	@JsonProperty(value = "account_id")
	private String account_id;

	/**
	 * 用户唯一编号
	 */
	@JsonProperty(value = "uid")
	private String uid;

	/**
	 * 用户名
	 */
	@JsonProperty(value = "username")
	private String username;

	/**
	 * 手机号码
	 */
	@JsonProperty(value = "mobile")
	private String mobile;

	/**
	 * 邮箱
	 */
	@JsonProperty(value = "email")
	private String email;

	/**
	 * 头像
	 */
	@JsonProperty(value = "avatar")
	private String avatar;

	/**
	 * 登陆时间
	 */
	@JsonProperty(value = "login_expiry_time")
	private int login_expiry_time;

	/**
	 * 性别
	 */
	@JsonProperty(value = "sex")
	private int sex;

	public String getAccount_id() {
		return account_id;
	}

	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getLogin_expiry_time() {
		return login_expiry_time;
	}

	public void setLogin_expiry_time(int login_expiry_time) {
		this.login_expiry_time = login_expiry_time;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}
}
