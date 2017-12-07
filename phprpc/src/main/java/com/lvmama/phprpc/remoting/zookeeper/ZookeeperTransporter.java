package com.lvmama.phprpc.remoting.zookeeper;

import com.lvmama.phprpc.common.Constants;
import com.lvmama.phprpc.common.URL;
import com.lvmama.phprpc.common.extension.Adaptive;
import com.lvmama.phprpc.common.extension.SPI;

@SPI("zkclient")
public interface ZookeeperTransporter {

	@Adaptive({Constants.CLIENT_KEY, Constants.TRANSPORTER_KEY})
	ZookeeperClient connect(URL url);

}
