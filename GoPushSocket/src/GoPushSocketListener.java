

public interface GoPushSocketListener {
	public void OnStart();
	public void OnStop();
	public void OnOfflineMessage(String message);
	public void OnOnlineMessage(String message);
	public void OnError(Exception e);
}
