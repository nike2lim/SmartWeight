package com.tangramfactory.smartweight.database;

import android.os.Environment;

import com.tangramfactory.smartweight.utility.DebugLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class DataExport {

	public static void dataExport() {
		File dbFile = new File(PathConstants.DB_PATH);

		File exportDir = new File(Environment.getExternalStorageDirectory(), PathConstants.DB_NAME);
		if (!exportDir.exists()) {
			exportDir.mkdirs();
		}
		File file = new File(exportDir, dbFile.getName());

		try {
			file.createNewFile();
			copyFile(dbFile, file);
			DebugLogger.d("DataExport", "Complete");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteExportData(){
		File dbFile = new File(PathConstants.DB_PATH);
		File exportDir = new File(Environment.getExternalStorageDirectory(), PathConstants.DB_NAME);
		if(exportDir.exists())
			exportDir.delete();
		File file = new File(exportDir, dbFile.getName());
		if(file.exists())
			file.delete();
	}
	
	private static void copyFile(File src, File dst) throws IOException {
	    FileInputStream inFileInputStream = new FileInputStream(src);
	    FileOutputStream outFileOutputStream = new FileOutputStream(dst);
        FileChannel inChannel = inFileInputStream.getChannel();
        FileChannel outChannel = outFileOutputStream.getChannel();
        try {
           inChannel.transferTo(0, inChannel.size(), outChannel);
           inChannel.close();
           outChannel.close();
           inFileInputStream.close();
           outFileOutputStream.close();
        } finally {
           if (inChannel != null){
               inChannel.close();
               inFileInputStream.close();
           }
           if (outChannel != null){
               outChannel.close();
               outFileOutputStream.close();
           }
        }
     }
}
