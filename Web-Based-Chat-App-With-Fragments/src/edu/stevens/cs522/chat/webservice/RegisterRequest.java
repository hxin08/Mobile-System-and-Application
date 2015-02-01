package edu.stevens.cs522.chat.webservice;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.JsonToken;

public class RegisterRequest extends Request {
	@SuppressWarnings("unused")
	private static final String TAG = RegisterRequest.class.getCanonicalName();
	private Uri uri;
	private String username;

	public RegisterRequest(long clientID, UUID registrationID, Uri uri,
			String username) {
		super(clientID, registrationID);
		// TODO Auto-generated constructor stub
		this.username = username;
		this.uri = uri;
	}

	public RegisterRequest(Parcel in) {
		super(in);
		// TODO Auto-generated constructor stub
		this.username = in.readString();
		this.uri = Uri.parse(in.readString());
	}

	@Override
	public void writeToParcel(Parcel out, int flag) {
		// TODO Auto-generated method stub
		super.writeToParcel(out, flag);
		out.writeString(username);
		out.writeString(uri.toString());
	}

	public static final Parcelable.Creator<RegisterRequest> CREATOR = new Parcelable.Creator<RegisterRequest>() {
		public RegisterRequest createFromParcel(Parcel in) {
			return new RegisterRequest(in);
		}

		public RegisterRequest[] newArray(int size) {
			return new RegisterRequest[size];
		}
	};

	@Override
	public Map<String, String> getRequestHeaders() {
		// TODO Auto-generated method stub
		Map<String, String> requestProperty = new HashMap<String, String>();
		requestProperty.put("Accept", "application/json");
		requestProperty.put("User_Agent", "android");
		requestProperty.put("Connection", "Keep-Alive");
		requestProperty.put("Charset", "UTF-8");
		return requestProperty;
	}

	@Override
	public Uri getRequestUri() {
		// TODO Auto-generated method stub
		String requestUri = this.uri.toString();
		requestUri += "/chat" + "?" + "regid=" + this.registrationID + "&"
				+ "username=" + this.username;
		return Uri.parse(requestUri);
	}

	@Override
	public String getRequestEntity() throws IOException {
		// TODO Auto-generated method stub
		String requestEntity = "{" + "regid: " + this.registrationID + ","
				+ "username: " + this.username + "}";
		return requestEntity;
	}

	@Override
	public Response getResponse(HttpURLConnection connection, JsonReader rd) {
		// TODO Auto-generated method stub
		String id = "";
		try {
			rd.beginObject();
			while (rd.peek() != JsonToken.END_OBJECT) {
				String lable = rd.nextName();
				if ("id".equals(lable)) {
					id = rd.nextString();
					break;
				}
			}
			rd.endObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			connection.disconnect();
		}

		return new RegisterResponse(id);
	}

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

}
