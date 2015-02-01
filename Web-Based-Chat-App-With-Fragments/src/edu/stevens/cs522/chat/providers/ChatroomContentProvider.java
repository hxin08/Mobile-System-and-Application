package edu.stevens.cs522.chat.providers;

import edu.stevens.cs522.chat.contracts.ChatContract;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class ChatroomContentProvider implements Parcelable {
	
	private long id;
	private String name;

	public ChatroomContentProvider(long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public ChatroomContentProvider(Parcel in) {
		// TODO Auto-generated constructor stub
		id = in.readLong();
		name = in.readString();
	}
	
	public ChatroomContentProvider(Cursor cursor) {
		// TODO Auto-generated constructor stub
			if(null != cursor){
				if(cursor.getCount() != 0){
					cursor.moveToFirst();
					this.id = ChatContract.getChatroomID(cursor);
					this.name = ChatContract.getChatroomName(cursor);
				}
			}
	}

	public static final Parcelable.Creator<ChatroomContentProvider> CREATOR = new Parcelable.Creator<ChatroomContentProvider>() {
		public ChatroomContentProvider createFromParcel(Parcel in) {
			return new ChatroomContentProvider(in);
		}

		public ChatroomContentProvider[] newArray(int size) {
			return new ChatroomContentProvider[size];
		}
	};
	 
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flag) {
		// TODO Auto-generated method stub
		if(null != out){
			out.writeLong(id);
			out.writeString(name);
		}
	}
	
	public void writeToProvider(ContentValues values){
		ChatContract.putPeerID(values, id);
		ChatContract.putPeerName(values, name);
	}
	
}
