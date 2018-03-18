package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by user on 2018/02/28.
 */
public class Constant {

    //当前用户Session Key
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    public interface ProductListOrder{
        Set<String> PRICE_ASC_DESC= Sets.newHashSet("price_desc","price_asc");
    }

    public interface Cart{
        int CHECKED = 1;// cart is selected
        int UNCHECKED = 0; // cart is unselected

        String LIMIT_NUM_FAIL="LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS="LIMIT_NUM_SUCCESS";
    }

    public interface Role {
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }

    public enum ProductStatusEnum{
        ON_SALE(1,"Online");

        private String value;
        private int code;
        ProductStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

}
