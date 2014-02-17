/**
 * 
 * @author liuyueming
 *
 */

public class GoPushSocket {
	private SocketClientServiceHelper instance;
	/**
	 *GoPushSocket的构造函数，传入context、key、domain
	 * @param context
	 * @param key 任意不为空字符串
	 * @param domain 长连接的域名
	 * @param maxPublicServerId 最大公信ID
	 * @param maxPrivateServerId 最大私信ID
	 */
	public GoPushSocket(String key,String domain,long maxPublicServerId,long maxPrivateServerId){
		instance = new SocketClientServiceHelper(key,domain,maxPublicServerId,maxPrivateServerId);
	}
	/**
	 * 设置GoPushSocket的监听器，用来监听连接开始、停止、离线消息、在线消息和异常错误
	 * @param webSocketListener
	 */
	public void setGoPushSocketListener(GoPushSocketListener webSocketListener){
		instance.setGoPushSocketListener(webSocketListener);
	}
	/**
	 * 设置GoPushSocket的心跳时间，默认是一分钟，设置时间最好大于30秒
	 * @param milliseconds
	 */
	public void setGoPushSocketTime(long milliseconds){
		instance.setGoPushSocketTime(milliseconds);
	}
	/**
	 * 开始长连接
	 */
	public void start(){
		instance.startService();
	}
	/**
	 * ͣ停止长连接
	 */
	public void stop(){
		instance.stopService();
	}
}
