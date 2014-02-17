

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class SocketClientService implements IKSNetObserver{
	private Socket socketClient = null;  
	private String ipAddress = null;
	private int port;
	private static final long HEART_BEAT_RATE = 1 * 60 * 1000;
	private long maxPrivateServerId = 0L;
	private long maxPublicServerId = 0L;
	private Boolean isStartTimer = false;
	private Timer writeHeartTimer ;
	private TimerTask writeHeartTimerTask ;
	private volatile Boolean isExit = false;
	
	private String key = "";
	private GoPushSocketListener goPushSocketListener;
	private String domain = "";
	private long heartBeatTime = HEART_BEAT_RATE;
	
	public SocketClientService(String key,String domain,long maxPublicServerId,long maxPrivateServerId){
		this.key = key;
		this.domain = domain;
		this.maxPublicServerId = maxPublicServerId;
		this.maxPrivateServerId = maxPrivateServerId;
	}
	
	public void setGoPushSocketListener(GoPushSocketListener goPushSocketListener){
		this.goPushSocketListener = goPushSocketListener;
	}
	
	public void setGoPushSocketTime(long milliseconds){
		this.heartBeatTime = milliseconds;
	}
	
	private void startTimer(){  
        if (writeHeartTimer == null) {  
        	writeHeartTimer = new Timer();  
        }  
  
        if (writeHeartTimerTask == null) {  
        	writeHeartTimerTask = new TimerTask() {  
                @Override  
                public void run() { 
                	System.out.println("heartBeat run!");
        			writeData("h");
                }  
            };  
        }  
  
        if(writeHeartTimer != null && writeHeartTimerTask != null )  
        	writeHeartTimer.schedule(writeHeartTimerTask, 0,heartBeatTime);  
        isStartTimer = true;
    }
	
	private void stopTimer(){  
        
        if (writeHeartTimer != null) {  
        	writeHeartTimer.cancel();  
        	writeHeartTimer = null;  
        }  
  
        if (writeHeartTimerTask != null) {  
        	writeHeartTimerTask.cancel();  
        	writeHeartTimerTask = null;  
        }     
        isStartTimer = false;
    }
	
	private synchronized void disconnect() {

        if (socketClient != null) {

            try {
            	socketClient.close();
            } catch (IOException ex) {
            }
            socketClient = null;
        }
    }
	
	private synchronized Boolean getConnect(){
		int status = 0;
    	if(socketClient!=null) {
    		status += 1;
    	} else {
    		return false;
    	}
    	if(!socketClient.isClosed()) {
    		status += 2;
    	}
    	if(socketClient.isConnected()) {
    		status += 4;
    	}
    	if(socketClient.isBound()) {
    		status += 8;
    	}
    	if(!socketClient.isInputShutdown()) {
    		status += 16;
    	}
    	if(!socketClient.isOutputShutdown()) {
    		status += 32;
    	}
		return status == 63;
	}
	
	public void onCreate() {
		isExit = false;
		System.out.println("socket service onCreate!");
		new NetProcessThread(NewsConstant.KS_NET_SOCKET_CONNECTION_ACTION).start();
	}

	public void onDestroy() {
		isExit = true;
		if(isStartTimer){
			stopTimer();
		}
		goPushSocketListener.OnStop();
		System.out.println("socket service onDestroy!");
		disconnect();
	}

	class NetProcessThread extends Thread{
		private String mAction = "";
		
		public NetProcessThread(String action){
			mAction = action;
		}

		@Override
		public void run() {
			if(mAction.equals(NewsConstant.KS_NET_SOCKET_CONNECTION_ACTION)){
				susribeNode("http://"+domain+"/server/get?key="+key+"&proto=2");
				initSocketService();
			}
		}

	}
	
	private void obtainOfflineMessage(){
		try {
			String str = HttpUtils.httpRequestGetString("http://"+domain+"/msg/get?key="+key+"&mid="+maxPrivateServerId+"&pmid="+maxPublicServerId);
			System.out.println("maxPrivateServerId: " + maxPrivateServerId + "maxPublicServerId: " + maxPublicServerId+" offlineMessage: "+str);
			goPushSocketListener.OnOfflineMessage(str);
			readData();
		} catch (Exception e) {
			e.printStackTrace();
			onError(e,NewsConstant.KS_NET_EXCEPTION_OFFLINE_CODE);
		}
	}
	
	private void susribeNode(String url){
		try {
			String str = HttpUtils.httpRequestGetString(url);
			onSubsribeNodeResult(str);
		} catch (Exception e) {
			e.printStackTrace();
			onError(e,NewsConstant.KS_NET_EXCEPTION_SUBSCRIBE_CODE);
		}
	}
	
	private void initSocketService(){
		try {  
			socketClient = new Socket(ipAddress, port);  
			socketClient.setKeepAlive(true);
			socketClient.setSoTimeout((int)(2*heartBeatTime));
			onConnect();
        } catch (UnknownHostException e) {  
            e.printStackTrace();  
            onError(e,NewsConstant.KS_NET_EXCEPTION_SOCKET_CODE);
        } catch (IOException e) {  
            e.printStackTrace();
            onError(e,NewsConstant.KS_NET_EXCEPTION_SOCKET_CODE);
        }
	}

	@Override
	public void onSubsribeNodeResult(String resultStr) {
		try {
			HashMap<String,Object> hm = NetDataProcessUtils.getPortAddress(resultStr);
			if(hm != null){
				ipAddress = (String)hm.get(NewsConstant.KS_NET_KEY_ADDRESS);
				port = (Integer)hm.get(NewsConstant.KS_NET_KEY_PORT);
			}else{
				onError(null,NewsConstant.KS_NET_EXCEPTION_SUBSCRIBE_CODE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			onError(e,NewsConstant.KS_NET_EXCEPTION_SUBSCRIBE_CODE);
		}
	}

	private void readFirstData(){
		if(getConnect()){
			String readStr = "";
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
				if((readStr = br.readLine()) != null){
					handleFirstData(readStr,br);
				}else{
					onDisconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
				onError(e,NewsConstant.KS_NET_EXCEPTION_SOCKET_CODE);
			} 
		}
	}
	
	private void readData(){
		while(getConnect()){
			String readStr = "";
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
				while((readStr = br.readLine()) != null){
					handleData(readStr,br);
				}
				if(readStr == null){
					onDisconnect();
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
				if(!isExit)
					onError(e,NewsConstant.KS_NET_EXCEPTION_SOCKET_CODE);
			} 
		}
	}
	
	private void handleData(String readStr,BufferedReader br) throws IOException{
		if(readStr != null){
			socketClient.setSoTimeout((int)(2*heartBeatTime));
			System.out.println(readStr);
			if(readStr.startsWith("+")){
			}else if(readStr.startsWith("$")){
				String message = br.readLine();
				goPushSocketListener.OnOnlineMessage(message);
			}
			else if(readStr.startsWith("-")){
				onDisconnect();
			}
		}
	}
	
	private void handleFirstData(String readStr,BufferedReader br) throws IOException{
		if(readStr != null){
			socketClient.setSoTimeout((int)(2*heartBeatTime));
			System.out.println(readStr);
			if(readStr.startsWith("+")){
				goPushSocketListener.OnStart();
				startTimer();
				obtainOfflineMessage();
			}else if(readStr.startsWith("-")){
				onDisconnect();
			}
		}
	}
	
	private void writeData(String dataStr){
		if(getConnect()){
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
				bw.write(dataStr);
				bw.flush();
			} catch (IOException e) {
				e.printStackTrace();
				onError(e,NewsConstant.KS_NET_EXCEPTION_SOCKET_CODE);
			} 
		}
	}

	@Override
	public void onConnect() {
		writeData(getProtocol());
		readFirstData();
	}

	@Override
	public void onError(Exception error,int code) {
		goPushSocketListener.OnError(error);
		switch(code){
			case NewsConstant.KS_NET_EXCEPTION_SUBSCRIBE_CODE:
				System.out.println("subsribe node failed!");
				onDisconnect();
				break;
			case NewsConstant.KS_NET_EXCEPTION_SOCKET_CODE:
				System.out.println("scocket connected failed!");
				onDisconnect();
				break;
			case NewsConstant.KS_NET_EXCEPTION_OFFLINE_CODE:
				System.out.println("obtain offline message failed!");
				onDisconnect();
				break;
			default:
				break;
		}
	}

	@Override
	public void onDisconnect() {
		disconnect();
		if(isStartTimer){
			stopTimer();
		}
	}

	@Override
	public void onHeartBeatSuccess() {
	}
	
	private String getProtocol(){
		String str = "*3\r\n$3\r\nsub\r\n$"+key.length()+"\r\n"+key+"\r\n$2\r\n60\r\n";
		return str;
	}
	
}
