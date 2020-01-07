package com.star.base.service;

import java.util.List;

import com.star.base.entity.CommonFile;

/**
 * 通用文件上传
 * @date 2020年1月7日
 * @version 1.0
 */
public interface ICommonFileService {
	
    /**
     * 按ID查找
     * @param id
     * @return
     */
    CommonFile findById(Integer id);
    
    /**
     * 保存
     * @param commonFile
     * @return
     */
    CommonFile save(CommonFile commonFile);

    /**
     * 更新
     * @param commonFile
     * @return
     */
    CommonFile update(CommonFile commonFile);

    /**
     * 删除
     * @param id
     * @return
     */
    Boolean del(Integer id);
    
    /**
     * 根据文件名删除
     * @param filename
     * @return
     */
    Boolean del(String filename);
    
    /**
     * 删除所有图片
     * @param uid
     */
    void delAll(Integer uid);
    
    /**
     * 按ID列表查找
     * @param uid
     * @return
     */
    List<CommonFile> findByUid(Integer uid, int page, int limit);
    
    /**
     * 查找totalcount
     * BigcbdDecoration commonFile 
     */
    int findTotalCount(CommonFile commonFile);
    
}
