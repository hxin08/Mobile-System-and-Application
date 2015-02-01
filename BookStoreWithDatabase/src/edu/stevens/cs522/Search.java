package edu.stevens.cs522;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class Search extends Activity {
	
	/*
	 * For now, search just adds the specified book to the shopping cart.
	 */
	static final private int MENU_BUY = R.id.BUY;
	static final private int MENU_CANCEL = R.id.Cancel;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// TODO: Add menu for BUY and CANCEL
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.search_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) 
		{
		case (MENU_BUY):
			buyBook();
			setResult(RESULT_OK, getIntent());
			finish();
			return true;
			// TODO: Add CANCEL option handling.
		case (MENU_CANCEL):
			setResult(RESULT_CANCELED, getIntent());
			finish();
			return true ;
		}
		return false;
	}
	
	public void buyBook()
	{
		/*
		 * Add the specified book to the shopping cart.
		 */
		EditText titleText = (EditText) findViewById(R.id.search_title);
		EditText authorText = (EditText) findViewById(R.id.search_author);
		EditText isbnText = (EditText) findViewById(R.id.search_isbn);
		EditText priceText = (EditText) findViewById(R.id.search_price);
	
		String title = titleText.getText().toString();
		String author = authorText.getText().toString();
		String isbn = isbnText.getText().toString();
		String price = priceText.getText().toString();
		
		// TODO: Add the book to the shopping cart.
		ContentResolver re = getContentResolver() ;
		ContentValues cv = new ContentValues(4) ;
		cv.put(CartDbAdapter.KEY_TITLE,title) ;
		cv.put(CartDbAdapter.KEY_AUTHOR,author) ;
		cv.put(CartDbAdapter.KEY_ISBN,isbn) ;
		cv.put(CartDbAdapter.KEY_PRICE,price) ;
		re.insert(BookProvider.CONTENT_URI,cv) ;
	}
	
}