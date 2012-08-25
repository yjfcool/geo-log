package com.geoscope.GeoEye;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.geoscope.GeoEye.Space.Defines.SpaceDefines;
import com.geoscope.GeoEye.Space.Defines.TDataConverter;
import com.geoscope.GeoEye.Space.Defines.TPolishMapFormatDefines;
import com.geoscope.GeoEye.Space.Defines.TXYCoord;

public class TMapObjectsPanel extends Activity {

	private static final int MESSAGE_SEARCHING_COMPLETED = 1;
	
	public class TMOItem {
        public int 		ID;
        public int 		FormatID;
        public int 		KindID;
        public int 		TypeID;
		public String 	Name;
        public int 		MapID;
		public String 	MapName;
		public int 		Ptr;
		public double 	X;
		public double 	Y;
	}
	
	private TReflector Reflector;
	//.
	private EditText edNameContext;
	private Button btnSearchByNameContext;
	private Button btnCloseMapObjectsPanel;
	private TSearchingByNameContext SearchingByNameContext = null;
	private ListView lvObjects;
	public TMOItem[] MOItems = null; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //.
		Reflector = TReflector.MyReflector;  
        //.
        setContentView(R.layout.mapobjects_panel);
        //.
        edNameContext = (EditText)findViewById(R.id.edNameContext); 
        btnSearchByNameContext = (Button)findViewById(R.id.btnSearchMapObjectsByName); 
        btnSearchByNameContext.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	StartSearching();
            }
        });
        btnCloseMapObjectsPanel = (Button)findViewById(R.id.btnCloseMapObjectsPanel);
        btnCloseMapObjectsPanel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });
        lvObjects = (ListView)findViewById(R.id.lvObjects);
        lvObjects.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lvObjects.setOnItemClickListener(new OnItemClickListener() {         
			@Override
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        		try {
        			TXYCoord C = new TXYCoord();
        			C.X = MOItems[arg2].X;
        			C.Y = MOItems[arg2].Y;
        			//.
        			Reflector.MoveReflectionWindow(C);
	    		}
	    		catch (Exception E) {
	    			Toast.makeText(TMapObjectsPanel.this, "������ ��������� ������� �������, "+E.getMessage(), Toast.LENGTH_SHORT).show();
			    }
            	//.
            	finish();
        	}              
        });         
	}

    @Override
	protected void onDestroy() {
		super.onDestroy();
	}

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

    public void StartSearching() {
    	String NameContext = edNameContext.getText().toString();
    	if (NameContext.length() < 3) {
    		Toast.makeText(TMapObjectsPanel.this, "������� �������� ������.", Toast.LENGTH_SHORT).show();
    		return; //. ->
    	}
    	if (SearchingByNameContext != null)
    		SearchingByNameContext.Cancel();
    	SearchingByNameContext = new TSearchingByNameContext(NameContext,MESSAGE_SEARCHING_COMPLETED);
    }
    
	public void UpdateListByData(byte[] ListData) throws Exception {
		int Idx = 0;
		//.
		int ItemsCount = TDataConverter.ConvertBEByteArrayToInt32(ListData,Idx); Idx += 4;
		if (ItemsCount == 0) {
			MOItems = null;
			//.
			lvObjects.setAdapter(null);
			//.
    		Toast.makeText(TMapObjectsPanel.this, "������� �� �������.", Toast.LENGTH_SHORT).show();
    		return; //. ->
		}
		MOItems = new TMOItem[ItemsCount];
		final String[] lvObjectsItems = new String[ItemsCount];
		for (int I = 0; I < ItemsCount; I++) {
			MOItems[I] = new TMOItem();
			MOItems[I].ID = TDataConverter.ConvertBEByteArrayToInt32(ListData,Idx); Idx += 8; /*SizeOf(Int64)*/
			MOItems[I].FormatID = TDataConverter.ConvertBEByteArrayToInt32(ListData,Idx); Idx += 4; 
			MOItems[I].KindID = TDataConverter.ConvertBEByteArrayToInt32(ListData,Idx); Idx += 4; 
			MOItems[I].TypeID = TDataConverter.ConvertBEByteArrayToInt32(ListData,Idx); Idx += 4;
			//.
			int SS = TDataConverter.ConvertBEByteArrayToInt32(ListData,Idx); Idx += 4;
			MOItems[I].Name = "";
			if (SS > 0) {
				MOItems[I].Name = new String(ListData, Idx,SS, "windows-1251"); Idx += SS;
			}
			//.
			MOItems[I].MapID = TDataConverter.ConvertBEByteArrayToInt32(ListData,Idx); Idx += 8; /*SizeOf(Int64)*/
			//.
			SS = TDataConverter.ConvertBEByteArrayToInt32(ListData,Idx); Idx += 4;
			MOItems[I].MapName = "";
			if (SS > 0) {
				MOItems[I].MapName = new String(ListData, Idx,SS, "windows-1251"); Idx += SS;
			}
			//.
			MOItems[I].Ptr = TDataConverter.ConvertBEByteArrayToInt32(ListData,Idx); Idx += 8; /*SizeOf(Int64)*/
			MOItems[I].X = TDataConverter.ConvertBEByteArrayToDouble(ListData,Idx); Idx += 8;
			MOItems[I].Y = TDataConverter.ConvertBEByteArrayToDouble(ListData,Idx); Idx += 8;
			//.
			String S = MOItems[I].Name;
			String ObjectType = "";
			switch (MOItems[I].FormatID) {
			case TPolishMapFormatDefines.FormatID:
				ObjectType = TPolishMapFormatDefines.GetObjectTypeName(MOItems[I].KindID,MOItems[I].TypeID);
				break; //. >
			}
			if (!ObjectType.equals(""))
				S = S+" ("+ObjectType+")";
			if (!MOItems[I].MapName.equals(""))
				S = S+"\n"+MOItems[I].MapName;
			lvObjectsItems[I] = S; 
		}
		ArrayAdapter<String> lvObjectsAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,lvObjectsItems);             
		lvObjects.setAdapter(lvObjectsAdapter);
	}
	
    private class TSearchingByNameContext implements Runnable {

    	private static final int MESSAGE_SHOWEXCEPTION = 0;
    	private static final int MESSAGE_PROGRESSBAR_SHOW = 1;
    	private static final int MESSAGE_PROGRESSBAR_HIDE = 2;
    	private static final int MESSAGE_PROGRESSBAR_PROGRESS = 3;
    	
    	private String NameContext;
    	private int OnCompletionMessage;
    	//.
    	private Thread _Thread;
    	private boolean flCancel = false;
    	//.
        int SummarySize = 0;
        private ProgressDialog progressDialog; 
    	
    	public TSearchingByNameContext(String pNameContext, int pOnCompletionMessage) {
    		NameContext = pNameContext;
    		OnCompletionMessage = pOnCompletionMessage;
    		//.
    		_Thread = new Thread(this);
    		_Thread.start();
    	}

		@Override
		public void run() {
			try {
				if (flCancel)
					return; //. ->
				//.
				NameContext = NameContext+"%";
				//.
				String URL1 = Reflector.ServerAddress;
				//. add command path
				URL1 = "http://"+URL1+"/"+"Space"+"/"+"2"/*URLProtocolVersion*/+"/"+Integer.toString(Reflector.User.UserID);
				String URL2 = "TypesSystem"+"/"+Integer.toString(SpaceDefines.idTGeoSpace)+"/"+"Co"+"/"+Integer.toString(Reflector.Configuration.GeoSpaceID)+"/"+"MapFormatMapObjects.dat";
				//. add command parameters
				URL2 = URL2+"?"+"1"/*command version*/+",'"+NameContext+"'";
				//.
				byte[] URL2_Buffer;
				try {
					URL2_Buffer = URL2.getBytes("windows-1251");
				} 
				catch (Exception E) {
					URL2_Buffer = null;
				}
				byte[] URL2_EncryptedBuffer = Reflector.User.EncryptBufferV2(URL2_Buffer);
				//. encode string
		        StringBuffer sb = new StringBuffer();
		        for (int I=0; I < URL2_EncryptedBuffer.length; I++) {
		            String h = Integer.toHexString(0xFF & URL2_EncryptedBuffer[I]);
		            while (h.length() < 2) 
		            	h = "0" + h;
		            sb.append(h);
		        }
				URL2 = sb.toString();
				//.
				String URL = URL1+"/"+URL2+".dat";
				//.
				if (flCancel)
					return; //. ->
				//.
    			MessageHandler.obtainMessage(MESSAGE_PROGRESSBAR_SHOW).sendToTarget();
    			try {
    				HttpURLConnection HttpConnection = Reflector.OpenHttpConnection(URL);
    				try {
    					if (flCancel)
    						return; //. ->
    					//.
    					InputStream in = HttpConnection.getInputStream();
    					try {
			    			if (flCancel)
			    				return; //. ->
			                //.
    						int RetSize = HttpConnection.getContentLength();
    						if (RetSize == 0) 
    							throw new Exception("wrong data"); //. =>
    						byte[] Data = new byte[RetSize];
    			            int Size;
    			            SummarySize = 0;
    			            int ReadSize;
    			            while (SummarySize < Data.length)
    			            {
    			                ReadSize = Data.length-SummarySize;
    			                Size = in.read(Data,SummarySize,ReadSize);
    			                if (Size <= 0) throw new Exception("���������� � �������� ������� ����������"); //. =>
    			                SummarySize += Size;
    			                //.
    			    			if (flCancel)
    			    				return; //. ->
    			    			//.
    			    			MessageHandler.obtainMessage(MESSAGE_PROGRESSBAR_PROGRESS,(Integer)(100*SummarySize/Data.length)).sendToTarget();
    			            }
    			    		//.
    			    		PanelHandler.obtainMessage(OnCompletionMessage,Data).sendToTarget();
    					}
    					finally {
    						in.close();
    					}                
    				}
    				finally {
    					HttpConnection.disconnect();
    				}
				}
				finally {
	    			MessageHandler.obtainMessage(MESSAGE_PROGRESSBAR_HIDE).sendToTarget();
				}
        	}
        	catch (InterruptedException E) {
        	}
        	catch (IOException E) {
    			MessageHandler.obtainMessage(MESSAGE_SHOWEXCEPTION,E).sendToTarget();
        	}
        	catch (Throwable E) {
    			MessageHandler.obtainMessage(MESSAGE_SHOWEXCEPTION,new Exception(E.getMessage())).sendToTarget();
        	}
		}

	    private final Handler MessageHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	            
	            case MESSAGE_SHOWEXCEPTION:
	            	Exception E = (Exception)msg.obj;
	                Toast.makeText(TMapObjectsPanel.this, "������ �������� ������, "+E.getMessage(), Toast.LENGTH_SHORT).show();
	            	//.
	            	break; //. >
	            	
	            case MESSAGE_PROGRESSBAR_SHOW:
	            	progressDialog = new ProgressDialog(TMapObjectsPanel.this);    
	            	progressDialog.setMessage("��������...");    
	            	progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);    
	            	progressDialog.setIndeterminate(false); 
	            	progressDialog.setCancelable(true);
	            	progressDialog.setOnCancelListener( new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface arg0) {
							Cancel();
						}
					});
	            	//.
	            	progressDialog.show(); 	            	
	            	//.
	            	break; //. >

	            case MESSAGE_PROGRESSBAR_HIDE:
	            	progressDialog.dismiss(); 
	            	//.
	            	break; //. >
	            
	            case MESSAGE_PROGRESSBAR_PROGRESS:
	            	progressDialog.setProgress((Integer)msg.obj);
	            	//.
	            	break; //. >
	            }
	        }
	    };
	    
    	public void Cancel() {
    		flCancel = true;
    		//.
    		_Thread.interrupt();
    	}
    }
		
	public Handler PanelHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_SEARCHING_COMPLETED: 
            	try {
                	byte[] ListData = (byte[])msg.obj;
                	UpdateListByData(ListData);
            	}
            	catch (Exception E) {
            		Toast.makeText(TMapObjectsPanel.this, "������ ���������� ������, "+E.getMessage(), Toast.LENGTH_SHORT).show();
            	}
            	break; //. >
            }
        }
    };
}