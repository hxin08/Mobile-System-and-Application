package edu.stevens.cs522.chat.fragments;

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
import edu.stevens.cs522.R;

public class PostMessageDialog extends DialogFragment implements
		OnEditorActionListener, OnClickListener {

	public interface SendMessageDialogListener {
		void onFinishSendDialog(String inputText);
	}

	private EditText mEditText;
	private Button mSendBtn;
	private Button mCancelBtn;

	public PostMessageDialog() {
		// Empty constructor required for DialogFragment
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_post_message, container);
		mEditText = (EditText) view.findViewById(R.id.txt_send_message);
		mSendBtn = (Button) view.findViewById(R.id.send_msg_btn);
		mCancelBtn = (Button) view.findViewById(R.id.cancel_msg_btn);
		getDialog().setTitle("Send Message");

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
			SendMessageDialogListener activity = (SendMessageDialogListener) getActivity();
			activity.onFinishSendDialog(mEditText.getText().toString());
			this.dismiss();
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send_msg_btn:
			SendMessageDialogListener activity = (SendMessageDialogListener) getActivity();
			activity.onFinishSendDialog(mEditText.getText().toString());
			this.dismiss();

			break;
		case R.id.cancel_msg_btn:
			this.dismiss();

			break;
		default:
			break;
		}
	}
}
