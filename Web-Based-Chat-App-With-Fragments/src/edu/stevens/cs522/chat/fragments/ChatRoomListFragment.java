package edu.stevens.cs522.chat.fragments;

import java.util.ArrayList;

import edu.stevens.cs522.R;
import edu.stevens.cs522.chat.contracts.ChatContract;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChatRoomListFragment extends ListFragment {
	@SuppressWarnings("unused")
	private static final String TAG = ChatRoomListFragment.class
			.getCanonicalName();

	private OnChatroomSelectedListener mCallback;
	private ArrayAdapter<String> arrayAdapter;

	public interface OnChatroomSelectedListener {
		public void onChatroomSelected(int position);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		arrayAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1);

		Activity parent = this.getActivity();
		ContentResolver cr = parent.getContentResolver();
		Cursor c = cr.query(ChatContract.CHATROOM_CONTENT_URI, null, null,
				null, null);
		if (c.getCount() > 0) {
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				arrayAdapter.add(ChatContract.getChatroomName(c));
			}
		}
		this.setListAdapter(arrayAdapter);
	}

	public void refresh(ArrayList<String> chatrooms) {

		for (String chatroom : chatrooms) {
			arrayAdapter.add(chatroom);
		}
		this.setListAdapter(arrayAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		// When in two-pane layout, set the listview to highlight the selected
		// list item
		// (We do this during onStart because at the point the listview is
		// available.)
		if (getFragmentManager().findFragmentById(R.id.fragment_index) != null) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception.
		try {
			mCallback = (OnChatroomSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnChatroomSelectedListener");
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onListItemClick(ListView l, View view, int position, long id) {
		// Notify the parent activity of selected item
		mCallback.onChatroomSelected(position);

		// Set the item as checked to be highlighted when in two-pane layout
		getListView().setItemChecked(position, true);

		for (int i = 0; i < l.getCount(); i++) {
			View v = l.getChildAt(i);
			if (position == i) {
				v.setBackgroundColor(Color.LTGRAY);
			} else {
				v.setBackgroundColor(Color.TRANSPARENT);
			}
		}
	}
}
