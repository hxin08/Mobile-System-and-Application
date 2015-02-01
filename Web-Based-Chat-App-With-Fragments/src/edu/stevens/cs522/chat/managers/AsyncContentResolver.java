package edu.stevens.cs522.chat.managers;

import edu.stevens.cs522.chat.interfaces.IProceed;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;


public class AsyncContentResolver extends AsyncQueryHandler {

	public AsyncContentResolver(ContentResolver cr) {
		super(cr);
		// TODO Auto-generated constructor stub
	}
	
	public void insertAsync(Uri uri, ContentValues values, IProceed<Uri> callback){
		this.startInsert(0, callback, uri, values);
	}
	
	@Override
	public void onInsertComplete(int token, Object cookie, Uri uri){
		if(cookie != null){
			@SuppressWarnings("unchecked")
			IProceed<Uri> callback = (IProceed<Uri>) cookie;
			callback.kontinue(uri);
		}
	}
	
	public void queryAsync(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder, IProceed<Cursor> callback) {
		this.startQuery(0, callback, uri, projection, selection, selectionArgs, sortOrder);
	}
	
	@Override
	public void onQueryComplete(int token, Object cookie, Cursor c){
		if(cookie != null){
			@SuppressWarnings("unchecked")
			IProceed<Cursor> callback = (IProceed<Cursor>) cookie;
			callback.kontinue(c);
		}
	}
	
	public void deleteAsync(Uri uri, String selection, String[] selectionArgs){
		this.startDelete(0, null, uri, selection, selectionArgs);
	}
	
	@Override
	public void onDeleteComplete(int token, Object cookie, int result){
		if(cookie != null){
			@SuppressWarnings("unchecked")
			IProceed<Integer> callback = (IProceed<Integer>) cookie;
			callback.kontinue(result);
		}
	}
	
	public void updateAsync(Uri uri, ContentValues values, String selection, String[] selectionArgs){
		this.startUpdate(0, null, uri, values, selection, selectionArgs);
	}
	
	@Override
	public void onUpdateComplete(int token, Object cookie, int result){
		if(cookie != null){
			@SuppressWarnings("unchecked")
			IProceed<Integer> callback = (IProceed<Integer>) cookie;
			callback.kontinue(result);
		}
	}
}
