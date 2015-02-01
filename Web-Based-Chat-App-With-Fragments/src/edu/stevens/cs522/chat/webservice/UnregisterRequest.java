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

public class UnregisterRequest extends Request {
	@SuppressWarnings("unused")
	private static final String TAG = UnregisterRequest.class.getCanonicalName();
	
	private Uri serverUri;
	public UnregisterRequest(long clientID, UUID registrationID, Uri serverUri) {
		super(clientID, registrationID);
		// TODO Auto-generated constructor stub
		this.serverUri = serverUri;
	}

	public UnregisterRequest(Parcel in) {
		super(in);
		// TODO Auto-generated constructor stub
		this.serverUri = Uri.parse(in.readString());
	}

	@Override
	public void writeToParcel(Parcel out, int flag) {
		// TODO Auto-generated method stub
		super.writeToParcel(out, flag);
		out.writeString(serverUri.toString());
	}
	
	public static final Parcelable.Creator<UnregisterRequest> CREATOR = new Parcelable.Creator<UnregisterRequest>() {
		 public UnregisterRequest createFromParcel(Parcel in) {
			 return new UnregisterRequest(in);
		 }

		 public UnregisterRequest[] newArray(int size) {
			 return new UnregisterRequest[size];
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
		String requestUri = this.serverUri.toString();
		requestUri += "/chat/" + this.clientID + "?" 
					+ "regid=" + this.registrationID;
		return Uri.parse(requestUri);
	}

	@Override
	public String getRequestEntity() throws IOException {
		// TODO Auto-generated method stub
		String requestEntity = "{" 
							+ "regid: " + this.registrationID
							+ "}";

		return requestEntity;
	}

	@Override
	public Response getResponse(HttpURLConnection connection, JsonReader rd) {
		// TODO Auto-generated method stub

		connection.disconnect();
		return new UnregisterResponse(1);
	}

	public Uri getServerUri() {
		return serverUri;
	}

	public void setServerUri(Uri serverUri) {
		this.serverUri = serverUri;
	}

}
