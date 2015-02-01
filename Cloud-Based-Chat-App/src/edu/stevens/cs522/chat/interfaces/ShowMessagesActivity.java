package edu.stevens.cs522.chat.interfaces;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import edu.stevens.cs522.chat.client.ChatProvider;
import edu.stevens.cs522.chat.service.DataBaseAdapter;
import edu.stevens.cs522.chat.service.R;

public class ShowMessagesActivity extends Activity {
	public SimpleCursorAdapter sca;
	String[] from = new String[] {DataBaseAdapter.SENDER, DataBaseAdapter.TEXT };
	int[] to = new int[] { R.id.cart_row_sender, R.id.cart_row_message };
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_list);
		Bundle b=getIntent().getExtras();
		String name=b.getString("name");
		TextView t1=(TextView)findViewById(R.id.textView1);
		t1.setText(name);
		try{
			sca= new SimpleCursorAdapter(this, R.layout.message, getContentResolver().query(ChatProvider.CONTENT_URI,
					ChatProvider.projection, "sender=?", new String[]{name}, null), from, to);
			//adapter=new BookAdapter(this,shoppingCart);
			ListView listView = (ListView) findViewById(R.id.listView1);
			
			listView.setAdapter(sca);
			}catch(Exception e){}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.message_list, menu);
		return true;
	}

}
