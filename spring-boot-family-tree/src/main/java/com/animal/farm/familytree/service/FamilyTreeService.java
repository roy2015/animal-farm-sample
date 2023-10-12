package com.animal.farm.familytree.service;

import java.util.List;

import com.animal.farm.familytree.dto.FamilyPersonDto;
import com.animal.farm.familytree.dto.request.AddFamilyPersonDto;
import com.animal.farm.familytree.infrastructure.exception.DataPlatformException;
import com.animal.farm.familytree.orm.po.FamilyTreePerson;

/**
 * @author guojun
 * @date 2023/10/11 15:15
 */
public interface FamilyTreeService {
  List<FamilyPersonDto> listAll();

  void syncData() throws DataPlatformException;


  void addFamilyPerson(AddFamilyPersonDto addFamilyPersonDto) throws DataPlatformException;


  void deleteFamilyPerson(Long id) throws DataPlatformException;
}
