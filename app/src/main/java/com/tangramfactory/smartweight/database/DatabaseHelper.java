/*
 ******************************************************************************
 * Parts of this code sample are licensed under Apache License, Version 2.0   *
 * Copyright (c) 2009, Android Open Handset Alliance. All rights reserved.    *
 *																			  *																			*
 * Except as noted, this code sample is offered under a modified BSD license. *
 * Copyright (C) 2010, Motorola Mobility, Inc. All rights reserved.           *
 * 																			  *
 * For more details, see MOTODEV_Studio_for_Android_LicenseNotices.pdf        * 
 * in your installation folder.                                               *
 ******************************************************************************
 */
package com.tangramfactory.smartweight.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;


public class DatabaseHelper extends SQLiteOpenHelper {

//	private static final String CLASSNAME = DatabaseHelper.class.getSimpleName();

	private final Context myContext;
	private volatile static DatabaseHelper mInstance;
	private static SQLiteDatabase db;
	private static String CLASSNAME = "DatabaseHelper";

	private DatabaseHelper(Context context)  throws Exception {
		super(context, PathConstants.DB_NAME, null, PathConstants.DB_VERSION);
		this.myContext = context;
		copyDatabaseFile();
	}

	private static void initialize(Context context) throws Exception {
		if (mInstance == null) {
			synchronized (DatabaseHelper.class) {
				if (mInstance == null) {
					Log.i(PathConstants.BASE_TAG, DatabaseHelper.CLASSNAME + " Try to create instance of database (" + PathConstants.DB_NAME + ")");
					mInstance = new DatabaseHelper(context);
					try {
						db = mInstance.getWritableDatabase();
						Log.i(PathConstants.BASE_TAG, "Creating or opening the database ( " + PathConstants.DB_NAME + " ).");
					} catch (SQLiteException se) {
						Log.e(PathConstants.BASE_TAG, "Cound not create and/or open the database ( " + PathConstants.DB_NAME + " ) that will be used for reading and writing.", se);
					}
					Log.i(PathConstants.BASE_TAG, DatabaseHelper.CLASSNAME + " instance of database (" + PathConstants.DB_NAME + ") created !");
				}
			}
		}
	}

	public static final DatabaseHelper getInstance(Context context) throws Exception {
		initialize(context);
		return mInstance;
	}

