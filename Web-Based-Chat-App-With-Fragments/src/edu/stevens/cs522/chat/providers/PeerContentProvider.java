package edu.stevens.cs522.chat.providers;


import edu.stevens.cs522.chat.contracts.ChatContract;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class PeerContentProvider implements Parcelable {

	public long id;
	public String name;
	public String address;
	public int port;
	public MessageContentProvider[] messages;
	
	public PeerContentProvider(long id, String name, String address, int port, MessageContentProvider[] message){
		this.id = id;
		this.name = name;
		this.address = address;
		this.port = port;
		this.messages = message;
	}
	
	public PeerContentProvider(Parcel in) {
		// TODO Auto-generated constructor stub
		id = in.readLong();
		name = in.readString();
		address = in.readString();
		port = in.readInt();
		int length = in.readInt();
		if(null == this.messages){
			messages = new MessageContentProvider[length];
		}
		in.readTypedArray(this.messages, MessageContentProvider.CREATOR);
	}
	
	public PeerContentProvider(Cursor cursor) {
		// TODO Auto-generated constructor stub
			if(null != cursor){
				if(cursor.getCount() != 0){
					cursor.moveToFirst();
					this.id = ChatContract.getPeerID(cursor);
					this.name = ChatContract.getPeerName(cursor);
					
					this.address = ChatContract.getPeerAddress(cursor);
	
					this.port = ChatContract.getPeerPort(cursor);
					
					
					String[] texts = ChatContract.SEPARATOR.split(ChatContract.getMessages(cursor)); 			
					if(null == this.messages){
						messages = new MessageContentProvider[texts.length];
					}
					for(int i = 0; i < texts.length; i++){
						messages[i] = new MessageContentProvider();
						messages[i].setMessageText(texts[i]);
					}
				}
			}
	}

	public PeerContentProvider() {
		// TODO Auto-generated constructor stub
		this.id = -1;
	}

	public static final Parcelable.Creator<PeerContentProvider> CREATOR = new Parcelable.Creator<PeerContentProvider>() {
		 public PeerContentProvider createFromParcel(Parcel in) {
			 return new PeerContentProvider(in);
		 }

		 public PeerContentProvider[] newArray(int size) {
			 return new PeerContentProvider[size];
		 }
	 };

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel out, int flag) {
		// TODO Auto-generated method stub
		if(null != out){
			out.writeLong(id);
			out.writeString(name);
			out.writeString(address);
			out.writeInt(port);
			out.writeInt(messages.length);
			out.writeTypedArray(messages, messages.length);
		}
	}
	
	public void writeToProvider(ContentValues values){
		ChatContract.putPeerID(values, id);
		ChatContract.putPeerName(values, name);
		ChatContract.putPeerAddress(values, address);
		ChatContract.putPeerPort(values, port);
		String temp = "";
		for(int i = 0; i < messages.length; i++){
			temp = temp + messages[i] + "|";
		}
		ChatContract.putMessageText(values, temp);
	}

	public long getID() {
		// TODO Auto-generated method stub
		return this.id;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	public String getAddress() {
		// TODO Auto-generated method stub
		return this.address;
	}

	public int getPort() {
		// TODO Auto-generated method stub
		return this.port;
	}

	public String getMessages() {
		// TODO Auto-generated method stub
		String rtn = "";
		for(int i = 0; i < messages.length; i++){
			rtn = rtn + messages[i] + "|";//+messages.length+"|";
		}
		
		return rtn;
	}

}
