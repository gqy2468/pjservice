package com.lvmama.phpsrv.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.HashMap;

/**
 * Created by libiying on 2016/10/14.
 */
public class Common {

    /**
     * 对象转数组
     * @param object Object
     * @return byte[]
     */
    public static byte[] objectToByteArr(Object object){
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * 数组转对象
     * @param bytes byte[]
     * @return Object
     */
    public static Object byteArrToObject(byte[] bytes){
        Object object = null;
        try{
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            object = ois.readObject();
            ois.close();
            bis.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
        return object;
    }

    /**
     * 对象转json
     * @param obj Object
     * @return String
     */
    public static String objectToJson(Object obj){
        String json = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
//            JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(System.out, JsonEncoding.UTF8);
//            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            json = objectMapper.writeValueAsString(obj);

        }catch (IOException ex){
            ex.printStackTrace();
        }
        return json;
    }

    /**
     * json转hash
     * @param json String
     * @return HashMap
     */
    public static HashMap jsonToHash(String json){
        HashMap hashMap = null;
        try{
            ObjectMapper mapper = new ObjectMapper();
            hashMap = mapper.readValue(json, HashMap.class);
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return hashMap;
    }
}
