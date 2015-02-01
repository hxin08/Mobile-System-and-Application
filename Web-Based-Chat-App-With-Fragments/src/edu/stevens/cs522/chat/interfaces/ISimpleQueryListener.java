package edu.stevens.cs522.chat.interfaces;

import java.util.List;

public interface ISimpleQueryListener<T>{
	public void handleResults(List<T> result);
}
