package com.lvmama.phpsrv;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 测试thrift服务使用，暂时保留
 *
 */
public class App 
{
    @SuppressWarnings("resource")
	public static void main( String[] args ) throws Exception
    {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext-Thrift.xml" });
		context.start();
		System.in.read(); // 为保证服务一直开着，利用输入流的阻塞来模拟
    }
}
