package edu.stevens.cs522.chatactivity;


import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import edu.stevens.cs522.R;
import edu.stevens.cs522.chat.contracts.ChatContract;
import edu.stevens.cs522.chat.fragments.ChatRoomDetailFragment;
import edu.stevens.cs522.chat.fragments.CreateChatRoomDialog;
import edu.stevens.cs522.chat.fragments.CreateChatRoomDialog.CreateChatroomDialogListener;
import edu.stevens.cs522.chat.fragments.ChatRoomListFragment;
import edu.stevens.cs522.chat.fragments.ChatRoomListFragment.OnChatroomSelectedListener;
import edu.stevens.cs522.chat.fragments.PostMessageDialog;
import edu.stevens.cs522.chat.fragments.PostMessageDialog.SendMessageDialogListener;
import edu.stevens.cs522.chat.webservice.ServiceHelper;
import edu.stevens.cs522.chat.providers.MessageContentProvider;

public class ClientActivity extends FragmentActivity 
	implements OnChatroomSelectedListener, CreateChatroomDialogListener, SendMessageDialogListener{
	
	@SuppressWarnings("unused")
	private static final String TAG = ClientActivity.class.getCanonicalName();

	public static final String SEND_SERVICE_REQUEST = null;
	public static final String SYNC_ALARM_ACTION = "edu.stevens.cs522.chat.alarm";
	public static final String SHOW_CLIENTS_KEY = "show";
	public static final String CHATROOM_ID_KEY = "chatroom_id";
	public static final int RECEIVE_RESULT_CODE_SEND = 0;
	public static final int RECEIVER_LOADER_ID = 0;

	private static ServiceHelper helper;
	
	private String clientName;
	private static long clientId;
	private Uri serverUri;
	private UUID uuid;
	private long currentChatroomId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_client);
		
		this.getActionBar().show();
		
		this.currentChatroomId = 1;
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            // During initial setup, plug in the chatroom fragment.
            ChatRoomDetailFragment chatroom = new ChatRoomDetailFragment();
    		Bundle args = new Bundle();
    		args.putLong(CHATROOM_ID_KEY, 1);
    		chatroom.setArguments(args);
  		
    		FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
    		transaction.replace(R.id.fragment_container, chatroom);
    		transaction.addToBackStack(null);
    		transaction.commit();
    		
        }
		
		loadPreferences();
		
		helper = new ServiceHelper(this, this.serverUri, this.clientName, uuid);

		Intent alarmIntent = new Intent(this, AlarmReceiver.class);
		alarmIntent.setAction(SYNC_ALARM_ACTION);
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
	
		AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 30000, sender);

	}
	
    private void showCreateDialog() {
    	FragmentManager fm = this.getSupportFragmentManager();
        CreateChatRoomDialog createChatroomDialog = new CreateChatRoomDialog();
        createChatroomDialog.show(fm, "fragment_create_chatroom");
    }
    
    private void showSendDialog() {
    	FragmentManager fm = this.getSupportFragmentManager();
        PostMessageDialog sendMessageDialog = new PostMessageDialog();
        sendMessageDialog.show(fm, "fragment_send_message");
    }

	@SuppressWarnings("static-access")
	private void loadPreferences(){
		
		SharedPreferences mySharedPreferences = this.getSharedPreferences(PreClientActivity.MY_PREFS, Activity.MODE_PRIVATE);
		this.clientId = mySharedPreferences.getLong(PreClientActivity.CLIENT_ID_KEY, 1);
		this.clientName = mySharedPreferences.getString(PreClientActivity.CLIENT_NAME_KEY, PreClientActivity.DEFAULT_CLIENT_NAME);
		String uri = mySharedPreferences.getString(PreClientActivity.SERVER_URI_KEY, PreClientActivity.DEFAULT_SEVER_URI);
		serverUri = Uri.parse(uri);
		String uuid = mySharedPreferences.getString(PreClientActivity.CLIENT_UUID_KEY, null);
		if(uuid != null){
			this.uuid = UUID.fromString(uuid);
		}else{
			this.uuid = UUID.randomUUID();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat_client, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_create_chatroom:
			showCreateDialog();
			return true;
		case R.id.action_send_message:
			showSendDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onFinishCreateDialog(String inputText) {
		// TODO Auto-generated method stub
		
		ContentResolver cr = getContentResolver();
		
		ContentValues values = new ContentValues();
		values.put(ChatContract.CHATROOM_NAME, inputText);
		currentChatroomId = ChatContract.getId(cr.insert(ChatContract.CHATROOM_CONTENT_URI, values));
		
		ChatRoomListFragment nf = (ChatRoomListFragment) this.getSupportFragmentManager().findFragmentById(R.id.fragment_index);
		ArrayList<String> chatrooms = new ArrayList<String>();
		chatrooms.add(inputText);
		nf.refresh(chatrooms);
	}

	@Override
	public void onFinishSendDialog(String inputText) {
		// TODO Auto-generated method stub
		Toast.makeText(this, inputText, Toast.LENGTH_LONG).show();
		
		ContentResolver cr = getContentResolver();
		
		String timestamp = new Date().toString();
		 
		ContentValues values = new ContentValues();
		values.put(ChatContract.MESSAGE_SENDER, clientName);
		values.put(ChatContract.MESSAGE_TIMESTAMP, timestamp);
		values.put(ChatContract.MESSAGE_TEXT, inputText);
		values.put(ChatContract.MESSAGE_SEQUENCE_NUMBER, 0);
		values.put(ChatContract.MESSAGE_FK, clientId);
		values.put(ChatContract.CHATROOM_FK, currentChatroomId);
		cr.insert(ChatContract.MESSAGE_CONTENT_URI, values);
		
	}
	
	@Override
	public void onChatroomSelected(int position) {
		// TODO Auto-generated method stub		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			ChatRoomDetailFragment chatroom = new ChatRoomDetailFragment();
			currentChatroomId = position + 1;
	
			Bundle args = new Bundle();
			args.putLong(CHATROOM_ID_KEY, position + 1);
			chatroom.setArguments(args);

			FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, chatroom);
			transaction.addToBackStack(null);
			transaction.commit();
		}else{
			Intent intent = new Intent(this, ShowClientsActivity.class);
			intent.putExtra(CHATROOM_ID_KEY, position + 1);
			startActivity(intent);
			
		}
	}

	public static class AlarmReceiver extends BroadcastReceiver{

		public AlarmReceiver() {
			super();
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(SYNC_ALARM_ACTION)){
				
				ContentResolver cr = context.getContentResolver();
				Cursor c = cr.query(ChatContract.MESSAGE_CONTENT_URI, null, null, null, null);
				
				ArrayList<MessageContentProvider> msgList = new ArrayList<MessageContentProvider>();
				long seqnum = 0;
				for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
					MessageContentProvider msg = new MessageContentProvider(c);
					if(msg.getSequenceNumber() == 0){
						msgList.add(msg);
					}else if(msg.getSequenceNumber() > seqnum){
						seqnum = msg.getSequenceNumber();
					}
				}
				helper.startSyncService(clientId, seqnum, msgList);
			}
		}
	}	
	
}
