package edu.stevens.cs522.chat.builders;

import edu.stevens.cs522.chat.contracts.ChatContract;
import edu.stevens.cs522.chat.interfaces.ICursor;
import edu.stevens.cs522.chat.interfaces.IQuery;
import edu.stevens.cs522.chat.managers.TypedCursor;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

public class QueryBuilder<T> implements LoaderManager.LoaderCallbacks<Cursor> {

	@SuppressWarnings("unused")
	private String tag;
	private int loaderID;
	private Context context;
	private ICursor<T> creator;
	private IQuery<T> listener;

	private QueryBuilder(String tag, Context context, Uri uri, int loaderID,
			ICursor<T> creator, IQuery<T> listener) {
		this.tag = tag;
		this.loaderID = loaderID;
		this.context = context;
		this.creator = creator;
		this.listener = listener;
	}

	public static <T> void executeQuery(String tag, Activity context, Uri uri,
			int loaderID, ICursor<T> creator, IQuery<T> listener) {
		QueryBuilder<T> qb = new QueryBuilder<T>(tag, context, uri, loaderID,
				creator, listener);
		LoaderManager lm = context.getLoaderManager();
		lm.initLoader(loaderID, null, qb);
		ContentResolver resolver = context.getContentResolver();
		listener.handleResults(new TypedCursor<T>(resolver.query(ChatContract.CONTENT_URI, null, null, null, null), creator));
	}

	public static <T> void executeQuery(String tag, Activity context, Uri uri,
			int loaderID, String[] projection, String selection,
			String[] selectionArgs, ICursor<T> creator,
			IQuery<T> listener) {
		QueryBuilder<T> qb = new QueryBuilder<T>(tag, context, uri, loaderID,
				creator, listener);
		LoaderManager lm = context.getLoaderManager();
		lm.initLoader(loaderID, null, qb);
		ContentResolver resolver = context.getContentResolver();
		listener.handleResults( new TypedCursor<T>(resolver.query(ChatContract.CONTENT_URI, projection, selection, selectionArgs, null), creator));
	}

	public static <T> void reexecuteQuery(String tag, Activity context, Uri uri,
			int loaderID, String[] projection, String selection,
			String[] selectionArgs, ICursor<T> creator,
			IQuery<T> listener) {
		QueryBuilder<T> qb = new QueryBuilder<T>(tag, context, uri, loaderID,
				creator, listener);
		LoaderManager lm = context.getLoaderManager();
		lm.restartLoader(loaderID, null, qb);
		ContentResolver resolver = context.getContentResolver();
		resolver.query(ChatContract.CONTENT_URI, projection, selection, selectionArgs, null);
		listener.handleResults( new TypedCursor<T>(resolver.query(ChatContract.CONTENT_URI, null, null, null, null), creator));
	}
	
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		// TODO Auto-generated method stub
		if (id == loaderID) {
			return new CursorLoader(context, ChatContract.CONTENT_URI,
					new String[] { ChatContract.PEER_ID, ChatContract.PEER_NAME, ChatContract.PEER_ADDRESS, ChatContract.PEER_PORT, ChatContract.PEER_MESSAGE},
					null, null, null);
		}
		return null;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// TODO Auto-generated method stub
		if (loader.getId() == loaderID) {
			listener.handleResults(new TypedCursor<T>(cursor, creator));
		} else {
			throw new IllegalStateException("Unexpected loader callback");
		}
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		if (loader.getId() == loaderID) {
			listener.closeResults();
		} else {
			throw new IllegalStateException("Unexpected loader callback");
		}
	}
}
