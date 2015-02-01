package edu.stevens.cs522.chatactivity;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import edu.stevens.cs522.R;
import edu.stevens.cs522.chat.contracts.ChatContract;

public class ShowClientsActivity extends ListActivity {
	@SuppressWarnings("unused")
	private static final String TAG = ShowClientsActivity.class.getCanonicalName();
	
    private ArrayAdapter<String> arrayAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_clients);
	
		arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);	
		
		Intent i = this.getIntent();
		long id = i.getIntExtra(ClientActivity.CHATROOM_ID_KEY, 1);
		
		ContentResolver cr = getContentResolver();
		Cursor c = cr.query(ChatContract.withExtendedPath(ChatContract.CHATROOM_CONTENT_URI, Long.toString(id)), null, "_id", null, null);

		if(c.getCount() > 0){
			for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
				String s = ChatContract.getMessageSender(c) + ": " 
						+ ChatContract.getMessageTimestamp(c) + "\n"
						+ ChatContract.getMessageText(c);
				
				arrayAdapter.add(s);
			}	
		}
		this.setListAdapter(arrayAdapter);
		setSelection(0);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_clients, menu);
		return true;
	}

}
