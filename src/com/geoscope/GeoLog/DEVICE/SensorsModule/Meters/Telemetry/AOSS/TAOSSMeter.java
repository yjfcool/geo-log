package com.geoscope.GeoLog.DEVICE.SensorsModule.Meters.Telemetry.AOSS;

import java.io.IOException;

import com.geoscope.GeoLog.DEVICE.SensorsModule.TSensorsModule;
import com.geoscope.GeoLog.DEVICE.SensorsModule.Measurement.Telemetry.TLR.TMeasurement;
import com.geoscope.GeoLog.DEVICE.SensorsModule.Measurements.TSensorsModuleMeasurements;
import com.geoscope.GeoLog.DEVICE.SensorsModule.Meter.TSensorMeterDescriptor;
import com.geoscope.GeoLog.DEVICE.SensorsModule.Meter.Telemetry.TLR.TTLRMeter;
import com.geoscope.GeoLog.DEVICE.SensorsModule.Model.Data.Stream.Channels.Telemetry.TLR.TTLRChannel;

public class TAOSSMeter extends TTLRMeter {

	public static final String TypeID = "Telemetry.AOSS";
	public static final String ContainerTypeID = "";
	//.
	public static final String Name = "Android OS state";
	public static final String Info = "Telemetry";
	
	public static class TMyProfile extends TProfile {
	}
	
	
	public TAOSSMeter(TSensorsModule pSensorsModule, String pID, String pProfileFolder) throws Exception {
		super(pSensorsModule, new TSensorMeterDescriptor(TypeID+"."+pID, TypeID,ContainerTypeID, Name,Info), TMyProfile.class, pProfileFolder);
	}
	
	@Override
	public String GetTypeID() {
		return TypeID;
	}
	
	@Override
	protected TTLRChannel GetSourceTLRChannel() throws Exception {
		if (SensorsModule.InternalSensorsModule.AOSSChannel == null)
			throw new IOException("no origin channel"); //. =>
		if (!SensorsModule.InternalSensorsModule.AOSSChannel.Enabled)
			throw new IOException("the origin channel is disabled"); //. =>
		return (TTLRChannel)SensorsModule.InternalSensorsModule.AOSSChannel.DestinationChannel_Get(); 	
	}
	
	@Override
	protected TMeasurement CreateTLRMeasurement() throws Exception {
		return new com.geoscope.GeoLog.DEVICE.SensorsModule.Measurements.Telemetry.AOSS.TMeasurement(SensorsModule.Device.idGeographServerObject, TSensorsModuleMeasurements.DataBaseFolder, TSensorsModuleMeasurements.Domain, TSensorsModuleMeasurements.CreateNewMeasurement(), com.geoscope.GeoLog.DEVICE.SensorsModule.Measurement.Model.Data.Stream.Channels.TChannelsProvider.Instance);
	}
}
