package com.star.common.mapper;

import java.util.List;

import com.star.common.dataobject.UserDO;
import com.star.common.dataobject.UserDOExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface UserDOMapper {
    int countByExample(UserDOExample example);

    int deleteByExample(UserDOExample example);

    int deleteByPrimaryKey(Integer uid);

    int insert(UserDO record);

    int insertSelective(UserDO record);

    List<UserDO> selectByExampleWithRowbounds(UserDOExample example, RowBounds rowBounds);

    List<UserDO> selectByExample(UserDOExample example);

    UserDO selectByPrimaryKey(Integer uid);

    int updateByExampleSelective(@Param("record") UserDO record,
        @Param("example") UserDOExample example);

    int updateByExample(@Param("record") UserDO record, @Param("example") UserDOExample example);

    int updateByPrimaryKeySelective(UserDO record);

    int updateByPrimaryKey(UserDO record);
}