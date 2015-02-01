package edu.stevens.cs522.chat.webservice;

import java.util.ArrayList;
import java.util.UUID;

import edu.stevens.cs522.R;
import edu.stevens.cs522.chat.providers.MessageContentProvider;
import edu.stevens.cs522.chatactivity.ClientActivity;
import edu.stevens.cs522.chatactivity.PreClientActivity;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.widget.Toast;

public class ServiceHelper {
	@SuppressWarnings("unused")
	private static final String TAG = ServiceHelper.class.getCanonicalName();
	
	public static final String RECEIVER_KEY = "receiver";
	public static final String CONTEXT_KEY = "context";
	public static final int REGISTER_SUCCESS = 1;
	public static final int REGISTER_FAILED = 2;
	
	private Context context;
	private Uri serverUri;
	private String clientName;
	private ConnectivityManager cm;
	private UUID uuid;
	
	public ServiceHelper(Context context, Uri uri, String name, UUID uuid){
		this.context = context;
		this.serverUri = uri;
		this.clientName = name;
		this.cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(uuid == null){
			this.uuid = UUID.randomUUID();
		}else{
			this.uuid = uuid;
		}

	}
	
	public boolean isOnline(){
		this.cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
	
	public void startRegisterService(){
		Intent request = new Intent(RequestService.REGISTER_ACTION);
		RegisterRequest rr = new RegisterRequest(0, uuid, serverUri, clientName);
		
		request.putExtra(RequestService.REGISTER_REQUEST_KEY, rr);
		request.putExtra(RECEIVER_KEY, new RegisterReceiver(new Handler()));
		context.startService(request);
	}
	
	public void startUnregisterService(long userId){
		Intent request = new Intent(RequestService.UNREGISTER_ACTION);
		UnregisterRequest ur = new UnregisterRequest(userId, uuid, serverUri);
		
		request.putExtra(RequestService.UNREGISTER_REQUEST_KEY, ur);
		context.startService(request);
	}
	
	public void startSyncService(long userId, long sequenceNumber, ArrayList<MessageContentProvider> msgList){
		if(isOnline()){
			Intent request = new Intent(RequestService.SYNCHRONIZE_ACTION);
			SynchronizeRequest sr = new SynchronizeRequest(userId, uuid, serverUri, clientName, sequenceNumber, msgList);
			request.putExtra(RequestService.SYNCHRONIZE_REQUEST_KEY, sr);
			context.startService(request);
		}else{
			Toast.makeText(context, "Cannot connect to internet, resent message 5 seconds later!", Toast.LENGTH_LONG).show();
		}
	}
	
	@SuppressWarnings({ "deprecation" })
	private void sendNotification(int id, String text){

		Notification notification = new Notification(R.drawable.ic_launcher, text, System.currentTimeMillis());
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_NO_CLEAR;

		String expandedText;
		Intent intent;
		if(id != 0){
			expandedText = "Register Success! Welcome " + this.clientName;
			intent = new Intent(context, ClientActivity.class);
		}else{
			expandedText = "Register Failed! Please try another usr name!";
			intent = new Intent(context, PreClientActivity.class);
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent lauchIntent = PendingIntent.getActivity(context, 0, intent, 0);
		notification.setLatestEventInfo(context, text, expandedText, lauchIntent);
		notificationManager.notify(id, notification);
	}
	
	protected void savePreferences(Long id, String clientName, UUID uuid){
		SharedPreferences mySharedPreferences = context.getSharedPreferences(PreClientActivity.MY_PREFS, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		
		editor.putLong(PreClientActivity.CLIENT_ID_KEY, id);
		editor.putString(PreClientActivity.CLIENT_NAME_KEY, clientName);
		editor.putString(PreClientActivity.CLIENT_UUID_KEY, uuid.toString());
		editor.putString(PreClientActivity.SERVER_URI_KEY, serverUri.toString());
		editor.commit();
	}
	
	public class RegisterReceiver extends ResultReceiver{

		public RegisterReceiver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			// TODO Auto-generated method stub
			super.onReceiveResult(resultCode, resultData);			
			long id = resultData.getLong(RequestService.REGISTER_ID_KEY);
			switch(resultCode){
			case REGISTER_SUCCESS:
				savePreferences(id, clientName, uuid);
				sendNotification(REGISTER_SUCCESS, "Register Success!");	
				break;
			case REGISTER_FAILED:
				sendNotification(REGISTER_FAILED, "Register Failed!");
				break;
			default:
				break;
			}
		}
		
	}
	


}

