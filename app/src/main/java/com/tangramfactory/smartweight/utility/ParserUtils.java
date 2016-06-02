/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.tangramfactory.smartweight.utility;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.text.TextUtils;
import android.util.Log;

public class ParserUtils {
	final private static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();


	public static String parse(final BluetoothGattCharacteristic characteristic) {
		return parse(characteristic.getValue());
	}

	public static String parse(final BluetoothGattDescriptor descriptor) {
		return parse(descriptor.getValue());
	}

	public static String parse(final byte[] data) {
		if (data == null || data.length == 0)
			return "";

		final char[] out = new char[data.length * 3 - 1];
		for (int j = 0; j < data.length; j++) {
			int v = data[j] & 0xFF;
			out[j * 3] = HEX_ARRAY[v >>> 4];
			out[j * 3 + 1] = HEX_ARRAY[v & 0x0F];
			if (j != data.length - 1)
				out[j * 3 + 2] = '-';
		}
		return "(0x) " + new String(out);
	}
	
	public static String prefixUpperString(String str){
		String prefixStr = str.substring(0,1).toUpperCase(Locale.getDefault());
		String otherStr = str.substring(1);	   
		return prefixStr.concat(otherStr);
	}
	
	public static void printScanRecord (byte[] scanRecord) {

	    // Simply print all raw bytes   
	    try {
	        String decodedRecord = new String(scanRecord,"UTF-8");
	        Log.d("DEBUG","decoded String : " + ByteArrayToString(scanRecord));
	        Log.d("DEBUG","decoded String : " + decodedRecord);
	    } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
	    }

	    // Parse data bytes into individual records
	    List<AdRecord> records = AdRecord.parseScanRecord(scanRecord);


	    // Print individual records 
	    if (records.size() == 0) {
	        Log.i("DEBUG", "Scan Record Empty");
	    } else {
	        Log.i("DEBUG", "Scan Record: " + TextUtils.join(",", records));
	    }

	}

	public static String parseAdertisedData(byte[] advertisedData) {      
        String name = null;
        try {
        	if( advertisedData == null ){
        		return "";
        	}
        	
        	ByteBuffer buffer = ByteBuffer.wrap(advertisedData).order(ByteOrder.LITTLE_ENDIAN);
        	while (buffer.remaining() > 2) {
        		byte length = buffer.get();
        		if (length == 0) break;
        		
        		byte type = buffer.get();
        		switch (type) {
        		case 0x02: // Partial list of 16-bit UUIDs
        		case 0x03: // Complete list of 16-bit UUIDs
        			while (length >= 2) {
        				length -= 2;
        			}
        			break;
        		case 0x06: // Partial list of 128-bit UUIDs
        		case 0x07: // Complete list of 128-bit UUIDs
        			while (length >= 16) {
        				long lsb = buffer.getLong();
        				long msb = buffer.getLong();
        				length -= 16;
        			}
        			break;
        		case 0x09:
        			byte[] nameBytes = new byte[length-1];
        			buffer.get(nameBytes);
        			try {
        				name = new String(nameBytes, "utf-8");
        			} catch (UnsupportedEncodingException e) {
        				return "";
        			}
        			break;
        		default:
        			buffer.position(buffer.position() + length - 1);
        			break;
        		}
        	}
		} catch (Exception e) {
			return "";
		}
        return name;
    }
	public static String ByteArrayToString(byte[] ba)
	{
	  StringBuilder hex = new StringBuilder(ba.length * 2);
	  for (byte b : ba)
	    hex.append(b + " ");

	  return hex.toString();
	}


	public static class AdRecord {

	    public AdRecord(int length, int type, byte[] data) {
	        String decodedRecord = "";
	        try {
	            decodedRecord = new String(data,"UTF-8");

	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        }

	        Log.d("DEBUG", "Length: " + length + " Type : " + type + " Data : " + ByteArrayToString(data));         
	    }

	    // ...

	    public static List<AdRecord> parseScanRecord(byte[] scanRecord) {
	        List<AdRecord> records = new ArrayList<AdRecord>();

	        int index = 0;
	        while (index < scanRecord.length) {
	            int length = scanRecord[index++];
	            //Done once we run out of records
	            if (length == 0) break;

	            int type = scanRecord[index];
	            //Done if our record isn't a valid type
	            if (type == 0) break;

	            byte[] data = Arrays.copyOfRange(scanRecord, index+1, index+length);

	            records.add(new AdRecord(length, type, data));
	            //Advance
	            index += length;
	        }

	        return records;
	    }

	    // ...
	}
}
