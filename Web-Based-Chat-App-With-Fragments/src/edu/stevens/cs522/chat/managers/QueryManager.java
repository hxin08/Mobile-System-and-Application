package edu.stevens.cs522.chat.managers;

import edu.stevens.cs522.chat.builders.QueryBuilder;
import edu.stevens.cs522.chat.builders.SimpleQueryBuilder;
import edu.stevens.cs522.chat.interfaces.ICursor;
import edu.stevens.cs522.chat.interfaces.IQuery;
import edu.stevens.cs522.chat.interfaces.ISimpleQuery;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

public abstract class QueryManager<T> {
	
	private final Context context;
	
	private final ICursor<T> creator;
	
	private final int loaderID;
	
	private final String tag;
	
	private ContentResolver syncResolver;
	
	private AsyncContentResolver asyncResolver;
	
	
	public QueryManager(Context context, ICursor<T> creator, int loaderID){
		this.context = context;
		this.creator = creator;
		this.loaderID = loaderID;
		this.tag = this.getClass().getCanonicalName();
	}
	
	protected ContentResolver getSyncResolver(){
		if(syncResolver == null){
			syncResolver = context.getContentResolver();
		}
		return syncResolver;
	}
	
	protected AsyncContentResolver getAsyncResolver(){
		if(asyncResolver == null){
			asyncResolver = new AsyncContentResolver(context.getContentResolver());
		}
		return asyncResolver;
	}
	
	protected void executeSimpleQuery(Uri uri, ISimpleQuery<T> listener){
		SimpleQueryBuilder.executeQuery((Activity)context, uri, creator, listener);
	}
	
	protected void executeSimpleQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, ISimpleQuery<T> listener){
		SimpleQueryBuilder.executeQuery((Activity)context, uri, projection, selection, selectionArgs, creator, listener);
	}
	
	protected void executeDelete(Uri uri, String selection, String[] selectionArgs){
		SimpleQueryBuilder.executeDelete((Activity)context, uri, selection, selectionArgs);
	}
	
	protected void executeQuery(Uri uri, IQuery<T> listener) {
		QueryBuilder.executeQuery(tag, (Activity) context, uri, loaderID, creator, listener);
	}

	protected void executeQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, IQuery<T> listener) {
		QueryBuilder.executeQuery(tag, (Activity) context, uri, loaderID, projection, selection, selectionArgs, creator, listener);
	}

	protected void reexecuteQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, IQuery<T> listener) {
		QueryBuilder.reexecuteQuery(tag, (Activity) context, uri, loaderID, projection, selection, selectionArgs, creator, listener);
	}


	
	
	
}
