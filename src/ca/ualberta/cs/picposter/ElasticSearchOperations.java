package ca.ualberta.cs.picposter;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.google.gson.Gson;

import ca.ualberta.cs.picposter.model.PicPostModel;

public class ElasticSearchOperations {
	public static void pushPicPostModel(final PicPostModel model) {
		// use a service if you do not want the information to be interrupted
		// this thread can be terminated by Android at any time
		Thread thread = new Thread() {
			@Override
			public void run(){
				Gson gson = new Gson();
				HttpClient client = new DefaultHttpClient();
				//HttpPut request = new HttpPut("http://cmput301.softwareprocess.es:8080/testing/dvyee/1");
				HttpPost request = new HttpPost("http://cmput301.softwareprocess.es:8080/testing/dvyee");
				
				try {
					request.setEntity(new StringEntity(gson.toJson(model)));
					HttpResponse response = client.execute(request);
					
					Log.e("ElasticSearch", response.getStatusLine().toString());
					
					HttpEntity entity = response.getEntity();
					BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
					
					String output = reader.readLine();
					while (output != null){
						Log.e("ElasticSeach", output);
						output = reader.readLine();
					}
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		};
		
		thread.start();
	}
}
