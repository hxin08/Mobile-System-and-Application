package edu.stevens.cs522.chat.server;

import android.os.Parcel;
import android.os.Parcelable;

public class Response implements Parcelable{

public long clientID = -1;
	
	public boolean isValid()
	{
		if( clientID == -1 )
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	public Response( long id )
	{
		clientID = id;
	}

	public Response( Parcel in )
	{
		clientID = in.readLong();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong( clientID );		
	}
	
	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Response createFromParcel(Parcel in) {
            return new Response(in); 
        }

        public Response[] newArray(int size) {
            return new Response[size];
        }
    };
}
