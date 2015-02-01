package edu.stevens.cs522.chat.service;

import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ResultReceiver;
import edu.stevens.cs522.chat.client.ChatProvider;
import edu.stevens.cs522.chat.interfaces.MessageProvider;

public class IChatSenderService extends Service {
	private final IBinder binder = new MyBinder();
	ResultReceiver resultReceiver;
	int clientPort;
	String TAG="sender";
	public IChatSenderService() {
		super();
	}
	public class MyBinder extends Binder {
		public IChatSenderService getService() {
	      return IChatSenderService.this;
	    }
	};
	@SuppressWarnings("unused")
	private class MessageHandler extends Handler{

		  @SuppressLint("HandlerLeak")
		public MessageHandler(ChatReceiverService chatReceiverService,
				Looper messengerLooper) {
			  super(messengerLooper);
			
		}

		public void handleMessage(Message message) {
		    Bundle data = message.getData();
		    ResultReceiver resultReceiver = data.getParcelable(null);
		}
	};
	@Override
	public IBinder onBind(Intent intent) {
		resultReceiver=(ResultReceiver)intent.getExtras().getParcelable("receiver");
		return binder;
	}
	
	public void sendmessage(Intent intent) {
		SendBackgroud SendTask= new SendBackgroud();
		SendTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intent.getExtras());
		
	}
	class SendBackgroud extends AsyncTask<Bundle, Void, Void> {

		@Override
		protected Void doInBackground(Bundle... bundles) {
			try{
				MessageProvider msg=new MessageProvider(0, bundles[0].getString("text"), bundles[0].getString("name"),1,new Date().getTime());
				ContentValues values=new ContentValues();
				msg.writeToProvider(values);
				getContentResolver().insert(ChatProvider.CONTENT_URI, values);
				resultReceiver.send(1, null);

			} catch (Exception e) {
				e.printStackTrace();
				//new Toast(this).makeText(this, "Port taken", Toast.LENGTH_SHORT).show();
			}finally{
				
			}
			return null;
		}
		
	}
}
