package edu.stevens.cs522.chat.service;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import edu.stevens.cs522.chat.client.ChatProvider;
import edu.stevens.cs522.chat.interfaces.MessageProvider;
import edu.stevens.cs522.chat.interfaces.PeerProvider;

public class DataBaseAdapter {
	private static final String DATABASE_NAME = "Chat.db";
	private static final String DATABASE_TABLE= "message";
	private static final String DATABASE_TABLE_PEER= "peer";
	public static final String TEXT = "text";
	public static final String ID = "_id";
	public static final String SENDER = "sender";
	public static final String DATE = "date";
	public static final String MESSAGEID = "messsageid";
	public static final String SENDERID = "senderid";
	public static final String DATABASE_CREATE =
		     "create table " + DATABASE_TABLE + " ("
		        + ID + " integer primary key, "
		        + TEXT + " text not null, "
		        + SENDER + " text not null,"
		        + DATE + " integer not null,"
		        + MESSAGEID + " integer not null,"
		        + SENDERID + " integer not null"
		+ ")";
	 public static final String DATABASE_CREATE_PEER =
		     "create table peer ("
		        + ID + " integer primary key, "
		        + "name text not null, "
		        + SENDERID + " integer not null"
		+ ")";
	 //private static final String DATABASE_CREATE_FK =
	//	     "CREATE INDEX PeerMessageIndex ON peer(peer_fk);";
	 public SQLiteDatabase db;
		// Context of the application using the database. 
	private Context context;
	private DatabaseHelper dbHelper;
	private static class DatabaseHelper extends SQLiteOpenHelper {
		   public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		// Database version mismatch: version on disk
		// needs to be upgraded to the current version.
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
		                                             int _newVersion) {
		      // Log the version upgrade.
		      Log.w("TaskDBAdapter",
		            "Upgrading from version " +  _oldVersion
		                             + " to " +  _newVersion);
		      // Upgrade: drop the old table and create a new one.
		      
		      _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
		      _db.execSQL("DROP TABLE IF EXISTS peer");
		      onCreate(_db);
		   }
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(DataBaseAdapter.DATABASE_CREATE);
			db.execSQL(DataBaseAdapter.DATABASE_CREATE_PEER);
		}
	}
	public DataBaseAdapter(Context _context) {
		context = _context;
		dbHelper = new DatabaseHelper(context, DATABASE_NAME, null,ChatProvider.DATABASE_VERSION);
	}
	public DataBaseAdapter open() throws SQLException { db = dbHelper.getWritableDatabase();
		return this;
	}
	public Cursor getAllEntries () {return db.query(DATABASE_TABLE,
            new String[] {ID, TEXT,SENDER},
            null, null, null, null, null);
	}	
	public boolean deleteAll(){
		return db.delete(DATABASE_TABLE, null, null)>0;
	}
	public boolean addMessage(MessageProvider b) {
		ContentValues contentValues = new ContentValues();
		b.writeToProvider(contentValues);
		return db.insert(DATABASE_TABLE, null, contentValues)>0;
		 
	}
	public boolean addPeer(PeerProvider b) {
		ContentValues contentValues = new ContentValues();
		b.writeToProvider(contentValues);
		db.delete(DATABASE_TABLE_PEER, "name ='" + b.name +"' and "+"senderid ='"+b.senderid+"'", null);
		return db.insert(DATABASE_TABLE_PEER, null, contentValues)>0;
		 
	}
	public boolean deletebytitle(String t,String a) throws SQLException{
		//boolean re=db.delete(DATABASE_TABLE, TITLE + "='" + t +"' and "+ AUTHOR+"='"+a+"'", null)>0;
		
		return true;
	}
	public boolean selectbytitle(String t,String a) throws SQLException{
		//boolean re=db.delete(DATABASE_TABLE, TITLE + "='" + t +"' and "+ AUTHOR+"='"+a+"'", null)>0;
		
		return true;
	}
	public boolean deletePeer(PeerProvider b) {
		ContentValues contentValues = new ContentValues();
		b.writeToProvider(contentValues);
		return db.delete(DATABASE_TABLE_PEER, "name ='" + b.name +"' and "+"senderid ='"+b.senderid+"'", null)>0;
		 
	}
	public Cursor getAllPeer () {return db.query(DATABASE_TABLE_PEER,
            new String[] {ID, "name","address","port"},
            null, null, null, null, null);
	}
	public Cursor getMessgeByPeer (String name) {
		String whereClause = "sender = ?";
		String[] whereArgs = new String[] {
		    name
		};
		return db.query(DATABASE_TABLE,
			 new String[] {ID, TEXT,SENDER},
	            whereClause, whereArgs, null, null, null);
	}
}
