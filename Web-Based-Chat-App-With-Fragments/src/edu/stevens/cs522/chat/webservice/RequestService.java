package edu.stevens.cs522.chat.webservice;

import edu.stevens.cs522.chat.interfaces.ICallBack;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public class RequestService extends IntentService {
	private static final String TAG = RequestService.class.getCanonicalName();
	public static final String REGISTER_ACTION = "edu.stevens.cs522.chat.register";
	public static final String SYNCHRONIZE_ACTION = "edu.stevens.cs522.chat.synchronize";
	public static final String UNREGISTER_ACTION = "edu.stevens.cs522.chat.unregister";
	public static final String REGISTER_REQUEST_KEY = "register";
	public static final String SYNCHRONIZE_REQUEST_KEY = "synchronize";
	public static final String UNREGISTER_REQUEST_KEY = "unregister";
	public static final String REGISTER_ID_KEY = "register_id";
	
	private RequestProcessor requestProcessor;
	private Long ClientId;
	
	public RequestService() {
		super(TAG);
		// TODO Auto-generated constructor stub
		ClientId = 0l;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
	
		ContentResolver cr = this.getContentResolver();

		this.requestProcessor = new RequestProcessor(cr, new ICallBack(){
			@Override
			public void feedback(long id) {
				// TODO Auto-generated method stub
				ClientId = id;
			}});
		
		if(intent.getAction().equalsIgnoreCase(REGISTER_ACTION)){
			requestProcessor.perform(( RegisterRequest) intent.getParcelableExtra(REGISTER_REQUEST_KEY));
			
			ResultReceiver resultReceiver = intent.getParcelableExtra(ServiceHelper.RECEIVER_KEY);
			Bundle result = new Bundle();
			result.putLong(REGISTER_ID_KEY, ClientId);
			if(ClientId != 0){
				resultReceiver.send(ServiceHelper.REGISTER_SUCCESS, result);
			}else{
				resultReceiver.send(ServiceHelper.REGISTER_FAILED, result);
			}
		}else if(intent.getAction().equalsIgnoreCase(SYNCHRONIZE_ACTION)){
			requestProcessor.perform(( SynchronizeRequest) intent.getParcelableExtra(SYNCHRONIZE_REQUEST_KEY));
		}else if(intent.getAction().equalsIgnoreCase(UNREGISTER_ACTION)){
			requestProcessor.perform(( UnregisterRequest) intent.getParcelableExtra(UNREGISTER_REQUEST_KEY));
		}else{
			
		}
	}	
}
