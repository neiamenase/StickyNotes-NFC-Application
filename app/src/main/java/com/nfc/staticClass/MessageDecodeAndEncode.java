package com.nfc.staticClass;

import java.util.Arrays;

import com.nfc.Object.ContactData;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.util.Log;

public class MessageDecodeAndEncode {

	// For Reference
	// 1 stands for Text
	// 2 stands for Image
	// 3 stands for Contact
	// (Both for control byte && Flag)

	private enum set {
		INT, FLOAT, LONG, STRING, BOOLEAN
	};

	public static byte[] encode(byte[] incoming, byte Flag) {

		if (incoming == null) {
			return null;
		}
		byte[] textBytes = new byte[incoming.length + 1];

		textBytes[0] = Flag;
		switch (Flag) {

		case 1:
		case 2:
		case 3: // same for Contact and Text

			for (int i = 0; i < incoming.length; i++) {
				textBytes[i + 1] = incoming[i];
			}
			break;

		default:
			Log.v("Flag", "Error, uncontrolled Flag");

			return null;
		}

		return textBytes;
	}

	public static String decodeNote(byte[] incoming) {
		String body = new String(Arrays.copyOfRange(incoming, 1,
				incoming.length));
		return body;
	}

	public static Bitmap decodeImage(byte[] incoming) {
		if(incoming == null){
			return null;
		}
		byte[] temp = Arrays.copyOfRange(incoming, 1, incoming.length);
		Bitmap bMap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
		return bMap;
	}

	public static String decodeContact(byte[] incoming) {
		String body = new String(Arrays.copyOfRange(incoming, 1,
				incoming.length));
		return body;
	}

	public static ContactData stringToContact(String body) {
		String[] seperated = body.split(",");

		return new ContactData(seperated[0], seperated[1], seperated[2],
				seperated[3]);
	}

	public static com.nfc.staticClass.MessageDecodeAndEncode.set indicateType(
			Object a) {
		if (a instanceof Integer)
			return set.INT;
		else if (a instanceof String)
			return set.STRING;
		else if (a instanceof Float)
			return set.FLOAT;
		else if (a instanceof Boolean)
			return set.BOOLEAN;
		else if (a instanceof Long)
			return set.LONG;

		return null;
	}

	public static void put(SharedPreferences sharedPreferences, String key, Object a){
		switch(indicateType(a)){
		
		case INT:
			HandleIntent.put((Integer) a,key,sharedPreferences);
			break;
		case STRING:
			HandleIntent.put((String) a,key,sharedPreferences);		
			break;
		case FLOAT:
			HandleIntent.put((Float) a,key,sharedPreferences);
			break;
		case BOOLEAN:
			HandleIntent.put((Boolean) a,key,sharedPreferences);
			break;
		case LONG:
			HandleIntent.put((Long) a,key,sharedPreferences);
			break;
		default:
			break;
		
		}
	}

	public static NdefMessage[] getNdefMessages(Intent intent) {
		// Parse the intent
		NdefMessage[] msgs = null;
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
		} else {
			Log.d("TAG", "Unknown intent.");
		}
		return msgs;
	}
}
