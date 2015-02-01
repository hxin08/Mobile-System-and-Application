package edu.stevens.cs522.chat.interfaces;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import edu.stevens.cs522.chat.client.ChatProvider;
import edu.stevens.cs522.chat.service.R;

public class ShowPeersActivity extends ListActivity{
	public SimpleCursorAdapter sca;
	String[] from = new String[] {"name"};
	int[] to = new int[] { R.id.cart_row_peer};
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_peer_list);
		
		sca= new SimpleCursorAdapter(this, R.layout.peerlist, getContentResolver().query(ChatProvider.CONTENT_URI_PEER, ChatProvider.projection2, null, null, null), from, to);
		//adapter=new BookAdapter(this,shoppingCart);
		//ListView listView = (ListView) findViewById(R.id.listView1);
		ListView listView = getListView();
		listView.setAdapter(sca);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.peer_list, menu);
		return true;
	}
	 protected void onListItemClick(ListView l, View v, int position, long id) {
         // TODO Auto-generated method stub
         super.onListItemClick(l, v, position, id);
         Context context = getApplicationContext();
         int duration = Toast.LENGTH_SHORT;
         TextView t=(TextView) v.findViewById(R.id.cart_row_peer);
         String name=t.getText().toString();
         Cursor c=sca.getCursor();
         c.moveToPosition(position);
         Intent intent=new Intent(this, ShowMessagesActivity.class);
         intent.putExtra("name",name);
         startActivityForResult(intent, 1);
	Toast toast = Toast.makeText(context,"Peer "+name+" is selected.", duration);
	toast.show();
	 }

}
