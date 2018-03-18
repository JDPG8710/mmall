package com.mmall.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Constant;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2018/3/11.
 */
@Service("iProductService")
public class IProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper iProductMapper;

    @Autowired
    private CategoryMapper iCategoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    public ServerResponse saveOrUpdateProdcut(Product product) {
        if (product != null) {
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
            }
            if (product.getId() != null) {
                int rowCount = iProductMapper.updateByPrimaryKey(product);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccess("Update product success");
                }
                return ServerResponse.createBySuccess("Update product failed");
            } else {
                int rowCount = iProductMapper.insert(product);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccess("Insert product success");
                }
                return ServerResponse.createBySuccess("Insert product failed");
            }
        }
        return ServerResponse.createByErrorMessage("Insert or update product is not correct");
    }

    public ServerResponse<String> setSaleStauts(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_AGUMENT.getCode(), ResponseCode.ILLEGAL_AGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = iProductMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("Update the product Sale Status successfully");
        }
        return ServerResponse.createByErrorMessage("Update the product Sale Status failed!");
    }

    public ServerResponse<ProductDetailVo> manageProductDetails(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_AGUMENT.getCode(), ResponseCode.ILLEGAL_AGUMENT.getDesc());
        }
        Product product = iProductMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("Product is deleted or not existed.");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);

    }

    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo .setDetail(product.getDetail());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());


        //imageHost
        productDetailVo .setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.dingmall.com/"));
        //parentCategoryId
        Category category = iCategoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCatogryId(0) ;//root id
        }else{
            productDetailVo.setParentCatogryId(category.getParentId());
        }
        //createTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));

        //updateTime
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }

    public ServerResponse getProductList(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = iProductMapper.selectList() ;

        List<ProductListVo> productListVosList = Lists.newArrayList();
        for(Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem) ;
            productListVosList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVosList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo .setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.dingmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product .getPrice());
        productListVo .setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum, pageSize);
                if(StringUtils.isNotBlank(productName)){
                    productName = new StringBuilder().append("%").append(productName).append("%").toString();
                }
        List<Product> productList = iProductMapper.selectByNameAndProductId(productName,productId);
        List<ProductListVo> productListVosList = Lists.newArrayList();
        for(Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem) ;
            productListVosList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVosList);
        return ServerResponse.createBySuccess(pageResult);
    }

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_AGUMENT.getCode(), ResponseCode.ILLEGAL_AGUMENT.getDesc());
        }
        Product product = iProductMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("Product is deleted or not existed.");
        }
        if(product.getStatus()!= Constant.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("Product is deleted or not existed.");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy)
    {
        if(StringUtils.isBlank(keyword) && categoryId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_AGUMENT.getCode(),ResponseCode.ILLEGAL_AGUMENT .getDesc()) ;
        }
        List<Integer> categoryIdList = new ArrayList<Integer>();

        if(categoryId!=null){
            Category category = iCategoryMapper .selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)){
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList =Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        PageHelper.startPage(pageNum, pageSize);

        if(StringUtils.isNotBlank(orderBy)){
            if(Constant.ProductListOrder.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray [1]);
            }
        }
        List<Product> productList =iProductMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword
                ,categoryIdList.size()==0?null:categoryIdList);

        List<ProductListVo > productListVoList =Lists.newArrayList();
        for(Product product:productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList .add((productListVo));
        }
        PageInfo pageInfo= new PageInfo(productList);
        pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccess(pageInfo) ;

    }

}
