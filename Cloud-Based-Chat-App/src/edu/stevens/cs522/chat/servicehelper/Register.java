package edu.stevens.cs522.chat.servicehelper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.UUID;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.JsonToken;
import edu.stevens.cs522.chat.client.Request;
import edu.stevens.cs522.chat.server.Response;

public class Register extends Request {
	public long clientID;
	public UUID registrationID;
	public String clientName;
	public String serverURI;
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	Register(long id,UUID regid, String clientName, String serverURI){
		this.clientID=id;
		this.registrationID=regid;
		this.clientName = clientName;
		this.serverURI = serverURI;
	}
	public void writeToParcel(Parcel out, int arg1) {
		// TODO Auto-generated method stub
		out.writeLong(clientID);
		out.writeString(registrationID.toString());
	}
	public Register(Parcel in) {
		readFromParcel(in) ;
	}
	public void readFromParcel(Parcel in) { 
		this.clientID=in.readLong();
		this.registrationID=UUID.fromString( in.readString() );
	    }  
	 public static final Parcelable.Creator<Register> CREATOR = new Parcelable.Creator<Register>() {  
		    
	        public Register createFromParcel(Parcel in) {  
	            return new Register(in);  
	        }  
	   
	        public Register[] newArray(int size) {  
	            return new Register[size];  
	        }  	          
	   };

	@Override
	public Map<String, String> getRequestHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri getRequestUri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response getResponse(HttpURLConnection connection, JsonReader rd) {
		// TODO Auto-generated method stub
		parse( rd );
		return null;
	}
	
	void parse(JsonReader rd) 
	{ 
		try {
			rd.beginObject();
			while (rd.peek() != JsonToken.END_OBJECT) {
				String label = rd.nextName(); 
				if( "id".equals( label ) ) 
				{ 
					//rd.beginArray();
					while( rd.peek() != JsonToken.END_OBJECT ) 
					{ 
						clientID = Integer.parseInt( rd.nextString() ); 
					} 
					//rd.endArray(); 
				} 
			} 
			rd.endObject(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
