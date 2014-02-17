

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
	
	public static String httpRequestPostString(String urlPath,String postStr) throws Exception{
		return httpRequestPostString(urlPath,postStr,5000);
	}
	
	public static String httpRequestPostString(String urlPath,String postStr,int connectTimeout) throws Exception{ 
		String result = "";
		if (!"".equals(urlPath) && urlPath != null) {
		    URL url = new URL(urlPath);   
		    HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
		    conn.setDoOutput(true);  
		    conn.setDoInput(true);  
		    conn.setRequestMethod("POST");  
		    conn.setUseCaches(false);  
		    conn.setConnectTimeout(connectTimeout);  
		    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		    conn.setRequestProperty("Charset", "UTF-8");
		    conn.connect();
		    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		    byte[] requestStringBytes = postStr.getBytes("UTF-8");
		    dos.write(requestStringBytes);  
		    dos.flush();  
		    dos.close();  
		    if(conn.getResponseCode() == 200){  
		    	BufferedReader bufferReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    	String readLine = null;
		    	while((readLine = bufferReader.readLine()) != null){
		    		result += readLine;
		    	}
		    	bufferReader.close();
		    }  
		    conn.disconnect();
		}
        return result;  
    }
	
	public static String httpRequestGetString(String urlPath) throws Exception{
		return httpRequestGetString(urlPath,5000,5000);
	}
	
	public static String httpRequestGetString(String urlPath,int connectionTime,int socketTimeOutTime) throws Exception{
		String strResult = "";
		if (!"".equals(urlPath) && urlPath != null) {
			HttpGet httpRequest = new HttpGet(urlPath);
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTime);
			client.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, socketTimeOutTime);
			HttpResponse httpResponse = client.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				strResult = EntityUtils.toString(httpResponse.getEntity());
			} else {
				return "";
			}
			
		}
		return strResult;
	}
}
