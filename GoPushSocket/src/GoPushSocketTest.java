
public class GoPushSocketTest {
	public static GoPushSocket goPushSocket;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		goPushSocket = new GoPushSocket("lym","114.112.93.13:80",0,0);
		goPushSocket.setGoPushSocketTime(3*1000);
		goPushSocket.setGoPushSocketListener(new GoPushSocketListener() {
			
			@Override
			public void OnStop() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void OnStart() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void OnOnlineMessage(String message) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void OnOfflineMessage(String message) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void OnError(Exception e) {
				// TODO Auto-generated method stub
				
			}
		});
		goPushSocket.start();
	}

}
