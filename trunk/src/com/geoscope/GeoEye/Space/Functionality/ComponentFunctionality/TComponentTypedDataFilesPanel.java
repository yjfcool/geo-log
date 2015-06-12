package com.geoscope.GeoEye.Space.Functionality.ComponentFunctionality;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.geoscope.Classes.Data.Types.Image.TDiskImageCache;
import com.geoscope.Classes.Data.Types.Image.TImageViewerPanel;
import com.geoscope.Classes.Data.Types.Image.Drawing.TDrawings;
import com.geoscope.Classes.Exception.CancelException;
import com.geoscope.Classes.MultiThreading.TAsyncProcessing;
import com.geoscope.Classes.MultiThreading.TCancelableThread;
import com.geoscope.Classes.MultiThreading.TProgressor;
import com.geoscope.GeoEye.R;
import com.geoscope.GeoEye.TReflector;
import com.geoscope.GeoEye.TReflectorComponent;
import com.geoscope.GeoEye.Space.Defines.SpaceDefines;
import com.geoscope.GeoEye.Space.Defines.TLocation;
import com.geoscope.GeoEye.Space.Functionality.TTypeFunctionality;
import com.geoscope.GeoEye.Space.Server.TGeoScopeServer;
import com.geoscope.GeoEye.Space.Server.TGeoScopeServerInfo;
import com.geoscope.GeoEye.Space.Server.User.TGeoScopeServerUser.TUserDescriptor.TActivity.TComponent;
import com.geoscope.GeoEye.Space.TypesSystem.TComponentStreamServer;
import com.geoscope.GeoEye.Space.TypesSystem.TTypesSystem;
import com.geoscope.GeoEye.Space.TypesSystem.CoComponent.ObjectModel.GeoMonitoredObject1.DEVICE.SensorsModule.MeasurementProcessor.TMeasurementProcessorPanel;
import com.geoscope.GeoEye.Space.TypesSystem.DATAFile.TDATAFileFunctionality;
import com.geoscope.GeoEye.Space.TypesSystem.DATAFile.Types.Image.Drawing.TDrawingDefines;
import com.geoscope.GeoEye.Space.TypesSystem.DATAFile.Types.Image.Drawing.TDrawingEditor;
import com.geoscope.GeoEye.Space.TypesSystem.Positioner.TPositionerFunctionality;
import com.geoscope.GeoEye.Space.URL.TURL;
import com.geoscope.GeoEye.UserAgentService.TUserAgent;
import com.geoscope.GeoLog.Application.TGeoLogApplication;
import com.geoscope.GeoLog.Application.THintManager;

@SuppressLint("HandlerLeak")
public class TComponentTypedDataFilesPanel extends Activity {

	public static final int		ItemImageSize = 512;
	public static final String 	ItemImageDataParams = "2;"+Integer.toString(ItemImageSize)+";"+"50"/*50% quality*/;
	//.
	public static final int		ImageDrawings_MaxDataSize = 1024*100; //. Kb
	public static final String 	ImageDrawings_ItemImageDataParams = "0;"+Integer.toString(ImageDrawings_MaxDataSize);
	
	private static final int 	MESSAGE_TYPEDDATAFILE_LOADED = 1;
	
	public static final int REQUEST_COMPONENT_CONTENT 	= 1;
	public static final int REQUEST_ADD_COMPONENT 		= 2;
	
	private static class TComponentListItem {
		
		public TGeoScopeServer Server;
		//.
		public int		DataType;
		public String 	DataFormat;
		public String 	Name;
		public String 	Info;
		//.
		public TComponent Component;
		//.
		public boolean BMP_flLoaded = false;
		public boolean BMP_flNull = false;
		
		public TComponentListItem(TGeoScopeServer pServer, int pDataType, String pDataFormat, String pName, String pInfo, TComponent pComponent) {
			Server = pServer;
			//.
			DataType = pDataType;
			DataFormat = pDataFormat;
			Name = pName;
			Info = pInfo;
			//.
			Component = pComponent;
		}
	}
	
	public static class TComponentListAdapter extends BaseAdapter {

		private static final String 		ImageCache_Name = "ComponentTypedDataFileImages";
		private static final int			ImageCache_Size = 1024*1024*10; //. Mb
		private static final CompressFormat ImageCache_CompressFormat = CompressFormat.PNG;
		private static final int			ImageCache_CompressQuality = 100;
		
		private static class TViewHolder {
			
			public TComponentListItem Item;
			//.
			public ImageView 	ivImage;
			public TextView 	lbName;
			public TextView 	lbInfo;
		}
		
		private static abstract class TProgressHandler {
			
			public abstract void DoOnStart();
			public abstract void DoOnFinish();
			public abstract void DoOnProgress(int Percentage);
		}
		
		private class TImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
			
			private TComponentListItem Item;
			
			private TViewHolder ViewHolder;

			public TImageLoadTask(TComponentListItem pItem, TViewHolder pViewHolder) {
				Item = pItem;
				ViewHolder = pViewHolder;
			}

			@Override
			protected void onPreExecute() {
				if (!Panel.flExists)
					return; //. ->
				//.
				ImageLoadingCount++;
				//.
				if (ImageLoadingCount > 0) 
					ProgressHandler.DoOnStart();
			}
			
			@Override
			protected Bitmap doInBackground(Void... params) {
				try {
					if (!Panel.flExists)
						return null; //. ->
					if (ViewHolder.Item != Item)
						return null; //. ->
					//.
					return LoadImage(); //. ->
				}
				catch (Exception E) {
					return null; //. ->
				}
			}

			@Override
			protected void onPostExecute(Bitmap bitmap) {
				if (!Panel.flExists)
					return; //. ->
				//.
				if ((bitmap != null) && (!isCancelled()) && (ViewHolder.Item == Item)) {
					ViewHolder.ivImage.setImageBitmap(bitmap);
					ViewHolder.ivImage.setOnClickListener(ImageClickListener);
				}
				//.
				ImageLoadingCount--;
				if (ImageLoadingCount == 0) 
					ProgressHandler.DoOnFinish();
			}

