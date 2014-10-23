package com.geoscope.GeoEye.Space.TypesSystem.CoComponent.ObjectModel.GeoMonitoredObject1.DEVICE.ControlsModule.Model.Data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.geoscope.Classes.Data.Containers.TDataConverter;
import com.geoscope.Classes.Data.Stream.Channel.TChannel;
import com.geoscope.Classes.IO.Net.TNetworkConnection;
import com.geoscope.Classes.MultiThreading.TCanceller;

public class TStreamChannel extends TChannel {

	protected OutputStream ConnectionOutputStream = null;
	protected InputStream ConnectionInputStream = null;
	
	public void DoStreaming(InputStream pInputStream, OutputStream pOutputStream, TCanceller Canceller) throws Exception {
	}
	
	public int ParseFromByteArrayAndProcess(byte[] BA, int Idx) throws Exception {
		return Idx;
	}

	public synchronized void SetConnection(OutputStream pChannelOutputStream, InputStream pChannelInputStream) {
		ConnectionOutputStream = pChannelOutputStream;
		ConnectionInputStream = pChannelInputStream;
	}
	
	public synchronized void ClearConnection() {
		ConnectionOutputStream = null;
		ConnectionInputStream = null;
	}
	
	private void CheckCommandResult(int Descriptor) throws Exception {
		if (Descriptor < 0)
			throw new Exception("error of processing command, RC: "+Integer.toString(Descriptor)); //. =>
	}
	
	public synchronized byte[] ProcessCommand(byte[] Command) throws Exception {
		if (ConnectionOutputStream == null)
			throw new IOException("channel connection does not exist"); //. =>
		//.
		byte[] DescriptorBA = new byte[4];
		int Descriptor;
		//.
		ConnectionOutputStream.write(Command);
		//. get and check result
		ConnectionInputStream.read(DescriptorBA);
		Descriptor = TDataConverter.ConvertLEByteArrayToInt32(DescriptorBA,0);
		CheckCommandResult(Descriptor);
		//.
		if (Descriptor == 0)
			return null; //. ->
		//.
		byte[] Result = new byte[Descriptor];
        if (TNetworkConnection.InputStream_ReadData(ConnectionInputStream, Result,Descriptor) <= 0) 
			throw new IOException("channel connection is closed unexpectedly"); //. =>
		return Result;
	}
}