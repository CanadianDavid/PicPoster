package ca.ualberta.cs.picposter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import ca.ualberta.cs.picposter.controller.PicPosterController;
import ca.ualberta.cs.picposter.model.PicPostModel;
import ca.ualberta.cs.picposter.model.PicPosterModelList;
import ca.ualberta.cs.picposter.view.PicPostModelAdapter;

public class PicPosterActivity extends Activity {

	private final Activity activity = this;
	public static final int OBTAIN_PIC_REQUEST_CODE = 117;

	// Http Connector
	private HttpClient httpclient = new DefaultHttpClient();
	// JSON Utilities
	private Gson gson = new Gson();

	EditText searchPostsEditText;
	ImageView addPicImageView;
	EditText addPicEditText;
	ListView picPostList;

	private Bitmap currentPicture;
	PicPosterModelList model;
	PicPosterController controller;
	PicPostModelAdapter adapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		this.searchPostsEditText = (EditText)this.findViewById(R.id.search_posts_edit_text);
		this.addPicImageView = (ImageView)this.findViewById(R.id.add_pic_image_view);
		this.addPicEditText = (EditText)this.findViewById(R.id.add_pic_edit_text);
		this.picPostList = (ListView)this.findViewById(R.id.pic_post_list);

		this.model = new PicPosterModelList();
		this.controller = new PicPosterController(this.model, this);
		this.adapter = new PicPostModelAdapter(this, R.layout.pic_post, model.getList());

		this.picPostList.setAdapter(this.adapter);
		this.model.setAdapter(this.adapter);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == OBTAIN_PIC_REQUEST_CODE && resultCode == RESULT_OK) {
			this.currentPicture = (Bitmap)data.getExtras().get("data");
			this.addPicImageView.setImageBitmap(this.currentPicture);
		}
	}


	public void obtainPicture(View view) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, OBTAIN_PIC_REQUEST_CODE);
	}


	public void pushPicture(View view) {
		this.controller.addPicPost(this.currentPicture, this.addPicEditText.getText().toString());
		this.addPicEditText.setText(null);
		this.addPicEditText.setHint(R.string.add_pic_edit_text_hint);
		this.addPicImageView.setImageResource(R.drawable.camera);
		this.currentPicture = null;
	}
	
	/*
	 * 
	 * 	public void searchsearchRecipes(String str) throws ClientProtocolException, IOException {
		HttpPost searchRequest = new HttpPost("http://cmput301.softwareprocess.es:8080/testing/lab02/_search?pretty=1");
		String query = 	"{\"query\" : {\"query_string\" : {\"default_field\" : \"ingredients\",\"query\" : \"" + str + "\"}}}";
		StringEntity stringentity = new StringEntity(query);

		searchRequest.setHeader("Accept","application/json");
		searchRequest.setEntity(stringentity);

		HttpResponse response = httpclient.execute(searchRequest);
		String status = response.getStatusLine().toString();
		System.out.println(status);

		String json = getEntityContent(response);

		Type elasticSearchSearchResponseType = new TypeToken<ElasticSearchSearchResponse<Recipe>>(){}.getType();
		ElasticSearchSearchResponse<Recipe> esResponse = gson.fromJson(json, elasticSearchSearchResponseType);
		System.err.println(esResponse);
		for (ElasticSearchResponse<Recipe> r : esResponse.getHits()) {
			Recipe recipe = r.getSource();
			System.err.println(recipe);
		}
		searchRequest.releaseConnection();
	}	
	 */


	public void searchPosts(View view) {
		
		new Thread() {
			@Override
			public void run(){
				activity.runOnUiThread(new Runnable(){
						@Override
						public void run(){
							try{
								String searchTerm = searchPostsEditText.getText().toString();
								
								//TODO : perform search, update model, etc
								// from ESClient.java ; ESDemo https://github.com/rayzhangcl/ESDemo
								HttpPost searchRequest = new HttpPost("http://cmput301.softwareprocess.es:8080/testing/dvyee/_search?pretty=1");
								String query = 	"{\"query\" : {\"query_string\" : {\"default_field\" : \"text\",\"query\" : \"" + searchTerm + "\"}}}";
								StringEntity stringentity = new StringEntity(query);
						
								searchRequest.setHeader("Accept","application/json");
								searchRequest.setEntity(stringentity);
						
								HttpResponse response = httpclient.execute(searchRequest);
								String status = response.getStatusLine().toString();
								Log.e("SearchPost",status);
						
								String json = getEntityContent(response);
						
								Type elasticSearchSearchResponseType = new TypeToken<ElasticSearchSearchResponse<PicPostModel>>(){}.getType();
								ElasticSearchSearchResponse<PicPostModel> esResponse = gson.fromJson(json, elasticSearchSearchResponseType);
								Log.e("SearchPost","we are here");
								Log.e("SearchPost",esResponse.toString());
								for (ElasticSearchResponse<PicPostModel> r : esResponse.getHits()) {
									PicPostModel picpost = r.getSource();
									Log.e("SearchPost",picpost.getText());
								}
								//searchRequest.releaseConnection();
								
								searchPostsEditText.setText(null);
								searchPostsEditText.setHint(R.string.search_posts_edit_text_hint);
							}
							catch(Exception e){
								e.printStackTrace();
							}
						}
						});
			}
		}.start();
		
		/*
		PicPosterActivity.this.runOnUiThread(new Runnable(){
			public void run(){
				try{
					String searchTerm = searchPostsEditText.getText().toString();
					
					//TODO : perform search, update model, etc
					// from ESClient.java ; ESDemo https://github.com/rayzhangcl/ESDemo
					HttpPost searchRequest = new HttpPost("http://cmput301.softwareprocess.es:8080/testing/dvyee/_search?pretty=1");
					String query = 	"{\"query\" : {\"query_string\" : {\"default_field\" : \"text\",\"query\" : \"" + searchTerm + "\"}}}";
					StringEntity stringentity = new StringEntity(query);
			
					searchRequest.setHeader("Accept","application/json");
					searchRequest.setEntity(stringentity);
			
					HttpResponse response = httpclient.execute(searchRequest);
					String status = response.getStatusLine().toString();
					Log.e("SearchPost",status);
			
					String json = getEntityContent(response);
			
					Type elasticSearchSearchResponseType = new TypeToken<ElasticSearchSearchResponse<PicPostModel>>(){}.getType();
					ElasticSearchSearchResponse<PicPostModel> esResponse = gson.fromJson(json, elasticSearchSearchResponseType);
					Log.e("SearchPost","we are here");
					Log.e("SearchPost",esResponse.toString());
					for (ElasticSearchResponse<PicPostModel> r : esResponse.getHits()) {
						PicPostModel picpost = r.getSource();
						Log.e("SearchPost",picpost.getText());
					}
					//searchRequest.releaseConnection();
					
					searchPostsEditText.setText(null);
					searchPostsEditText.setHint(R.string.search_posts_edit_text_hint);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			});
			*/
	}
	
	/**
	 * get the http response and return json string
	 */
	String getEntityContent(HttpResponse response) throws IOException {
		BufferedReader br = new BufferedReader(
				new InputStreamReader((response.getEntity().getContent())));
		String output;
		Log.e("SearchPostError", "Output from Server -> ");
		String json = "";
		while ((output = br.readLine()) != null) {
			System.err.println(output);
			json += output;
		}
		Log.e("SearchPostError","JSON:"+json);
		return json;
	}
}