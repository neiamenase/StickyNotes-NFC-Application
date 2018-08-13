package com.nfc.staticClass;

import java.io.IOException;

import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.util.Log;

public class MessageNdef{

	public MessageNdef() {
		// TODO Auto-generated constructor stub
	}
	
	
	

	public static boolean writeTag(NdefMessage message, Tag tag) {
		int size = message.toByteArray().length;

		try {
			Ndef ndef = Ndef.get(tag);
			if (ndef != null) {
				ndef.connect();

				if (!ndef.isWritable()) {
					//toast("Tag is read-only.");
					
					return false;
				}
				if (ndef.getMaxSize() < size) {
//					toast("Tag capacity is " + ndef.getMaxSize()
//							+ " bytes, message is " + size + " bytes.");
					return false;
				}

				ndef.writeNdefMessage(message);
				//toast("write tag","Wrote message to pre-formatted tag.");
				return true;
			} else {
				NdefFormatable format = NdefFormatable.get(tag);
				if (format != null) {
					try {
						format.connect();
						format.format(message);
					Log.v("write tag","Formatted tag and wrote message");
						return true;
					} catch (IOException e) {
						Log.v("write tag","Failed to format tag.");
						return false;
					}
				} else {
					Log.v("write tag","Tag doesn't support NDEF.");
					return false;
				}
			}
		} catch (Exception e) {
			Log.v("write tag","Failed to write tag");
		}

		return false;
	}
	
}
