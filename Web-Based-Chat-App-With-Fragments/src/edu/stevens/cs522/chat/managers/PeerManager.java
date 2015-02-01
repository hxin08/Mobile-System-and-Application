package edu.stevens.cs522.chat.managers;

import edu.stevens.cs522.chat.contracts.ChatContract;
import edu.stevens.cs522.chat.interfaces.IProceed;
import edu.stevens.cs522.chat.interfaces.ICursor;
import edu.stevens.cs522.chat.providers.ChatroomContentProvider;
import edu.stevens.cs522.chat.providers.MessageContentProvider;
import edu.stevens.cs522.chat.providers.PeerContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;



public class PeerManager extends QueryManager<PeerContentProvider> {

	private AsyncContentResolver asyncResolver;
	private ContentResolver syncResolver;
	private ICursor<PeerContentProvider> creator;
	
	public PeerManager(Context context, ICursor<PeerContentProvider> creator,
			int loaderID) {
		super(context, creator, loaderID);
		// TODO Auto-generated constructor stub
		this.creator = creator;
		asyncResolver = this.getAsyncResolver();
		syncResolver = this.getSyncResolver();
	}

	
	public void persistAsync(PeerContentProvider peer, MessageContentProvider message, IProceed<Uri> callback) {
		ContentValues values = new ContentValues();
		peer.writeToProvider(values);
		message.writeToProvider(values);
		asyncResolver.insertAsync(ChatContract.CONTENT_URI, values, callback);
	}

	public void persistAsync(final PeerContentProvider peer, MessageContentProvider message) {
		ContentValues values = new ContentValues();
		peer.writeToProvider(values);
		message.writeToProvider(values);
		asyncResolver.insertAsync(ChatContract.CONTENT_URI, values, new IProceed<Uri>(){

			public void kontinue(Uri uri) {
				// TODO Auto-generated method stub
				peer.id = ChatContract.getId(uri);
			}
			
		});
		
	}
	
	public void deleteAsync(Uri uri, String selection, String[] selectionArgs){
		this.executeDelete(uri, selection, selectionArgs);
	}
	
	public void queryAsync(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, IProceed<Cursor> callback){
		asyncResolver.queryAsync(uri, projection, selection, selectionArgs, sortOrder, callback);
	}

	public TypedCursor<PeerContentProvider> getAll(){
		Cursor cursor = syncResolver.query(ChatContract.CONTENT_URI, null, null, null, null);
		cursor.setNotificationUri(syncResolver, ChatContract.CONTENT_URI);
		return new TypedCursor<PeerContentProvider>(cursor, this.creator);
	}
	
	public TypedCursor<MessageContentProvider> getAllMessage(){
		Cursor cursor = syncResolver.query(ChatContract.MESSAGE_CONTENT_URI, null, null, null, null);
		return new TypedCursor<MessageContentProvider>(cursor, new ICursor<MessageContentProvider>(){
			public MessageContentProvider create(Cursor cursor) {
				// TODO Auto-generated method stub
				return new MessageContentProvider(cursor);
			}});
	}
	
	public TypedCursor<ChatroomContentProvider> getAllChatroom(){
		Cursor cursor = syncResolver.query(ChatContract.CHATROOM_CONTENT_URI, null, null, null, null);
		return new TypedCursor<ChatroomContentProvider>(cursor, new ICursor<ChatroomContentProvider>(){
			public ChatroomContentProvider create(Cursor cursor) {
				// TODO Auto-generated method stub
				return new ChatroomContentProvider(cursor);
			}});
	}
	
	public void getAll(IProceed<Cursor> callback){
		 asyncResolver.queryAsync(ChatContract.CONTENT_URI, null,null, null, null, callback);
	}
	
}
