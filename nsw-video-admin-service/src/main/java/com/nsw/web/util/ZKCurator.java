package com.nsw.web.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKCurator {

	private CuratorFramework client = null;
	final static Logger log = LoggerFactory.getLogger(ZKCurator.class);
	
	public ZKCurator(CuratorFramework client) {
		this.client = client;
	}
	
	public void init() {
		client = client.usingNamespace("admin");
		
		try {
			//判断在admin命名空间下是否有bgm节点  -->/admin/bgm
			if(client.checkExists().forPath("/bgm") == null) {
				/**
				 * 对于zk来讲，有两种类型的节点：
				 * 持久节点：当你创建一个节点的时候，这个节点就永远存在了，除非手动删除
				 * 临时节点：创建一个节点之后，会话断开，会自动删除，当然也可以手动删除
				 */
				client.create().creatingParentsIfNeeded()//递归创建
				.withMode(CreateMode.PERSISTENT)  //节点类型：持久节点
				.withACL(Ids.OPEN_ACL_UNSAFE)     //acl:匿名权限
				.forPath("/bgm");                 //要操作的节点
				log.info("zookeeper初始化成功...");
				
				log.info("zookeeper服务器状态：{}",client.isStarted());
			}
		} catch (Exception e) {
			log.error("zookeeper客户端连接初始化错误...");
			e.printStackTrace();
		}
	}
	
	/**
	 * 增加或删除bgm,向zk-server创建子节点，供小程序后端监听
	 * @param bgmId
	 * @param operType
	 */
	public void sendBgmOperator(String bgmId,String operObj) {
		try {
			client.create().creatingParentsIfNeeded()//递归创建
				.withMode(CreateMode.PERSISTENT)  //节点类型：持久节点
				.withACL(Ids.OPEN_ACL_UNSAFE)     //acl:匿名权限
				.forPath("/bgm/" + bgmId, operObj.getBytes());    //要操作的节点
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	
}
