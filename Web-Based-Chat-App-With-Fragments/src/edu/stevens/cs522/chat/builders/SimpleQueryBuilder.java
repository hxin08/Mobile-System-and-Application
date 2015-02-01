package edu.stevens.cs522.chat.builders;

import java.util.ArrayList;
import java.util.List;

import edu.stevens.cs522.chat.interfaces.IProceed;
import edu.stevens.cs522.chat.interfaces.ICursor;
import edu.stevens.cs522.chat.interfaces.ISimpleQuery;
import edu.stevens.cs522.chat.managers.AsyncContentResolver;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

public class SimpleQueryBuilder<T> implements IProceed<Cursor>{

	private ICursor<T> helper;
	@SuppressWarnings("unused")
	private ISimpleQuery<T> listener;
	
	private SimpleQueryBuilder(ICursor<T> helper, ISimpleQuery<T> listener){
		this.helper = helper;
		this.listener = listener;
	}
	
	public static <T> void executeQuery(Activity context, Uri uri, ICursor<T> helper, ISimpleQuery<T> listener){
		SimpleQueryBuilder<T> qb = new SimpleQueryBuilder<T>(helper, listener);
		AsyncContentResolver resolver = new AsyncContentResolver(context.getContentResolver());
		resolver.queryAsync(uri, null, null, null, null, qb);
	} 
	
	public static <T> void executeQuery(Activity context, Uri uri,  String[] projection, String selection, String[] selectionArgs, ICursor<T> helper, ISimpleQuery<T> listener){
		SimpleQueryBuilder<T> qb = new SimpleQueryBuilder<T>(helper, listener);
		AsyncContentResolver resolver = new AsyncContentResolver(context.getContentResolver());
		resolver.queryAsync(uri, projection, selection, selectionArgs, null, qb);
	} 
	
	public static <T> void executeDelete(Activity context, Uri uri, String selection, String[] selectionArgs){
		AsyncContentResolver resolver = new AsyncContentResolver(context.getContentResolver());
		resolver.deleteAsync(uri, selection, selectionArgs);
	}
	
	public void kontinue(Cursor cursor) {
		// TODO Auto-generated method stub
		List<T> instances = new ArrayList<T>();
		if(cursor.moveToFirst()){
			do{
				T instance = helper.create(cursor);
				instances.add(instance);
			}while (cursor.moveToNext());
		}
	}
	
}
