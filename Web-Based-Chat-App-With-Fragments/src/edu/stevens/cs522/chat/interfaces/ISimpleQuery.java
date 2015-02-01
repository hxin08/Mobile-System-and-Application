package edu.stevens.cs522.chat.interfaces;

import java.util.List;

public interface ISimpleQuery<T>{
	public void handleResults(List<T> result);
}
