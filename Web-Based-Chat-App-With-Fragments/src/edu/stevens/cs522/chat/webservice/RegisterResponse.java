package edu.stevens.cs522.chat.webservice;


public class RegisterResponse extends Response {
	@SuppressWarnings("unused")
	private static final String TAG = RegisterResponse.class.getCanonicalName();
	private String id;
	
	public RegisterResponse(String id) {
		// TODO Auto-generated constructor stub
		this.id = id;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		if(id == null || id.isEmpty()){
			return false;
		}else{
			return true;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
