package com.geoscope.GeoEye.Space.TypesSystem.GeoSpace;

import java.io.File;

import com.geoscope.GeoEye.Space.TypesSystem.TTypeSystem;
import com.geoscope.GeoEye.Space.TypesSystem.TTypesSystem;

public class TSystemTGeoSpace extends TTypeSystem {

	public static String ContextFolder = TTypesSystem.ContextFolder+"/"+"GeoSpace";
	
	public TSystemTGeoSpace(TTypesSystem pTypesSystem) {
		super(pTypesSystem);
	}

	@Override
	public String Context_GetFolder() {
		File CF = new File(ContextFolder);
		CF.mkdirs();
		return ContextFolder;
	}	
}