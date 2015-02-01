package edu.stevens.cs522;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;


public class BookProvider extends ContentProvider
{
	public static final String PROVIDER_AUTHORITY = "edu.stevens.cs522.bookprovider" ;
	public static final String TABLE_NAME = "cart";
	public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_AUTHORITY+ "/" + TABLE_NAME);
	
	CartDbAdapter dbAdapter ;
	
	private static  UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH); ;
	static
	{
		sUriMatcher.addURI(PROVIDER_AUTHORITY, TABLE_NAME, 1);
		sUriMatcher.addURI(PROVIDER_AUTHORITY, TABLE_NAME +"/#", 2);
	}
	
	@Override
	public boolean onCreate() 
	{
		dbAdapter = new CartDbAdapter(getContext()) ;
		dbAdapter.open();
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) 
	{
		Cursor c ;
		switch(sUriMatcher.match(uri))
		{
			//return all the rows
			case 1:
				//return dbAdapter.fetchAllItems() ;
				c  = dbAdapter.fetchAllItems() ; ;
				c.setNotificationUri(getContext().getContentResolver(), uri); 
				return c; 
				
			//return one row
			case 2:
				c =  dbAdapter.fetchItem(Integer.valueOf(uri.getLastPathSegment()));
				c.setNotificationUri(getContext().getContentResolver(), uri); 
				return c ;
			
		}
		return null;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) 
	{
		/*
			public static final String KEY_TITLE = "title";
			public static final String KEY_AUTHOR = "author";
			public static final String KEY_ISBN = "isbn";
			public static final String KEY_PRICE = "price";
		 */
		//long dbAdapter.createItem(String title, String author, String isbn, String price) 
		long rowId =  dbAdapter.createItem(values.getAsString(CartDbAdapter.KEY_TITLE),
									values.getAsString(CartDbAdapter.KEY_AUTHOR),
									values.getAsString(CartDbAdapter.KEY_ISBN),
									values.getAsString(CartDbAdapter.KEY_PRICE) )  ;
		getContext().getContentResolver().notifyChange(Uri.withAppendedPath(CONTENT_URI,String.valueOf(rowId)), null);
		return Uri.withAppendedPath(CONTENT_URI,String.valueOf(rowId)) ;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) 
	{
		//DO NOT FORGET TO MENSION THE IMPLEMENTATION OF deleteAll()
		//boolean dbAdapter.deleteItem(long rowId) 
		//boolean dbAdapter.deleteAll() 
		switch(sUriMatcher.match(uri))
		{
			//delete all the rows
			case 1:
				getContext().getContentResolver().notifyChange(CONTENT_URI, null);
				return dbAdapter.deleteAll() ? 1 : 0 ;
				
			//delete one row
			case 2:
				getContext().getContentResolver().notifyChange(Uri.withAppendedPath(CONTENT_URI,String.valueOf(uri.getLastPathSegment())), null);
				return dbAdapter.deleteItem(Integer.valueOf(uri.getLastPathSegment())) ? 1 : 0 ;
			
		}
		return 0;
	}

	@Override
	public String getType(Uri uri) 
	{
		return "text/plain" ;
		//return null;
	}
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) 
	{
		return 0;
	}
}
