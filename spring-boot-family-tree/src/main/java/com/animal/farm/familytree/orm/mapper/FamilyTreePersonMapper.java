package com.animal.farm.familytree.orm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.animal.farm.familytree.orm.po.FamilyTreePerson;

public interface FamilyTreePersonMapper {
    int deleteByPrimaryKey(Long id);

    int insert(FamilyTreePerson record);

    int insertSelective(FamilyTreePerson record);

    FamilyTreePerson selectByPrimaryKey(Long id);

    FamilyTreePerson selectByCode(@Param("code") String code);

    List<FamilyTreePerson> listChildrenByCode(@Param("parentCode") String parentCode);

    List<FamilyTreePerson> listAll();

    int updateByPrimaryKeySelective(FamilyTreePerson record);

    int updateByPrimaryKey(FamilyTreePerson record);
}