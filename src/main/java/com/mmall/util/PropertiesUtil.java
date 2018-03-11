package com.mmall.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by user on 2018/3/11.
 */
public class PropertiesUtil {

    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties props;

    static{
        String fileName = "mmall.properties";
        props = new Properties();
        try {
            props.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
           logger.error("Failed to read property files",e);
        }
    }

    public static String getProperty(String key){
        String value = props.getProperty(key.trim()) ;
        if(StringUtils.isBlank(value)){
            return StringUtils.EMPTY;
        }
        return value.trim();
    }

    public static String getProperty(String key,String defaultValue){
        String value = props.getProperty(key.trim()) ;
        if(StringUtils.isBlank(value)){
            return defaultValue;
        }
        return value.trim();
    }
}
