package com.star.common.service.impl;

import java.util.Date;
import java.util.List;

import com.star.common.config.PlatformConfig;
import com.star.common.dataobject.UserDO;
import com.star.common.dataobject.UserDOExample;
import com.star.common.entity.User;
import com.star.common.mapper.UserDOMapper;
import com.star.common.service.IUserService;
import com.star.common.tools.EntityUtils;
import com.star.common.tools.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private UserDOMapper userDOMapper;
	@Autowired
	private PlatformConfig platformConfig;
	
	@Override
	public int save(User user) {
		UserDO userDO = EntityUtils.transform(user, UserDO.class);
		userDOMapper.insertSelective(userDO);
		return userDO.getUid();
	}
	
	@Override
	public void updateByUid(User user) {
		UserDO userDO = EntityUtils.transform(user, UserDO.class);
		userDOMapper.updateByPrimaryKeySelective(userDO);
	}

	@Override
	public void updateBaseInfo(Integer uid, User entity) {
		UserDO userDO = EntityUtils.transform(entity, UserDO.class);
		userDO.setUid(uid);
		userDOMapper.updateByPrimaryKeySelective(userDO);
	}

	@Override
	public void updatePassword(Integer uid, String password) {
		UserDO userDO = new UserDO();
		userDO.setUid(uid);
		userDO.setPassword(password);
		userDOMapper.updateByPrimaryKeySelective(userDO);
	}

	@Override
	public void updateState(Integer uid, Short state) {
		UserDO userDO = new UserDO();
		userDO.setUid(uid);
		userDO.setState(state);
		userDOMapper.updateByPrimaryKeySelective(userDO);
	}

	@Override
	public void updateEmail(Integer uid, String email) {
		UserDO userDO = new UserDO();
		userDO.setUid(uid);
		userDO.setEmail(email);
		userDOMapper.updateByPrimaryKeySelective(userDO);
	}
	
	@Override
	public void updateTel(Integer uid, String tel) {
		UserDO userDO = new UserDO();
		userDO.setUid(uid);
		userDO.setTel(tel);
		userDOMapper.updateByPrimaryKeySelective(userDO);
	}
	
	@Override
	public void updateLastLoginTime(Integer uid) {
		UserDO userDO = new UserDO();
		userDO.setUid(uid);
		userDO.setLastLoginTime(new Date());
		userDOMapper.updateByPrimaryKeySelective(userDO);
	}

	@Override
	public User findByUid(Integer uid) {
		UserDO userDO = userDOMapper.selectByPrimaryKey(uid);
		return EntityUtils.transform(userDO, User.class);
	}
	
	@Override
	public User findByLoginInfo(String accountName, String password) {
		UserDOExample example = new UserDOExample();
		example.createCriteria().andAccountNameEqualTo(accountName)
			.andPasswordEqualTo(password);
		List<UserDO> sourceList = userDOMapper.selectByExample(example);
		List<User> targetList = EntityUtils.transform(sourceList, User.class);
		return ObjectUtils.isNull(targetList) ? null : targetList.get(0);
	}
	
	@Override
	public User findByOpenid(String openid) {
		UserDOExample example = new UserDOExample();
		example.createCriteria().andAccountNameEqualTo(openid);
		List<UserDO> list = userDOMapper.selectByExample(example);
		if (!ObjectUtils.isNull(list)) {
			return EntityUtils.transform(list.get(0), User.class);
		}
		return null;
	}

	@Override
	public boolean isEmailExists(String email) {
		UserDOExample example = new UserDOExample();
		example.createCriteria().andEmailEqualTo(email);
		List<UserDO> sourceList = userDOMapper.selectByExample(example);
		List<User> targetList = EntityUtils.transform(sourceList, User.class);
		return !ObjectUtils.isNull(targetList);
	}

	@Override
	public boolean isTelExists(String tel) {
		UserDOExample example = new UserDOExample();
		example.createCriteria().andTelEqualTo(tel);
		List<UserDO> sourceList = userDOMapper.selectByExample(example);
		List<User> targetList = EntityUtils.transform(sourceList, User.class);
		return !ObjectUtils.isNull(targetList);
	}

	@Override
	public boolean isAccountNameExists(String accountName) {
		UserDOExample example = new UserDOExample();
		example.createCriteria().andAccountNameEqualTo(accountName);
		List<UserDO> sourceList = userDOMapper.selectByExample(example);
		List<User> targetList = EntityUtils.transform(sourceList, User.class);
		return !ObjectUtils.isNull(targetList);
	}
	
}
