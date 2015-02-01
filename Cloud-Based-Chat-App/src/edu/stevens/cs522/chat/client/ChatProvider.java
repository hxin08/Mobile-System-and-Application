package edu.stevens.cs522.chat.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import edu.stevens.cs522.chat.interfaces.MessageProvider;
import edu.stevens.cs522.chat.interfaces.PeerProvider;
import edu.stevens.cs522.chat.service.DataBaseAdapter;


public class ChatProvider extends ContentProvider {
	private static final String AUTHORITY = "chat";
	private static final String DATABASE_NAME = "Chat.db";
	private static final String DATABASE_TABLE= "message";
	private static final String DATABASE_TABLE_PEER= "peer";
	public static final String TEXT = "text";
	public static final String ID = "_id";
	public static final String SENDER = "sender";
	public static final int DATABASE_VERSION = 8;
	private static final int ALL_ROWS = 1; 
	private static final int SINGLE_ROW = 2;
	private static final int ALL_ROWS_PEER = 3;
	private static final int SINGLE_ROW_PEER = 4;
	private DatabaseHelper database;
	@SuppressWarnings("unused")
	private Context context;
	private SQLiteDatabase db;
	private String clientName;
	private int clientPort;
	private String clientHost;
	private String clientRegId;
	private long clientId;
	@SuppressWarnings("unused")
	private String charGroup="_default";
	public HttpURLConnection httpConnection;
	private URL url;
	private URL urlSend;
	List<String> usersList = null;
	List<String[]> msgList=null;
	public static boolean inited=false;
	long lastsync=0;
	long latestsync=0;
	public static final Uri CONTENT_URI= Uri.parse("content://" + AUTHORITY
	      + "/" + DATABASE_TABLE);
	public static final Uri CONTENT_URI_PEER= Uri.parse("content://" + AUTHORITY
		      + "/" + DATABASE_TABLE_PEER);
	public static final String CONTENT_PATH = "content://chat/message";
	public static final String CONTENT_PATH_ITEM =  "content://chat/message#";
	  public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
	      + "/message";
	  public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
	      + "/message";
	  public static String[] projection = new String[] {
	          ID,TEXT,SENDER,"date","messageId","senderId"
	        };
	  public static String[] projection2 = new String[] {
	          ID,"name","senderid"
	        };
	  private static class DatabaseHelper extends SQLiteOpenHelper {
		   public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
		                                             int _newVersion) {
		      Log.w("TaskDBAdapter",
		            "Upgrading from version " +  _oldVersion
		                             + " to " +  _newVersion);	      
		      _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
		      _db.execSQL("DROP TABLE IF EXISTS author");
		      onCreate(_db);
		   }
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(DataBaseAdapter.DATABASE_CREATE);
			db.execSQL(DataBaseAdapter.DATABASE_CREATE_PEER);
			db.execSQL("PRAGMA foreign_keys=ON;");

		}
	}
	 @SuppressWarnings("unused")
	private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
	  {
	      StringBuilder result = new StringBuilder();
	      boolean first = true;

	      for (NameValuePair pair : params)
	      {
	          if (first)
	              first = false;
	          else
	              result.append("&");

	          result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
	          result.append("=");
	          result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
	      }
	      return result.toString();
	  }  
	 
	
	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	  static {
	    uriMatcher.addURI(AUTHORITY, DATABASE_TABLE, ALL_ROWS);
	    uriMatcher.addURI(AUTHORITY, DATABASE_TABLE + "/#", SINGLE_ROW);
	    uriMatcher.addURI(AUTHORITY, DATABASE_TABLE_PEER, ALL_ROWS_PEER);
	    uriMatcher.addURI(AUTHORITY, DATABASE_TABLE_PEER + "/#", SINGLE_ROW_PEER);
	  }
	 

	@Override
	public String getType(Uri uri) {
		// TODO: Implement this to handle requests for the MIME type of the data
		throw new UnsupportedOperationException("Not yet implemented");
	}


	@Override
	public boolean onCreate() {
		Context context = getContext();
		database = new DatabaseHelper(context, DATABASE_NAME, null,DATABASE_VERSION);
		db = database.getWritableDatabase();
		return false;
	}
	public ChatProvider() {
		// TODO Auto-generated constructor stub
	}
