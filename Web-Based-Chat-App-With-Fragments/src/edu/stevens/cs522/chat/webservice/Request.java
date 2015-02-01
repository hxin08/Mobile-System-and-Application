package edu.stevens.cs522.chat.webservice;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.UUID;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;

public abstract class Request implements Parcelable {

	public Request(long clientID, UUID registrationID) {
		this.clientID = clientID;
		this.registrationID = registrationID;
	}

	public Request(Parcel in) {
		// TODO Auto-generated constructor stub
		this.clientID = in.readLong();
		this.registrationID = new UUID(in.readLong(), in.readLong());
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flag) {
		// TODO Auto-generated method stub
		if(out != null){
			out.writeLong(this.clientID);
			out.writeLong(this.registrationID.getMostSignificantBits());	
			out.writeLong(this.registrationID.getLeastSignificantBits());
		}
	}
	 
	public long clientID;
	public UUID	registrationID;	// sanity check
	
	// App-Â­â€?specific HTTP request headers.
	public abstract Map<String,String> getRequestHeaders();
	
	// Chat service URI with parameters e.g. query string parameters.
	public abstract Uri getRequestUri();
	
	// JSON body (if not null) for request data not passed in headers.
	public abstract String getRequestEntity() throws IOException;
	
	// Define your own Response class, including HTTP response code.
	public abstract Response getResponse(HttpURLConnection connection, JsonReader rd /* Null for streaming */);

}
