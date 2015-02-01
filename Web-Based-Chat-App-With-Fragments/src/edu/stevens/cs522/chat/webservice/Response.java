package edu.stevens.cs522.chat.webservice;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class Response implements Parcelable {

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

	public abstract boolean isValid();
	
}
