package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * Created by user on 2018/3/11.
 */
public interface IProductService {

    ServerResponse saveOrUpdateProdcut(Product product);

    ServerResponse<String> setSaleStauts(Integer productId,Integer status);

    ServerResponse<ProductDetailVo> manageProductDetails(Integer productId);

    ServerResponse getProductList(int pageNum,int pageSize);

    ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize);
}
