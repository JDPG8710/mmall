package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Constant;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import com.sun.deploy.net.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by user on 2018/3/11.
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    @RequestMapping("product_save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        User user =(User) session.getAttribute(Constant.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"Please login your admin account");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.saveOrUpdateProdcut(product);
        }
        else{
           return  ServerResponse.createByErrorMessage("No auth message");
        }
    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId,Integer status){
        User user =(User) session.getAttribute(Constant.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"Please login your admin account");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.setSaleStauts(productId,status);
        }
        else{
            return  ServerResponse.createByErrorMessage("No auth message");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session,Integer productId){
        User user =(User) session.getAttribute(Constant.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"Please login your admin account");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.manageProductDetails(productId);
        }
        else{
            return  ServerResponse.createByErrorMessage("No auth message");
        }
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value="pageNum",defaultValue = "1")int pageNum, @RequestParam(value="pageSize",defaultValue = "10") int pageSize){
        User user =(User) session.getAttribute(Constant.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"Please login your admin account");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
           return iProductService.getProductList(pageNum, pageSize);
        }
        else{
            return  ServerResponse.createByErrorMessage("No auth message");
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse getSearch(HttpSession session,String productName,Integer productId, @RequestParam(value="pageNum",defaultValue = "1")int pageNum, @RequestParam(value="pageSize",defaultValue = "10") int pageSize){
        User user =(User) session.getAttribute(Constant.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"Please login your admin account");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.searchProduct(productName, productId,pageNum, pageSize);
        }
        else{
            return  ServerResponse.createByErrorMessage("No auth message");
        }
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,@RequestParam(value = "upload_file",required = false)MultipartFile file ,HttpServletRequest request){
        User user =(User) session.getAttribute(Constant.CURRENT_USER);
        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"Please login your admin account");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix"+targetFileName);
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
        }
        else{
            return  ServerResponse.createByErrorMessage("No auth message");
        }
    }

    @RequestMapping("richText_img_upload.do")
    @ResponseBody
    public Map richTextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false)MultipartFile file , HttpServletRequest servletRequest, HttpServletResponse servletResponse){
        Map resultMap = Maps.newHashMap();
        User user =(User) session.getAttribute(Constant.CURRENT_USER);
        if(user ==null){
            resultMap.put("success",false);
            resultMap.put("msg","Not auth to access");
            return resultMap;
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            String path = servletRequest.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","Upload failed");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix"+targetFileName);
            resultMap.put("success",true);
            resultMap.put("msg","Upload Success");
            resultMap.put("file_Path",url);
            servletResponse.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }
        else{
            resultMap.put("success",false);
            resultMap.put("msg","Not auth to access");
            return resultMap;
        }
    }
}
