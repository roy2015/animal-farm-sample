package com.animal.farm.familytree.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animal.farm.familytree.dto.FamilyPersonDto;
import com.animal.farm.familytree.dto.FamilyTreeNode;
import com.animal.farm.familytree.dto.request.AddFamilyPersonDto;
import com.animal.farm.familytree.infrastructure.exception.DataPlatformException;
import com.animal.farm.familytree.infrastructure.util.CollectionUtil;
import com.animal.farm.familytree.infrastructure.util.FreeMarkerTemplateUtil;
import com.animal.farm.familytree.infrastructure.util.JsonUtil;
import com.animal.farm.familytree.infrastructure.util.ObjectUtil;
import com.animal.farm.familytree.infrastructure.util.StringUtil;
import com.animal.farm.familytree.infrastructure.web.ErrorCode;
import com.animal.farm.familytree.infrastructure.web.MessageCode;
import com.animal.farm.familytree.orm.mapper.FamilyTreePersonMapper;
import com.animal.farm.familytree.orm.po.FamilyTreePerson;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author guojun
 * @date 2023/10/11 15:15
 */

@Slf4j
@Service
public class FamilyTreeServiceImpl implements FamilyTreeService{
  @Resource
  private FamilyTreePersonMapper familyTreePersonMapper;


  @Override
  public List<FamilyPersonDto> listAll() {
    List<FamilyTreePerson> familyTreePeople = familyTreePersonMapper.listAll();
    if (CollectionUtil.isNullOrEmpty(familyTreePeople)) {
      return Collections.emptyList();
    }
    List<FamilyPersonDto> res = familyTreePeople.stream()
        .map(item -> {
          FamilyPersonDto personDto = new FamilyPersonDto();
          BeanUtils.copyProperties(item, personDto);
          return personDto;
        }).collect(Collectors.toList());

    return res;
  }



  @Override
  public void syncData() throws DataPlatformException {

    List<FamilyTreePerson> familyTreePeople = familyTreePersonMapper.listAll();
    if (CollectionUtil.isNullOrEmpty(familyTreePeople)) {
      return ;
    }

    List<FamilyTreeNode> datas = familyTreePeople.stream()
        .map(item -> {
          FamilyTreeNode personDto = new FamilyTreeNode();
          BeanUtils.copyProperties(item, personDto);
          return personDto;
        }).collect(Collectors.toList());

    datas.forEach(x-> {
//      x.setId(Long.valueOf(x.getCode()));
      if (!StringUtil.isEmpty(x.getMark())) {
        x.setName(new StringBuffer(x.getName()).append("-").append(x.getMark()).toString());
      } else {}

    });//转long

    Map<String, List<FamilyTreeNode>> treeNodeGroups;
    List<FamilyTreeNode> subRootDtos;
    FamilyTreeNode root;

    treeNodeGroups = datas.stream().collect(
        Collectors.groupingBy(x-> x.getParentCode()));

    //按sort字段排序
    for (Entry<String, List<FamilyTreeNode>> longListEntry : treeNodeGroups.entrySet()) {
      List<FamilyTreeNode> treeNodes = longListEntry.getValue();
      Collections.sort(treeNodes, Comparator.comparingInt(FamilyTreeNode::getSort));
    }

    //二级根
    subRootDtos = datas.stream().filter(x -> "-1".equals(x.getParentCode())).sorted(
        Comparator.comparingInt(FamilyTreeNode::getSort)).collect(Collectors.toList());

    //虚拟一个root
    root = new FamilyTreeNode().setId(-1L).setName("root").setParentCode("-100");
    root.setChildren(subRootDtos);

    //构建tree
    buildTree(subRootDtos, treeNodeGroups);
    String treeStr = JsonUtil.writeValueAsString(root, true);
    log.info("{}", treeStr);

    //写文件
    byte[] tplBytes = new byte[1024 * 1024 * 2];
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("treeJs.tpl");
    FileOutputStream outputStream = null;

    try {
      int actualRead = IOUtils.read(inputStream, tplBytes, 0, tplBytes.length);

      log.info("expectRead: {} actualRead: {}", tplBytes.length, actualRead);
      tplBytes = Arrays.copyOf(tplBytes, actualRead);
      String tplStr = new String(tplBytes);

      HashMap<String, String> paraMap = new HashMap<>();
      paraMap.put("data", treeStr);
      String resStr = FreeMarkerTemplateUtil.freemarkerProcess(tplStr, paraMap);

      outputStream = new FileOutputStream(
          "/Users/apple/guojun/code/front-end-code/my-family-tree-1/v1/flare.js");
      IOUtils.write(resStr, outputStream);

    } catch (IOException | DataPlatformException e) {
      log.error(e.getMessage(), e);
    } finally {
      IOUtils.closeQuietly(inputStream);
      IOUtils.closeQuietly(outputStream);
    }
  }

