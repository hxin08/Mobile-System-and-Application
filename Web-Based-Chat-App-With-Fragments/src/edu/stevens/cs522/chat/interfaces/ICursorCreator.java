package edu.stevens.cs522.chat.interfaces;

import android.database.Cursor;

public interface ICursorCreator<T>{
	public T create(Cursor cursor);
}
