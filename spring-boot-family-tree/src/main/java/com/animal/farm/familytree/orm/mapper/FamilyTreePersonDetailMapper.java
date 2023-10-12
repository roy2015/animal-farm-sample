package com.animal.farm.familytree.orm.mapper;

import com.animal.farm.familytree.orm.po.FamilyTreePersonDetail;

public interface FamilyTreePersonDetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FamilyTreePersonDetail record);

    int insertSelective(FamilyTreePersonDetail record);

    FamilyTreePersonDetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(FamilyTreePersonDetail record);

    int updateByPrimaryKey(FamilyTreePersonDetail record);
}