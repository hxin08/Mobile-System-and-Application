package edu.stevens.cs522.chat.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import edu.stevens.cs522.chat.contracts.ChatContract;

public class MessageContentProvider implements Parcelable {

	private long id;
	private String messageText;
	private String sender;
	private String timestamp;
	private long sequenceNumber;
	
	public MessageContentProvider(long sequenceNumber, String messageText, String sender, String timestamp){
		this.id = 0;
		this.messageText = messageText;
		this.sender = sender;
		this.timestamp = timestamp;
		this.sequenceNumber =  sequenceNumber;
	}
	
	public MessageContentProvider(Parcel in){
		this.id = in.readLong();
		this.messageText = in.readString();
		this.sender = in.readString();
		this.timestamp = in.readString();
		this.sequenceNumber = in.readLong();
	}
	
	public MessageContentProvider(Cursor cursor) {
		this.messageText = ChatContract.getMessageText(cursor);
		this.sender = ChatContract.getMessageSender(cursor);
		this.timestamp = ChatContract.getMessageTimestamp(cursor);
		this.sequenceNumber = ChatContract.getMessageSequenceNumber(cursor);
	}
	
	public MessageContentProvider() {
		// TODO Auto-generated constructor stub
	}

	public static final Parcelable.Creator<MessageContentProvider> CREATOR = new Parcelable.Creator<MessageContentProvider>() {
		 public MessageContentProvider createFromParcel(Parcel in) {
			 return new MessageContentProvider(in);
		 }

		 public MessageContentProvider[] newArray(int size) {
			 return new MessageContentProvider[size];
		 }
	 };
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel out, int flag) {
		// TODO Auto-generated method stub
		out.writeLong(id);
		out.writeString(messageText);
		out.writeString(sender);
		out.writeString(timestamp);
		out.writeLong(sequenceNumber);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@Override
	public String toString(){
		return messageText;
	}

	public void writeToProvider(ContentValues values) {
		// TODO Auto-generated method stub
		ChatContract.putSender(values, sender);
		ChatContract.putTimestamp(values, timestamp);
		ChatContract.putSequenceNumber(values, sequenceNumber);

	}
}
