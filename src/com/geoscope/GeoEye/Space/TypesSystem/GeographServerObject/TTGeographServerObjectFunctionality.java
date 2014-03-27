package com.geoscope.GeoEye.Space.TypesSystem.GeographServerObject;

import com.geoscope.GeoEye.Space.Defines.SpaceDefines;
import com.geoscope.GeoEye.Space.Defines.TGeoScopeServer;
import com.geoscope.GeoEye.Space.Functionality.TComponentFunctionality;
import com.geoscope.GeoEye.Space.Functionality.TTypeFunctionality;

public class TTGeographServerObjectFunctionality extends TTypeFunctionality {

	public TTGeographServerObjectFunctionality(TGeoScopeServer pServer) {
		super(pServer,SpaceDefines.idTGeoGraphServerObject);
	}

	public TComponentFunctionality TComponentFunctionality_Create(int idComponent) {
		return null;
	}
}
