/*
 * Copyright 2011, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nfc;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.nfc.Object.ContactData;
import com.nfc.staticClass.CardManager;
import com.nfc.staticClass.HandleIntent;
import com.nfc.staticClass.MessageDecodeAndEncode;
import com.nfc.staticClass.MessageNdef;
import com.nfc.staticClass.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StickyNotesActivity extends Activity implements
		OnLongClickListener {
	private static final String TAG = "stickynotes";
	final String WELCOMESCREENSHOWNPREF = "welcomeScreenShown";
	final CharSequence[] ITEMS = { "Text", "Image", "Contact" };
	public static byte Flag = 0x01;
	private boolean mResumed = false;
	private boolean mWriteMode = false;

	// UI components
	NfcAdapter mNfcAdapter;
	EditText mNote;
	ImageView myImage;
	TextView textView;

	// global variables
	Bitmap bMap;
	Boolean capture = false;

	Intent in;
	Uri selectedImage = null;
	ContactData con = null;

	PendingIntent mNfcPendingIntent;
	IntentFilter[] mWriteTagFilters;
	IntentFilter[] mNdefExchangeFilters;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showWelcomeMessage();
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		setContentView(R.layout.main);
		mNote = ((EditText) findViewById(R.id.note));
		mNote.addTextChangedListener(mTextWatcher);

		myImage = (ImageView) findViewById(R.id.imageView1);
		myImage.setOnLongClickListener(this);

		textView = (TextView) findViewById(R.id.textView);

		findViewById(R.id.contact).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				in.setType(ContactsContract.Contacts.CONTENT_TYPE);
				startActivity(in);
				findViewById(R.id.contact).setEnabled(false);
			}
		});
		findViewById(R.id.contact).setEnabled(false);

		findViewById(R.id.write_tag).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				disableNdefExchangeMode();
				enableTagWriteMode();

				new AlertDialog.Builder(StickyNotesActivity.this)
						.setTitle("Touch tag to write")
						.setOnCancelListener(
								new DialogInterface.OnCancelListener() {
									@Override
									public void onCancel(DialogInterface dialog) {
										disableTagWriteMode();
										enableNdefExchangeMode();
									}
								}).create().show();
			}
		});

		findViewById(R.id.send).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myListAlertDialog();
			}
		});

		findViewById(R.id.testImageButton).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						selectedImage = null;
						capture = false;
						enableNdefExchangeMode();
					}
				});
		findViewById(R.id.testImageButton).setEnabled(false);
		findViewById(R.id.path).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(StickyNotesActivity.this)
						.setTitle("Image Path")
						// dialog!
						.setPositiveButton("Take an image",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										Intent takePicture = new Intent(
												MediaStore.ACTION_IMAGE_CAPTURE);
										startActivityForResult(takePicture, 0);
										enableNdefExchangeMode();
									}
								})
						.setNegativeButton("Pick an Image",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										Intent pickPhoto = new Intent(
												Intent.ACTION_PICK,
												android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
										startActivityForResult(pickPhoto, 1);
										enableNdefExchangeMode();
									}
								}).show();
			}
		});
		findViewById(R.id.path).setEnabled(false);

		mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// Intent filters for reading a note from a tag or exchanging over p2p.

		IntentFilter ndefDetected = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		IntentFilter tagDetected = new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED);
		IntentFilter techDetected = new IntentFilter(
				NfcAdapter.ACTION_TECH_DISCOVERED);
		try {
			ndefDetected.addDataType("text/plain");

		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		try {
			techDetected.addDataType("*/*");

		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}

		mNdefExchangeFilters = new IntentFilter[] { ndefDetected, tagDetected,
				techDetected };

		mWriteTagFilters = new IntentFilter[] { tagDetected };

	}

	private void showWelcomeMessage() {

		boolean welcomeScreenShown = false;

		SharedPreferences mPrefs = ((OpenActivity) getApplication())
				.getPreferences();

		welcomeScreenShown = mPrefs.getBoolean(WELCOMESCREENSHOWNPREF, false);
		if (!welcomeScreenShown) {

			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Brief Introduction")
					.setMessage("You may read a IC card in any second.\nTo send data though NFC, please press Send Data and choose which type to be sent")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).show();

			((OpenActivity) getApplication()).put(WELCOMESCREENSHOWNPREF, true);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		mResumed = true;
		// Sticky notes received from Android
		Log.v("resume", "resume");
		Log.v("intent", "" + getIntent().getAction());
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {

			NdefMessage[] messages = MessageDecodeAndEncode
					.getNdefMessages(getIntent());
			if (messages != null) {
				byte[] payload = messages[0].getRecords()[0].getPayload();
				// setNoteBody(new String(payload));
				Bitmap bMap = BitmapFactory.decodeByteArray(payload, 0,
						payload.length);
				myImage.setImageBitmap(bMap);
				setIntent(new Intent());
			} else {
				Log.i("NullValue", "message is null");
			}// Consume this intent.
		}
		enableNdefExchangeMode();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mResumed = false;
		Log.v("lifecycle", "pause");
		mNfcAdapter.disableForegroundNdefPush(this);

	}

	private void promptForContent(final NdefMessage msg) {
		if (msg != null)
			new AlertDialog.Builder(this)
					.setTitle("Replace current content?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									Log.v("bytearray", ""+ msg.getRecords()[0].getPayload());

									byte zero = msg.getRecords()[0]
											.getPayload()[0];

									switch (zero) {
									case (byte) 0x01:

										setNoteBody(MessageDecodeAndEncode
												.decodeNote(msg.getRecords()[0]
														.getPayload()));
										Log.v("byte", "0x01");
										break;
									case (byte) 0x02:

										bMap = MessageDecodeAndEncode
												.decodeImage(msg.getRecords()[0]
														.getPayload());
										myImage.setImageBitmap(bMap);
										break;
									case (byte) 0x03:

										findViewById(R.id.contact).setEnabled(
												true);
										String contact = MessageDecodeAndEncode
												.decodeContact(msg.getRecords()[0]
														.getPayload());
										con = MessageDecodeAndEncode
												.stringToContact(contact);

										try {
											mNote.setText(HandleIntent
													.CombineAllWithPhone(
															con.getName(),
															con.getEmail(),
															con.getEmailType(),
															con.getPhone()));
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

										in = new Intent(Intent.ACTION_INSERT);
										in.putExtra(
												ContactsContract.Intents.Insert.PHONE,
												con.getPhone());
										in.putExtra(
												ContactsContract.Intents.Insert.NAME,
												con.getName());
										in.putExtra(
												ContactsContract.Intents.Insert.EMAIL,
												con.getEmail());

										break;
									default:
										Log.v("nth", "nth");

										break;
									}
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {

								}
							}).show();
		else
			Log.v("Error", "Msg in promopt for Contact is null");
	}

	private NdefMessage getNoteAsNdef() {

		NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				"text/plain".getBytes(), new byte[] {},
				MessageDecodeAndEncode.encode(mNote.getText().toString()
						.getBytes(), Flag));
		return new NdefMessage(new NdefRecord[] { textRecord });
	}

	private NdefMessage getContactAsNdef() {

		byte[] tem = null;
		NdefRecord textRecord;

		if (con != null) {
			tem = (con.getName() + "," + con.getEmail() + ","
					+ con.getEmailType() + "," + con.getPhone()).getBytes();
		} else{
			tem = null;
		}
		textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				"text/plain".getBytes(), new byte[] {},
				MessageDecodeAndEncode.encode(tem, Flag));

		return new NdefMessage(new NdefRecord[] { textRecord });

	}

	private NdefMessage getImageAsNdef() {// Image
		byte[] image;

		// Log.v("getImageAsNdef", "" + image);
		InputStream stream = null;

		if (capture) { // cap?
			Log.v("tag", "capture detected" + bMap);
		} else {

			if (selectedImage == null) {
				bMap = BitmapFactory.decodeResource(getResources(),
						R.drawable.earth);
				textView.setText("");
			} else {

				try {
					stream = getContentResolver()
							.openInputStream(selectedImage);
					bMap = BitmapFactory.decodeStream(stream);
					stream.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		myImage.setImageBitmap(bMap);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		image = baos.toByteArray();

		NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				"*/*".getBytes(), new byte[0], MessageDecodeAndEncode.encode(
						image, Flag));
		return new NdefMessage(new NdefRecord[] { mimeRecord });
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent returnedIntent) {
		super.onActivityResult(requestCode, resultCode, returnedIntent);
		switch (requestCode) {

		case 0: // Camera Type
			if (resultCode == RESULT_OK) {
				Bitmap photo = (Bitmap) returnedIntent.getExtras().get("data");
				myImage.setImageBitmap(photo);
				bMap = photo;
				capture = true;
			}

			break;
		case 1: // Image Type
			if (resultCode == RESULT_OK) {
				selectedImage = returnedIntent.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);

				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();
				String name = filePath;
				textView.setText(name);
				myImage.setImageURI(selectedImage);
				capture = false;
			}
			break;

		case 2: // Contact Type
			if (resultCode == RESULT_OK) {

				Uri result = returnedIntent.getData();
				String rid = HandleIntent.getid(result);

				Cursor cursor1 = getContentResolver().query(result, null, null,
						null, null);
				Cursor cursor2 = getContentResolver().query(Email.CONTENT_URI,
						null, Email.CONTACT_ID + "=?", new String[] { rid },
						null);
				Cursor cursor3 = getContentResolver().query(Email.CONTENT_URI,
						null, Email.CONTACT_ID + "=?", new String[] { rid },
						null);
				Cursor cursor4 = getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, Phone.CONTACT_ID + "=?", new String[] { rid },
						null);

				try {
					con = new ContactData(HandleIntent.getname(cursor1),
							HandleIntent.getemail(cursor2),
							HandleIntent.getemailType(cursor3),
							HandleIntent.getPhone(cursor4));

					mNote.setText(HandleIntent.CombineAllWithPhone(
							con.getName(), con.getEmail(), con.getEmailType(),
							con.getPhone()));

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		}
	}

	private void enableNdefExchangeMode() {
		Log.v("enable", "enable" + Flag);
		switch (Flag) {
		case 1:
			mNfcAdapter.enableForegroundNdefPush(StickyNotesActivity.this,
					getNoteAsNdef());
			Log.v("case", "case1");
			break;
		case 2:
			mNfcAdapter.enableForegroundNdefPush(StickyNotesActivity.this,
					getImageAsNdef());
			Log.v("case", "case2");
			break;
		case 3:
			mNfcAdapter.enableForegroundNdefPush(StickyNotesActivity.this,
					getContactAsNdef());
			Log.v("case", "case2");
			break;
		default:
			Log.v("Flag", "Uncontrolled Flag!!");
			break;
		}

		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
				mNdefExchangeFilters, null);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// NDEF exchange mode
		if (!mWriteMode
				&& NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			NdefMessage[] msgs = MessageDecodeAndEncode.getNdefMessages(intent);
			Log.v("intent", "NDEF");

			if (msgs != null)
				promptForContent(msgs[0]);
		}

		// Tag writing mode
		else if (mWriteMode
				&& NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			Log.v("intent", "tag");
			MessageNdef.writeTag(getNoteAsNdef(), detectedTag);
			// MessageNdef.writeTag(getNoteAsNdef(), detectedTag);

		}

		else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			NfcF nfcfTag = NfcF.get(tag);
			String ID = "ID: " + "" + Util.getHex(nfcfTag.getTag().getId());
			String PMM = "Manufacturer: " + ""
					+ Util.getHex(nfcfTag.getManufacturer());
			final Parcelable p = intent
					.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			setNoteBody((p != null) ? "Card Record: \n" + ID + "\n" + PMM
					+ "\n" + CardManager.load(p) : null);

		}
	}

	private void setNoteBody(String body) {
		Editable text = mNote.getText();
		text.clear();
		Log.v("string", "" + body);
		text.append(body);
	}

	public void doLaunchContactPicker() {
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
				Contacts.CONTENT_URI);
		startActivityForResult(contactPickerIntent, 2);
	}

	private void disableNdefExchangeMode() {
		mNfcAdapter.disableForegroundNdefPush(this);
		mNfcAdapter.disableForegroundDispatch(this);
	}

	private void enableTagWriteMode() {
		mWriteMode = true;
		IntentFilter tagDetected = new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED);
		mWriteTagFilters = new IntentFilter[] { tagDetected };
		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
				mWriteTagFilters, null);
	}

	private void disableTagWriteMode() {
		mWriteMode = false;
		mNfcAdapter.disableForegroundDispatch(this);
	}

	private TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			if (mResumed) {
				mNfcAdapter.enableForegroundNdefPush(StickyNotesActivity.this,
						getNoteAsNdef());

			}
		}
	};

	@Override
	public boolean onLongClick(View arg0) {
		new AlertDialog.Builder(StickyNotesActivity.this)
				.setTitle("Saving Image")
				.setMessage("Do you want to save the Image?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {

								try {
									MediaStore.Images.Media.insertImage(
											getContentResolver(), bMap,
											"image_from_nfc.jpg", null);
									Toast.makeText(StickyNotesActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
								} catch (Exception e) {
									Log.v("Image to File",
											"Failed to write to SD card with exception:"
													+ e.toString());
								}
								
							}

						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						return;
					}
				}).show();

		return false;
	}

	public void myListAlertDialog() {

		Builder MyListAlertDialog = new AlertDialog.Builder(this);
		MyListAlertDialog.setTitle("Send Data");

		DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(StickyNotesActivity.this, ITEMS[which],
						Toast.LENGTH_SHORT).show();
				if (ITEMS[which].equals("Text")) {
					Flag = 0x01;
					Log.v("flag", "0x01");
					enableNdefExchangeMode();
					findViewById(R.id.path).setEnabled(false);
					findViewById(R.id.testImageButton).setEnabled(false);
					findViewById(R.id.contact).setEnabled(false);
					capture = false;
					Toast.makeText(StickyNotesActivity.this, "success",
							Toast.LENGTH_LONG).show();
				} else if (ITEMS[which].equals("Image")) {
					Flag = 0x02;
					Log.v("flag", "0x02");
					enableNdefExchangeMode();
					findViewById(R.id.path).setEnabled(true);
					findViewById(R.id.testImageButton).setEnabled(true);
					findViewById(R.id.contact).setEnabled(false);

				} else if (ITEMS[which].equals("Contact")) {
					Flag = 0x03;
					Log.v("flag", "0x03");
					doLaunchContactPicker();
					enableNdefExchangeMode();
					findViewById(R.id.path).setEnabled(false);
					findViewById(R.id.testImageButton).setEnabled(false);
					findViewById(R.id.contact).setEnabled(false);
					capture = false;
				}

			}
		};
		DialogInterface.OnClickListener cancelClick = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
		MyListAlertDialog.setItems(ITEMS, ListClick);
		MyListAlertDialog.setNeutralButton("cancel", cancelClick);
		MyListAlertDialog.show();
	}
}