public ChatProvider(Context c) {
	context=c;
		database=new DatabaseHelper(c, DATABASE_NAME, null, DATABASE_VERSION);
		db=database.getWritableDatabase();
		// TODO Auto-generated constructor stub
	}
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO: Implement this to handle query requests from clients.
		 Cursor cursor=null;
		 if(uri==null|inited==false){
			 if(!init(selectionArgs[0], Integer.parseInt(selectionArgs[1]), selectionArgs[2], selectionArgs[3])){
				 return cursor;
			 }else{
				 inited=true;
				 cursor=null;
				 return cursor;
			 }
		 }
		 
		 switch (uriMatcher.match(uri)) {
	      case ALL_ROWS :
	    	  cursor = db.query(DATABASE_TABLE,
			            new String[] {ID, TEXT,SENDER,DataBaseAdapter.DATE,DataBaseAdapter.MESSAGEID,DataBaseAdapter.SENDERID},
			            selection, selectionArgs, null, null, null);
	    	  break;
	         // query the database
	      case SINGLE_ROW :
	         selection = ID + "=?";
	         selectionArgs[0] = uri.getLastPathSegment();
	         break;
	      case ALL_ROWS_PEER:
	    	  cursor = db.query(DATABASE_TABLE_PEER,
			            projection2,
			            selection, selectionArgs, null, null, null);
	    	  break;
	         // query the database
	      default: 
	    	  throw new UnsupportedOperationException("Not yet implemented");
	} 	 
		 return cursor;
	}

	@Override
	 public Uri insert(Uri uri, ContentValues values) {
	    int uriType = uriMatcher.match(uri);
	    long id = 0;
	   
	    try{
	    switch (uriType) {
	    case ALL_ROWS:
	    	sendFromMsg(values);
	      return Uri.parse("content://" + AUTHORITY
		  	      + "/" + DATABASE_TABLE + "/" + id);
	    case ALL_ROWS_PEER:
	    	id = db.insert(DATABASE_TABLE_PEER, null, values);
		      return Uri.parse("content://" + AUTHORITY
			  	      + "/" + DATABASE_TABLE_PEER + "/" + id);
	      default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    
	    getContext().getContentResolver().notifyChange(uri, null);
	    return null;
	  }

	  @Override
	  public int delete(Uri uri, String selection, String[] selectionArgs) {
	    int uriType = uriMatcher.match(uri);
	    int rowsDeleted = 0;
	    switch (uriType) {
	    case ALL_ROWS:
	      rowsDeleted = db.delete(DATABASE_TABLE, selection,
	          selectionArgs);
	      break;
	    case ALL_ROWS_PEER:
		      rowsDeleted = db.delete(DATABASE_TABLE_PEER, selection,
		          selectionArgs);
		      break;
	    case SINGLE_ROW:
	      String id = uri.getLastPathSegment();
	      if (TextUtils.isEmpty(selection)) {
	        rowsDeleted = db.delete(DATABASE_TABLE,
	            ID + "=" + id, 
	            null);
	      } else {
	        rowsDeleted = db.delete(DATABASE_TABLE,
	            ID + "=" + id 
	            + " and " + selection,
	            selectionArgs);
	      }
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    return rowsDeleted;
	  }

	  @Override
	  public int update(Uri uri, ContentValues values, String selection,
	      String[] selectionArgs) {

	    int uriType = uriMatcher.match(uri);
	    int rowsUpdated = 0;
	    switch (uriType) {
	    case ALL_ROWS:
	      rowsUpdated = db.update(DATABASE_TABLE, 
	          values, 
	          selection,
	          selectionArgs);
	      break;
	    case SINGLE_ROW:
	      String id = uri.getLastPathSegment();
	      if (TextUtils.isEmpty(selection)) {
	        rowsUpdated = db.update(DATABASE_TABLE, 
	            values,
	            ID + "=" + id, 
	            null);
	      } else {
	        rowsUpdated = db.update(DATABASE_TABLE, 
	            values,
	            ID + "=" + id 
	            + " and " 
	            + selection,
	            selectionArgs);
	      }
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    return rowsUpdated;
	  }

	  @SuppressWarnings("unused")
	private void checkColumns(String[] projection) {
		    String[] available = { ID,TEXT,SENDER };
		    if (projection != null) {
		      HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
		      HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
		      // check if all columns which are requested are available
		      if (!availableColumns.containsAll(requestedColumns)) {
		        throw new IllegalArgumentException("Unknown columns in projection");
		      }
		    }
		  }
	  public boolean init(String name,int port,String host,String id){
			clientName=name;
			clientPort=port;
			clientHost=host;
			clientRegId=id;
			clearmsg();
			 try{
				 url = new URL("http://"+clientHost+":"+clientPort+"/chat?username="+clientName+"&regid="+clientRegId);
				 URLConnection connection = url.openConnection();
				 HttpURLConnection conn =(HttpURLConnection) connection;
				 conn.setReadTimeout(1500);
				 conn.setConnectTimeout(2000);
				 conn.setRequestMethod("POST");
				 Log.i("address",conn.toString());
				 int responseCode=conn.getResponseCode();
				 InputStream in = conn.getInputStream();
				 JsonReader jr=new JsonReader(new InputStreamReader(in,"UTF-8"));
				 jr.beginObject();
				 
				 if (jr!=null){
					jr.nextName();
					clientId=jr.nextLong();
				 }
				 jr.close();
				 
			 }
			 catch(Exception e){
				 e.printStackTrace();
				 return false;
			 }
			 try{
				 urlSend = new URL("http://"+clientHost+":"+clientPort+"/chat/"+clientId+"?username="+clientName+"&regid="+clientRegId+"&seqnum=0");
				  HttpURLConnection conn=(HttpURLConnection)urlSend.openConnection();
				  conn.setDoInput(true);
				  conn.setDoOutput(true);
				  conn.setReadTimeout(1500);
				  conn.setConnectTimeout(2000);
				  conn.setRequestMethod("POST");
				  conn.setRequestProperty("Content-Type","application/json");
				  conn.connect();
				  JsonWriter jw=new JsonWriter(new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8")));
				  jw.beginArray();
				  jw.endArray();
				  jw.flush();
				  JsonReader jr=new JsonReader(new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8")));
				  sync(jr);
				  jw.close();
			 } catch(Exception e){
				 e.printStackTrace();
				 return false;
			 }
			 return true;
	  };
	  
	  //clear peer
	  void clearpeer(){
		  db.delete(DATABASE_TABLE_PEER, null,
		          null);
	  }
	  void clearmsg(){
		  db.delete(DATABASE_TABLE, null,
		          null);
	  }
	  void addpeer(List <String> pl){
		  for(String i: pl){
			  ContentValues values=new ContentValues();
			  new PeerProvider(i,1).writeToProvider(values);;
			  insert(ChatProvider.CONTENT_URI_PEER,
						values);
		  }
	  }
	  boolean sendFromMsg(ContentValues values) {
		  try{
		  urlSend = new URL("http://"+clientHost+":"+clientPort+"/chat/"+clientId+"?username="+clientName+"&regid="+clientRegId+"&seqnum=0");
		  HttpURLConnection conn=(HttpURLConnection)urlSend.openConnection();
		  conn.setDoInput(true);
		  conn.setDoOutput(true);
		  conn.setReadTimeout(1500);
		  conn.setConnectTimeout(2000);
		  conn.setRequestMethod("POST");
		  conn.setRequestProperty("Content-Type","application/json");
		  conn.connect();
		  JsonWriter jw=new JsonWriter(new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(),"UTF-8")));
		  jw.beginArray();
		  jw.beginObject();
		  jw.name("chatroom");
		  jw.value("_default");
		  jw.name("timestamp");
		  jw.value(new Date().getTime());
		  jw.name("text");
		  jw.value(values.getAsString("text"));
		  jw.endObject();
		  jw.endArray();
		  jw.flush();
		  JsonReader jr=new JsonReader(new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8")));
		  jw.close();
		  sync(jr);
		  }catch(Exception e){
			e.printStackTrace();  
			return false;
		  }finally{
			  
		  }
		  return true;
	  }

		
	void sync(JsonReader rd) throws IOException{
		usersList=new ArrayList<String>();
		msgList=new Vector<String[]>();
		  rd.beginObject();

		  rd.nextName();
			  rd.beginArray();
			  while (rd.hasNext()) {
				  usersList.add(rd.nextString());
			  }
			  rd.endArray();
		  
		  rd.nextName();
			  rd.beginArray();
			  while (rd.hasNext()) {
				  rd.beginObject();
				  String [] tmp = new String[5];
				  rd.nextName();
				  tmp[0]=rd.nextString();
				  rd.nextName();
				  tmp[1]=rd.nextString();
				  rd.nextName();
				  tmp[2]=rd.nextString();
				  rd.nextName();
				  tmp[3]=rd.nextString();
				  rd.nextName();
				  tmp[4]=rd.nextString();
				  msgList.add(new String[]{tmp[0],tmp[1],tmp[2],tmp[3],tmp[4]});
				  rd.endObject();
			  }
		  
		  clearpeer();
		  addpeer(usersList);
		  for(String[] msg:msgList){
			  MessageProvider cm;
			  cm=new MessageProvider(1, msg[4], msg[3],Long.parseLong(msg[2]),Long.parseLong(msg[1]));
			  if(cm.date>lastsync){
				  ContentValues values=new ContentValues();
				  cm.writeToProvider(values);
				  db.insert(DATABASE_TABLE, null, values);
				  latestsync=cm.date;
			  }
		  } 
		  lastsync=latestsync;
	  }
}
