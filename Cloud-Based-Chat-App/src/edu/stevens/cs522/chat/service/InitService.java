package edu.stevens.cs522.chat.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.ResultReceiver;
import edu.stevens.cs522.chat.client.ChatProvider;

public class InitService extends IntentService{
	ResultReceiver resultReceiver;
	public InitService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	public InitService() {
		super("initservice");
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle b=intent.getExtras();
		resultReceiver=(ResultReceiver)intent.getExtras().getParcelable("receiver");
		// TODO Auto-generated method stub
		try{
		 Cursor c=getContentResolver().query(ChatProvider.CONTENT_URI_PEER, null, null, new String[]{
			    	b.getString("name"),b.getString("port"),b.getString("host"),b.getString("uuid")}, null);		 
		 if (c==null){
			 resultReceiver.send(1, null);
		 }else resultReceiver.send(0, null);
		}catch(Exception e){
			resultReceiver.send(0, null);
		} 
	}
}
