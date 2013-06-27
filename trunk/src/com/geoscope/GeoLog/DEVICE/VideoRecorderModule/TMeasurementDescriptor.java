package com.geoscope.GeoLog.DEVICE.VideoRecorderModule;

public class TMeasurementDescriptor {

	public String	ID = null;
	public short 	Mode = TVideoRecorderModule.MODE_UNKNOWN;
	public double 	StartTimestamp = 0.0;
	public double 	FinishTimestamp = 0.0;
	//.
	public int 		AudioFormat = 0;
	public int		AudioSPS = -1;
	public int		AudioPackets = -1;
	//.
	public int 		VideoFormat = 0;
	public int		VideoFPS = -1;
	public int		VideoPackets = -1;
	
	public TMeasurementDescriptor(String pID) {
		ID = pID;
	}
	
	public boolean IsStarted() {
		return (StartTimestamp != 0.0);
	}

	public boolean IsFinished() {
		return (FinishTimestamp != 0.0);
	}
	
	public double Duration() {
		return (FinishTimestamp-StartTimestamp);
	}

	public int DurationInMs() {
		return (int)(Duration()*24.0*3600.0*1000.0);
	}

	public long DurationInNs() {
		return (long)(Duration()*24.0*3600.0*1000000000.0);
	}
}
