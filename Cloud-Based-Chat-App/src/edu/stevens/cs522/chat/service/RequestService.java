package edu.stevens.cs522.chat.service;

import android.app.IntentService;
import android.content.Intent;
import edu.stevens.cs522.chat.servicehelper.Register;
import edu.stevens.cs522.chat.servicehelper.Synchronize;
import edu.stevens.cs522.chat.servicehelper.Unregister;

public class RequestService extends IntentService{

	public RequestService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	public void perform(Register request) { 
		
	} 
	public void perform(Synchronize request) { 
	} 
	public void perform(Unregister request) { 
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		int type=intent.getExtras().getInt("type");
		switch (type) {
		case 0:
			@SuppressWarnings("unused")
			Register reg=intent.getExtras().getParcelable("request");
			break;
		case 1:
			@SuppressWarnings("unused")
			Synchronize sync=intent.getExtras().getParcelable("request");
			break;
		case 2:
			@SuppressWarnings("unused")
			Unregister unreg=intent.getExtras().getParcelable("request");
			break;
		default:
			break;
		}
	}
}