			private Bitmap LoadImage() throws Exception {
				if (Item.BMP_flLoaded) {
					if (!Item.BMP_flNull) 
						return ImageCache.getBitmap(Item.Component.GetKey()); //. ->
					else 
						return null; //. ->
				}
				//.
				Bitmap Result = null;
				//. 
				boolean flProcessAsDefault = true;
				switch (Item.DataType) {

				case SpaceDefines.TYPEDDATAFILE_TYPE_DocumentName:
					if ((Item.DataFormat != null) && Item.DataFormat.equals(SpaceDefines.TYPEDDATAFILE_TYPE_Document_FORMAT_XML)) {
						TComponentTypedDataFiles ComponentTypedDataFiles = new TComponentTypedDataFiles(context, SpaceDefines.TYPEDDATAFILE_MODEL_HUMANREADABLECOLLECTION, SpaceDefines.TYPEDDATAFILE_TYPE_Document);
						ComponentTypedDataFiles.PrepareForComponent(Item.Component.idTComponent,Item.Component.idComponent, (Item.Component.idTComponent == SpaceDefines.idTCoComponent), Item.Server);
						//.
						TComponentTypedDataFile ComponentTypedDataFile = ComponentTypedDataFiles.GetRootItem(); 
						if ((ComponentTypedDataFile != null) && ComponentTypedDataFile.DataFormat.equals(SpaceDefines.TYPEDDATAFILE_TYPE_Document_FORMAT_XML)) {
							TComponentFunctionality CF = Item.Server.User.Space.TypesSystem.TComponentFunctionality_Create(ComponentTypedDataFile.DataComponentType,ComponentTypedDataFile.DataComponentID);
							if (CF != null) 
								try {
									CF.ParseFromXMLDocument(ComponentTypedDataFile.GetFileData());
									Result = CF.GetThumbnailImage();
									flProcessAsDefault = (Result == null);
								}
								finally {
									CF.Release();
								}
						}
					}
					break; //. >
					
				case SpaceDefines.TYPEDDATAFILE_TYPE_ImageName:
					if ((Item.DataFormat != null) && Item.DataFormat.equals(TDrawingDefines.DataFormat)) {
						Item.Component.TypedDataFiles.PrepareForComponent(Item.Component.idTComponent,Item.Component.idComponent, ImageDrawings_ItemImageDataParams, (Item.Component.idTComponent == SpaceDefines.idTCoComponent), Item.Server);
						TComponentTypedDataFile DataFile = Item.Component.TypedDataFiles.GetRootItem(); 
						if ((DataFile != null) && (DataFile.Data != null)) {
							TDrawings Drawings = new TDrawings();
							Drawings.LoadFromByteArray(DataFile.Data,0);
							Result = Drawings.ToBitmap(ItemImageSize);
						}
						flProcessAsDefault = false;
					}
					break; //. >

				case SpaceDefines.TYPEDDATAFILE_TYPE_AudioName:
				case SpaceDefines.TYPEDDATAFILE_TYPE_MeasurementName:
					flProcessAsDefault = false;
					break; //. >
				}
				//.
				if (flProcessAsDefault) {
					Item.Component.TypedDataFiles.PrepareForComponent(Item.Component.idTComponent,Item.Component.idComponent, ItemImageDataParams, (Item.Component.idTComponent == SpaceDefines.idTCoComponent), Item.Server);
					TComponentTypedDataFile DataFile = Item.Component.TypedDataFiles.GetRootItem(); 
					if ((DataFile != null) && (DataFile.Data != null)) 
						Result = BitmapFactory.decodeByteArray(DataFile.Data, 0,DataFile.Data.length); 
				}
				//.
				if (Result != null) 
					ImageCache.put(Item.Component.GetKey(), Result);
				else
					Item.BMP_flNull = true;
				Item.BMP_flLoaded = true;
				//.
				return Result;
			}
		}
		
		private class TImageRestoreTask extends TAsyncProcessing {
			
			private TComponentListItem Item;
			
			private TViewHolder ViewHolder;
			
			private Bitmap bitmap = null;

			public TImageRestoreTask(TComponentListItem pItem, TViewHolder pViewHolder) {
				super(null);
				//.
				Item = pItem;
				ViewHolder = pViewHolder;
			}

			@Override
			public void Process() throws Exception {
				if (!Panel.flExists)
					return; //. ->
				if (ViewHolder.Item != Item)
					return; //. ->
				//.
				bitmap = RestoreImage();
			}

			@Override 
			public void DoOnCompleted() throws Exception {
				if (!Panel.flExists)
					return; //. ->
				if ((bitmap != null) && (!Canceller.flCancel) && (ViewHolder.Item == Item)) {
					ViewHolder.ivImage.setImageBitmap(bitmap);
					ViewHolder.ivImage.setOnClickListener(ImageClickListener);
				}
			}

			private Bitmap RestoreImage() throws Exception {
				return ImageCache.getBitmap(Item.Component.GetKey());
			}
		}
		
		private Context context;
		//.
		private TComponentTypedDataFilesPanel Panel;
		//.
		private ListView MyListView;
		//.
		private View 				ProgressBar;
		private TProgressHandler 	ProgressHandler;
		//.
		private TComponentListItem[] Items;
		//.
		private LayoutInflater layoutInflater;
		//.
		private Executor 	ImageLoadingExecutor = Executors.newFixedThreadPool(5);
		private int 		ImageLoadingCount = 0;
		//.
		private TDiskImageCache ImageCache;
		//.
		public OnClickListener ImageClickListener = new OnClickListener() {
			@Override
	        public void onClick(View v) {
	            int position = MyListView.getPositionForView((View)v.getParent());
	            //.
				TComponentListItem Item = (TComponentListItem)Items[position];
				if (Item.BMP_flLoaded && (!Item.BMP_flNull)) {
		        	AlertDialog alert = new AlertDialog.Builder(context).create();
		        	alert.setCancelable(true);
		        	alert.setCanceledOnTouchOutside(true);
		        	LayoutInflater factory = LayoutInflater.from(context);
		        	View layout = factory.inflate(R.layout.image_preview_dialog_layout, null);
		        	ImageView IV = (ImageView)layout.findViewById(R.id.ivPreview);
		        	IV.setImageDrawable(((ImageView)v).getDrawable());
		        	alert.setView(layout);
		        	//.
		        	alert.show();    
				}
	        }
		};
		//.
		public boolean flListIsScrolling = false;
	        
		public TComponentListAdapter(TComponentTypedDataFilesPanel pPanel, ListView pMyListView, View pProgressBar, TComponentListItem[] pItems) {
			context = pPanel;
			//.
			Panel = pPanel;
			MyListView = pMyListView;
			ProgressBar = pProgressBar;
			//.
			Items = pItems;
			layoutInflater = LayoutInflater.from(context);
			//.
			ImageCache = new TDiskImageCache(context, ImageCache_Name,ImageCache_Size,ImageCache_CompressFormat,ImageCache_CompressQuality, false);
			//.
			ProgressHandler = new TProgressHandler() {
				@Override
				public void DoOnStart() {
					ProgressBar.setVisibility(View.VISIBLE);
				}
				@Override
				public void DoOnFinish() {
					ProgressBar.setVisibility(View.GONE);
				}
				@Override
				public void DoOnProgress(int Percentage) {
				}
			};
		}

		@Override
		public int getCount() {
			return Items.length;
		}

