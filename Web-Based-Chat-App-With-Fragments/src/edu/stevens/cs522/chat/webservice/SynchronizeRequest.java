package edu.stevens.cs522.chat.webservice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;
import edu.stevens.cs522.chat.providers.MessageContentProvider;

public class SynchronizeRequest extends Request {
	@SuppressWarnings("unused")
	private static final String TAG = SynchronizeRequest.class
			.getCanonicalName();
	private Uri uri;
	private long sequenceNumber;
	private MessageContentProvider[] msgList;
	private String clientName;

	public SynchronizeRequest(long clientID, UUID registrationID, Uri uri, String clientName,
			long seqnum, ArrayList<MessageContentProvider> messages) {
		super(clientID, registrationID);
		// TODO Auto-generated constructor stub
		this.clientName = clientName;
		this.uri = uri;
		this.sequenceNumber = seqnum;
		msgList = new MessageContentProvider[messages.size()];
		for (int i = 0; i< messages.size(); i++) {
			msgList[i] = new MessageContentProvider();
			msgList[i].setId(messages.get(i).getId());
			msgList[i].setMessageText(messages.get(i).getMessageText());
			msgList[i].setSender(messages.get(i).getSender());
			msgList[i].setSequenceNumber(messages.get(i).getSequenceNumber());
			msgList[i].setTimestamp(messages.get(i).getTimestamp());
		}
	}

	public SynchronizeRequest(Parcel in) {
		super(in);
		// TODO Auto-generated constructor stub
		this.uri = Uri.parse(in.readString());
		this.sequenceNumber = in.readLong();
		int length = in.readInt();
		if (null == this.msgList) {
			msgList = new MessageContentProvider[length];
		}
		in.readTypedArray(this.msgList, MessageContentProvider.CREATOR);
	}

	@Override
	public void writeToParcel(Parcel out, int flag) {
		// TODO Auto-generated method stub
		super.writeToParcel(out, flag);
		out.writeString(uri.toString());
		out.writeLong(sequenceNumber);
		out.writeInt(msgList.length);
		out.writeTypedArray(msgList, msgList.length);
	}

	public static final Parcelable.Creator<SynchronizeRequest> CREATOR = new Parcelable.Creator<SynchronizeRequest>() {
		public SynchronizeRequest createFromParcel(Parcel in) {
			return new SynchronizeRequest(in);
		}

		public SynchronizeRequest[] newArray(int size) {
			return new SynchronizeRequest[size];
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
		requestUri += "/chat/" + this.clientID + "?" + "regid="
				+ this.registrationID + "&" + "seqnum=" + this.sequenceNumber;
		return Uri.parse(requestUri);
	}

	public String getRequestEntity() throws IOException {
		// TODO Auto-generated method stub
		try {
			JSONObject msgJSON = new JSONObject();
			for (MessageContentProvider msg : msgList) {
				JSONObject info = new JSONObject();
				info.put("chatroom", "_default");
				info.put("timestamp", msg.getTimestamp());
				info.put("text", msg.getMessageText());
			}
			return msgJSON.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public Response getResponse(HttpURLConnection connection, JsonReader rd) {
		// TODO Auto-generated method stub

		try {
			OutputStream os = connection.getOutputStream();
			JsonWriter wr = new JsonWriter(new BufferedWriter(
					new OutputStreamWriter(os, "UTF-8")));

			wr.beginArray();
			for (MessageContentProvider msg : msgList) {
				wr.beginObject();
				wr.name("chatroom");
				wr.value("_default");
				wr.name("timestamp");
				wr.value(Date.parse(msg.getTimestamp()));
				wr.name("text");
				wr.value(msg.getMessageText());
				wr.endObject();
			}
			wr.endArray();

			wr.flush();
			wr.close();

			rd = new JsonReader(
						new BufferedReader(
							new InputStreamReader(connection.getInputStream())));
				
			rd.beginObject();

			ArrayList<String> clients = new ArrayList<String>();
			ArrayList<MessageContentProvider> messages = new ArrayList<MessageContentProvider>();

			while (rd.peek() != JsonToken.END_OBJECT) {
				String lable = rd.nextName();
				if ("clients".equals(lable)) {
					rd.beginArray();
					while (rd.peek() != JsonToken.END_ARRAY) {
						clients.add(rd.nextString());
					}
					rd.endArray();
				}
				if ("messages".equals(lable)) {
					rd.beginArray();
					while (rd.peek() != JsonToken.END_ARRAY) {
						rd.beginObject();
						@SuppressWarnings("unused")
						String chatroom = "";
						long timestamp = 0;
						long seqnum = 0;
						String sender = "anonym";
						String text = "";
						while(rd.peek() != JsonToken.END_OBJECT){
							String temp = rd.nextName();
							if (temp.equals("chatroom")) {
								chatroom = rd.nextString();
							} else if (temp.equals("timestamp")) {
								timestamp = rd.nextLong();
							} else if (temp.equals("seqnum")) {
								seqnum = rd.nextLong();
							} else if (temp.equals("sender")) {
								sender = rd.nextString();
							} else if(temp.equals("text")) {
								text = rd.nextString();
							}
						}
						rd.endObject();
						MessageContentProvider message = new MessageContentProvider(seqnum, text, sender, new Date(timestamp).toString());
						messages.add(message);
					}
	
					rd.endArray();
				}
			}
			rd.endObject();
			rd.close();
			return new SynchronizeResponse(clientID, clientName, clients, messages);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new SynchronizeResponse(0, null, null, null);
		}
	}

	public Uri getUri() {
		// TODO Auto-generated method stub
		return this.uri;
	}

}
