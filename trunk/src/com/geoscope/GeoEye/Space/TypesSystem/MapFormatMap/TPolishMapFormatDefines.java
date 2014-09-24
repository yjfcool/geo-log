package com.geoscope.GeoEye.Space.TypesSystem.MapFormatMap;

import com.geoscope.GeoEye.Space.TypesSystem.MapFormatObject.PMFPOI;

public final class TPolishMapFormatDefines {

	public static final int FormatID = 1;
	//.
	public static final int OBJECTKIND_UNKNOWN = 0;
	public static final int OBJECTKIND_POLYLINE = 1;
	public static final int OBJECTKIND_POLYGON = 2;
	public static final int OBJECTKIND_POI = 3;

    public static final PMFPOI[] POITypes = 
    {
        new PMFPOI(0x2c04,"<�����������>"),
        new PMFPOI(0x100,"��������� (����� 10 ���.)"),
        new PMFPOI(0x200,"��������� (5-10 ���.)"),
        new PMFPOI(0x300,"������� ����� (2-5 ���.)"),
        new PMFPOI(0x400,"������� ����� (1-2 ���.)"),
        new PMFPOI(0x500,"������� ����� (0.5-1 ���.)"),
        new PMFPOI(0x600,"����� (200-500 ���.)"),
        new PMFPOI(0x700,"����� (100-200 ���.)"),
        new PMFPOI(0x800,"����� (50-100 ���.)"),
        new PMFPOI(0x900,"����� (20-50 ���.)"),
        new PMFPOI(0xa00,"���������� ����� (10-20 ���.)"),
        new PMFPOI(0xb00,"���������� ����� (4-10 ���.)"),
        new PMFPOI(0xc00,"���������� ����� (2-4 ���.)"),
        new PMFPOI(0xd00,"���������� ����� (1-2 ���.)"),
        new PMFPOI(0xe00,"������� (500-1000)"),
        new PMFPOI(0xf00,"������� (200-500)"),
        new PMFPOI(0x1000,"������� (100-200)"),
        new PMFPOI(0x1100,"������� (����� 100)"),
        new PMFPOI(0x1400,"�������� �������� �����������"),
        new PMFPOI(0x1500,"�������� ������ �����������"),
        new PMFPOI(0x1e00,"�������� �������, ���������, �����"),
        new PMFPOI(0x1f00,"�������� ������, ������"),
        new PMFPOI(0x2800,"�������"),
        new PMFPOI(0x1600,"����"),
        new PMFPOI(0x1601,"�������� �����"),
        new PMFPOI(0x1602,"���������"),
        new PMFPOI(0x1603,"��������"),
        new PMFPOI(0x1604,"������� ���� (������� �����������)"),
        new PMFPOI(0x1605,"������� ���� (������� �������)"),
        new PMFPOI(0x1606,"������� ���� (����� ����)"),
        new PMFPOI(0x1607,"������������ ���� �����"),
        new PMFPOI(0x1608,"������������ ���� �������"),
        new PMFPOI(0x1609,"������������ ���� �������"),
        new PMFPOI(0x160a,"������������ ���� ������"),
        new PMFPOI(0x160b,"������������ ���� ������"),
        new PMFPOI(0x160c,"������������ ���� ���������"),
        new PMFPOI(0x160d,"������������ ���� ������������"),
        new PMFPOI(0x160e,"���������� ����"),
        new PMFPOI(0x160f,"���������� ���� �����"),
        new PMFPOI(0x1610,"���������� ���� �������"),
        new PMFPOI(0x1611,"���������� ���� �������"),
        new PMFPOI(0x1612,"���������� ���� ������"),
        new PMFPOI(0x1613,"���������� ���� ���������"),
        new PMFPOI(0x1614,"���������� ���� ����������"),
        new PMFPOI(0x1615,"���������� ���� �����"),
        new PMFPOI(0x1616,"���������� ���� ������������"),
        new PMFPOI(0x1c00,"������ ��������"),
        new PMFPOI(0x1c01,"������� ���������������"),
        new PMFPOI(0x1c02,"����������� �������, �������"),
        new PMFPOI(0x1c03,"����������� �������, �� �������"),
        new PMFPOI(0x1c04,"�������, ��������� ������"),
        new PMFPOI(0x1c05,"��������, ������� ��� ������� ����"),
        new PMFPOI(0x1c06,"�������� �� ������ ����"),
        new PMFPOI(0x1c07,"�������� ���� ������ ����"),
        new PMFPOI(0x1c08,"��������, ��������� ������"),
        new PMFPOI(0x1c09,"���� �� ������ ����"),
        new PMFPOI(0x1c0a,"��������� ����"),
        new PMFPOI(0x1c0b,"������� �������"),
        new PMFPOI(0x1d00,"���������� ��������"),
        new PMFPOI(0x2000,"����� � �����"),
        new PMFPOI(0x2100,"����� � ����� � ���������������"),
        new PMFPOI(0x210F,"����� � �������"),
        new PMFPOI(0x2110,"����� � ���������������"),
        new PMFPOI(0x2200,"����� � �������"),
        new PMFPOI(0x2300,"����� � ��������"),
        new PMFPOI(0x2400,"����� � ������� �������"),
        new PMFPOI(0x2500,"����� �������"),
        new PMFPOI(0x2600,"����� � ����������"),
        new PMFPOI(0x2700,"����� � �����"),
        new PMFPOI(0x2a00,"����������� �������"),
        new PMFPOI(0x2a01,"�������� (������������ �����)"),
        new PMFPOI(0x2a02,"�������� (��������� �����)"),
        new PMFPOI(0x2a03,"�������� (������)"),
        new PMFPOI(0x2a04,"�������� (��������� �����)"),
        new PMFPOI(0x2a05,"�������� (����������, �����, ��������)"),
        new PMFPOI(0x2a06,"�������� (����������������� �����)"),
        new PMFPOI(0x2a07,"�������� �������� �������"),
        new PMFPOI(0x2a08,"�������� (����������� �����)"),
        new PMFPOI(0x2a09,"�������� (������������ �����)"),
        new PMFPOI(0x2a0a,"��������"),
        new PMFPOI(0x2a0b,"�������� (������������)"),
        new PMFPOI(0x2a0c,"�������� (�����)"),
        new PMFPOI(0x2a0d,"������� (������������ �������)"),
        new PMFPOI(0x2a0e,"����"),
        new PMFPOI(0x2a0f,"�������� (����������� �����)"),
        new PMFPOI(0x2a10,"�������� (�������� �����)"),
        new PMFPOI(0x2a11,"�������� (���������� ��������� �����)"),
        new PMFPOI(0x2a11,"�������� (���������� ��������� �����)"),
        new PMFPOI(0x2a12,"����������� ������� ��������"),
        new PMFPOI(0x2900,"������ ������"),
        new PMFPOI(0x2b00,"���������"),
        new PMFPOI(0x2b01,"����� ��� ������"),
        new PMFPOI(0x2b02,"����� � ���������"),
        new PMFPOI(0x2b03,"�������"),
        new PMFPOI(0x2b04,"��������� �����"),
        new PMFPOI(0x2c00,"������ ��������, ������"),
        new PMFPOI(0x2c01,"����"),
        new PMFPOI(0x2c02,"�����"),
        new PMFPOI(0x2c03,"����������"),
        new PMFPOI(0x2c04,"���������������������"),
        new PMFPOI(0x2c05,"�����"),
        new PMFPOI(0x2c06,"����/���"),
        new PMFPOI(0x2c07,"�������/��������"),
        new PMFPOI(0x2c08,"�������"),
        new PMFPOI(0x2c09,"���"),
        new PMFPOI(0x2c0a,"������ ��������"),
        new PMFPOI(0x2c0b,"����/������/��������"),
        new PMFPOI(0x2d00,"��������������� ���������"),
        new PMFPOI(0x2d01,"�����"),
        new PMFPOI(0x2d02,"���/������ ����"),
        new PMFPOI(0x2d03,"���������"),
        new PMFPOI(0x2d04,"������"),
        new PMFPOI(0x2d05,"�����-����"),
        new PMFPOI(0x2d06,"������ �����/������"),
        new PMFPOI(0x2d07,"�������-�����"),
        new PMFPOI(0x2d08,"�����"),
        new PMFPOI(0x2d09,"�������"),
        new PMFPOI(0x2d0a,"��������/������-�����"),
        new PMFPOI(0x2d0b,"���������� ��������"),
        new PMFPOI(0x2e00,"�������� ������"),
        new PMFPOI(0x2e01,"���������"),
        new PMFPOI(0x2e02,"�������"),
        new PMFPOI(0x2e03,"�������� �����"),
        new PMFPOI(0x2e04,"�������� �����"),
        new PMFPOI(0x2e05,"������"),
        new PMFPOI(0x2e06,"������ ������������� ������"),
        new PMFPOI(0x2e07,"������"),
        new PMFPOI(0x2e08,"������ ��� ���� � ����"),
        new PMFPOI(0x2e09,"������"),
        new PMFPOI(0x2e0a,"������������������ �������"),
        new PMFPOI(0x2e0b,"����������/��"),
        new PMFPOI(0x2f00,"������"),
        new PMFPOI(0x2f01,"���"),
        new PMFPOI(0x2f02,"������ �����������"),
        new PMFPOI(0x2f03,"����������"),
        new PMFPOI(0x2f04,"����������"),
        new PMFPOI(0x2f05,"�������� ���������"),
        new PMFPOI(0x2f06,"����"),
        new PMFPOI(0x2f07,"�����������"),
        new PMFPOI(0x2f08,"�������/��������� ��������� ����������"),
        new PMFPOI(0x2f09,"������ �����, �������, ���"),
        new PMFPOI(0x2f0a,"��������� ������, ���������"),
        new PMFPOI(0x2f0b,"�����������"),
        new PMFPOI(0x2f0c,"����� ������, ���������� ��� ��������"),
        new PMFPOI(0x2f0d,"��������"),
        new PMFPOI(0x2f0e,"���������"),
        new PMFPOI(0x2f0f,"����� ����� Garmin"),
        new PMFPOI(0x2f10,"������ ���� (���������, ���������)"),
        new PMFPOI(0x2f11,"������-������"),
        new PMFPOI(0x2f12,"����� �����"),
        new PMFPOI(0x2f13,"���� �������"),
        new PMFPOI(0x2f14,"�����"),
        new PMFPOI(0x2f15,"������������ ������"),
        new PMFPOI(0x2f16,"������� ����������"),
        new PMFPOI(0x2f17,"��������� ������������� ����������"),
        new PMFPOI(0x3000,"��������������� ��� ���������� ������"),
        new PMFPOI(0x3001,"��������� �������"),
        new PMFPOI(0x3002,"��������"),
        new PMFPOI(0x3003,"�����"),
        new PMFPOI(0x3004,"���"),
        new PMFPOI(0x3005,"��������� ��� ���������� ������������ �����������"),
        new PMFPOI(0x3006,"����������� �����"),
        new PMFPOI(0x3007,"��������������� ����������"),
        new PMFPOI(0x3008,"�������� �����"),
        new PMFPOI(0x5900,"��������"),
        new PMFPOI(0x5901,"������� ��������"),
        new PMFPOI(0x5902,"������� ��������"),
        new PMFPOI(0x5903,"����� ��������"),
        new PMFPOI(0x5904,"����������� ��������"),
        new PMFPOI(0x5905,"��������"),
        new PMFPOI(0x6400,"������������� ����������"),
        new PMFPOI(0x6401,"����"),
        new PMFPOI(0x6402,"������"),
        new PMFPOI(0x6403,"��������"),
        new PMFPOI(0x6404,"����/������/��������"),
        new PMFPOI(0x6405,"������������ ������"),
        new PMFPOI(0x6406,"�����������, ���������, �������"),
        new PMFPOI(0x6407,"�������"),
        new PMFPOI(0x6408,"��������"),
        new PMFPOI(0x6409,"�������, ����������"),
        new PMFPOI(0x640a,"���������"),
        new PMFPOI(0x640b,"������� ������"),
        new PMFPOI(0x640c,"�����, ������"),
        new PMFPOI(0x640d,"������������� �����"),
        new PMFPOI(0x640e,"����"),
        new PMFPOI(0x640f,"�����"),
        new PMFPOI(0x6410,"�����"),
        new PMFPOI(0x6411,"�����, �����"),
        new PMFPOI(0x6412,"�����"),
        new PMFPOI(0x6413,"�������"),
        new PMFPOI(0x6414,"�������� ����, ������, �������"),
        new PMFPOI(0x6415,"����������� �����"),
        new PMFPOI(0x6416,"����������"),
        new PMFPOI(0x6500,"������ �����������"),
        new PMFPOI(0x6501,"������, �������� �����"),
        new PMFPOI(0x6502,"������"),
        new PMFPOI(0x6503,"�����"),
        new PMFPOI(0x6504,"�������� ����"),
        new PMFPOI(0x6505,"������������� �����"),
        new PMFPOI(0x6506,"������"),
        new PMFPOI(0x6507,"�����"),
        new PMFPOI(0x6508,"�������"),
        new PMFPOI(0x6509,"������"),
        new PMFPOI(0x650a,"������"),
        new PMFPOI(0x650b,"������"),
        new PMFPOI(0x650c,"������"),
        new PMFPOI(0x650d,"�����"),
        new PMFPOI(0x650e,"������"),
        new PMFPOI(0x650f,"�������������"),
        new PMFPOI(0x6510,"����"),
        new PMFPOI(0x6511,"������"),
        new PMFPOI(0x6512,"�����"),
        new PMFPOI(0x6513,"������"),
        new PMFPOI(0x6600,"��������� �������� ������"),
        new PMFPOI(0x6601,"����"),
        new PMFPOI(0x6602,"�����, �������"),
        new PMFPOI(0x6603,"���������"),
        new PMFPOI(0x6604,"�����"),
        new PMFPOI(0x6605,"������, �����"),
        new PMFPOI(0x6606,"���"),
        new PMFPOI(0x6607,"����"),
        new PMFPOI(0x6608,"������"),
        new PMFPOI(0x6609,"�����"),
        new PMFPOI(0x660a,"���"),
        new PMFPOI(0x660b,"������, ��������"),
        new PMFPOI(0x660c,"����� ������"),
        new PMFPOI(0x660d,"��������"),
        new PMFPOI(0x660e,"����"),
        new PMFPOI(0x660f,"�����, �������"),
        new PMFPOI(0x6610,"�������"),
        new PMFPOI(0x6611,"�������"),
        new PMFPOI(0x6612,"����������"),
        new PMFPOI(0x6613,"������"),
        new PMFPOI(0x6614,"�����"),
        new PMFPOI(0x6615,"�����"),
        new PMFPOI(0x6616,"������� ����� ��� ����"),
        new PMFPOI(0x6617,"������"),
        new PMFPOI(0x6618,"���"),
        new PMFPOI(0x5a00,"������������ �����"),
        new PMFPOI(0x5b00,"�������"),
        new PMFPOI(0x5c00,"����� ��� ��������"),
        new PMFPOI(0x5d00,"������� ���� (������� �������)"),
        new PMFPOI(0x5e00,"������� ���� (������� �����������)"),
        new PMFPOI(0x6000,"����������������"),
        new PMFPOI(0x6100,"���"),
        new PMFPOI(0x6200,"������� �������"),
        new PMFPOI(0x6300,"������� ������"),
        new PMFPOI(0x4000,"�����-����"),
        new PMFPOI(0x4100,"����� ��� �������"),
        new PMFPOI(0x4200,"�������, ���������"),
        new PMFPOI(0x4300,"��������"),
        new PMFPOI(0x4400,"���"),
        new PMFPOI(0x4500,"��������"),
        new PMFPOI(0x4600,"���"),
        new PMFPOI(0x4700,"�������� ������"),
        new PMFPOI(0x4800,"�������"),
        new PMFPOI(0x4900,"����"),
        new PMFPOI(0x4a00,"����� ��� �������"),
        new PMFPOI(0x4b00,"��������"),
        new PMFPOI(0x4c00,"����������"),
        new PMFPOI(0x4d00,"�����������"),
        new PMFPOI(0x4e00,"������"),
        new PMFPOI(0x4f00,"���"),
        new PMFPOI(0x5000,"�������� ����"),
        new PMFPOI(0x5100,"�������"),
        new PMFPOI(0x5200,"�������� ���"),
        new PMFPOI(0x5300,"������ ����"),
        new PMFPOI(0x5400,"����� ��� �������"),
        new PMFPOI(0x5500,"�����, �������"),
        new PMFPOI(0x5600,"��������� ����"),
        new PMFPOI(0x5700,"������� ����"),
        new PMFPOI(0x5800,"������������ ������")
    };
    
    public static String GetObjectTypeName(int KindID, int TypeID) {
    	switch (KindID) {
    	case OBJECTKIND_POI:
    		for (int I = 0; I < POITypes.length; I++)
    			if (POITypes[I].TypeID == TypeID)
    				return POITypes[I].TypeName;
    		return ""; //. ->
    		
    	default:
    		return ""; //. ->
    	}
    }
}