package edu.stevens.cs522.chat.service;

import java.net.HttpURLConnection;

public class StreamingResponse{
	
	public HttpURLConnection connection;
	//public Response response;
	
	public StreamingResponse( HttpURLConnection connection )
	{
		this.connection = connection;
	}

}
