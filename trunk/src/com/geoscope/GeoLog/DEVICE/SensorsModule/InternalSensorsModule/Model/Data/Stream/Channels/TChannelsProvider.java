package com.geoscope.GeoLog.DEVICE.SensorsModule.InternalSensorsModule.Model.Data.Stream.Channels;

import com.geoscope.GeoLog.DEVICE.SensorsModule.InternalSensorsModule.TInternalSensorsModule;
import com.geoscope.GeoLog.DEVICE.SensorsModule.InternalSensorsModule.Model.Data.TStreamChannel;
import com.geoscope.GeoLog.DEVICE.SensorsModule.InternalSensorsModule.Model.Data.Stream.Channels.AndroidState.ADS.TADSChannel;
import com.geoscope.GeoLog.DEVICE.SensorsModule.InternalSensorsModule.Model.Data.Stream.Channels.EnvironmentConditions.XENVC.TXENVCChannel;
import com.geoscope.GeoLog.DEVICE.SensorsModule.InternalSensorsModule.Model.Data.Stream.Channels.GeoLocation.GPS.TGPSChannel;
import com.geoscope.GeoLog.DEVICE.SensorsModule.InternalSensorsModule.Model.Data.Stream.Channels.Telemetry.TLR.TTLRChannel;
import com.geoscope.GeoLog.DEVICE.SensorsModule.InternalSensorsModule.Model.Data.Stream.Channels.UserMessaging.LUM.TLUMChannel;

public class TChannelsProvider extends com.geoscope.Classes.Data.Stream.Channel.TChannelProvider {

	private TInternalSensorsModule InternalSensorsModule;
	
	public TChannelsProvider(TInternalSensorsModule pInternalSensorsModule) {
		InternalSensorsModule = pInternalSensorsModule;
	}
	
	@Override
	public TStreamChannel GetChannel(String pTypeID) {
		if (TADSChannel.TypeID.equals(pTypeID))
			return (new TADSChannel(InternalSensorsModule)); // =>
		if (TXENVCChannel.TypeID.equals(pTypeID))
			return (new TXENVCChannel(InternalSensorsModule)); // =>
		if (TTLRChannel.TypeID.equals(pTypeID))
			return (new TTLRChannel(InternalSensorsModule)); // =>
		if (TLUMChannel.TypeID.equals(pTypeID))
			return (new TLUMChannel(InternalSensorsModule)); // =>
		if (TGPSChannel.TypeID.equals(pTypeID))
			return (new TGPSChannel(InternalSensorsModule)); // =>
		else
			return null;
	}	
}