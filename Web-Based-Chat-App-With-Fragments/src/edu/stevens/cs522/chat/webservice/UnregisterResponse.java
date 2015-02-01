package edu.stevens.cs522.chat.webservice;

public class UnregisterResponse extends Response {
	@SuppressWarnings("unused")
	private static final String TAG = UnregisterResponse.class.getCanonicalName();
	private int status;
	
	public UnregisterResponse(int status){
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

}