	public void close() {
		if (mInstance != null) {
			Log.i(PathConstants.BASE_TAG, DatabaseHelper.CLASSNAME + "Closing the database [ " + PathConstants.DB_NAME + " ].");
			db.close();
			mInstance = null;
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v(PathConstants.BASE_TAG, DatabaseHelper.CLASSNAME + "Create & Copy : " + PathConstants.DB_NAME+" version : "+db.getVersion());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(PathConstants.BASE_TAG, "onUpgrade oldVersion : "+oldVersion+" newVersion : "+newVersion);
		
		try {
			switch (oldVersion) {
			case 1:
				String sql_01 = " ALTER TABLE [RopeReport] ADD COLUMN [intervalTimeStamp] NUMERIC ";
				StringBuffer sql_02 = new StringBuffer();
				sql_02.append(" CREATE TABLE [Interval] ( 				   	");
				sql_02.append(" [timeStamp] NUMERIC PRIMARY KEY NOT NULL,	");
				sql_02.append(" [createDate] TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      				");
				sql_02.append(" [goalCount] NUMERIC,          				");
				sql_02.append(" [goalSet] NUMERIC,            				");
				sql_02.append(" [level] NUMERIC,              				");
				sql_02.append(" [success] BOOL )             				");
				
				db.execSQL(sql_01);
				db.execSQL(sql_02.toString());

			case 2:
				String sql = "ALTER TABLE [Interval] ADD COLUMN " + "isSecond" + " BOOL DEFAULT 0" ;
				db.execSQL(sql);
				break;

			case 3 :
				StringBuffer sql_03 = new StringBuffer();
				sql_03.append(" CREATE TABLE [UnderArmour] ( ");
				sql_03.append(" [id] integer primary key autoincrement,	");
				sql_03.append(" [startTime] NUMERIC, ");
				sql_03.append(" [calorie] NUMERIC, ");
				sql_03.append(" [duration] NUMERIC ) ");
				db.execSQL(sql_03.toString());
				break;

			case 4:
				String sql2 = "ALTER TABLE `UnderArmour` RENAME TO `WorkoutData`";
				db.execSQL(sql2);
				sql2 = "ALTER TABLE [WorkoutData] ADD COLUMN endTime NUMERIC";
				db.execSQL(sql2);
				sql2 = "ALTER TABLE [WorkoutData] ADD COLUMN count NUMERIC";
				db.execSQL(sql2);
				sql2 = "ALTER TABLE [WorkoutData] ADD COLUMN source TEXT";
				db.execSQL(sql2);
				break;

			default:
				break;
			}
		} catch (Exception e) {
			Log.e(PathConstants.BASE_TAG, e.getLocalizedMessage(), e);
		}
	}

	/****
	 * Method for select statements
	 * 
	 * @param sql
	 *            : sql statements
	 * @return : cursor
	 */
	public Cursor get(String sql) {
		return db.rawQuery(sql, null);
	}

	/****
	 * Method for select statements
	 * 
	 * @param sql
	 *            : sql statements
	 * @return : cursor
	 */
	public Cursor get(String sql, String[] agrs) {
		return db.rawQuery(sql, agrs);
	}

	/***
	 * Method to insert record
	 * 
	 * @param table
	 *            : table name
	 * @param values
	 *            : ContentValues instance
	 * @return : long (rowid)
	 */
	public long insert(String table, ContentValues values) {
		return db.insert(table, null, values);
	}

	/***
	 * Method to update record
	 * 
	 * @param table
	 *            : table name
	 * @param values
	 *            : ContentValues instance
	 * @param whereClause
	 *            : Where Clause
	 * @return ; int
	 */
	public int update(String table, ContentValues values, String whereClause) {
		return db.update(table, values, whereClause, null);
	}

	/***
	 * Method to delete record
	 * 
	 * @param table
	 *            : table name
	 * @param whereClause
	 *            : Where Clause
	 * @return : int
	 */
	public int delete(String table, String whereClause) {
		return db.delete(table, whereClause, null);
	}

	/***
	 * Method to run sql
	 * 
	 * @param sql
	 */
	public void exec(String sql) {
		db.execSQL(sql);
	}
	
	public void exec(String sql, String[] args) {
		db.execSQL(sql, args);
	}

	/****
	 * logCursorInfo : Cursor�?리턴받는 Result�?로깅?�는 메소??
	 * 
	 * @param c
	 */
	public void logCursorInfo(Cursor c) {
		Log.i(PathConstants.BASE_TAG, "*** Cursor Begin *** " + "Results:" + c.getCount() + " Colmns: " + c.getColumnCount());

		// Column Name print
		String rowHeaders = "|| ";
		for (int i = 0; i < c.getColumnCount(); i++) {
			rowHeaders = rowHeaders.concat(c.getColumnName(i) + " || ");
		}

		Log.i(PathConstants.BASE_TAG, "COLUMNS " + rowHeaders);
		// Record Print
		c.moveToFirst();
		while (c.isAfterLast() == false) {
			String rowResults = "|| ";
			for (int i = 0; i < c.getColumnCount(); i++) {
				rowResults = rowResults.concat(c.getString(i) + " || ");
			}

			Log.i(PathConstants.BASE_TAG, "Row " + c.getPosition() + ": " + rowResults);

			c.moveToNext();
		}
//		c.close();
		Log.i(PathConstants.BASE_TAG, "*** Cursor End ***");
	}

	public void copyDatabaseFile() {
		Log.i(PathConstants.BASE_TAG, "DatabaseHelper.copyDatabaseFile()");
		InputStream myInput = null;
		OutputStream myOutput = null;
		SQLiteDatabase database = null;
		if (!checkDataBaseExistence()) {
			database = getReadableDatabase();
			try {
				myInput = myContext.getAssets().open(PathConstants.DB_NAME);
				String outFileName = PathConstants.DB_PATH;
				myOutput = new FileOutputStream(outFileName);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = myInput.read(buffer)) > 0) {
					myOutput.write(buffer, 0, length);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					myOutput.flush();
					myOutput.close();
					myInput.close();
					if (database != null && database.isOpen()) {
						database.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean checkDataBaseExistence() {

		SQLiteDatabase dbToBeVerified = null;

		try {
			String dbPath = PathConstants.DB_PATH;
			Log.i(PathConstants.BASE_TAG, dbPath);
			dbToBeVerified = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
		}
		if (dbToBeVerified != null) {
			dbToBeVerified.close();
		}
		return dbToBeVerified != null ? true : false;
	}

	public void exportDB() {
		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String currentDBPath = Environment.getDataDirectory().getPath() + File.separator + "com.tangramfactory.smartgym"+ File.separator+"databases"+File.separator+ PathConstants.DB_NAME;
				String backupDBPath = "/SmartGymDB/"+ PathConstants.DB_NAME;
				String expeortDirctoryPath = Environment.getExternalStorageDirectory() + "/SmartGymDB/";

				File exportF = new File(expeortDirctoryPath);
				if(false == exportF.exists()) {
					exportF.mkdir();
				}

				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				Toast.makeText(myContext, "Backup Successful!", Toast.LENGTH_SHORT).show();

			}
		} catch (Exception e) {
			Toast.makeText(myContext, "Backup Failed!", Toast.LENGTH_SHORT).show();
		}
	}


}
