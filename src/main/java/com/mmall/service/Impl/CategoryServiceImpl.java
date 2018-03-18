package com.mmall.service.Impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.plugin.ClassLoaderInfo;

import java.util.List;
import java.util.Set;

/**
 * Created by user on 2018/03/04.
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger= LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse addCategory(String categoryName, Integer parentId){
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("Category para is wrong");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//Category is available

        int rowCount = categoryMapper .insert(category);
        if(rowCount>0){
            return ServerResponse.createBySuccess("Category added Successfully");
        }
        return ServerResponse.createByErrorMessage("Category added failed");
    }

    public ServerResponse updateCategoryName(Integer categoryId , String categoryName){
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("Category para is wrong");
        }
        Category category = new Category();
        category.setId(categoryId) ;
        category.setName(categoryName);
        int rowCount = categoryMapper .updateByPrimaryKeySelective(category) ;

        if(rowCount>0){
            return ServerResponse.createBySuccess("Update failed") ;
        }
        else{
            return ServerResponse.createByErrorMessage("Update failed");
        }
    }

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper .selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.info("No children category");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /***
     * 递归查询本节点的ID和孩子节点的ID
     * @param categoryId
     * @return
     */
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){

        Set<Category> categorySet  = Sets.newHashSet();
        findChildrenCategory(categorySet ,categoryId);
        List<Integer> categoryIdList= Lists.newArrayList();
        if(categoryId !=null){
            for(Category categoryItem : categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    //递归算法，算出子节点
    private Set<Category> findChildrenCategory(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category !=null){
            categorySet .add(category);
        }

        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for(Category categoryItem : categoryList){
            findChildrenCategory(categorySet,categoryItem .getId());
        }
        return categorySet;
    }
}
