package com.star.base.service.impl;

import java.util.List;

import com.star.base.dataobject.CommonFileDO;
import com.star.base.dataobject.CommonFileDOExample;
import com.star.base.dataobject.CommonFileDOExample.Criteria;
import com.star.base.entity.CommonFile;
import com.star.base.mapper.CommonFileDOMapper;
import com.star.base.service.ICommonFileService;
import com.star.common.tools.EntityUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonFileService implements ICommonFileService {
	
	@Autowired
    private CommonFileDOMapper commonFileMapper;
	
	@Override
	public CommonFile findById(Integer id) {
		return EntityUtils.transform(commonFileMapper.selectByPrimaryKey(id), CommonFile.class);
	}

	@Override
	public CommonFile save(CommonFile commonFile) {
		int id = commonFileMapper.insertSelective(EntityUtils.transform(commonFile, CommonFileDO.class));
		commonFile.setId(id);
		
		return commonFile;
	}

	@Override
	public CommonFile update(CommonFile commonFile) {
		commonFileMapper.updateByPrimaryKeySelective(EntityUtils.transform(commonFile, CommonFileDO.class));
		
		return commonFile;
	}

	@Override
	public Boolean del(Integer id) {
		CommonFileDO commonFileDO = new CommonFileDO();
		commonFileDO.setId(id);
		commonFileDO.setDeleted(true);
		commonFileMapper.updateByPrimaryKeySelective(commonFileDO);
		
		return true;
	}

	@Override
	public Boolean del(String filename) {
		CommonFileDOExample example = new CommonFileDOExample();
		Criteria criteria = example.createCriteria();
		criteria.andFilenameEqualTo(filename);
		
		CommonFileDO record = new CommonFileDO();
		record.setDeleted(true);
		commonFileMapper.updateByExampleSelective(record, example);
		return true;
	}

	@Override
	public List<CommonFile> findByUid(Integer uid, int page, int limit) {
		RowBounds rowBounds = new RowBounds(page * limit, limit);
		CommonFileDOExample example = new CommonFileDOExample();
		example.createCriteria().andUidEqualTo(uid).andDeletedNotEqualTo(true);
		example.setOrderByClause("create_time desc");
		List<CommonFileDO> list = commonFileMapper.selectByExampleWithRowbounds(example, rowBounds);
		return EntityUtils.transform(list, CommonFile.class);
	}

	@Override
	public int findTotalCount(CommonFile commonFile) {
		CommonFileDOExample example = new CommonFileDOExample();
		example.createCriteria().andUidEqualTo(commonFile.getUid()).andDeletedNotEqualTo(true);
		return commonFileMapper.countByExample(example);
	}

	@Override
	public void delAll(Integer uid) {
		CommonFileDOExample example = new CommonFileDOExample();
		example.createCriteria().andUidEqualTo(uid);
		
		CommonFileDO record = new CommonFileDO();
		record.setDeleted(true);
		commonFileMapper.updateByExampleSelective(record, example);
	}

}
