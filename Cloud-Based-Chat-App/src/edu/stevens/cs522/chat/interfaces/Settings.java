package edu.stevens.cs522.chat.interfaces;

import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import edu.stevens.cs522.chat.client.ChatProvider;
import edu.stevens.cs522.chat.service.CloudChatApp;
import edu.stevens.cs522.chat.service.InitService;
import edu.stevens.cs522.chat.service.R;

public class Settings extends Activity {
	public static String NAME="name";
	public static String PORT="port";
	public static String HOST="host";
	AckReceiver receiver;
	String un,pt,hi,uuid;
	int ipt;
	Context context;
	static{
	     StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();  
	     StrictMode.setThreadPolicy(policy);  
	}
	public class AckReceiver extends ResultReceiver {
		public AckReceiver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}
		protected void onReceiveResult(int resultCode, Bundle result) {
			switch (resultCode) {
			case 1:
				if(ChatProvider.inited==false)break;
				Intent i=new Intent(context,CloudChatApp.class);
				 i.putExtra(NAME,un);
				 Log.i("name",un);
				 i.putExtra(PORT,ipt);
				 i.putExtra(HOST,hi);
				 startActivity(i);
				break;
			case 0:
				Toast.makeText(context, "Login Fail, Can't find server.", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}		
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		receiver=new AckReceiver(new Handler());
		context=this;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	public void sendLogin(View view) {
	    
	    EditText username= (EditText)findViewById(R.id.editText1);
	    EditText port= (EditText)findViewById(R.id.editText2);
	    EditText host= (EditText)findViewById(R.id.hostip);
	    un=username.getText().toString();
	    pt=port.getText().toString();
	    hi=host.getText().toString();
	    uuid=UUID.randomUUID().toString();
	    ipt=0;
	    try{
	    ipt=Integer.parseInt(pt);
	    }catch(Exception e){}
	    if(ipt>65535|ipt<1){
	    	Context context = getApplicationContext();
			 int duration = Toast.LENGTH_SHORT;
			 Toast toast = Toast.makeText(context,"Port number should be 1-65535.", duration);
			 toast.show();
			 return;
	    }
	    if(!un.isEmpty()){
	    	Intent initIntent=new Intent(this,InitService.class);
	    	initIntent.putExtra("receiver",receiver);
	    	initIntent.putExtra("name", un);
	    	initIntent.putExtra("host", hi);
	    	initIntent.putExtra("port", pt);
	    	initIntent.putExtra("uuid", uuid);
	    	startService(initIntent);
	    }
	    Toast.makeText(context, "Loging in", Toast.LENGTH_SHORT).show();
	    return;
	}
}
