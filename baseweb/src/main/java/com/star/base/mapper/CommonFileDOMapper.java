package com.star.base.mapper;

import java.util.List;

import com.star.base.dataobject.CommonFileDO;
import com.star.base.dataobject.CommonFileDOExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface CommonFileDOMapper {
    int countByExample(CommonFileDOExample example);

    int deleteByExample(CommonFileDOExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(CommonFileDO record);

    int insertSelective(CommonFileDO record);

    List<CommonFileDO> selectByExampleWithRowbounds(CommonFileDOExample example,
        RowBounds rowBounds);

    List<CommonFileDO> selectByExample(CommonFileDOExample example);

    CommonFileDO selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") CommonFileDO record,
        @Param("example") CommonFileDOExample example);

    int updateByExample(@Param("record") CommonFileDO record,
        @Param("example") CommonFileDOExample example);

    int updateByPrimaryKeySelective(CommonFileDO record);

    int updateByPrimaryKey(CommonFileDO record);
}