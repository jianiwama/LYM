

public class SocketClientServiceHelper {

	public boolean isServiceStarted = false;
	private SocketClientService socketClientService = null;

    public void setGoPushSocketListener(GoPushSocketListener webSocketListener){
    	socketClientService.setGoPushSocketListener(webSocketListener);
	}
    
    public void setGoPushSocketTime(long milliseconds){
    	socketClientService.setGoPushSocketTime(milliseconds);
	}
    
    public SocketClientServiceHelper(String key,String domain,long maxPublicServerId,long maxPrivateServerId) {
        socketClientService = new SocketClientService(key,domain,maxPublicServerId,maxPrivateServerId);
    }
    
    public synchronized void startService(){    
        if(!isServiceStarted) {
            isServiceStarted = true;
            socketClientService.onCreate();
        }
    }
    
    public synchronized void stopService(){
        if(isServiceStarted) {
            isServiceStarted = false;
            
            socketClientService.onDestroy();
            socketClientService = null;
        }
    }
}
