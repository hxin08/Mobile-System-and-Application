package edu.stevens.cs522.chat.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.util.Log;
import edu.stevens.cs522.chat.client.ChatProvider;
import edu.stevens.cs522.chat.interfaces.MessageProvider;
import edu.stevens.cs522.chat.interfaces.PeerProvider;

public class ChatReceiverService extends Service {
	private final IBinder binder = new MyBinder();
	//private ChatReceiverService service;
	public Messenger messenger;
	HandlerThread messengerThread;
	//private Handler handler;
	ResultReceiver resultReceiver;
	final static public String TAG = CloudChatApp.class.getCanonicalName();
	int port=6666;
	public class MyBinder extends Binder {
		public ChatReceiverService getService() {
	      return ChatReceiverService.this;
	    }
	};
	private class ChatAsyncTask extends AsyncTask<String,Integer,Integer>{

		@Override
		protected Integer doInBackground(String... arg0) {
			 int myProgress = 0;
			 byte[] receiveData = new byte[1024];
			 DatagramSocket serverSocket; 
			 DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			 
				try {
					String []res;
					serverSocket= new DatagramSocket(port);
					serverSocket.receive(receivePacket);
					Log.i(TAG, "Received a packet");

					InetAddress sourceIPAddress = receivePacket.getAddress();
					Log.i(TAG, "Source IP Address: " + sourceIPAddress);
					
					/*
					 * TODO: Extract sender and receiver from message and display.
					 */
					receiveData=receivePacket.getData();
					res=(new String(receiveData, 0, receivePacket.getLength())).split(":");
					MessageProvider m=new MessageProvider(1, res[1], res[0],1,new Date().getTime());
					PeerProvider p=new PeerProvider(res[0], 1);
					if(!addMessage(m)){
						Log.e("addMessge", "fail");
					};
					if(!addPeer(p)){
						Log.e("addPeer", "fail");
					}
					serverSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}finally{
						resultReceiver.send(1, null);
					}
				
			 publishProgress(myProgress);
			 receiveMessage();
			 return myProgress;
		}
		@Override
		protected void onProgressUpdate(Integer... progress) {
			// Update progress bar, Notification, or other UI element 	
		}
		@SuppressWarnings("unused")
		protected void onPostExecute(Integer... result) {
			// Report results via UI update, Dialog, or notification 
		}
		public boolean addMessage(MessageProvider b) {
			ContentValues contentValues = new ContentValues();
			b.writeToProvider(contentValues);
			getContentResolver().insert(ChatProvider.CONTENT_URI, contentValues);
			return true;
		}

		public boolean addPeer(PeerProvider b) {
			ContentValues contentValues = new ContentValues();
			b.writeToProvider(contentValues);
			getContentResolver().delete(
					ChatProvider.CONTENT_URI_PEER,
					"name ='" + b.name + "' and " + "senderid ='"
							+ b.senderid + "'", null);
			getContentResolver().insert(ChatProvider.CONTENT_URI_PEER,
					contentValues);
			return true;
		}
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//Bundle bundle=getIntent().getExtras();
		receiveMessage();
	}
	public void receiveMessage() {
		  ChatAsyncTask reTask= new ChatAsyncTask();
		  reTask.execute(new String[] { "null" });
	}
	public ChatReceiverService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		port=intent.getExtras().getInt("port");
		resultReceiver=(ResultReceiver)intent.getExtras().getParcelable("receiver");
		return binder;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	

	}