		@Override
		public Object getItem(int position) {
			return Items[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TViewHolder holder;
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.user_activitycomponentlist_row_layout, null);
				holder = new TViewHolder();
				holder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
				holder.lbName = (TextView) convertView.findViewById(R.id.lbName);
				holder.lbInfo = (TextView) convertView.findViewById(R.id.lbInfo);
				//.
				convertView.setTag(holder);
			} 
			else 
				holder = (TViewHolder)convertView.getTag();
			//. updating view
			TComponentListItem Item = (TComponentListItem)Items[position];
			//.
			holder.Item = Item;
			//.
			holder.lbName.setText(Item.Name);
			//.
			holder.lbInfo.setText(Item.Info);
			//.
			Bitmap BMP = null;
			//.
			if (!Item.BMP_flLoaded)
				new TImageLoadTask(Item,holder).executeOnExecutor(ImageLoadingExecutor);
			else {
				if (!Item.BMP_flNull)
					if (flListIsScrolling)
						new TImageRestoreTask(Item,holder).Start();
					else {
						BMP = ImageCache.getBitmap(Item.Component.GetKey());
						if (BMP == null) {
							Item.BMP_flLoaded = false;
							//.
							new TImageLoadTask(Item,holder).executeOnExecutor(ImageLoadingExecutor);
						}
					}
			}
			//.
			if (BMP != null) {
				holder.ivImage.setImageBitmap(BMP);
				holder.ivImage.setOnClickListener(ImageClickListener);
			}
			else {
				switch (Item.DataType) {
				
				case SpaceDefines.TYPEDDATAFILE_TYPE_DocumentName:
					holder.ivImage.setImageDrawable(context.getResources().getDrawable(R.drawable.user_activity_component_list_placeholder_text));
					break; //. >
					
				case SpaceDefines.TYPEDDATAFILE_TYPE_ImageName:
					if ((Item.DataFormat != null) && Item.DataFormat.toUpperCase(Locale.US).equals(TDrawingDefines.DataFormat))
						holder.ivImage.setImageDrawable(context.getResources().getDrawable(R.drawable.user_activity_component_list_placeholder_image_drawing));
					else 
						holder.ivImage.setImageDrawable(context.getResources().getDrawable(R.drawable.user_activity_component_list_placeholder_image));
					break; //. >
					
				case SpaceDefines.TYPEDDATAFILE_TYPE_AudioName:
					holder.ivImage.setImageDrawable(context.getResources().getDrawable(R.drawable.user_activity_component_list_placeholder_audio));
					break; //. >
					
				case SpaceDefines.TYPEDDATAFILE_TYPE_VideoName:
					holder.ivImage.setImageDrawable(context.getResources().getDrawable(R.drawable.user_activity_component_list_placeholder_video));
					break; //. >
					
				case SpaceDefines.TYPEDDATAFILE_TYPE_MeasurementName:
					holder.ivImage.setImageDrawable(context.getResources().getDrawable(R.drawable.user_activity_component_list_placeholder_measurement));
					break; //. >
					
				default:
					holder.ivImage.setImageDrawable(context.getResources().getDrawable(R.drawable.user_activity_component_list_placeholder));
					break; //. >
				}
				holder.ivImage.setOnClickListener(null);
			}
			//.
			return convertView;
		}
	}
	
	public boolean flExists = false;
	//.
	private boolean flAutoStart = false; 
    //.
	private TReflectorComponent Component;
	//.
    private byte[] 						DataFilesBA = null;
    private TComponentTypedDataFiles 	DataFiles = null;
	private TComponentListAdapter 		lvDataFilesAdapter; 
	private ListView 					lvDataFiles;
	//.
	private TextView lbName;
	private View ProgressBar;
	//.
	private Button btnUpdate;
	private Button btnCreateNewComponent;
	//.
	private TUpdating	Updating = null;
	private int			UpdateCount = 0;
	//.
	private TComponentTypedDataFileLoading ComponentTypedDataFileLoading = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//.
		//.
        int ComponentID = 0;
        Bundle extras = getIntent().getExtras(); 
        if (extras != null) { 
			ComponentID = extras.getInt("ComponentID");
        	DataFilesBA = extras.getByteArray("DataFiles");
        	flAutoStart = extras.getBoolean("AutoStart");
        }
        else {
        	finish();
        	return; //. ->
        }
		Component = TReflectorComponent.GetComponent(ComponentID);
		//.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        //. 
        setContentView(R.layout.componenttypeddatafiles_panel);
        //.
        lbName = (TextView)findViewById(R.id.lbName);
        lbName.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				final CharSequence[] _items;
				_items = new CharSequence[1];
				_items[0] = TComponentTypedDataFilesPanel.this.getString(R.string.SGetURLFile);
				AlertDialog.Builder builder = new AlertDialog.Builder(TComponentTypedDataFilesPanel.this);
				builder.setTitle(R.string.SOperations);
				builder.setNegativeButton(TComponentTypedDataFilesPanel.this.getString(R.string.SCancel),null);
				builder.setSingleChoiceItems(_items, 0, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.dismiss();
						//.
		            	try {
							switch (arg1) {
							
							case 0: 
			            		if (DataFiles == null)
			            			return; //. ->
								TComponentTypedDataFile RootItem = DataFiles.GetRootItem();
								if (RootItem == null)
									throw new Exception("there is no a root element of the data files"); //. =>
								//.
			            		String URLFN = TGeoLogApplication.GetTempFolder()+"/"+TURL.DefaultURLFileName;
			            		com.geoscope.GeoEye.Space.URLs.Functionality.ComponentFunctionality.ComponentTypedDataFiles.Panel.TURL URL = new com.geoscope.GeoEye.Space.URLs.Functionality.ComponentFunctionality.ComponentTypedDataFiles.Panel.TURL(RootItem.DataComponentType,RootItem.DataComponentID);
			        			URL.Name = lbName.getText().toString();
			            		URL.ConstructURLFile(URLFN);
			            		//.
				    		    new AlertDialog.Builder(TComponentTypedDataFilesPanel.this)
				    	        .setIcon(android.R.drawable.ic_dialog_alert)
				    	        .setTitle(R.string.SInfo)
				    	        .setMessage(TComponentTypedDataFilesPanel.this.getString(R.string.SURLFileNameHasBeenSaved)+URLFN+"\n"+TComponentTypedDataFilesPanel.this.getString(R.string.SUseItForImport))
				    		    .setPositiveButton(R.string.SOk, null)
				    		    .show();
								break; //. >
							}
						}
						catch (Exception E) {
							String S = E.getMessage();
							if (S == null)
								S = E.getClass().getName();
		        			Toast.makeText(TComponentTypedDataFilesPanel.this, TComponentTypedDataFilesPanel.this.getString(R.string.SError)+S, Toast.LENGTH_LONG).show();  						
						}
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				//.
				return true;
			}
		});
        //.
        lvDataFiles = (ListView)findViewById(R.id.lvDataFiles);
        lvDataFiles.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lvDataFiles.setOnItemClickListener(new OnItemClickListener() {         
			@Override
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if ((DataFiles == null) || (DataFiles.Count() == 0))
					return; //. ->
				TComponentTypedDataFile ComponentTypedDataFile = DataFiles.Items[arg2];
				ComponentTypedDataFile_Process(ComponentTypedDataFile);
        	}              
        });         
        lvDataFiles.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if ((DataFiles == null) || (DataFiles.Count() == 0))
					return false; //. ->
            	//.
				final TComponentTypedDataFile ComponentTypedDataFile = DataFiles.Items[arg2];
				//.
	    		final CharSequence[] _items;
	    		int SelectedIdx = -1;
	    		_items = new CharSequence[4];
	    		_items[0] = TComponentTypedDataFilesPanel.this.getString(R.string.SOpen); 
	    		_items[1] = TComponentTypedDataFilesPanel.this.getString(R.string.SContent1); 
	    		_items[2] = TComponentTypedDataFilesPanel.this.getString(R.string.SGetURLFile); 
	    		_items[3] = TComponentTypedDataFilesPanel.this.getString(R.string.SRemove); 
	    		//.
	    		AlertDialog.Builder builder = new AlertDialog.Builder(TComponentTypedDataFilesPanel.this);
	    		builder.setTitle(R.string.SSelect);
	    		builder.setNegativeButton(R.string.SClose,null);
	    		builder.setSingleChoiceItems(_items, SelectedIdx, new DialogInterface.OnClickListener() {
	    			@Override
	    			public void onClick(DialogInterface arg0, int arg1) {
	    		    	try {
	    		    		switch (arg1) {
	    		    		
	    		    		case 0: //. open
	    						ComponentTypedDataFile_Process(ComponentTypedDataFile);
	        		    		//.
	    		    			break; //. >
	    		    			
	    		    		case 1: //. content
	    						try {
    		    					final TComponentTypedDataFile _ComponentTypedDataFile = ComponentTypedDataFile;
    		    					//.
    		    					TAsyncProcessing ContentOpening = new TAsyncProcessing(TComponentTypedDataFilesPanel.this) {

    		    						private TComponentTypedDataFiles TypedDataFiles;
    		    						
    		    						@Override
    		    						public void Process() throws Exception {
    		    		    				TUserAgent UserAgent = TUserAgent.GetUserAgent();
    		    		    				if (UserAgent == null)
    		    		    					throw new Exception(TComponentTypedDataFilesPanel.this.getString(R.string.SUserAgentIsNotInitialized)); //. =>
    		    		    				//.
    				    					TypedDataFiles = new TComponentTypedDataFiles(context, SpaceDefines.TYPEDDATAFILE_MODEL_HUMANREADABLECOLLECTION, SpaceDefines.TYPEDDATAFILE_TYPE_AllName);
    				    					TypedDataFiles.PrepareForComponent(_ComponentTypedDataFile.DataComponentType,_ComponentTypedDataFile.DataComponentID, true, UserAgent.Server);
    		    						}

    		    						@Override
    		    						public void DoOnCompleted() throws Exception {
    	    								Intent intent = new Intent(context, TComponentTypedDataFilesPanel.class);
    	    								if (Component != null)
    	    									intent.putExtra("ComponentID", Component.ID);
    	    								intent.putExtra("DataFiles", TypedDataFiles.ToByteArrayV0());
    	    								intent.putExtra("AutoStart", false);
    	    								//.
    	    								startActivityForResult(intent, REQUEST_COMPONENT_CONTENT);
    		    						}
    		    						
    		    						@Override
    		    						public void DoOnException(Exception E) {
    		    							Toast.makeText(TComponentTypedDataFilesPanel.this, E.getMessage(),	Toast.LENGTH_LONG).show();
    		    						}
    		    					};
    		    					ContentOpening.Start();
		    						//.
		        		    		arg0.dismiss();
	    						}
	    						catch (Exception E) {
	    			                Toast.makeText(TComponentTypedDataFilesPanel.this, E.getMessage(), Toast.LENGTH_LONG).show();
	    						}
	        		    		//.
	    		    			break; //. >
	    		    			
	    		    		case 2: //. get URL-file
		    					TAsyncProcessing Processing = new TAsyncProcessing(TComponentTypedDataFilesPanel.this) {

		    						private TComponentTypedDataFile _ComponentTypedDataFile = ComponentTypedDataFile.Clone();
		    						//.
		    						private String URLFN;
		    						
		    						@Override
		    						public void Process() throws Exception {
	    								TUserAgent UserAgent = TUserAgent.GetUserAgent();
	    								if (UserAgent == null)
	    									throw new Exception(TComponentTypedDataFilesPanel.this.getString(R.string.SUserAgentIsNotInitialized)); //. =>
	    								//.
	    								TComponentFunctionality CF = UserAgent.User().Space.TypesSystem.TComponentFunctionality_Create(_ComponentTypedDataFile.DataComponentType,_ComponentTypedDataFile.DataComponentID);
	    								if (CF != null) 
	    									try {
	    										TURL URL = CF.GetDefaultURL();
	    										if (URL != null) 
	    											try {
	    												if (URL.HasData()) {
	    													_ComponentTypedDataFile.DataType = SpaceDefines.TYPEDDATAFILE_TYPE_Document; 
	    													_ComponentTypedDataFile.PrepareForComponent(_ComponentTypedDataFile.DataComponentType,_ComponentTypedDataFile.DataComponentID, false, UserAgent.Server);
	    													if (_ComponentTypedDataFile.DataFormat.equals(SpaceDefines.TYPEDDATAFILE_TYPE_Document_FORMAT_XML)) 
			    	    										CF.ParseFromXMLDocument(_ComponentTypedDataFile.GetFileData());
	    												}
	    	    										//.
		    											URLFN = TGeoLogApplication.GetTempFolder()+"/"+TURL.DefaultURLFileName;
		    											URL.Name = ComponentTypedDataFile.DataName;
		    											URL.ConstructURLFile(URLFN);
	    											}
	    											finally {
	    												URL.Release();
	    											}
    	    										else
    	    											throw new Exception("there is no URL there"); //. =>
	    												
	    									}
	    									finally {
	    										CF.Release();
	    									}
    										else
    											throw new Exception("there is no component functionality there"); //. =>
		    						}

		    						@Override
		    						public void DoOnCompleted() throws Exception {
		    			    		    new AlertDialog.Builder(context)
		    			    	        .setIcon(android.R.drawable.ic_dialog_alert)
		    			    	        .setTitle(R.string.SInfo)
		    			    	        .setMessage(context.getString(R.string.SURLFileNameHasBeenSaved)+URLFN+"\n"+context.getString(R.string.SUseItForImport))
		    			    		    .setPositiveButton(R.string.SOk, null)
		    			    		    .show();
		    						}
		    						
		    						@Override
		    						public void DoOnException(Exception E) {
		    							Toast.makeText(TComponentTypedDataFilesPanel.this, E.getMessage(),	Toast.LENGTH_LONG).show();
		    						}
		    					};
		    					Processing.Start();
	    		    			//.
	        		    		arg0.dismiss();
	        		    		//.
	    		    			break; //. >
	    		    			
	    		    		case 3: //. remove component
	    		    			AlertDialog.Builder alert = new AlertDialog.Builder(TComponentTypedDataFilesPanel.this);
	    		    			//.
	    		    			alert.setTitle(R.string.SRemoval);
	    		    			alert.setMessage(R.string.SRemoveSelectedComponent);
	    		    			//.
	    		    			alert.setPositiveButton(R.string.SOk, new DialogInterface.OnClickListener() {
	    		    				
	    		    				@Override
	    		    				public void onClick(DialogInterface dialog, int whichButton) {
	    		    					final TComponentTypedDataFile _ComponentTypedDataFile = ComponentTypedDataFile;
	    		    					//.
	    		    					TAsyncProcessing Removing = new TAsyncProcessing(TComponentTypedDataFilesPanel.this, TComponentTypedDataFilesPanel.this.getString(R.string.SRemoving)) {

	    		    						@Override
	    		    						public void Process() throws Exception {
	    		    		    				TUserAgent UserAgent = TUserAgent.GetUserAgent();
	    		    		    				if (UserAgent == null)
	    		    		    					throw new Exception(TComponentTypedDataFilesPanel.this.getString(R.string.SUserAgentIsNotInitialized)); //. =>
	    		    		    				//.
	    		    							TTypeFunctionality TF = UserAgent.User().Space.TypesSystem.TTypeFunctionality_Create(ComponentTypedDataFile.DataComponentType);
	    		    							if (TF != null)
	    		    								try {
	    		    									TF.DestroyInstance(_ComponentTypedDataFile.DataComponentID);
	    		    								} finally {
	    		    									TF.Release();
	    		    								}
	    		    								else
	    		    									throw new Exception("there is no functionality for type, idType = "+Integer.toString(_ComponentTypedDataFile.DataComponentType)); //. =>
	    		    									
	    		    						}

	    		    						@Override
	    		    						public void DoOnCompleted() throws Exception {
	    		    							DataFiles.RemoveItem(_ComponentTypedDataFile);
	    		    							//.
	    		    							Update();	    		    							
	    		    							//.
	    		    							Toast.makeText(TComponentTypedDataFilesPanel.this, R.string.SObjectHasBeenRemoved, Toast.LENGTH_LONG).show();
	    		    						}
	    		    						
	    		    						@Override
	    		    						public void DoOnException(Exception E) {
	    		    							Toast.makeText(TComponentTypedDataFilesPanel.this, E.getMessage(),	Toast.LENGTH_LONG).show();
	    		    						}
	    		    					};
	    		    					Removing.Start();
	    		    				}
	    		    			});
	    		    			//.
	    		    			alert.setNegativeButton(R.string.SCancel, null);
	    		    			//.
	    		    			alert.show();
	    		    			//.
	        		    		arg0.dismiss();
	        		    		//.
	    		    			break; //. >
	    		    		}
	    		    	}
	    		    	catch (Exception E) {
	    		    		Toast.makeText(TComponentTypedDataFilesPanel.this, E.getMessage(), Toast.LENGTH_LONG).show();
	    		    		//.
	    		    		arg0.dismiss();
	    		    	}
	    			}
	    		});
	    		AlertDialog alert = builder.create();
	    		alert.show();
            	//.
            	return true; 
			}
		}); 
        lvDataFiles.setOnScrollListener(new OnScrollListener() {
        	
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
            	if (lvDataFilesAdapter != null)
            		lvDataFilesAdapter.flListIsScrolling = (scrollState != OnScrollListener.SCROLL_STATE_IDLE); 
            }
        });
        //.
        ProgressBar = findViewById(R.id.pbProgress);
        //.
        btnUpdate = (Button)findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new OnClickListener() {
        	
        	@Override
            public void onClick(View v) {
        		StartUpdating();
            }
        });
        //.
        btnCreateNewComponent = (Button)findViewById(R.id.btnCreateNewComponent);
        btnCreateNewComponent.setOnClickListener(new OnClickListener() {
        	
        	@Override
            public void onClick(View v) {
        		CreateNewComponent();
            }
        });
        //.
        final int HintID = THintManager.HINT__ComponentTypedDataFilesPanel;
        final TextView lbListHint = (TextView)findViewById(R.id.lbHint);
        String Hint = THintManager.GetHint(HintID, this);
        if (Hint != null) {
        	lbListHint.setText(Hint);
            lbListHint.setOnLongClickListener(new OnLongClickListener() {
            	
    			@Override
    			public boolean onLongClick(View v) {
    				THintManager.SetHintAsDisabled(HintID);
    	        	lbListHint.setVisibility(View.GONE);
    	        	//.
    				return true;
    			}
    		});
            //.
        	lbListHint.setVisibility(View.VISIBLE);
        }
        else
        	lbListHint.setVisibility(View.GONE);
        //.
        setResult(RESULT_CANCELED);
        //.
        flExists = true;
	}

	@Override
	protected void onDestroy() {
		flExists = false;
		//. 
		if (ComponentTypedDataFileLoading != null) {
			ComponentTypedDataFileLoading.Cancel();
			ComponentTypedDataFileLoading = null;
		}
		//.
		if (Updating != null) {
			Updating.Cancel();
			Updating = null;
		}
		//.
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
        //.
		if (UpdateCount == 0)
			StartUpdating();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {        

        case REQUEST_ADD_COMPONENT: 
        	if (resultCode == RESULT_OK) {   
        	    setResult(RESULT_OK);
        	    //.
        	    StartUpdating();
        	}
        	break; //. >
        }
    }
	
	private class TUpdating extends TCancelableThread {

    	private static final int MESSAGE_EXCEPTION = -1;
    	private static final int MESSAGE_COMPLETED = 0;
    	private static final int MESSAGE_FINISHED = 1;
    	private static final int MESSAGE_PROGRESSBAR_SHOW = 2;
    	private static final int MESSAGE_PROGRESSBAR_HIDE = 3;
    	private static final int MESSAGE_PROGRESSBAR_PROGRESS = 4;
    	private static final int MESSAGE_PROGRESSBAR_MESSAGE = 5;
    	
    	private TComponentTypedDataFiles _DataFiles;
    	//.
    	private boolean flShowProgress = false;
    	private boolean flClosePanelOnCancel = false;
    	
        private ProgressDialog progressDialog;
    	
    	public TUpdating(TComponentTypedDataFiles pDataFiles, boolean pflShowProgress, boolean pflClosePanelOnCancel) {
    		super();
    		//.
    		_DataFiles = pDataFiles;
    		flShowProgress = pflShowProgress;
    		flClosePanelOnCancel = pflClosePanelOnCancel;
    		//.
    		_Thread = new Thread(this);
    		_Thread.start();
    	}

		@Override
		public void run() {
			try {
				try {
					if (flShowProgress)
						MessageHandler.obtainMessage(MESSAGE_PROGRESSBAR_SHOW).sendToTarget();
	    			try {
	    				if (_DataFiles == null) {
		    	        	_DataFiles = new TComponentTypedDataFiles(TComponentTypedDataFilesPanel.this, SpaceDefines.TYPEDDATAFILE_MODEL_HUMANREADABLECOLLECTION);
		    	        	_DataFiles.FromByteArrayV0(DataFilesBA);
		    	        	_DataFiles.PrepareAsNames();
		    	        	//.
		    	        	Thread.sleep(100);
	    				}
	    				else {
	    					TComponentTypedDataFile RootItem = _DataFiles.GetRootItem();
	    					if (RootItem == null)
	    						throw new Exception("there is no a root element of the data files"); //. =>
		    				TUserAgent UserAgent = TUserAgent.GetUserAgent();
		    				if (UserAgent == null)
		    					throw new Exception(getString(R.string.SUserAgentIsNotInitialized)); //. =>
		    				//.
		    	        	_DataFiles.PrepareAsNames();
	    					_DataFiles.PrepareForComponent(RootItem.DataComponentType,RootItem.DataComponentID, true, UserAgent.Server);
	    				}
	    			}
					finally {
						if (flShowProgress)
							MessageHandler.obtainMessage(MESSAGE_PROGRESSBAR_HIDE).sendToTarget();
					}
    				//.
	    			MessageHandler.obtainMessage(MESSAGE_COMPLETED,_DataFiles).sendToTarget();
	        	}
	        	catch (InterruptedException E) {
	        	}
	        	catch (IOException E) {
	    			MessageHandler.obtainMessage(MESSAGE_EXCEPTION,E).sendToTarget();
	        	}
	        	catch (Throwable E) {
	    			MessageHandler.obtainMessage(MESSAGE_EXCEPTION,new Exception(E.getMessage())).sendToTarget();
	        	}
			}
			finally {
    			MessageHandler.obtainMessage(MESSAGE_FINISHED).sendToTarget();
			}
		}

		private final Handler MessageHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	        	try {
		            switch (msg.what) {
		            
		            case MESSAGE_EXCEPTION:
		            	if (Canceller.flCancel)
			            	break; //. >
		            	Exception E = (Exception)msg.obj;
		                Toast.makeText(TComponentTypedDataFilesPanel.this, E.getMessage(), Toast.LENGTH_LONG).show();
		            	//.
		            	break; //. >
		            	
		            case MESSAGE_COMPLETED:
						if (Canceller.flCancel)
			            	break; //. >
						TComponentTypedDataFilesPanel.this.DataFiles = (TComponentTypedDataFiles)msg.obj;
						//.
	           		 	TComponentTypedDataFilesPanel.this.Update();
	           		 	//. auto-start if there is only one item
	    				if (flAutoStart && (UpdateCount == 1) && ((DataFiles != null) && (DataFiles.Count() == 1))) {
		    				TComponentTypedDataFile ComponentTypedDataFile = DataFiles.Items[0];
		    				ComponentTypedDataFile_Process(ComponentTypedDataFile);
	    				}
		            	//.
		            	break; //. >
		            	
		            case MESSAGE_FINISHED:
		            	if (TComponentTypedDataFilesPanel.this.Updating == TUpdating.this)
		            		TComponentTypedDataFilesPanel.this.Updating = null;
		            	//.
		            	break; //. >
		            	
		            case MESSAGE_PROGRESSBAR_SHOW:
		            	progressDialog = new ProgressDialog(TComponentTypedDataFilesPanel.this);    
		            	progressDialog.setMessage(TComponentTypedDataFilesPanel.this.getString(R.string.SLoading));    
		            	progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);    
		            	progressDialog.setIndeterminate(true); 
		            	progressDialog.setCancelable(true);
		            	progressDialog.setOnCancelListener( new OnCancelListener() {
		            		
							@Override
							public void onCancel(DialogInterface arg0) {
								Cancel();
								//.
								if (flClosePanelOnCancel)
									TComponentTypedDataFilesPanel.this.finish();
								else
									if (TComponentTypedDataFilesPanel.this.DataFiles == null)
										TComponentTypedDataFilesPanel.this.finish();
							}
						});
		            	progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, TComponentTypedDataFilesPanel.this.getString(R.string.SCancel), new DialogInterface.OnClickListener() {
		            		
		            		@Override 
		            		public void onClick(DialogInterface dialog, int which) { 
								Cancel();
								//.
								if (flClosePanelOnCancel)
									TComponentTypedDataFilesPanel.this.finish();
								else
									if (TComponentTypedDataFilesPanel.this.DataFiles == null)
										TComponentTypedDataFilesPanel.this.finish();
		            		} 
		            	}); 
		            	//.
		            	progressDialog.show(); 	            	
		            	//.
		            	break; //. >

		            case MESSAGE_PROGRESSBAR_HIDE:
		                if ((!isFinishing()) && progressDialog.isShowing()) 
		                	progressDialog.dismiss(); 
		            	//.
		            	break; //. >
		            
		            case MESSAGE_PROGRESSBAR_PROGRESS:
		            	progressDialog.setProgress((Integer)msg.obj);
		            	//.
		            	break; //. >

		            case MESSAGE_PROGRESSBAR_MESSAGE:
		            	String S = (String)msg.obj;
		            	//.
		            	if ((S != null) && (!S.equals(""))) 
		            		progressDialog.setMessage(TComponentTypedDataFilesPanel.this.getString(R.string.SLoading)+"  "+S);
		            	else
		            		progressDialog.setMessage(TComponentTypedDataFilesPanel.this.getString(R.string.SLoading));
		            	break; //. >
		            }
	        	}
	        	catch (Throwable E) {
	        		TGeoLogApplication.Log_WriteError(E);
	        	}
	        }
	    };
    }   
	
    private void Update() throws Exception {
    	if ((DataFiles == null) || (DataFiles.Items.length == 0)) {
    		lvDataFiles.setAdapter(null);
    		return; //. ->
    	}
    	//.
    	lbName.setText(DataFiles.Items[0].DataName);
    	//.
		TUserAgent UserAgent = TUserAgent.GetUserAgent();
		if (UserAgent == null)
			throw new Exception(getString(R.string.SUserAgentIsNotInitialized)); //. =>
		//.
		TComponentListItem[] Items = new TComponentListItem[DataFiles.Items.length];
		for (int I = 0; I < Items.length; I++) {
			TComponentTypedDataFile DataFile = DataFiles.Items[I];
			String Name = DataFile.DataName;
			if (!SpaceDefines.TYPEDDATAFILE_TYPE_Document_IsXMLFormat(DataFile.DataType,DataFile.DataFormat))
				Name = Name+" "+"/"+SpaceDefines.TYPEDDATAFILE_TYPE_String(DataFile.DataType,this)+"/";
			TComponent _Component = new TComponent(0, DataFile.DataComponentType,DataFile.DataComponentID, 0.0);
			_Component.TypedDataFiles = new TComponentTypedDataFiles(this, SpaceDefines.TYPEDDATAFILE_MODEL_HUMANREADABLECOLLECTION,SpaceDefines.TYPEDDATAFILE_TYPE_Image);
			//.
			TComponentListItem Item = new TComponentListItem(UserAgent.Server, DataFile.DataType,DataFile.DataFormat,Name,"", _Component);
			Items[I] = Item;
		}
		lvDataFilesAdapter = new TComponentListAdapter(this, lvDataFiles, ProgressBar, Items); 
		lvDataFiles.setAdapter(lvDataFilesAdapter);
		//.
		UpdateCount++;
    }

    private void StartUpdating() {
    	if (Updating != null)
    		Updating.Cancel();
    	Updating = new TUpdating(DataFiles, true, false);
    }    
    
	private class TComponentTypedDataFileLoading extends TCancelableThread {

		private static final int MESSAGE_SHOWEXCEPTION = 0;
		private static final int MESSAGE_PROGRESSBAR_SHOW = 1;
		private static final int MESSAGE_PROGRESSBAR_HIDE = 2;
		private static final int MESSAGE_PROGRESSBAR_PROGRESS = 3;

		private TComponentTypedDataFile ComponentTypedDataFile;
		private int OnCompletionMessage;
		// .
		int SummarySize = 0;
		private ProgressDialog progressDialog;

		public TComponentTypedDataFileLoading(TComponentTypedDataFile pComponentTypedDataFile, int pOnCompletionMessage) {
    		super();
    		//.
			ComponentTypedDataFile = pComponentTypedDataFile;
			OnCompletionMessage = pOnCompletionMessage;
			// .
			_Thread = new Thread(this);
			_Thread.start();
		}

		@Override
		public void run() {
			try {
				TUserAgent UserAgent = TUserAgent.GetUserAgent();
				if (UserAgent == null)
					throw new Exception(getString(R.string.SUserAgentIsNotInitialized)); //. =>
				//.
				switch (ComponentTypedDataFile.DataComponentType) {

				case SpaceDefines.idTDATAFile:
					TGeoScopeServerInfo.TInfo ServersInfo = UserAgent.Server.Info.GetInfo();
					TComponentStreamServer CSS = new TComponentStreamServer(TComponentTypedDataFilesPanel.this, ServersInfo.SpaceDataServerAddress,ServersInfo.SpaceDataServerPort, UserAgent.Server.User.UserID, UserAgent.Server.User.UserPassword);
					try {
						String CFN = TTypesSystem.TypesSystem.SystemTDATAFile.Context_GetFolder()+"/"+ComponentTypedDataFile.FileName();
						//.
						MessageHandler.obtainMessage(MESSAGE_PROGRESSBAR_SHOW).sendToTarget();
						try {
							CSS.ComponentStreamServer_GetComponentStream_Begin(ComponentTypedDataFile.DataComponentType,ComponentTypedDataFile.DataComponentID);
							try {
								File CF = new File(CFN);
								RandomAccessFile ComponentStream = new RandomAccessFile(CF,"rw");
								try {
									ComponentStream.seek(ComponentStream.length());
									//.
									CSS.ComponentStreamServer_GetComponentStream_Read(Long.toString(ComponentTypedDataFile.DataComponentID),ComponentStream, Canceller, new TProgressor() {
										@Override
										public synchronized boolean DoOnProgress(int Percentage) {
											MessageHandler.obtainMessage(MESSAGE_PROGRESSBAR_PROGRESS,Percentage).sendToTarget();
											return true;
										}
									});
								}
								finally {
									ComponentStream.close();
								}
							}
							finally {
								CSS.ComponentStreamServer_GetComponentStream_End();						
							}
						}
						finally {
							MessageHandler.obtainMessage(MESSAGE_PROGRESSBAR_HIDE).sendToTarget();
						}
						//.
						ComponentTypedDataFile.PrepareAsFullFromFile(CFN);
						//.
						Canceller.Check();
						//.
						TComponentTypedDataFilesPanel.this.MessageHandler.obtainMessage(OnCompletionMessage,ComponentTypedDataFile).sendToTarget();
					}
					finally {
						CSS.Destroy();
					}
					break; //. >

				default:
					String URL1 = UserAgent.Server.Address;
					// . add command path
					URL1 = "http://" + URL1 + "/" + "Space" + "/" + "2"/* URLProtocolVersion */
							+ "/" + Long.toString(UserAgent.Server.User.UserID);
					String URL2 = "Functionality" + "/"
							+ "ComponentDataDocument.dat";
					// . add command parameters
					int WithComponentsFlag = 0;
					URL2 = URL2
							+ "?"
							+ "1"/* command version */
							+ ","
							+ Integer
									.toString(ComponentTypedDataFile.DataComponentType)
							+ ","
							+ Long
									.toString(ComponentTypedDataFile.DataComponentID)
							+ ","
							+ Integer
									.toString(SpaceDefines.TYPEDDATAFILE_MODEL_HUMANREADABLECOLLECTION)
							+ ","
							+ Integer.toString(ComponentTypedDataFile.DataType+SpaceDefines.TYPEDDATAFILE_TYPE_SHIFT_FromName_ToFull)
							+ "," + Integer.toString(WithComponentsFlag);
					// .
					byte[] URL2_Buffer;
					try {
						URL2_Buffer = URL2.getBytes("windows-1251");
					} catch (Exception E) {
						URL2_Buffer = null;
					}
					byte[] URL2_EncryptedBuffer = UserAgent.Server.User.EncryptBufferV2(URL2_Buffer);
					// . encode string
					StringBuffer sb = new StringBuffer();
					for (int I = 0; I < URL2_EncryptedBuffer.length; I++) {
						String h = Integer
								.toHexString(0xFF & URL2_EncryptedBuffer[I]);
						while (h.length() < 2)
							h = "0" + h;
						sb.append(h);
					}
					URL2 = sb.toString();
					// .
					String URL = URL1 + "/" + URL2 + ".dat";
					// .
					if (Canceller.flCancel)
						return; // . ->
					// .
					MessageHandler.obtainMessage(MESSAGE_PROGRESSBAR_SHOW)
							.sendToTarget();
					try {
						HttpURLConnection Connection = UserAgent.Server.OpenConnection(URL);
						try {
							if (Canceller.flCancel)
								return; // . ->
							// .
							InputStream in = Connection.getInputStream();
							try {
								if (Canceller.flCancel)
									return; // . ->
								// .
								int RetSize = Connection.getContentLength();
								if (RetSize == 0) {
									ComponentTypedDataFile.Data = null;
									return; // . ->
								}
								byte[] Data = new byte[RetSize];
								int Size;
								SummarySize = 0;
								int ReadSize;
								while (SummarySize < Data.length) {
									ReadSize = Data.length - SummarySize;
									Size = in.read(Data, SummarySize, ReadSize);
									if (Size <= 0)
										throw new Exception(TComponentTypedDataFilesPanel.this.getString(R.string.SConnectionIsClosedUnexpectedly)); // =>
									SummarySize += Size;
									// .
									if (Canceller.flCancel)
										return; // . ->
									// .
									MessageHandler
											.obtainMessage(
													MESSAGE_PROGRESSBAR_PROGRESS,
													(Integer) (100 * SummarySize / Data.length))
											.sendToTarget();
								}
								// .
								ComponentTypedDataFile.FromByteArrayV0(Data);
								// .
								TComponentTypedDataFilesPanel.this.MessageHandler
										.obtainMessage(OnCompletionMessage,
												ComponentTypedDataFile)
										.sendToTarget();
							} finally {
								in.close();
							}
						} finally {
							Connection.disconnect();
						}
					} finally {
						MessageHandler.obtainMessage(MESSAGE_PROGRESSBAR_HIDE)
								.sendToTarget();
					}
					break; //. >
				}
			} catch (InterruptedException E) {
			} catch (CancelException E) {
			} catch (IOException E) {
				MessageHandler.obtainMessage(MESSAGE_SHOWEXCEPTION, E)
						.sendToTarget();
			} catch (Throwable E) {
				MessageHandler.obtainMessage(MESSAGE_SHOWEXCEPTION,
						new Exception(E.getMessage())).sendToTarget();
			}
		}

		private final Handler MessageHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
	        	try {
					switch (msg.what) {

					case MESSAGE_SHOWEXCEPTION:
						if (Canceller.flCancel)
			            	break; //. >
						Exception E = (Exception) msg.obj;
						Toast.makeText(
								TComponentTypedDataFilesPanel.this,
								TComponentTypedDataFilesPanel.this.getString(R.string.SErrorOfDataLoading)
										+ E.getMessage(), Toast.LENGTH_SHORT)
								.show();
						// .
						break; // . >

					case MESSAGE_PROGRESSBAR_SHOW:
						progressDialog = new ProgressDialog(TComponentTypedDataFilesPanel.this);
						progressDialog.setMessage(TComponentTypedDataFilesPanel.this.getString(R.string.SLoading));
						progressDialog
								.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						progressDialog.setIndeterminate(false);
						progressDialog.setCancelable(true);
						progressDialog.setOnCancelListener(new OnCancelListener() {
							@Override
							public void onCancel(DialogInterface arg0) {
								Cancel();
							}
						});
						progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE,
								TComponentTypedDataFilesPanel.this.getString(R.string.SCancel),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Cancel();
									}
								});
						// .
						progressDialog.show();
						// .
						break; // . >

					case MESSAGE_PROGRESSBAR_HIDE:
		                if ((!isFinishing()) && progressDialog.isShowing()) 
		                	progressDialog.dismiss(); 
						// .
						break; // . >

					case MESSAGE_PROGRESSBAR_PROGRESS:
						progressDialog.setProgress((Integer) msg.obj);
						// .
						break; // . >
					}
	        	}
	        	catch (Throwable E) {
	        		TGeoLogApplication.Log_WriteError(E);
	        	}
			}
		};
	}

	public void ComponentTypedDataFile_Process(TComponentTypedDataFile ComponentTypedDataFile) {
		if (ComponentTypedDataFile.IsLoaded()) {
			ComponentTypedDataFile_Open(ComponentTypedDataFile);
		} else {
			if (ComponentTypedDataFileLoading != null)
				ComponentTypedDataFileLoading.Cancel();
			ComponentTypedDataFileLoading = new TComponentTypedDataFileLoading(ComponentTypedDataFile, MESSAGE_TYPEDDATAFILE_LOADED);
		}
	}
	
	public void ComponentTypedDataFile_Open(final TComponentTypedDataFile ComponentTypedDataFile) {
		try {
			if (ComponentTypedDataFile.FileIsEmpty())
				throw new Exception(getString(R.string.SThereIsNoDataYet)); //. =>
			//.
			TUserAgent UserAgent = TUserAgent.GetUserAgent();
			if (UserAgent == null)
				throw new Exception(getString(R.string.SUserAgentIsNotInitialized)); //. =>
			//.
			Intent intent = null;
			switch (ComponentTypedDataFile.DataType) {

			case SpaceDefines.TYPEDDATAFILE_TYPE_DocumentName:
			case SpaceDefines.TYPEDDATAFILE_TYPE_Document:
				try {
					if (ComponentTypedDataFile.DataFormat.equals(SpaceDefines.TYPEDDATAFILE_TYPE_Document_FORMAT_TXT)) {
						String Text = new String(ComponentTypedDataFile.GetFileData(),"windows-1251");
						byte[] TextData = Text.getBytes("utf-16");
						// .
						File TempFile = ComponentTypedDataFile.GetTempFile();
						FileOutputStream fos = new FileOutputStream(TempFile);
						try {
							fos.write(TextData, 0, TextData.length);
						} finally {
							fos.close();
						}
						// . open appropriate extent
						intent = new Intent();
						intent.setDataAndType(Uri.fromFile(TempFile), "text/plain");
					}
					else
						if (ComponentTypedDataFile.DataFormat.equals(SpaceDefines.TYPEDDATAFILE_TYPE_Document_FORMAT_XML)) {
							final TComponentFunctionality CF = UserAgent.User().Space.TypesSystem.TComponentFunctionality_Create(ComponentTypedDataFile.DataComponentType,ComponentTypedDataFile.DataComponentID);
							if (CF != null)
								try {
									int Version = CF.ParseFromXMLDocument(ComponentTypedDataFile.GetFileData());
									if (Version > 0) 
										switch (CF.TypeFunctionality.idType) {
										
										case SpaceDefines.idTDATAFile:
											TDATAFileFunctionality DFF = (TDATAFileFunctionality)CF;
											DFF.Open(TComponentTypedDataFilesPanel.this);
											return; // . ->

										case SpaceDefines.idTPositioner:
											TPositionerFunctionality PF = (TPositionerFunctionality)CF;
											//.
											TReflectorComponent Reflector = Component;
											if (Reflector == null) 
												throw new Exception(getString(R.string.SReflectorIsNull)); //. =>
											//.
											TLocation P = new TLocation(PF._Name);
											P.RW.Assign(Reflector.ReflectionWindow.GetWindow());
											P.RW.X0 = PF._X0; P.RW.Y0 = PF._Y0;
											P.RW.X1 = PF._X1; P.RW.Y1 = PF._Y1;
											P.RW.X2 = PF._X2; P.RW.Y2 = PF._Y2;
											P.RW.X3 = PF._X3; P.RW.Y3 = PF._Y3;
											P.RW.BeginTimestamp = PF._Timestamp; P.RW.EndTimestamp = PF._Timestamp;
											P.RW.Normalize();
											intent = new Intent(TComponentTypedDataFilesPanel.this,TReflector.class);
											intent.putExtra("Reason", TReflectorComponent.REASON_SHOWLOCATIONWINDOW);
											intent.putExtra("LocationWindow", P.ToByteArray());
											TComponentTypedDataFilesPanel.this.startActivity(intent);
											return; // . ->

										default:
											TComponentFunctionality.TPropsPanel PropsPanel = CF.TPropsPanel_Create(TComponentTypedDataFilesPanel.this);
											if (PropsPanel != null)
												startActivity(PropsPanel.PanelActivity);
											return; // . ->
										}
								}
							finally {
								CF.Release();
							}
						}
				} catch (Exception E) {
					Toast.makeText(
							TComponentTypedDataFilesPanel.this,
							getString(R.string.SErrorOfPreparingDataFile)
									+ ComponentTypedDataFile.FileName(),
							Toast.LENGTH_LONG).show();
					return; // . ->
				}
				break; // . >

			case SpaceDefines.TYPEDDATAFILE_TYPE_ImageName:
			case SpaceDefines.TYPEDDATAFILE_TYPE_Image:
				try {
					if (ComponentTypedDataFile.DataFormat.toLowerCase(Locale.ENGLISH).equals("."+TDrawingDefines.FileExtension)) {
			    		intent = new Intent(this, TDrawingEditor.class);
			  		    intent.putExtra("FileName", ComponentTypedDataFile.GetFile().getAbsolutePath()); 
			  		    intent.putExtra("ReadOnly", true); 
			  		    startActivity(intent);
			  		    //.
						return; // . ->
					}
					else { 
			    		intent = new Intent(this, TImageViewerPanel.class);
			  		    intent.putExtra("FileName", ComponentTypedDataFile.GetFile().getAbsolutePath()); 
			  		    startActivity(intent);
			  		    //.
						return; // . ->
					}
				} catch (Exception E) {
					Toast.makeText(
							TComponentTypedDataFilesPanel.this,
							getString(R.string.SErrorOfPreparingDataFile)
									+ ComponentTypedDataFile.FileName(),
							Toast.LENGTH_SHORT).show();
					return; // . ->
				}

			case SpaceDefines.TYPEDDATAFILE_TYPE_AudioName:
			case SpaceDefines.TYPEDDATAFILE_TYPE_Audio:
				try {
					// . open appropriate extent
					intent = new Intent();
					intent.setDataAndType(
							Uri.fromFile(ComponentTypedDataFile.GetFile()),
							"audio/*");
				} catch (Exception E) {
					Toast.makeText(
							TComponentTypedDataFilesPanel.this,
							getString(R.string.SErrorOfPreparingDataFile)
									+ ComponentTypedDataFile.FileName(),
							Toast.LENGTH_SHORT).show();
					return; // . ->
				}
				break; // . >

			case SpaceDefines.TYPEDDATAFILE_TYPE_VideoName:
			case SpaceDefines.TYPEDDATAFILE_TYPE_Video:
				try {
					// . open appropriate extent
					intent = new Intent();
					intent.setDataAndType(
							Uri.fromFile(ComponentTypedDataFile.GetFile()),
							"video/*");
				} catch (Exception E) {
					Toast.makeText(
							TComponentTypedDataFilesPanel.this,
							getString(R.string.SErrorOfPreparingDataFile)
									+ ComponentTypedDataFile.FileName(),
							Toast.LENGTH_SHORT).show();
					return; // . ->
				}
				break; // . >

			case SpaceDefines.TYPEDDATAFILE_TYPE_MeasurementName:
			case SpaceDefines.TYPEDDATAFILE_TYPE_Measurement:
				try {
					String MeasurementID = Integer.toString(ComponentTypedDataFile.DataComponentType)+"_"+Long.toString(ComponentTypedDataFile.DataComponentID);
					//. open appropriate extent
		            Intent ProcessorPanel = new Intent(this, TMeasurementProcessorPanel.class);
		            ProcessorPanel.putExtra("MeasurementDatabaseFolder",TGeoLogApplication.GetTempFolder());
		            ProcessorPanel.putExtra("MeasurementID",MeasurementID);
		            ProcessorPanel.putExtra("MeasurementDataFile",ComponentTypedDataFile.GetFile().getAbsolutePath());
		            ProcessorPanel.putExtra("MeasurementStartPosition",0);
		            startActivity(ProcessorPanel);	            	
		  		    //.
					return; // . ->
				} catch (Exception E) {
					Toast.makeText(this, getString(R.string.SErrorOfPreparingDataFile)+ComponentTypedDataFile.FileName(), Toast.LENGTH_SHORT).show();
					return; // . ->
				}

			default:
				Toast.makeText(TComponentTypedDataFilesPanel.this, R.string.SUnknownDataFileFormat,
						Toast.LENGTH_LONG).show();
				return; // . ->
			}
			if (intent != null) {
				intent.setAction(android.content.Intent.ACTION_VIEW);
				startActivity(intent);
			}
		} catch (Exception E) {
			Toast.makeText(this, E.getMessage(),Toast.LENGTH_SHORT).show();
		}
	}

	public Handler MessageHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
        	try {
    			switch (msg.what) {

    			case MESSAGE_TYPEDDATAFILE_LOADED:
    				if (!flExists)
    	            	break; //. >
    				TComponentTypedDataFile ComponentTypedDataFile = (TComponentTypedDataFile) msg.obj;
    				if (ComponentTypedDataFile != null)
    					ComponentTypedDataFile_Open(ComponentTypedDataFile);
    				// .
    				break; // . >				
    			}
        	}
        	catch (Throwable E) {
        		TGeoLogApplication.Log_WriteError(E);
        	}
		}
	};
	
	private void CreateNewComponent() {
		if (DataFiles == null)
			return; //. ->
		TComponentTypedDataFile RootItem = DataFiles.GetRootItem();
		if (RootItem == null)
			return; //. ->
		//.
    	Intent intent = new Intent(this, TComponentCreatingPanel.class);
		intent.putExtra("idTOwner", RootItem.DataComponentType);
		intent.putExtra("idOwner", RootItem.DataComponentID);
    	startActivityForResult(intent, REQUEST_ADD_COMPONENT);
	}
}
