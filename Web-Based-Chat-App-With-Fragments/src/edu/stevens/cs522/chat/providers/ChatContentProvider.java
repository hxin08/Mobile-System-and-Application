package edu.stevens.cs522.chat.providers;

import java.util.regex.Pattern;

import edu.stevens.cs522.chat.contracts.ChatContract;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.util.Log;

public class ChatContentProvider extends ContentProvider {

	public static final char SEPARATOR_CHAR = '|';
	public static final Pattern SEPARATOR = Pattern.compile(Character.toString(SEPARATOR_CHAR), Pattern.LITERAL);
	
	private static final int ALL_ROWS = 1;
	private static final int SINGLE_ROW = 2;
	private static final int ALL_MESSAGE_ROW = 3;
	private static final int ALL_CHATROOM_ROW = 4;
	private static final int SINGLE_CHATROOM_ROW = 5;
	
	private static final String DATABASE_NAME = "chat.db";
	private static final String MESSAGES_TABLE = "Messages";
	private static final String PEERS_TABLE = "Peers";
	private static final String CHATROOMS_TABLE = "Chatrooms";
	private static final int DATABASE_VERSION = 1;
	
	private static final String CREATE_MESSAGES_TABLE = "create table if not exists "
			+ MESSAGES_TABLE + " ( "
			+ ChatContract.MESSAGE_ID + " integer primary key, "
			+ ChatContract.MESSAGE_SENDER + " text not null, "
			+ ChatContract.MESSAGE_TEXT + " text not null, "
			+ ChatContract.MESSAGE_TIMESTAMP + " text not null, "
			+ ChatContract.MESSAGE_SEQUENCE_NUMBER + " integer, "
			+ ChatContract.MESSAGE_FK + " integer not null, "
			+ ChatContract.CHATROOM_FK + " integer not null, "
			+ " FOREIGN KEY (" + ChatContract.MESSAGE_FK + ") REFERENCES "
			+ PEERS_TABLE + "("	+ ChatContract.PEER_ID + ") ON DELETE CASCADE, "
			+ " FOREIGN KEY (" + ChatContract.CHATROOM_FK + ") REFERENCES "
			+ CHATROOMS_TABLE + "("	+ ChatContract.CHATROOM_ID + ") ON DELETE CASCADE ) ";

	private static final String CREATE_PEERS_TABLE = "create table if not exists "
			+ PEERS_TABLE + " ( "
			+ ChatContract.PEER_ID + " integer primary key, "
			+ ChatContract.PEER_NAME + " text not null, "
			+ ChatContract.PEER_ADDRESS + " text not null, "
			+ ChatContract.PEER_PORT + " integer not null ) ";
	
	private static final String CREATE_CHATROOMS_TABLE = "create table if not exists "
			+ CHATROOMS_TABLE + " ( "
			+ ChatContract.CHATROOM_ID + " integer primary key, "
			+ ChatContract.CHATROOM_NAME + " text not null ) ";

	private static final String SELECT_PEERS = " SELECT " + PEERS_TABLE + "."
			+ ChatContract.PEER_ID + ", " + ChatContract.PEER_NAME + ", "
			+ ChatContract.PEER_ADDRESS + ", " + ChatContract.PEER_PORT + ", "
			+ " GROUP_CONCAT(text, '|') as " + ChatContract.PEER_MESSAGE
			+ " FROM " + PEERS_TABLE + " LEFT OUTER JOIN " + MESSAGES_TABLE
			+ " ON " + PEERS_TABLE + "." + ChatContract.PEER_ID + " = "
			+ MESSAGES_TABLE + "." + ChatContract.MESSAGE_FK + " GROUP BY "
			+ PEERS_TABLE + "." + ChatContract.PEER_ID + ", "
			+ ChatContract.PEER_NAME + ", " + ChatContract.PEER_ADDRESS + ", "
			+ ChatContract.PEER_PORT;
	
	// Variable to hold the database instance
	private SQLiteDatabase db;

	// Database open/upgrade helper
	private DatabaseHelper dbHelper;
	
