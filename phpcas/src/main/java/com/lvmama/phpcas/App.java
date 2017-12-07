package com.lvmama.phpcas;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lvmama.phpcas.Service.StaffService;

/**
 * 测试thrift服务使用，暂时保留
 *
 */
public class App 
{
    @SuppressWarnings("resource")
	public static <T> void main( String[] args ) throws Exception
    {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "applicationContext-Client.xml" });
		context.start();

		StaffService.Iface staffService = (StaffService.Iface) context.getBean("staffService");
		try {
			String list = TClient.exec(staffService, "listAction", "{\"id\":\"3\"}");
			System.out.println(list);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.in.read(); // 为保证服务一直开着，利用输入流的阻塞来模拟
    }
}
