package edu.stevens.cs522.chat.servicehelper;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import edu.stevens.cs522.chat.client.Request;
import edu.stevens.cs522.chat.server.Response;

public class Synchronize extends Request {
	
	public String text;
	public String serverURI;
	public ArrayList<String> clients;
	
	public Synchronize( String text, UUID uuid, String serverURI, long clientID )
	{
		this.text = text;
		this.registrationID = uuid;
		this.serverURI = serverURI;
		this.clientID = clientID;
	}
		
	public Synchronize( Parcel in)
	{
		this.text = in.readString();
		this.registrationID = UUID.fromString( in.readString() );
		this.serverURI = in.readString();
		this.clientID = in.readLong();		
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Synchronize createFromParcel(Parcel in) {
            return new Synchronize(in); 
        }

        public Synchronize[] newArray(int size) {
            return new Synchronize[size];
        }
    };
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString( text );
		dest.writeString( registrationID.toString() );
		dest.writeString( serverURI );
		dest.writeLong( clientID );
	}

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
		return null;
	}

}
