package edu.stevens.cs522.chat.managers;

import edu.stevens.cs522.chat.interfaces.ICursor;
import android.database.Cursor;

public class TypedCursor<T> {
	private Cursor cursor;
	private ICursor<T> creator;
	
	public TypedCursor(Cursor cursor, ICursor<T> creator){
		this.cursor = cursor;
		this.creator = creator;
	}

	public Cursor getCursor() {
		return cursor;
	}

	public void setCursor(Cursor cursor) {
		this.cursor = cursor;
	}

	public ICursor<T> getCreator() {
		return creator;
	}

	public void setCreator(ICursor<T> creator) {
		this.creator = creator;
	}
	
}
