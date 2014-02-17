GoPushSocket
===
public static GoPushSocket goPushSocket;

public static void main(String[] args) {
	// TODO Auto-generated method stub
	
	goPushSocket = new GoPushSocket(key,domain,maxPublicServerId,maxPrivateServerId);//初始化GoPushSocket
	goPushSocket.setGoPushSocketTime(HEART_BEAT_TIME);//设置心跳时间
	goPushSocket.setGoPushSocketListener(new GoPushSocketListener() {
		
		@Override
		public void OnStop() {
			// TODO Auto-generated method stub 长连接停止
			
		}
		
		@Override
		public void OnStart() {
			// TODO Auto-generated method stub 长连接开始
			
		}
		
		@Override
		public void OnOnlineMessage(String message) {
			// TODO Auto-generated method stub 在线消息
			
		}
		
		@Override
		public void OnOfflineMessage(String message) {
			// TODO Auto-generated method stub 离线消息
			
		}
		
		@Override
		public void OnError(Exception e) {
			// TODO Auto-generated method stub 异常
			
		}
	});
	goPushSocket.start();//开始长连接
}




//停止长连接
goPushSocket.stop();