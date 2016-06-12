package com.tangramfactory.smartweight.database;

import android.os.Environment;

import java.io.File;

public class PathConstants {

	public static final String BASE_TAG = "SmartWeightDatabase";

	public static final int DB_VERSION = 1;
	public static final String DB_NAME = "smartweight.s3db";
	public static final String DB_PATH = Environment.getDataDirectory().getPath() + Environment.getDataDirectory().getPath() + File.separator + "com.tangramfactory.smartweight"+ File.separator+"databases"+File.separator+DB_NAME;
}
