package edu.stevens.cs522.chat.webservice;

import java.util.ArrayList;
import android.util.Log;
import edu.stevens.cs522.chat.providers.MessageContentProvider;
public class SynchronizeResponse extends Response {
	private static final String TAG = SynchronizeResponse.class.getCanonicalName();
	private ArrayList<String> clients;
	private ArrayList<MessageContentProvider> messages;
	private Long clientId;
	private String clientName;
	
	public SynchronizeResponse(){
		super();
	}
	public SynchronizeResponse(long id, String name, ArrayList<String> clients,
			ArrayList<MessageContentProvider> messages) {
		// TODO Auto-generated constructor stub
		clientId = id;
		clientName = name;
		
		if(clients != null){
			this.clients = new ArrayList<String>();
			for(String s : clients){
				this.clients.add(s);
			}
		}

		if(messages != null){
			this.messages = new ArrayList<MessageContentProvider>();
			for(MessageContentProvider m : messages){
				this.messages.add(m);
			}
		}
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		
		if(clientId == 0){
			Log.i(TAG, "isValid= false" + messages);
			return false;
		}else{
			Log.i(TAG, "isValid= true" + messages);
			return true;
		}
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public ArrayList<String> getClients() {
		return clients;
	}

	public void setClients(ArrayList<String> clients) {
		this.clients = clients;
	}

	public ArrayList<MessageContentProvider> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<MessageContentProvider> messages) {
		this.messages = messages;
	}

}
