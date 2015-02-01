package edu.stevens.cs522.chat.client;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.UUID;

import android.net.Uri;
import android.os.Parcelable;
import android.util.JsonReader;
import edu.stevens.cs522.chat.server.Response;

public abstract class Request implements Parcelable{
	public long clientID;
	public UUID registrationID; // sanity check
	// App-specific HTTP request headers.
	public abstract Map<String,String> getRequestHeaders();
	// Chat service URI with parameters e.g. query string parameters.
	public abstract Uri getRequestUri();
	// JSON body (if not null) for request data not passed in headers. public String getRequestEntity() throws IOException;
	// Define your own Response class, including HTTP response code. 
	public abstract Response getResponse(HttpURLConnection connection,JsonReader rd );
	public String getRequestEntity() {
		// TODO Auto-generated method stub
		return null;
	}
}
