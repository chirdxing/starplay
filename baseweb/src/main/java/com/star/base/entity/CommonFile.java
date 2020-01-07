package com.star.base.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 通用文件上传记录
 * @date 2020年1月7日
 * @version 1.0
 */
public class CommonFile implements Serializable{
	private static final long serialVersionUID = 1L;

	// 唯一标记
	private Integer id;

	// 账号ID
    private Integer uid;

    // 文件相对路径
    private String filename;

    // 文件全路径
    private String url;

    // 创建时间
    private Date createTime;

    // 删除标记
    private Boolean deleted;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	
}
