package edu.stevens.cs522.chat.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;
import edu.stevens.cs522.chat.contracts.ChatContract;
import edu.stevens.cs522.chatactivity.ClientActivity;

public class ChatRoomDetailFragment extends ListFragment {
	@SuppressWarnings("unused")
	private static final String TAG = ChatRoomDetailFragment.class.getCanonicalName();
	private ArrayAdapter<String> arrayAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Activity parent = this.getActivity();

		arrayAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1);

		Bundle bundle = this.getArguments();
		long id = bundle.getLong(ClientActivity.CHATROOM_ID_KEY, 1);

		ContentResolver cr = parent.getContentResolver();
		Cursor cursor = cr.query(ChatContract.withExtendedPath(
				ChatContract.CHATROOM_CONTENT_URI, Long.toString(id)), null,
				null, null, null);

		if (cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				String s = ChatContract.getMessageSender(cursor) + ": "
						+ ChatContract.getMessageTimestamp(cursor) + "\n"
						+ ChatContract.getMessageText(cursor);

				arrayAdapter.add(s);
			}
		}

		this.setListAdapter(arrayAdapter);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

}
