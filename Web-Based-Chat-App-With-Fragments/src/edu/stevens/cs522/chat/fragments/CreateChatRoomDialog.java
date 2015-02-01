package edu.stevens.cs522.chat.fragments;

import edu.stevens.cs522.R;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class CreateChatRoomDialog extends DialogFragment implements
		OnEditorActionListener, OnClickListener {

	public interface CreateChatroomDialogListener {
		void onFinishCreateDialog(String inputText);
	}

	private EditText mEditText;
	private Button mSendBtn;
	private Button mCancelBtn;

	public CreateChatRoomDialog() {
		// Empty constructor required for DialogFragment
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_create_chatroom,
				container);
		mEditText = (EditText) view.findViewById(R.id.txt_chatroom_name);
		mSendBtn = (Button) view.findViewById(R.id.sendbtn);
		mCancelBtn = (Button) view.findViewById(R.id.cancelbtn);
		getDialog().setTitle("Create Chatroom");

		// Show soft keyboard automatically
		mEditText.requestFocus();
		getDialog().getWindow().setSoftInputMode(4);
		mEditText.setOnEditorActionListener(this);
		mSendBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		return view;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (EditorInfo.IME_ACTION_DONE == actionId) {
			// Return input text to activity
			CreateChatroomDialogListener activity = (CreateChatroomDialogListener) getActivity();
			activity.onFinishCreateDialog(mEditText.getText().toString());
			this.dismiss();
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sendbtn:
			CreateChatroomDialogListener activity = (CreateChatroomDialogListener) getActivity();
			activity.onFinishCreateDialog(mEditText.getText().toString());
			this.dismiss();
			break;

		case R.id.cancelbtn:
			this.dismiss();
			break;

		default:
			break;
		}
	}
}
