/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2012 Stevens Institute of Technology

 **********************************************************************/
package edu.stevens.cs522.chat.service;

import java.net.DatagramSocket;
import java.util.ArrayList;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import edu.stevens.cs522.chat.client.ChatProvider;
import edu.stevens.cs522.chat.interfaces.MessageProvider;
import edu.stevens.cs522.chat.interfaces.Settings;
import edu.stevens.cs522.chat.interfaces.PeerProvider;
import edu.stevens.cs522.chat.interfaces.ShowPeersActivity;
import edu.stevens.cs522.chat.service.R;

public class CloudChatApp extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final int MY_LOADER_ID = 0;
	private static final int MY_LOADER_ID2 = 1;
	public String CLIENT_NAME_KEY = "un";
	public String CLIENT_PORT_KEY = "pt";
	private String clientName;
	private int clientPort;
	public static String clientHost;
	final static public String TAG = CloudChatApp.class.getCanonicalName();
	@SuppressWarnings("unused")
	private ServiceConnection connection;
	private ServiceConnection connectionSender;
	@SuppressWarnings("unused")
	private ChatReceiverService service;
	private IChatSenderService senderservice;
	Messenger messenger;
	/*
	 * Socket used both for sending and receiving
	 */
	private DatagramSocket serverSocket;
	DataBaseAdapter da;
	/*
	 * True as long as we don't get socket errors
	 */
	private boolean socketOK = true;
	private/*
			 * TODO: Declare UI.
			 */
	ArrayList<String> messageList;
	ArrayAdapter<String> adapter;
	// DbAdapter da;
	public SimpleCursorAdapter sca;
	/*
	 * End Todo
	 */
	String[] from = new String[] { DataBaseAdapter.SENDER, DataBaseAdapter.TEXT };
	int[] to = new int[] { R.id.cart_row_sender, R.id.cart_row_message };
	Button next;

	private EditText messageText;
	
	
	
	
	public class AckReceiver extends ResultReceiver {
		public AckReceiver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}
		@SuppressWarnings("deprecation")
		protected void onReceiveResult(int resultCode, Bundle result) {
			Context context2 = getApplicationContext();
			int duration2 = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context2, "Message received.", duration2);
			toast.show();
			sca.getCursor().requery();
		}
	};
	
	
	public class ResultReceiverWrapper extends ResultReceiver {
		  public ResultReceiverWrapper(Handler handler) {
		    super(handler);
		}
		  private IReceiver receiver;
		  public void setReceiver(IReceiver receiver) {
			  this.receiver = receiver;
		  }
		  protected void onReceiveResult(int resultCode, Bundle data) {
			  if (receiver != null) {
			    receiver.onReceiveResult(resultCode, data);
			    Context context2 = getApplicationContext();
				int duration2 = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context2, "Message sended.", duration2);
				toast.show();
			  } 
		  }
	}
	public interface IReceiver {
		public void onReceiveResult(int resultCode,Bundle resultData);
	};
	/*
	 * Called when the activity is first created.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Intent callingIntent = getIntent();
		clientName=callingIntent.getExtras().getString(Settings.NAME);
		clientHost=callingIntent.getExtras().getString(Settings.HOST);
		clientPort=callingIntent.getExtras().getInt(Settings.PORT);
		connection = new ServiceConnection() { 
			public void onServiceConnected(ComponentName className, IBinder binder) {
			// Called when the connection is made.
				service = ((ChatReceiverService.MyBinder)binder).getService();
	//			messenger = new Messenger(binder);
			}
			public void onServiceDisconnected(ComponentName className) {
				service = null;
				//messenger=null;
			}
		};
		connectionSender = new ServiceConnection() { 
			public void onServiceConnected(ComponentName className, IBinder binder) {
			// Called when the connection is made.
				senderservice = ((IChatSenderService.MyBinder)binder).getService();
	//			messenger = new Messenger(binder);
			}
			public void onServiceDisconnected(ComponentName className) {
				senderservice = null;
				//messenger=null;
			}
		};

			messageText = (EditText) findViewById(R.id.message_text);
		
		/*
		 * TODO: Initialize the UI.
		 */
		try {
			 Cursor c = getContentResolver().query(ChatProvider.CONTENT_URI, ChatProvider.projection, null,null, null);
			 if(c==null){
				 Toast.makeText(getBaseContext(), "Please relogin", Toast.LENGTH_LONG).show();
				 finish();
			 }
			sca= new SimpleCursorAdapter(this, R.layout.message, c, from, to);
			getListView().setAdapter(sca);
		} catch (Exception e) {
			Log.e(e.getClass().getName(), e.getMessage());
		}
		Intent sendIntent=new Intent(this,IChatSenderService.class);
		sendIntent.putExtra("receiver",new AckReceiver(new Handler()));
		bindService(sendIntent, connectionSender, Context.BIND_AUTO_CREATE);
		/**
		 * Let's be clear, this is a HACK to allow you to do network
		 * communication on the main thread. This WILL cause an ANR, and is only
		 * provided to simplify the pedagogy. We will see how to do this right
		 * in a future assignment (using a Service managing background threads).
		 */
		messageList = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this, R.layout.message, messageList);
		
		/*
		 * End Todo
		 */
	}
	public void closeSocket() {
		serverSocket.close();
	}

	/*
	 * If the socket is OK, then it's running
	 */
	boolean socketIsOK() {
		return socketOK;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// TODO
		switch (item.getItemId()) {
		case R.id.peer:
			Intent intent_a = new Intent(this, ShowPeersActivity.class);
			startActivityForResult(intent_a, 1);
			this.onPause();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}

	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		switch (loaderID) {
		case MY_LOADER_ID:
			return new CursorLoader(getActivity(), ChatProvider.CONTENT_URI,
					ChatProvider.projection, null, null, null);
		case MY_LOADER_ID2:
			return new CursorLoader(getActivity(),
					ChatProvider.CONTENT_URI_PEER, ChatProvider.projection2,
					null, null, null);
		default:
			return null;
		}
	}

	public boolean addMessage(MessageProvider b) {
		ContentValues contentValues = new ContentValues();
		b.writeToProvider(contentValues);
		try{
		getContentResolver().insert(ChatProvider.CONTENT_URI, contentValues);
		}catch(Exception e){
			return false;
		}
		return true;
	}

	public boolean addPeer(PeerProvider b) {
		ContentValues contentValues = new ContentValues();
		b.writeToProvider(contentValues);
		try{
		getContentResolver().delete(
				ChatProvider.CONTENT_URI_PEER,
				"name ='" + b.name + "' and " + "senderid ='"
						+ b.senderid + "'", null);
		getContentResolver().insert(ChatProvider.CONTENT_URI_PEER,
				contentValues);
	}catch(Exception e){
		return false;
	}
		return true;
	}

	private Context getActivity() {
		// TODO Auto-generated method stub
		return this;
	}

	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		sca.swapCursor(arg1);
	}

	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		sca.swapCursor(null);
	}
	public void send(View v) {
		Intent sender=new Intent(this,IChatSenderService.class);
		sender.putExtra("port", clientPort);
		sender.putExtra("host", clientHost);
		sender.putExtra("text", messageText.getText().toString());
		sender.putExtra("name", clientName);
		messageText.setText("");
		try{
		if(senderservice!=null)
			senderservice.sendmessage(sender);
		}catch(Exception e){
			Log.e(TAG, e.getClass().getName());
		}
	}
}
