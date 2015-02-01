package edu.stevens.cs522.chat.webservice;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.JsonReader;
import android.util.Log;
import edu.stevens.cs522.chat.contracts.ChatContract;
import edu.stevens.cs522.chat.interfaces.ICallBack;
import edu.stevens.cs522.chat.providers.MessageContentProvider;

public class RequestProcessor {
	private static final String TAG = RequestProcessor.class.getCanonicalName();
	
	private RestMethod restMethod;
	private ICallBack callback;
	
	public ContentResolver resolver;
	
	public RequestProcessor(ContentResolver resolver, ICallBack cb) {
		this.callback = cb;
		this.restMethod = new RestMethod();
		this.resolver = resolver ;
		
	}

	public void perform(RegisterRequest rr) {
		try {
			RegisterResponse response = (RegisterResponse) restMethod.perform(rr);
			if(response != null){
				String id = response.getId();
				callback.feedback(Long.valueOf(id));
			}else{
				callback.feedback(0);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//For Test
	public void perform(SynchronizeRequest sr){
		StreamingResponse response;
		try {
			response = (StreamingResponse) restMethod.perform(sr);
			
			if(response != null){
				Log.i(TAG, "uri=" + sr.getUri());
			}else{
				Log.i(TAG, "uri=" + sr.getUri());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void perform(UnregisterRequest ur){
		UnregisterResponse response;
		try {
			response = (UnregisterResponse) restMethod.perform(ur);
			if(response != null){				
				@SuppressWarnings("unused")
				int status = response.getStatus();
			}else{
				Log.i(TAG, "uri=" + ur.getServerUri());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class StreamingResponse{
		public HttpURLConnection connection;
		public SynchronizeResponse response;
		
		public StreamingResponse(HttpURLConnection connection,
				Response response) {
			// TODO Auto-generated constructor stub
			this.connection = connection;
			this.response = (SynchronizeResponse)response;
			

			if(this.response.isValid()){
				resolver.delete(ChatContract.CONTENT_URI, null, null);
				
				@SuppressWarnings("unused")
				Long clientId = this.response.getClientId();
				@SuppressWarnings("unused")
				String clientName = this.response.getClientName();
				
				if(this.response.getClients() != null){	
					for(String s : this.response.getClients()){
						ContentValues values = new ContentValues();
						ChatContract.putPeerName(values, s);
						ChatContract.putPeerAddress(values, "remote");
						ChatContract.putPeerPort(values, 8080);
					    resolver.insert(ChatContract.withExtendedPath(ChatContract.CONTENT_URI, "1"), values);
					}
				}
				resolver.delete(ChatContract.MESSAGE_CONTENT_URI, "sequence_number=?", new String[]{"0"});

				if(this.response.getMessages() != null){
					for(MessageContentProvider m : this.response.getMessages()){ 
						ContentValues values = new ContentValues();
						ChatContract.putSender(values, m.getSender());
						ChatContract.putMessageText(values, m.getMessageText());
						ChatContract.putTimestamp(values, m.getTimestamp());
						ChatContract.putSequenceNumber(values, m.getSequenceNumber());
						Cursor pc = resolver.query(ChatContract.withExtendedPath(ChatContract.CONTENT_URI, "1") , 
													new String[]{"Peers._id"}, 
													"name",
													new String[]{m.getSender()}, null);
						if(pc.getCount()>0){
							pc.moveToFirst();
							long peer_id = ChatContract.getPeerID(pc);
							ChatContract.putMessageFk(values, peer_id);
						}else{
							ChatContract.putMessageFk(values, 1);
						}
						
						Cursor cc = resolver.query(ChatContract.CHATROOM_CONTENT_URI, 
													new String[]{"Chatrooms._id"}, 
													"Chatrooms.name=?",
													new String[]{"default"}, null);

						if(cc.getCount()>0){
							cc.moveToFirst();
							long chatroom_id = ChatContract.getChatroomID(cc);					
							ChatContract.putChatroomFk(values, chatroom_id);
						}else{
							ChatContract.putChatroomFk(values, 1);
						}
					    resolver.insert(ChatContract.MESSAGE_CONTENT_URI, values);
					}
				}
			}
				
			connection.disconnect();
		}
	}
	
	public class RestMethod{

		private HttpURLConnection connection;
		
		public Response perform(RegisterRequest rr) throws IOException{
			connection = getConnection(rr.getRequestUri());

			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			
			Map<String, String> headers = rr.getRequestHeaders();
			for(Entry<String, String> header : headers.entrySet()){
				connection.addRequestProperty(header.getKey(), header.getValue());			
			}
			
			outputRequestEntity(rr);

			connection.connect();
			throwErrors(connection);
			
			JsonReader rd = new JsonReader(
								new BufferedReader(
									new InputStreamReader(
										connection.getInputStream())));
			
			Response response = rr.getResponse(connection, rd);
			rd.close();
			if(response.isValid()){
				return response;
			}
			return null;
		}

		public StreamingResponse perform(SynchronizeRequest sr) throws IOException{

			connection = getConnection(sr.getRequestUri());

			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
	
			Map<String, String> headers = sr.getRequestHeaders();
			for(Entry<String, String> header : headers.entrySet()){
				connection.addRequestProperty(header.getKey(), header.getValue());			
			}
			
			
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setChunkedStreamingMode(0);
			
			JsonReader rd = null;
			Response response = sr.getResponse(connection, rd);

			return new StreamingResponse(connection, response);
			
		}
		
		public Response perform(UnregisterRequest ur) throws IOException{
			connection = getConnection(ur.getRequestUri());

			connection.setRequestMethod("DELETE");
			connection.setUseCaches(false);
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			
			Map<String, String> headers = ur.getRequestHeaders();
			for(Entry<String, String> header : headers.entrySet()){
				connection.addRequestProperty(header.getKey(), header.getValue());			
			}
			
			connection.connect();
			throwErrors(connection);
			
			JsonReader rd = new JsonReader(
								new BufferedReader(
									new InputStreamReader(
										connection.getInputStream())));
			
			Response response = ur.getResponse(connection, rd);
			rd.close();
			if(response.isValid()){
				return response;
			}
			return null;
		}
	
		private void throwErrors(HttpURLConnection connection) throws IOException {
			// TODO Auto-generated method stub
			final int status = connection.getResponseCode();
			if(status < 200 || status >= 300){
				String exceptionMessage = "Error response" 
						+ status + " "
						+ connection.getResponseMessage()
						+ " for " + connection.getURL();
				throw new IOException(exceptionMessage);
			}
		}
		
		private HttpURLConnection getConnection(Uri uri) throws IOException{
			URL url = new URL(uri.toString());
			return (HttpURLConnection) url.openConnection();
		}
		
		private void outputRequestEntity(Request request) throws IOException{
			String requestEntity = request.getRequestEntity();
			if(requestEntity != null){
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				byte[] outputEntity = requestEntity.getBytes("UTF-8");
				
				connection.setFixedLengthStreamingMode(outputEntity.length);
				
				OutputStream out = new BufferedOutputStream(connection.getOutputStream());
				out.write(outputEntity);
				out.flush();
				out.close();
			}else{
				Log.i(TAG, "requestEntity == null");
			}
		}
		
		
	}
	
}
