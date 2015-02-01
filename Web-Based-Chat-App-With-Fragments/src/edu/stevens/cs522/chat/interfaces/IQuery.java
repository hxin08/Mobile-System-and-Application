package edu.stevens.cs522.chat.interfaces;

import edu.stevens.cs522.chat.managers.TypedCursor;

public interface IQuery<T>{
	public void handleResults(TypedCursor<T> results);
	public void closeResults();
}
