package edu.stevens.cs522.chat.interfaces;
import edu.stevens.cs522.chat.service.Contract;
import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

public class MessageProvider implements Parcelable{
	public long id;
	public String messageText;
	public String sender;
	public long date;
	public long messageId;
	public long senderId;
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public MessageProvider() {
	}
	public MessageProvider(int i,String m,String s,long mid,long dt) {
	   this.id=i;
       this.messageText  = m;
       this.sender = s;
       this.date=dt;
       this.messageId=mid;
       this.senderId=1;
	}
	public void writeToParcel(Parcel out, int arg1) {
		// TODO Auto-generated method stub
		out.writeLong(id);
		out.writeStringArray(new String[] {this.sender,
                this.messageText});
	}
	public MessageProvider(Parcel in) {
		readFromParcel(in) ;
	}
	public void readFromParcel(Parcel in) { 
		this.id=in.readLong();
		 String[] data = new String[2];
        in.readStringArray(data);
        this.messageText  = data[0];
        this.sender = data[1];
	    }  
	 public static final Parcelable.Creator<MessageProvider> CREATOR = new Parcelable.Creator<MessageProvider>() {  
		    
	        public MessageProvider createFromParcel(Parcel in) {  
	            return new MessageProvider(in);  
	        }  
	   
	        public MessageProvider[] newArray(int size) {  
	            return new MessageProvider[size];  
	        }  
	          
	   };
	public void writeToProvider(ContentValues values) {
			Contract.putId(values,id);
			Contract.putSender(values, sender);
			Contract.putText(values, messageText);
			Contract.putDate(values, date);
			Contract.putMessageID(values, messageId);
			Contract.putSenderId(values, senderId);
		// TODO Auto-generated method stub
	}
}
