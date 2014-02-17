

import java.util.HashMap;

public class NetDataProcessUtils {

	public static HashMap<String,Object> getPortAddress(String jsonStr) throws Exception{
		HashMap<String,Object> hm = new HashMap<String,Object>();
		JSONObject jo = new JSONObject(jsonStr);
		int ret = jo.getInt(NewsConstant.KS_NET_JSON_KEY_RET);
		if(ret == NewsConstant.KS_NET_STATE_OK){
			JSONObject jot = jo.getJSONObject(NewsConstant.KS_NET_JSON_KEY_DATA);
			String server = jot.getString(NewsConstant.KS_NET_JSON_KEY_SERVER);
			String[] strs = server.split(":");
			hm.put(NewsConstant.KS_NET_KEY_ADDRESS, strs[0]);
			hm.put(NewsConstant.KS_NET_KEY_PORT, Integer.parseInt(strs[1]));
		}else{
			return null;
		}
		return hm;
	}
	
}
