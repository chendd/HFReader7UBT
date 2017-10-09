package com.example.rr7036ubt;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class ReadWActivity extends Activity {
	EditText content;
	Button rButton;
	Button wButton;
	RadioButton rb_typeA;
	RadioButton rb_typeB;
	EditText blocknum;
	EditText tUID;
	public byte[] uid=new byte[8];
	public byte[]rdata=new byte[50];
	public byte[]wdata=new byte[50];
	public byte state=0;
	public String str_update="";
	private static final int MSG_UPDATE_DATA = 0;
    private static final int MSG_UPDATE_WRITE = 1;
    private Handler myHandler = new Handler() {  
        //2.重写消息处理函数
        public void handleMessage(Message msg) {   
             switch (msg.what) {   
                  //判断发送的消息
                  case MSG_UPDATE_DATA:   
                  {
                	  content.setText(str_update);
                     break;   
                  }  
                  case MSG_UPDATE_WRITE:   
                  {
                	  if(str_update=="00")
                	  {
                		  Toast.makeText(ReadWActivity.this, "Success！", Toast.LENGTH_SHORT).show();
                	  }else
                	  {
                		  Toast.makeText(ReadWActivity.this, "Failed！", Toast.LENGTH_SHORT).show();
                	  }
                      break;   
                  }  
             }
             super.handleMessage(msg);   
        }  
        
   };  
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_readw);
		rButton =(Button)findViewById(R.id.button_rr9read);
		rButton.setOnClickListener(myListener);
		wButton =(Button)findViewById(R.id.button_rr9write);
		wButton.setOnClickListener(myListener);
		rb_typeA = (RadioButton)findViewById(R.id.rb_typeA);
		rb_typeB = (RadioButton)findViewById(R.id.rb_typeB);
		blocknum = (EditText)findViewById(R.id.et_rr9num);
		blocknum.setText("0");
		content =(EditText)findViewById(R.id.et_rr9content);
		tUID = (EditText)findViewById(R.id.et_rr9id);
		tUID.setText(BTClient.gettag_id());
	}
	private OnClickListener myListener= new OnClickListener(){

		@Override
		public void onClick(View v) {
			String struid=tUID.getText().toString();
			if(struid.length()!=16)return;
			
			int result=-1;
			uid=BTClient.hexStringToBytes(struid);
			int temp=Integer.valueOf(blocknum.getText().toString());
			final byte blockNumber = (byte)(temp);
			if(v==rButton)
			{
				content.setText("");
				
				Thread thread=new Thread(new Runnable()  
	            {  
	                @Override  
	                public void run()  
	                { 
	                	int result=BTClient.ReadSingleBlock(uid, blockNumber, rdata);
	    				if(result==0)
	    				{
	    					str_update=BTClient.bytesToHexString(rdata, 1, 4);
	    					myHandler.removeMessages(MSG_UPDATE_DATA);
	                		myHandler.sendEmptyMessage(MSG_UPDATE_DATA);
	    				}else
	    				{
	    					str_update="";
	    					myHandler.removeMessages(MSG_UPDATE_DATA);
	                		myHandler.sendEmptyMessage(MSG_UPDATE_DATA);
	                	}
	                }  
	            });  
				thread.start();
			}else if(v==wButton)
			{
				String temps=content.getText().toString();
				if(temps.length()!=8)return;
				wdata=BTClient.hexStringToBytes(temps);
				if(rb_typeA.isChecked())state=0;
				if(rb_typeB.isChecked())state=8;
				Thread thread=new Thread(new Runnable()  
	            {  
	                @Override  
	                public void run()  
	                { 
	                	int result=BTClient.WriteSingleBlock(state, uid, blockNumber, wdata);
	    				if(result==0)
	    				{
	    					str_update="00";
	    					myHandler.removeMessages(MSG_UPDATE_WRITE);
	                		myHandler.sendEmptyMessage(MSG_UPDATE_WRITE);
	    				}else
	    				{
	    					str_update="";
	    					myHandler.removeMessages(MSG_UPDATE_WRITE);
	                		myHandler.sendEmptyMessage(MSG_UPDATE_WRITE);
	                	}
	                }  
	            });  
				thread.start();
			}
		}
	};


}
