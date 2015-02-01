package edu.stevens.cs522;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class Checkout extends Activity {
	static final private int MENU_ORDER = R.id.Order;
	static final private int MENU_CANCEL = R.id.cancel_checkout ;

	private int nitems = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkout);
		
		// TODO: Have title specify how many books are being purchased.
		Intent i = getIntent() ;
		Bundle extra = i.getExtras() ;
		nitems = extra.getInt("NUMBER_OF_BOOKS") ;

		TextView titleText = (TextView) findViewById(R.id.title);
		titleText.setText("Checking out "+nitems+" books.");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		/*
		 * TODO: Provide menu for ORDER and CANCEL.
		 */
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.checkout_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case MENU_ORDER:
			String msg = nitems>1 ? "Books ordered!" : "Book ordered!";
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
			setResult(RESULT_OK);
			finish();
			return true;
		case MENU_CANCEL:
			setResult(RESULT_CANCELED);
			finish();
			return true;
		}
		return false;
	}

}