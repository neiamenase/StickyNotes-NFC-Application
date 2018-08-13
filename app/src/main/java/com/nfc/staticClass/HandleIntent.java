package com.nfc.staticClass;

import com.nfc.Object.ContactData;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.widget.Toast;

public class HandleIntent {

	int typeINT = 0, typeFLOAT = 1;

	public enum TYPE {
		INT, FLOAT, STRING
	};

	public static String getid(Uri result) {
		return result.getLastPathSegment();
	}

	public static String getname(Cursor cursor) {

		String rname;
		cursor.moveToFirst();
		rname = cursor.getString(cursor
				.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

		cursor.close();
		return rname;
	}

	public static String getemail(Cursor cursor) throws Exception {

		int emailIdx = cursor.getColumnIndex(Email.DATA);
		String email = null;
		// let's just get the first email
		if (cursor.moveToFirst()) { // Email

			email = cursor.getString(emailIdx);
			Log.v("Email", "Got email: " + email);

			cursor.close();

		} else {
			Log.w("Email", "No results");
		}
		return email;
	}

	public static String getemailType(Cursor cursor) throws Exception {

		String type = null;
		if (cursor.moveToFirst()) { // Email
			int emailIdx = cursor.getColumnIndex(Email.TYPE);
			type = cursor.getString(emailIdx);
			Log.v("Email", "Got email: " + type);

			cursor.close();

		} else {
			Log.w("Email", "No results");
		}
		if (!(type == null)) {
			switch (Integer.parseInt(type)) {
			case 0:
				type = "CUSTOM";
				break;
			case 1:
				type = "HOME";
				break;
			case 2:
				type = "WORK";
				break;
			case 3:
				type = "OTHER";
				break;
			case 4:
				type = "MOBILE";
				break;
			default:
				break;
			}
		}

		return type;
	}

	public static String getPhone(Cursor cursor) {
		String phone = null;
		int PhoneIdx = cursor.getColumnIndex(Phone.DATA);
		if (cursor.moveToFirst()) {
			phone = cursor.getString(PhoneIdx);

			cursor.close();
			Log.v("Phone", "Got Phone: " + phone);

		} else {
			Log.w("Phone", "No results");
		}
		return phone;
	}

	public static String CombineAllWithPhone(String rname, String remail,
			String type, String phone) throws Exception {

		if (type != null) {

			return "Contact Type \n" + "Name: " + rname + "\n" + "Phone: "
					+ phone + "\n" + type + " Email: " + remail;
		}
		return "Contact Type \n" + "Name: " + rname + "\n" + "Phone: " + phone
				+ "\n" + "Email: " + remail;

	}

	public static ContactData makeContactObject(String name_, String email_,
			String emailType_, String phone_) {
		return new ContactData(name_, email_, emailType_, phone_);
	}

	public static void put(boolean input_, String key,
			SharedPreferences sharedPreferences_) {
		SharedPreferences.Editor editor = sharedPreferences_.edit();
		editor.putBoolean(key, input_);
		editor.commit(); // save the preference
	}

	public static void put(String input_, String key,
			SharedPreferences sharedPreferences_) {
		SharedPreferences.Editor editor = sharedPreferences_.edit();
		editor.putString(key, input_);
		editor.commit(); // save the preference

	}

	public static void put(Float input_, String key,
			SharedPreferences sharedPreferences_) {
		SharedPreferences.Editor editor = sharedPreferences_.edit();
		editor.putFloat(key, input_);
		editor.commit(); // save the preference

	}

	public static void put(int input_, String key, SharedPreferences sharedPreferences_) {
		SharedPreferences.Editor editor = sharedPreferences_.edit();
		editor.putInt(key, input_);
		editor.commit(); // save the preference

	}

	public static void put(Long input_, String key,
			SharedPreferences sharedPreferences_) {
		SharedPreferences.Editor editor = sharedPreferences_.edit();
		editor.putLong(key, input_);
		editor.commit(); // save the preference

	}

}
