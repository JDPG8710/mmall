package com.mmall.common;

/**
 * Created by user on 2018/02/28.
 */
public class Constant {

    //当前用户Session Key
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    public interface Role {
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }
}
