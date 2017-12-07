package com.lvmama.phprpc.remoting.zookeeper;

import java.util.List;

import com.lvmama.phprpc.common.URL;

public interface ZookeeperClient {

	void create(String path, boolean ephemeral);

	void delete(String path);

	List<String> getChildren(String path);

	List<String> addChildListener(String path, ChildListener listener);

	void removeChildListener(String path, ChildListener listener);

	void addStateListener(StateListener listener);
	
	void removeStateListener(StateListener listener);

	boolean isConnected();

	void close();

	URL getUrl();

}
