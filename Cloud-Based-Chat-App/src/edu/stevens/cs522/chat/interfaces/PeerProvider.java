package edu.stevens.cs522.chat.interfaces;

import edu.stevens.cs522.chat.service.Contract;
import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

public class PeerProvider implements Parcelable {
	public long id;
	public String name;
	public int senderid;
	public PeerProvider() {
	}
	public PeerProvider(String n,int id) {
		name=n;
		senderid=id;
	}
	public PeerProvider(Parcel in) {
		readFromParcel(in);
	}
	public void readFromParcel(Parcel in) { 
		this.id=in.readLong();
        this.name  = in.readString();
        this.senderid = in.readInt();
        
	 } 
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeInt(senderid);
	}
	public static final Parcelable.Creator<PeerProvider> CREATOR = new Parcelable.Creator<PeerProvider>() {  
	    
        public PeerProvider createFromParcel(Parcel in) {  
            return new PeerProvider(in);  
        }  
   
        public PeerProvider[] newArray(int size) {  
            return new PeerProvider[size];  
        }  
          
    };
    public void writeToProvider(ContentValues values) {
		Contract.putId(values,id);
		Contract.putName(values, name);
		Contract.putSenderid(values, senderid);
	// TODO Auto-generated method stub
	
}

}
