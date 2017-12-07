package com.lvmama.phpcas;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.transport.TTransport;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.lvmama.phprpc.rpc.protocol.ThriftProtocol;

/**
 * thrift客户端封装类
 *
 */
public class TClient 
{
	/**
	 * 发送请求，参数和返回为字符串
	 * 
	 * @param T service
	 * @param String action
	 * @param String data
	 * @return String result
	 */
	public static <T> String exec(T service, String action, String data) throws Exception, Throwable {
		String result = null;
        Class<?> cls = service.getClass();
        Class<?>[] ifaces = cls.getInterfaces();
        Class<?> iface = ifaces[ifaces.length - 1];
		Method method = cls.getMethod(action, String.class);
		synchronized (iface) {
	        try{
	        	TTransport ttransport = ThriftProtocol.ttransport.get(iface.getName());
	        	if (!ttransport.isOpen()) ttransport.open();
		        result = (String) method.invoke(service, data);
	        	if (ttransport.isOpen()) ttransport.close();
	        }catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		return result;
    }
	
	/**
	 * 发送请求，参数和返回为数组
	 * 
	 * @param T service
	 * @param String action
	 * @param String data
	 * @return String result
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static <T> ArrayList<T> exec(T service, String action, ArrayList<T> data) throws Exception, Throwable {
		ArrayList<T> result = null;
		JSONArray jArray = JSONArray.fromObject(TClient.exec(service, action, JSONArray.fromObject(data).toString()));
		result = (ArrayList<T>) JSONArray.toList(jArray);
		return result;
    }
	
	/**
	 * 发送请求，参数和返回为对象
	 * 
	 * @param T service
	 * @param String action
	 * @param String data
	 * @return String result
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> exec(T service, String action, List<T> data) throws Exception, Throwable {
		List<T> result = null;
		JSONObject jObject = JSONObject.fromObject(TClient.exec(service, action, JSONObject.fromObject(data).toString()));
		result = (List<T>) JSONObject.toBean(jObject);
		return result;
    }
	
	/**
	 * 发送请求，参数和返回为字符串
	 * 
	 * @param T service
	 * @param Class<?> clazz
	 * @param String action
	 * @param String data
	 * @return String result
	 */
	public static <T> String exec(T service, Class<?> clazz, String action, String data) throws Exception, Throwable {
		String result = null;
        Class<?> cls = service.getClass();
		Method method = cls.getMethod(action, String.class);
		synchronized (clazz) { 
	        try{
	        	TTransport ttransport = ThriftProtocol.ttransport.get(clazz.getName());
	        	if (!ttransport.isOpen()) ttransport.open();
		        result = (String) method.invoke(service, data);
	        	if (ttransport.isOpen()) ttransport.close();
	        }catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		return result;
    }
	
	/**
	 * 发送请求，参数和返回为数组
	 * 
	 * @param T service
	 * @param Class<?> clazz
	 * @param String action
	 * @param String data
	 * @return String result
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static <T> ArrayList<T> exec(T service, Class<?> clazz, String action, ArrayList<T> data) throws Exception, Throwable {
		ArrayList<T> result = null;
		JSONArray jArray = JSONArray.fromObject(TClient.exec(service, clazz, action, JSONArray.fromObject(data).toString()));
		result = (ArrayList<T>) JSONArray.toList(jArray);
		return result;
    }
	
	/**
	 * 发送请求，参数和返回为对象
	 * 
	 * @param T service
	 * @param Class<?> clazz
	 * @param String action
	 * @param String data
	 * @return String result
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> exec(T service, Class<?> clazz, String action, List<T> data) throws Exception, Throwable {
		List<T> result = null;
		JSONObject jObject = JSONObject.fromObject(TClient.exec(service, clazz, action, JSONObject.fromObject(data).toString()));
		result = (List<T>) JSONObject.toBean(jObject);
		return result;
    }
}
