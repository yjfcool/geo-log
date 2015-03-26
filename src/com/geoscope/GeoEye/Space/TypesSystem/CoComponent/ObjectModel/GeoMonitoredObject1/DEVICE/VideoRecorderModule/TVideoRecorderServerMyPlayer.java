package com.geoscope.GeoEye.Space.TypesSystem.CoComponent.ObjectModel.GeoMonitoredObject1.DEVICE.VideoRecorderModule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.geoscope.GeoEye.R;

@SuppressLint("HandlerLeak")
public class TVideoRecorderServerMyPlayer extends Activity {
    
	@SuppressWarnings("unused")
	private boolean flExists = false;
	//.
	@SuppressWarnings("unused")
	private boolean IsInFront = false;
	//.
	private double MeasurementStartPosition = 0.0;
	private String MeasurementDatabaseFolder = null;
	private String MeasurementID = null;
	//.
	private FrameLayout ProcessorLayout;
	//.
	private TVideoRecorderServerMyPlayerComponent ProcessorComponent = null;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //.
        Bundle extras = getIntent().getExtras(); 
        if (extras != null) {
        	MeasurementDatabaseFolder = extras.getString("MeasurementDatabaseFolder");
        	MeasurementID = extras.getString("MeasurementID");
        	MeasurementStartPosition = extras.getDouble("MeasurementStartPosition");
        }
        //.
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//.
        setContentView(R.layout.video_recorder_server_myplayer);
        //.
        ProcessorLayout = (FrameLayout)findViewById(R.id.VideoRecorderServerMyPlayerLayout);
        //.
        try {
			ProcessorComponent = new TVideoRecorderServerMyPlayerComponent(this, ProcessorLayout, new TVideoRecorderServerMyPlayerComponent.TOnSurfaceChangedHandler() {
				
				@Override
				public void DoOnSurfaceChanged(SurfaceHolder surface) {
					try {
						Setup();
					} catch (Exception E) {
						String S = E.getMessage();
						if (S == null)
							S = E.getClass().getName();
						Toast.makeText(TVideoRecorderServerMyPlayer.this, S, Toast.LENGTH_LONG).show();
					}
				}
			}, new TVideoRecorderServerMyPlayerComponent.TOnProgressHandler() {
				
				@Override
				public void DoOnProgress(double ProgressFactor) {
					if (ProcessorComponent != null) {
						MeasurementStartPosition = ProcessorComponent.MeasurementDescriptor.Duration()*ProgressFactor; 
					}
				}
			});
		} catch (Exception E) {
			String S = E.getMessage();
			if (S == null)
				S = E.getClass().getName();
			Toast.makeText(this, S, Toast.LENGTH_LONG).show();
			//.
			finish();
			//.
			return; //. ->
		}
		//.
		setResult(RESULT_CANCELED);
		//.
		flExists = true;
    }
	
    public void onDestroy() {
    	flExists = false;
    	//.
    	try {
    		if (ProcessorComponent != null) {
    			ProcessorComponent.Destroy();
    			ProcessorComponent = null;
    		}
		} catch (Exception E) {
			String S = E.getMessage();
			if (S == null)
				S = E.getClass().getName();
			Toast.makeText(this, S, Toast.LENGTH_LONG).show();
		}
    	//.
		super.onDestroy();
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		IsInFront = true;
	}

    @Override
	protected void onPause() {
		super.onPause();
		IsInFront = false;
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	public void onStart() {
    	super.onStart();
    }
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		DoOnExit();
		//.
		super.onBackPressed();
	}
	
	private void Setup() throws Exception {
		ProcessorComponent.Setup(MeasurementDatabaseFolder, MeasurementID);
		//.
		ProcessorComponent.SetPosition(MeasurementStartPosition, 0/*Delay, ms*/, false);
	}
	
	private void DoOnExit() {
		if ((ProcessorComponent != null) && ProcessorComponent.flInitialized) {
			double MeasurementCurrentPosition = ProcessorComponent.MeasurementDescriptor.StartTimestamp+ProcessorComponent.MeasurementDescriptor.Duration()*ProcessorComponent.MeasurementCurrentPositionFactor;
	    	Intent intent = getIntent();
	    	intent.putExtra("MeasurementCurrentPosition",MeasurementCurrentPosition);
	        //.
	    	setResult(RESULT_OK,intent);
		}
	}
}