  public void processBuildTree(FamilyTreeNode node, Map<String, List<FamilyTreeNode>> treeNodeGroups) {

    List<FamilyTreeNode> childrenNodes = treeNodeGroups.getOrDefault(node.getCode(), Collections.EMPTY_LIST);
    node.setChildren(childrenNodes);

    for (FamilyTreeNode child : node.getChildren()) {
      processBuildTree(child, treeNodeGroups);
    }
  }

  public void buildTree(List<FamilyTreeNode> subRootDtos, Map<String, List<FamilyTreeNode>> treeNodeGroups) {
    for (FamilyTreeNode subRootDto : subRootDtos) {
      processBuildTree(subRootDto, treeNodeGroups);
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void addFamilyPerson(AddFamilyPersonDto addFamilyPersonDto) throws DataPlatformException {
    String parentCode = addFamilyPersonDto.getParentCode();
    if (StringUtil.isEmpty(parentCode)) {
      throw new DataPlatformException(MessageCode.ERROR_1001, "父code");
    }
    FamilyTreePerson familyTreePerson = familyTreePersonMapper.selectByCode(parentCode);

    if (null == familyTreePerson) {
      throw new DataPlatformException(MessageCode.ERROR_1101, "父亲节点");
    }

    addFamilyPersonDto.setParentName(familyTreePerson.getName());

    List<FamilyTreePerson> childrens = familyTreePersonMapper.listChildrenByCode(parentCode);

    //不承认sort=0
    if (null != addFamilyPersonDto.getSort() && addFamilyPersonDto.getSort().intValue() ==0) {
      addFamilyPersonDto.setSort(1);
    }
    //有兄弟且sort没传时候系统计算
    if (CollectionUtil.isNotEmpty(childrens) && null == addFamilyPersonDto.getSort()) {
      Integer maxSort = childrens.stream().map(FamilyTreePerson::getSort).max(Comparator.comparingInt(Integer::intValue)).orElse(0);
      addFamilyPersonDto.setSort(maxSort + 1);
    } else {}

    FamilyTreePerson toInsertFamilyTreePerson = BeanUtil.copyProperties(addFamilyPersonDto, FamilyTreePerson.class);
    int count = familyTreePersonMapper.insertSelective(toInsertFamilyTreePerson);
    log.info("success add {} family person!", count);
  }

  @Override
  public void deleteFamilyPerson(Long id) throws DataPlatformException {
    if (null == id) {
      throw new DataPlatformException(MessageCode.ERROR_1001, "id");
    }
    FamilyTreePerson familyTreePerson = familyTreePersonMapper.selectByPrimaryKey(id);
    if (null == familyTreePerson) {
      throw new DataPlatformException(MessageCode.ERROR_1109, "成员");
    }

    List<FamilyTreePerson> childrens = familyTreePersonMapper.listChildrenByCode(familyTreePerson.getCode());
    if (childrens != null && childrens.size() > 0) {
      throw new DataPlatformException(MessageCode.ERROR_2300, "拥有后代节点不能删除");
    }
    int count = familyTreePersonMapper.deleteByPrimaryKey(id);
    log.info("success delete {} family person!", count);
  }
}
