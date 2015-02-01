package edu.stevens.cs522.chat.servicehelper;

import java.net.HttpURLConnection;
import java.util.Map;

import android.net.Uri;
import android.os.Parcel;
import android.util.JsonReader;
import edu.stevens.cs522.chat.client.Request;
import edu.stevens.cs522.chat.server.Response;

public class Unregister extends Request {

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

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
