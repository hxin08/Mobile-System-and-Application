package edu.stevens.cs522.chat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import edu.stevens.cs522.chat.client.Request;
import edu.stevens.cs522.chat.service.StreamingResponse;
import edu.stevens.cs522.chat.servicehelper.Register;
import edu.stevens.cs522.chat.servicehelper.Synchronize;
import edu.stevens.cs522.chat.servicehelper.Unregister;

public class RestMethod
{
	HttpURLConnection connection;
	Response response;
	
	public boolean checkIfOnline( Context context )
	{
		ConnectivityManager cm = ( ConnectivityManager ) context.getSystemService( Context.CONNECTIVITY_SERVICE );
		
		return cm.getActiveNetworkInfo() != null && 
				cm.getActiveNetworkInfo().isConnectedOrConnecting() ;
	}
	
	public void initializeConnection( URL url )
	{
		Log.i("test2", url.toString() );
		try {

			connection = ( HttpURLConnection ) url.openConnection();
			connection.setUseCaches( false );
			connection.setConnectTimeout( 2000 );
			connection.setReadTimeout( 1000 );
			connection.setRequestMethod( "POST" );
			
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void throwErrors ( HttpURLConnection connection ) throws IOException
	{
		final int status = connection.getResponseCode();
		if( status < 200 || status >= 300 )
		{
			String exceptionMessage = "Error response "
					+ status + " "
					+ connection.getResponseMessage()
					+ " for " + connection.getURL();
			throw new IOException( exceptionMessage );
		}
	}
	
	public void executeRequest( Request request)
	{
		try 
		{
			connection.setDoInput( true );
			connection.setDoOutput( true );
			outputRequestEntity( request );
			connection.connect();

			throwErrors( connection );
			
			JsonReader rd = 
					new JsonReader(
							new BufferedReader(
									new InputStreamReader(
											connection.getInputStream())));
			
			response = request.getResponse( connection, rd );
			rd.close();			
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
		
	@SuppressWarnings("resource")
	public void outputRequestEntity( Request request ) throws IOException
	{
		String requestEntity = request.getRequestEntity();
		
		if( requestEntity != null )
		{
			connection.setRequestProperty("content-type", "application/json");
			
			JsonWriter jw = new JsonWriter(
					new BufferedWriter(
							new OutputStreamWriter(
									connection.getOutputStream(),"UTF-8")));
			jw.beginArray();
			jw.beginObject();
			jw.name( "chatroom" );
			jw.value( "_default" );
			jw.name( "timestamp" );
			jw.value( new Date().getTime() );
			jw.name( "text" );
			jw.value( requestEntity );
			jw.endObject();
			jw.endArray();
			jw.flush();
					
		}
	}
	
	public Response perform( Register request )
	{	
		try
		{
			initializeConnection( new URL( 
					request.getRequestUri().toString() 
					+ "?username=" + request.clientName 
					+ "&regid=" + request.registrationID.toString() ) );
			executeRequest( request );
			Log.i( "test4", "Registered" );
		}
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}

		connection.disconnect();
		
		if( response.isValid() )
		{
			return response;
		}
		
		return null;
	}
	
	public StreamingResponse perform( Synchronize request )
	{
		try
		{
			initializeConnection( new URL( 
					request.getRequestUri().toString()
					+ "/" + request.clientID 
					+ "?regid=" + request.registrationID.toString()
					+ "&seqnum=0") );
						
			connection.setDoInput( true );
			connection.setChunkedStreamingMode(0);
			connection.setDoOutput( true );
			connection.setRequestProperty("content-type", "application/json");
						
			return new StreamingResponse( connection );
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		} 
		
		return null;
	}
	
	public Response perform( Unregister request )
	{
		return null;
	}
}