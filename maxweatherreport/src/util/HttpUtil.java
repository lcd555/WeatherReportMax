package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;

public class HttpUtil {//����������
	
	private static String rep;
	 public static void sendHttpRequest(final String address, final unusual listener) {
	        new Thread(new Runnable() {
	        	
	            public void run() {
	            	
	                HttpURLConnection connection = null;
	                try {
	                	
	                    URL url = new URL(address);//��ȡ��ҳurl
	                    
	                    connection = (HttpURLConnection) url.openConnection();
	                    connection.setRequestMethod("GET");
	                    connection.setConnectTimeout(8000);
	                    connection.setReadTimeout(8000);
	                    connection.setRequestProperty("apikey", "CQeerRWXqN4U0LoPQMYsAYYaiG1v8ge0");
	                    connection.connect();
	                    //���������ݷ���response
	                    InputStream in = connection.getInputStream();
	                    BufferedReader reader = new BufferedReader(
	                            new InputStreamReader(in, "UTF-8"));
	                    StringBuilder response = new StringBuilder();
	                   
	                    String line;
	                    while ((line = reader.readLine()) != null) {
	                        response.append(line);
	                    }
	                    
	                    if (listener != null) {
	                        listener.onFinish(response.toString());
	                    }
	                } catch (Exception e) {
	                    if (listener != null) {
	                    	//listener.onFinish("");
	                        listener.onError(e);
	                    }
	                } finally {
	                    if (connection != null) {
	                        connection.disconnect();
	                    }
	                }
	            }
	        }).start();//�߳̿�ʼ
	    }
	public static void setRep(String r){
		
		rep=r;
		
	}
	public static String getRep(){
		return rep;
	}
	
}
