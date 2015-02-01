package edu.stevens.cs522.chatactivity;

import java.util.UUID;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import edu.stevens.cs522.R;
import edu.stevens.cs522.chat.contracts.ChatContract;
import edu.stevens.cs522.chat.webservice.ServiceHelper;

public class PreClientActivity extends Activity {
	@SuppressWarnings("unused")
	private static final String TAG = PreClientActivity.class.getCanonicalName();
	
	public static final String SERVER_URI_KEY = "server_uri";
	public static final String DEFAULT_SEVER_URI = "http://155.246.142.33:8080";
	public static final String CLIENT_NAME_KEY = "client_name";
	public static final String DEFAULT_CLIENT_NAME = "HongsenXin";
	public static final String CLIENT_ID_KEY = "client_id";
	public static final String CLIENT_UUID_KEY = "uuid";
	public static final String MY_PREFS = "mySharedPreferences";
	public static final long DEFAULT_CLIENT_ID = 0;
	public static final String DEFAULT_CHATROOM = "default";

	private String clientName;
	private long clientId;
	private UUID uuid;
	private Uri uri;
	private ServiceHelper helper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_client);
		
		this.loadPreferences();
		
		if(this.clientId != 0){
			((EditText) findViewById(R.id.client_name)).setText(this.clientName);
		}
		
		ContentResolver cr = getContentResolver();
		
		Cursor c = cr.query(ChatContract.CHATROOM_CONTENT_URI, null, null, null, null);
		if(c.getCount() == 0){
			ContentValues values = new ContentValues();
			values.put(ChatContract.CHATROOM_NAME, DEFAULT_CHATROOM);
			cr.insert(ChatContract.CHATROOM_CONTENT_URI, values);	
		}
		
		c = cr.query(ChatContract.CONTENT_URI, null, null, null, null);
		if(c.getCount() == 0){
			ContentValues values = new ContentValues();
			values.put(ChatContract.PEER_NAME, clientName);
			values.put(ChatContract.PEER_ADDRESS, "localhost");
			values.put(ChatContract.PEER_PORT, 8080);
			cr.insert(ChatContract.withExtendedPath(ChatContract.CONTENT_URI, Long.toString(1)), values);	
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.client_parent, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_unregister:
			removePreferences();
			helper = new ServiceHelper(this, uri, clientName, uuid);
			helper.startUnregisterService(this.clientId);
			this.clientId = DEFAULT_CLIENT_ID;
			this.clientName = DEFAULT_CLIENT_NAME;
			this.uri = Uri.parse(DEFAULT_SEVER_URI);
			this.uuid = null;
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void startClient(View v) {
		Uri uri = Uri.parse(((EditText) findViewById(R.id.server_uri)).getText().toString()); 
		String name = ((EditText) findViewById(R.id.client_name)).getText().toString();
		
		if(name.equals(this.clientName)){
			Intent intent = new Intent(this, ClientActivity.class);			
			startActivity(intent);
		}else{
			helper = new ServiceHelper(this, uri, name, null);
			
			helper.startRegisterService();

		}
	}
	
	private void loadPreferences(){
		SharedPreferences mySharedPreferences = this.getSharedPreferences(MY_PREFS, Activity.MODE_PRIVATE);
		this.clientId = mySharedPreferences.getLong(CLIENT_ID_KEY, 0);
		this.clientName = mySharedPreferences.getString(CLIENT_NAME_KEY, DEFAULT_CLIENT_NAME);
		String uri = mySharedPreferences.getString(SERVER_URI_KEY, PreClientActivity.DEFAULT_SEVER_URI);
		this.uri = Uri.parse(uri);
		String uuid = mySharedPreferences.getString(CLIENT_UUID_KEY, null);
		if(uuid != null){
			this.uuid = UUID.fromString(uuid);
		}else{
			this.uuid = UUID.randomUUID();
		}
	}
	
	protected void removePreferences(){  
		SharedPreferences mySharedPreferences = getSharedPreferences(PreClientActivity.MY_PREFS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.remove(PreClientActivity.CLIENT_ID_KEY);
		editor.remove(PreClientActivity.CLIENT_NAME_KEY);
		editor.remove(PreClientActivity.CLIENT_UUID_KEY);
		editor.remove(PreClientActivity.SERVER_URI_KEY);
		editor.commit();
	}
}
