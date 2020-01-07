package com.star.common.mapper;

import java.util.List;

import com.star.common.dataobject.EmailLogDO;
import com.star.common.dataobject.EmailLogDOExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface EmailLogDOMapper {
    int countByExample(EmailLogDOExample example);

    int deleteByExample(EmailLogDOExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(EmailLogDO record);

    int insertSelective(EmailLogDO record);

    List<EmailLogDO> selectByExampleWithRowbounds(EmailLogDOExample example, RowBounds rowBounds);

    List<EmailLogDO> selectByExample(EmailLogDOExample example);

    EmailLogDO selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") EmailLogDO record,
        @Param("example") EmailLogDOExample example);

    int updateByExample(@Param("record") EmailLogDO record,
        @Param("example") EmailLogDOExample example);

    int updateByPrimaryKeySelective(EmailLogDO record);

    int updateByPrimaryKey(EmailLogDO record);
}