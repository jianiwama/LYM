

public interface IKSNetObserver {
	public void onSubsribeNodeResult(String resultStr);
	public void onHeartBeatSuccess();
	public void onConnect();
	public void onDisconnect();
	public void onError(Exception error,int code);
}
