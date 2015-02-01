package edu.stevens.cs522.chat.contracts;

import java.util.regex.Pattern;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class ChatContract {
	public static final char SEPARATOR_CHAR = '|';
	public static final Pattern SEPARATOR = Pattern.compile(Character.toString(SEPARATOR_CHAR), Pattern.LITERAL);
	
	public static final String MESSAGE_ID = "_id";
	public static final String MESSAGE_TEXT = "text";
	public static final String MESSAGE_SENDER = "sender";
	public static final String MESSAGE_TIMESTAMP = "timestamp";
	public static final String MESSAGE_SEQUENCE_NUMBER = "sequence_number";
	public static final String MESSAGE_FK = "peer_fk";
	public static final String CHATROOM_FK = "chatroom_fk";
	
	public static final String PEER_ID = "_id";
	public static final String PEER_NAME = "name";
	public static final String PEER_ADDRESS = "address";
	public static final String PEER_PORT = "port";
	public static final String PEER_MESSAGE = "messages";
	
	public static final String CHATROOM_ID = "_id";
	public static final String CHATROOM_NAME = "name";
	
	public static final String AUTHORITY = "edu.stevens.cs522.chat";
	public static final String PATH = "/peers";	
	public static final String MESSAGE_PATH = "/messages";	
	public static final String CHATROOM_PATH = "/chatrooms";	
	
	public static final Uri CONTENT_URI = new Uri.Builder().scheme("content").authority(AUTHORITY).path(PATH).build();
	public static final Uri MESSAGE_CONTENT_URI = new Uri.Builder().scheme("content").authority(AUTHORITY).path(MESSAGE_PATH).build();
	public static final Uri CHATROOM_CONTENT_URI = new Uri.Builder().scheme("content").authority(AUTHORITY).path(CHATROOM_PATH).build();
	public static final Uri CONTENT_PATH = Uri.parse(contentPath(CONTENT_URI));


	public static long getId(Uri uri){		
		return Long.parseLong(uri.getLastPathSegment());
	}

	public static long getMessageId(Cursor cursor) {
		return cursor.getLong(cursor.getColumnIndexOrThrow(MESSAGE_ID));
	}

	public static String getMessageText(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_TEXT));
	}
	
	public static String getMessageSender(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_SENDER));
	}
	
	public static String getMessageTimestamp(Cursor cursor) {
		// TODO Auto-generated method stub
		return cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_TIMESTAMP));
	}
		
	public static long getMessageSequenceNumber(Cursor cursor) {
		// TODO Auto-generated method stub
		return cursor.getInt(cursor.getColumnIndexOrThrow(MESSAGE_SEQUENCE_NUMBER));
	}
	
	public static long getPeerID(Cursor cursor) {
		return cursor.getLong(cursor.getColumnIndexOrThrow(PEER_ID));
	}
	
	public static String getPeerName(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndexOrThrow(PEER_NAME));
	}
	
	public static String getPeerAddress(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndexOrThrow(PEER_ADDRESS)); 
	}
	
	public static int getPeerPort(Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndexOrThrow(PEER_PORT));
	}	
	
	public static String getMessages(Cursor cursor) {
		// TODO Auto-generated method stub
		if(cursor != null){
			return cursor.getString(cursor.getColumnIndexOrThrow(PEER_MESSAGE));
		}else{
			return "";
		}
	}
	
	public static int getChatroomID(Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndexOrThrow(CHATROOM_ID));
	}
	
	public static String getChatroomName(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndexOrThrow(CHATROOM_NAME));
	}
	
	public static void putMessageId(ContentValues values, int id) {
		values.put(MESSAGE_ID, id);
	}
	
	public static void putMessageText(ContentValues values, String text) {
		values.put(MESSAGE_TEXT, text);
	}
	
	public static void putSender(ContentValues values, String sender) {
		values.put(MESSAGE_SENDER, sender);
	}
	
	public static void putTimestamp(ContentValues values, String timestamp) {
		values.put(MESSAGE_TIMESTAMP, timestamp);
	}
	
	public static void putSequenceNumber(ContentValues values, long sequencNumber) {
		values.put(MESSAGE_SEQUENCE_NUMBER, sequencNumber);
	}
	
	public static void putPeerID(ContentValues values, long id) {
		values.put(PEER_ID, id);
	}
	
	public static void putPeerName(ContentValues values, String name) {
		values.put(PEER_NAME, name);
	}

	public static void putPeerAddress(ContentValues values, String address) {
		values.put(PEER_ADDRESS, address);
	}

	public static void putPeerPort(ContentValues values, int port) {
		values.put(PEER_PORT, port);
	}

	public static void putMessageFk(ContentValues values, long id) {
		// TODO Auto-generated method stub
		values.put(MESSAGE_FK, id);
	}
	
	public static void putChatroomID(ContentValues values, long id) {
		values.put(CHATROOM_ID, id);
	}
	
	public static void putChatroomName(ContentValues values, String name) {
		values.put(CHATROOM_NAME, name);
	}
	
	public static void putChatroomFk(ContentValues values, long id) {
		values.put(CHATROOM_FK, id);
	}
	
	public static Uri withExtendedPath(Uri uri, String... path){
		Uri.Builder builder = uri.buildUpon();
		for(String p : path)
			builder.appendPath(p);
		return builder.build();
	}
	
	public static String contentType(String content){
		return "vnd.android.cursor/vnd." + AUTHORITY + "." + content + "s";
	}
	
	public static String contentItemType(String content){
		return "vnd.android.cursor.item/vnd." + AUTHORITY + "." + content;
	}
	
	public static String contentPath(Uri uri){
		return uri.getPath().substring(1);
	}
	
	public static String contentPathItem(Uri uri, String id){
		return withExtendedPath(uri, id).getPath().substring(1);
	}

}