	private static final UriMatcher uriMatcher;
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(ChatContract.AUTHORITY, ChatContract.contentPath(ChatContract.CONTENT_URI), ALL_ROWS);
		uriMatcher.addURI(ChatContract.AUTHORITY, ChatContract.contentPathItem(ChatContract.CONTENT_URI, "#"), SINGLE_ROW);
		uriMatcher.addURI(ChatContract.AUTHORITY, ChatContract.contentPath(ChatContract.MESSAGE_CONTENT_URI), ALL_MESSAGE_ROW);
		uriMatcher.addURI(ChatContract.AUTHORITY, ChatContract.contentPath(ChatContract.CHATROOM_CONTENT_URI), ALL_CHATROOM_ROW);
		uriMatcher.addURI(ChatContract.AUTHORITY, ChatContract.contentPathItem(ChatContract.CHATROOM_CONTENT_URI, "#"), SINGLE_CHATROOM_ROW);
	}
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		// TODO Auto-generated method stub
		switch(uriMatcher.match(uri)){
		case ALL_ROWS:
			return db.delete(PEERS_TABLE, where, whereArgs);			
		case SINGLE_ROW:
			return db.delete(PEERS_TABLE, where, whereArgs);		
		case ALL_MESSAGE_ROW:
			return db.delete(MESSAGES_TABLE, where, whereArgs);
		case ALL_CHATROOM_ROW:
			return db.delete(CHATROOMS_TABLE, where, whereArgs);	
		default: 
			throw new IllegalArgumentException("Unsupport URI:" + uri);	
		}
	}

	@Override
	public String getType(Uri _uri) {
		// TODO Auto-generated method stub
		switch(uriMatcher.match(_uri)){
		case ALL_ROWS:
			return ChatContract.contentType("peer");
		case SINGLE_ROW:
			return ChatContract.contentItemType("peer");
		default: 
			throw new IllegalArgumentException("Unsupported URI:" + _uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		long row = 0;
		switch(uriMatcher.match(uri)){
		case ALL_ROWS:			
			row = values.getAsLong(ChatContract.PEER_ID);
			if(query(ChatContract.withExtendedPath(uri, Long.toString(row)), null, null, null, null).getCount() == 0 ){
				ContentValues contentPeer = new ContentValues();
				
//				contentPeer.put(ChatContract.PEER_ID, values.getAsString(ChatContract.PEER_ID));
				contentPeer.put(ChatContract.PEER_NAME, values.getAsString(ChatContract.PEER_NAME));
				contentPeer.put(ChatContract.PEER_ADDRESS, values.getAsString(ChatContract.PEER_ADDRESS));
				contentPeer.put(ChatContract.PEER_PORT, values.getAsString(ChatContract.PEER_PORT));
				
				row = db.insert(PEERS_TABLE, null, contentPeer);
			}

			String messages = values.getAsString(ChatContract.MESSAGE_TEXT);

			String[] names = SEPARATOR.split(messages); 
			ContentValues contentMessage = new ContentValues();
			for(int i = 0; i < names.length; i++){
				ChatContract.putMessageText(contentMessage, names[i]);	
				ChatContract.putSender(contentMessage, values.getAsString(ChatContract.MESSAGE_SENDER));	
				ChatContract.putTimestamp(contentMessage, values.getAsString(ChatContract.MESSAGE_TIMESTAMP));
				ChatContract.putSequenceNumber(contentMessage, values.getAsLong(ChatContract.MESSAGE_SEQUENCE_NUMBER));
				ChatContract.putMessageFk(contentMessage, row);
				db.insert(MESSAGES_TABLE, null, contentMessage);
				contentMessage.clear();
			}
			
			if (row > 0){
				Uri instanceUri = ContentUris.withAppendedId(uri, row);
				getContext().getContentResolver().notifyChange(instanceUri, null);
				return instanceUri;
			}
		case SINGLE_ROW:
			row = db.insert(PEERS_TABLE, null, values);
			if (row > 0){
				Uri instanceUri = ContentUris.withAppendedId(ChatContract.CONTENT_URI, row);
				getContext().getContentResolver().notifyChange(instanceUri, null);
				return instanceUri;
			}
		case ALL_CHATROOM_ROW:	
			row = db.insert(CHATROOMS_TABLE, null, values);
			if (row > 0){
				Uri instanceUri = ContentUris.withAppendedId(uri, row);
				getContext().getContentResolver().notifyChange(instanceUri, null);
				return instanceUri;
			}
		case ALL_MESSAGE_ROW:
			row = db.insert(MESSAGES_TABLE, null, values);
			if (row > 0){
				Uri instanceUri = ContentUris.withAppendedId(ChatContract.MESSAGE_CONTENT_URI, row);
				getContext().getContentResolver().notifyChange(instanceUri, null);
				return instanceUri;
			}
		default:
			throw new SQLException("Insertion failed");
		}
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		dbHelper = new DatabaseHelper(this.getContext(), DATABASE_NAME, null,
				DATABASE_VERSION);
		db = dbHelper.getWritableDatabase();
//		db.execSQL("PRAGMA foreign_keys=ON;");
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sort) {
		// TODO Auto-generated method stub
		switch(uriMatcher.match(uri)){
		case ALL_ROWS:
			return db.rawQuery(SELECT_PEERS, null);
		case SINGLE_ROW:
			SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
			String where = PEERS_TABLE + "." + selection + "=?";

			String table = "Peers LEFT OUTER JOIN Messages ON (Peers._id = Messages.peer_fk)";
			String[] columns = new String[] { "Peers._id", "name","address",
					"port", "group_concat(text,'|') as messages" };
			qBuilder.setTables(table);
			String groupBy = "Peers._id,name,address,port";

			return db.query(qBuilder.getTables(),columns, where, selectionArgs, groupBy, null, null);		
		case ALL_MESSAGE_ROW:
			return db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sort);
		case ALL_CHATROOM_ROW:
			return db.query(CHATROOMS_TABLE, projection, selection, selectionArgs, null, null, sort);	
		case SINGLE_CHATROOM_ROW:
			SQLiteQueryBuilder qBuilder1 = new SQLiteQueryBuilder();
			String where1 = CHATROOMS_TABLE + "." + ChatContract.CHATROOM_ID + "=?";
			String[] whereArgs1 = { Long.toString(ChatContract.getId(uri)) };
			String table1 = "Chatrooms LEFT OUTER JOIN Messages ON (Chatrooms._id = Messages.chatroom_fk)";
			String[] columns1 = new String[] { "Chatrooms._id", "Chatrooms.name", "Messages.sender", "Messages.timestamp", "Messages.text" };
			qBuilder1.setTables(table1);
			String groupBy1 = "Chatrooms._id, Chatrooms.name, Messages.sender, Messages.timestamp";

			return db.query(qBuilder1.getTables(),columns1, where1, whereArgs1, groupBy1, null, null);		
		default:
			throw new IllegalArgumentException("Unsupported URI:" + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		// TODO Auto-generated method stub
		switch(uriMatcher.match(uri)){
		case ALL_ROWS:
			return db.update(PEERS_TABLE, values, where, whereArgs);
		case SINGLE_ROW:
			if(null != where && !"".equals(where)){
				where = where + " and " + ChatContract.getId(uri);
			}
			return db.update(PEERS_TABLE, values, where, whereArgs);			
		default: 
			throw new IllegalArgumentException("Unsupported URI:" + uri);
		}
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			// TODO Auto-generated method stub
			_db.execSQL(CREATE_PEERS_TABLE);
			_db.execSQL(CREATE_CHATROOMS_TABLE);
			_db.execSQL(CREATE_MESSAGES_TABLE);
			_db.execSQL(" CREATE INDEX MessagesSenderIndex ON Messages(peer_fk) ");
			_db.execSQL(" CREATE INDEX MessagesChatroomIndex ON Messages(chatroom_fk) ");
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {
			// TODO Auto-generated method stub
			// Log the version upgrade.
			Log.w("ChatProvider", "Upgrading from version" + _oldVersion
					+ " to " + _newVersion);
			// Upgrade: drop the old table and create a new one.
			_db.execSQL("DROP TABLE IF EXISTS " + PEERS_TABLE);
			_db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE);
			_db.execSQL("DROP TABLE IF EXISTS " + CHATROOMS_TABLE);
			// Create a new one.
			onCreate(_db);
		}
	}
}
