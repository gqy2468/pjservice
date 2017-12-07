package com.lvmama.phprpc.remoting.zookeeper.zkclient;

import com.lvmama.phprpc.common.URL;
import com.lvmama.phprpc.remoting.zookeeper.ZookeeperClient;
import com.lvmama.phprpc.remoting.zookeeper.ZookeeperTransporter;

public class ZkclientZookeeperTransporter implements ZookeeperTransporter {

	public ZookeeperClient connect(URL url) {
		return new ZkclientZookeeperClient(url);
	}

}
