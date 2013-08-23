package com.geoscope.GeoEye.Space.TypesSystem.GeographServerObject;

import java.io.File;

import com.geoscope.GeoEye.TReflector;
import com.geoscope.GeoEye.Space.TypesSystem.TTypeSystem;

public class TSystemTGeographServerObject extends TTypeSystem {

	public static String ContextFolder = TReflector.TypesSystemContextFolder+"/"+"GeographServerObject";
	
	@Override
	public String GetContextFolder() {
		File CF = new File(ContextFolder);
		CF.mkdirs();
		return ContextFolder;
	}	
}