package edu.stevens.cs522;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

// TODO: (ME) inherent Activity (ActivityFragment)
//********** CHANGES HAS BEEN MADE HERE **********
//************************************************
public class BookStore extends Activity implements LoaderManager.LoaderCallbacks<Cursor>
{
	public static final int ListViewLoader_ID = 0 ;
	
	ListView cartList ;
	SimpleCursorAdapter adapter ;
	static final private int SEARCH_REQUEST = 0;
	static final private int CHECKOUT_REQUEST = SEARCH_REQUEST + 1;

	long last_selected_item_on_list = -1 ;
	//private CartDbAdapter cartDbAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		
		// Set the layout
		setContentView(R.layout.cart);
		cartList = (ListView)findViewById(R.id.booksList) ;
		cartList.setEmptyView(findViewById(R.id.empty_list_item));
		cartList.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long id) 
			{
				last_selected_item_on_list = id ;
			}
		}) ;

		// Open the database and display its contents in the list view
		//cartDbAdapter = new CartDbAdapter(this); //%%%
		//cartDbAdapter.open();                    //%%%	
		/*
		ContentResolver re = getContentResolver() ;
		ContentValues cv = new ContentValues(4) ;
		cv.put(CartDbAdapter.KEY_TITLE,"BookTitle2") ;
		cv.put(CartDbAdapter.KEY_AUTHOR,"BookAuther2") ;
		cv.put(CartDbAdapter.KEY_ISBN,"12345ISBN") ;
		cv.put(CartDbAdapter.KEY_PRICE,"$550000") ;
		re.insert(BookProvider.CONTENT_URI,cv) ;
		*/		
		//re.delete(BookProvider.CONTENT_URI,null,null) ;		
		fillData();
	}

	private void fillData() 
	{
		// Get all of the books from the cart database 
		//Cursor c = cartDbAdapter.fetchAllItems(); //%%%
		//TODO: (Me) don't use startManaginCursor
		//startManagingCursor(c);                   //%%%
		getLoaderManager().initLoader(ListViewLoader_ID, null, this);

		// For the list adapter that will exchange data with the list view, we need
		// to specify which layout object to bind to which data object.
		String[] to = new String[] { CartDbAdapter.KEY_TITLE, CartDbAdapter.KEY_AUTHOR };
        int[] from = new int[] { R.id.cart_row_title, R.id.cart_row_author };
        
        //TODO: (ME) don't use SimpleCursoeAdapter
        // Now create a list adaptor that encapsulates the result of a DB query
        /*                                         //%%%
        ListAdapter adapter = new SimpleCursorAdapter(
                this,       // Context.
                R.layout.cart_row,  // Specify the row template to use 
                c,          // Cursor encapsulates the DB query result.
                to, 		// Array of cursor columns to bind to.
                from);      // Parallel array of which layout objects to bind to those columns.
         */
        adapter = new SimpleCursorAdapter(this, R.layout.cart_row, null, to, from, 0);
        // Bind to our new adapter.
      //TODO: (ME) don't use the old class Adapter for the ListView
      //********** CHANGES HAS BEEN MADE HERE **********
      //************************************************
        cartList.setAdapter(adapter);  //%%%
        //setListAdapter(adapter);
        cartList.setSelection(0) ;
        
       //************************************************
       //************************************************
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		super.onCreateOptionsMenu(menu);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		super.onOptionsItemSelected(item);
		Intent i;

		switch (item.getItemId()) 
		{
		case (R.id.main_search):
			i = new Intent(this, Search.class);
			startActivityForResult(i, SEARCH_REQUEST);
			return true;
		case (R.id.main_checkout):
			// TODO: Launch checkout IF there is at least one book in the cart.
			if (cartList.getCount() > 0) 
			{
				i = new Intent(this,Checkout.class) ;
				i.putExtra("NUMBER_OF_BOOKS", cartList.getCount()) ;
				startActivityForResult(i, CHECKOUT_REQUEST);
			} 
			else 
			{
				Toast.makeText(this, "Empty cart!", Toast.LENGTH_SHORT).show();
			}
			return true;
		case (R.id.main_delete): 
			// TODO: Delete the selected row.
			//if is negative then it means that no item was selected
			if(last_selected_item_on_list >= 0)
			{
				getContentResolver().delete(Uri.withAppendedPath(BookProvider.CONTENT_URI,String.valueOf(last_selected_item_on_list)), CartDbAdapter.KEY_ROWID +"= ?",new String[]{String.valueOf(last_selected_item_on_list)}) ;
				last_selected_item_on_list = -1 ;
			}
			else
			{
				Toast.makeText(this, "Select a book to remove!", Toast.LENGTH_SHORT).show();
			}
    		return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,Intent intent) 
	{
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode) 
		{
		case SEARCH_REQUEST:
			switch (resultCode) 
			{
			case RESULT_OK:
				//fillData();
				//adapter.notifyDataSetChanged() ;
				return;
			case RESULT_CANCELED:
				return;
			}
		case CHECKOUT_REQUEST:
			switch (resultCode) 
			{
			case RESULT_OK:
				// TODO: use the loader
	    		//cartDbAdapter.deleteAll();
				getContentResolver().delete(BookProvider.CONTENT_URI,null,null) ;
	    		//fillData();
				return;
			case RESULT_CANCELED:
				return;
			}
		}
	}
	
	//LoaderManager.LoaderCallbacks<Cursor>
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) 
	{
			return new CursorLoader(BookStore.this, BookProvider.CONTENT_URI, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) 
	{
		adapter.swapCursor(arg1);
		//emptyCartText.setVisibility(TextView.INVISIBLE) ; 
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) 
	{
		adapter.swapCursor(null);
	}	
}