package com.nfc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.nfc.Object.ContactData;
import com.nfc.staticClass.MessageDecodeAndEncode;

import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

public class OpenActivity extends Application {

    public static final boolean DEV_MODE = true;
    public static final int LOG_LEVEL = DEV_MODE ? Log.VERBOSE : Log.ERROR;

    /*
     * key to hold boolean whether application is registered.
     */
    
    private ContactData con;
    
    private ConnectivityManager connectivityManager;
    private SharedPreferences sharedPreferences;
    
    public SharedPreferences getPreferences(){
    	SharedPreferences mPrefs = null;
		try {
			mPrefs = sharedPreferences;
		} catch (Exception e) {
			Log.v("Tag", "unsuccessful get");
		}

		if (mPrefs == null) {
			mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			sharedPreferences = mPrefs;
		}
		return mPrefs;
    }
    
    
    
    
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}




	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}




	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}




	@Override
	public void onTrimMemory(int level) {
		// TODO Auto-generated method stub
		super.onTrimMemory(level);
	}




	@Override
	public void registerActivityLifecycleCallbacks(
			ActivityLifecycleCallbacks callback) {
		// TODO Auto-generated method stub
		super.registerActivityLifecycleCallbacks(callback);
	}




	@Override
	public void registerComponentCallbacks(ComponentCallbacks callback) {
		// TODO Auto-generated method stub
		super.registerComponentCallbacks(callback);
	}




	@Override
	public void unregisterActivityLifecycleCallbacks(
			ActivityLifecycleCallbacks callback) {
		// TODO Auto-generated method stub
		super.unregisterActivityLifecycleCallbacks(callback);
	}




	@Override
	public void unregisterComponentCallbacks(ComponentCallbacks callback) {
		// TODO Auto-generated method stub
		super.unregisterComponentCallbacks(callback);
	}




	@Override
	protected void attachBaseContext(Context base) {
		// TODO Auto-generated method stub
		super.attachBaseContext(base);
	}




	@Override
	public boolean bindService(Intent service, ServiceConnection conn, int flags) {
		// TODO Auto-generated method stub
		return super.bindService(service, conn, flags);
	}




	@Override
	public int checkCallingOrSelfPermission(String permission) {
		// TODO Auto-generated method stub
		return super.checkCallingOrSelfPermission(permission);
	}




	@Override
	public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
		// TODO Auto-generated method stub
		return super.checkCallingOrSelfUriPermission(uri, modeFlags);
	}




	@Override
	public int checkCallingPermission(String permission) {
		// TODO Auto-generated method stub
		return super.checkCallingPermission(permission);
	}




	@Override
	public int checkCallingUriPermission(Uri uri, int modeFlags) {
		// TODO Auto-generated method stub
		return super.checkCallingUriPermission(uri, modeFlags);
	}




	@Override
	public int checkPermission(String permission, int pid, int uid) {
		// TODO Auto-generated method stub
		return super.checkPermission(permission, pid, uid);
	}




	@Override
	public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
		// TODO Auto-generated method stub
		return super.checkUriPermission(uri, pid, uid, modeFlags);
	}




	@Override
	public int checkUriPermission(Uri uri, String readPermission,
			String writePermission, int pid, int uid, int modeFlags) {
		// TODO Auto-generated method stub
		return super.checkUriPermission(uri, readPermission, writePermission, pid, uid,
				modeFlags);
	}




	@Override
	public void clearWallpaper() throws IOException {
		// TODO Auto-generated method stub
		super.clearWallpaper();
	}




	@Override
	public Context createPackageContext(String packageName, int flags)
			throws NameNotFoundException {
		// TODO Auto-generated method stub
		return super.createPackageContext(packageName, flags);
	}




	@Override
	public String[] databaseList() {
		// TODO Auto-generated method stub
		return super.databaseList();
	}




	@Override
	public boolean deleteDatabase(String name) {
		// TODO Auto-generated method stub
		return super.deleteDatabase(name);
	}




	@Override
	public boolean deleteFile(String name) {
		// TODO Auto-generated method stub
		return super.deleteFile(name);
	}




	@Override
	public void enforceCallingOrSelfPermission(String permission, String message) {
		// TODO Auto-generated method stub
		super.enforceCallingOrSelfPermission(permission, message);
	}




	@Override
	public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags,
			String message) {
		// TODO Auto-generated method stub
		super.enforceCallingOrSelfUriPermission(uri, modeFlags, message);
	}




	@Override
	public void enforceCallingPermission(String permission, String message) {
		// TODO Auto-generated method stub
		super.enforceCallingPermission(permission, message);
	}




	@Override
	public void enforceCallingUriPermission(Uri uri, int modeFlags,
			String message) {
		// TODO Auto-generated method stub
		super.enforceCallingUriPermission(uri, modeFlags, message);
	}




	@Override
	public void enforcePermission(String permission, int pid, int uid,
			String message) {
		// TODO Auto-generated method stub
		super.enforcePermission(permission, pid, uid, message);
	}




	@Override
	public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags,
			String message) {
		// TODO Auto-generated method stub
		super.enforceUriPermission(uri, pid, uid, modeFlags, message);
	}




	@Override
	public void enforceUriPermission(Uri uri, String readPermission,
			String writePermission, int pid, int uid, int modeFlags,
			String message) {
		// TODO Auto-generated method stub
		super.enforceUriPermission(uri, readPermission, writePermission, pid, uid,
				modeFlags, message);
	}




	@Override
	public String[] fileList() {
		// TODO Auto-generated method stub
		return super.fileList();
	}




	@Override
	public Context getApplicationContext() {
		// TODO Auto-generated method stub
		return super.getApplicationContext();
	}




	@Override
	public ApplicationInfo getApplicationInfo() {
		// TODO Auto-generated method stub
		return super.getApplicationInfo();
	}




	@Override
	public AssetManager getAssets() {
		// TODO Auto-generated method stub
		return super.getAssets();
	}




	@Override
	public Context getBaseContext() {
		// TODO Auto-generated method stub
		return super.getBaseContext();
	}




	@Override
	public File getCacheDir() {
		// TODO Auto-generated method stub
		return super.getCacheDir();
	}




	@Override
	public ClassLoader getClassLoader() {
		// TODO Auto-generated method stub
		return super.getClassLoader();
	}




	@Override
	public ContentResolver getContentResolver() {
		// TODO Auto-generated method stub
		return super.getContentResolver();
	}




	@Override
	public File getDatabasePath(String name) {
		// TODO Auto-generated method stub
		return super.getDatabasePath(name);
	}




	@Override
	public File getDir(String name, int mode) {
		// TODO Auto-generated method stub
		return super.getDir(name, mode);
	}




	@Override
	public File getExternalCacheDir() {
		// TODO Auto-generated method stub
		return super.getExternalCacheDir();
	}




	@Override
	public File getExternalFilesDir(String type) {
		// TODO Auto-generated method stub
		return super.getExternalFilesDir(type);
	}




	@Override
	public File getFileStreamPath(String name) {
		// TODO Auto-generated method stub
		return super.getFileStreamPath(name);
	}




	@Override
	public File getFilesDir() {
		// TODO Auto-generated method stub
		return super.getFilesDir();
	}




	@Override
	public Looper getMainLooper() {
		// TODO Auto-generated method stub
		return super.getMainLooper();
	}




	@Override
	public File getObbDir() {
		// TODO Auto-generated method stub
		return super.getObbDir();
	}




	@Override
	public String getPackageCodePath() {
		// TODO Auto-generated method stub
		return super.getPackageCodePath();
	}




	@Override
	public PackageManager getPackageManager() {
		// TODO Auto-generated method stub
		return super.getPackageManager();
	}




	@Override
	public String getPackageName() {
		// TODO Auto-generated method stub
		return super.getPackageName();
	}




	@Override
	public String getPackageResourcePath() {
		// TODO Auto-generated method stub
		return super.getPackageResourcePath();
	}




	@Override
	public Resources getResources() {
		// TODO Auto-generated method stub
		return super.getResources();
	}




	@Override
	public SharedPreferences getSharedPreferences(String name, int mode) {
		// TODO Auto-generated method stub
		return super.getSharedPreferences(name, mode);
	}




	@Override
	public Object getSystemService(String name) {
		// TODO Auto-generated method stub
		return super.getSystemService(name);
	}




	@Override
	public Theme getTheme() {
		// TODO Auto-generated method stub
		return super.getTheme();
	}




	@Override
	public Drawable getWallpaper() {
		// TODO Auto-generated method stub
		return super.getWallpaper();
	}




	@Override
	public int getWallpaperDesiredMinimumHeight() {
		// TODO Auto-generated method stub
		return super.getWallpaperDesiredMinimumHeight();
	}




	@Override
	public int getWallpaperDesiredMinimumWidth() {
		// TODO Auto-generated method stub
		return super.getWallpaperDesiredMinimumWidth();
	}




	@Override
	public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {
		// TODO Auto-generated method stub
		super.grantUriPermission(toPackage, uri, modeFlags);
	}




	@Override
	public boolean isRestricted() {
		// TODO Auto-generated method stub
		return super.isRestricted();
	}




	@Override
	public FileInputStream openFileInput(String name)
			throws FileNotFoundException {
		// TODO Auto-generated method stub
		return super.openFileInput(name);
	}




	@Override
	public FileOutputStream openFileOutput(String name, int mode)
			throws FileNotFoundException {
		// TODO Auto-generated method stub
		return super.openFileOutput(name, mode);
	}




	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory) {
		// TODO Auto-generated method stub
		return super.openOrCreateDatabase(name, mode, factory);
	}




	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory, DatabaseErrorHandler errorHandler) {
		// TODO Auto-generated method stub
		return super.openOrCreateDatabase(name, mode, factory, errorHandler);
	}




	@Override
	public Drawable peekWallpaper() {
		// TODO Auto-generated method stub
		return super.peekWallpaper();
	}




	@Override
	public Intent registerReceiver(BroadcastReceiver receiver,
			IntentFilter filter) {
		// TODO Auto-generated method stub
		return super.registerReceiver(receiver, filter);
	}




	@Override
	public Intent registerReceiver(BroadcastReceiver receiver,
			IntentFilter filter, String broadcastPermission, Handler scheduler) {
		// TODO Auto-generated method stub
		return super.registerReceiver(receiver, filter, broadcastPermission, scheduler);
	}




	@Override
	public void removeStickyBroadcast(Intent intent) {
		// TODO Auto-generated method stub
		super.removeStickyBroadcast(intent);
	}




	@Override
	public void revokeUriPermission(Uri uri, int modeFlags) {
		// TODO Auto-generated method stub
		super.revokeUriPermission(uri, modeFlags);
	}




	@Override
	public void sendBroadcast(Intent intent) {
		// TODO Auto-generated method stub
		super.sendBroadcast(intent);
	}




	@Override
	public void sendBroadcast(Intent intent, String receiverPermission) {
		// TODO Auto-generated method stub
		super.sendBroadcast(intent, receiverPermission);
	}




	@Override
	public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
		// TODO Auto-generated method stub
		super.sendOrderedBroadcast(intent, receiverPermission);
	}




	@Override
	public void sendOrderedBroadcast(Intent intent, String receiverPermission,
			BroadcastReceiver resultReceiver, Handler scheduler,
			int initialCode, String initialData, Bundle initialExtras) {
		// TODO Auto-generated method stub
		super.sendOrderedBroadcast(intent, receiverPermission, resultReceiver,
				scheduler, initialCode, initialData, initialExtras);
	}




	@Override
	public void sendStickyBroadcast(Intent intent) {
		// TODO Auto-generated method stub
		super.sendStickyBroadcast(intent);
	}




	@Override
	public void sendStickyOrderedBroadcast(Intent intent,
			BroadcastReceiver resultReceiver, Handler scheduler,
			int initialCode, String initialData, Bundle initialExtras) {
		// TODO Auto-generated method stub
		super.sendStickyOrderedBroadcast(intent, resultReceiver, scheduler,
				initialCode, initialData, initialExtras);
	}




	@Override
	public void setTheme(int resid) {
		// TODO Auto-generated method stub
		super.setTheme(resid);
	}




	@Override
	public void setWallpaper(Bitmap bitmap) throws IOException {
		// TODO Auto-generated method stub
		super.setWallpaper(bitmap);
	}




	@Override
	public void setWallpaper(InputStream data) throws IOException {
		// TODO Auto-generated method stub
		super.setWallpaper(data);
	}




	@Override
	public void startActivities(Intent[] intents) {
		// TODO Auto-generated method stub
		super.startActivities(intents);
	}




	@Override
	public void startActivity(Intent intent) {
		// TODO Auto-generated method stub
		super.startActivity(intent);
	}




	@Override
	public boolean startInstrumentation(ComponentName className,
			String profileFile, Bundle arguments) {
		// TODO Auto-generated method stub
		return super.startInstrumentation(className, profileFile, arguments);
	}




	@Override
	public void startIntentSender(IntentSender intent, Intent fillInIntent,
			int flagsMask, int flagsValues, int extraFlags)
			throws SendIntentException {
		// TODO Auto-generated method stub
		super.startIntentSender(intent, fillInIntent, flagsMask, flagsValues,
				extraFlags);
	}




	@Override
	public ComponentName startService(Intent service) {
		// TODO Auto-generated method stub
		return super.startService(service);
	}




	@Override
	public boolean stopService(Intent name) {
		// TODO Auto-generated method stub
		return super.stopService(name);
	}




	@Override
	public void unbindService(ServiceConnection conn) {
		// TODO Auto-generated method stub
		super.unbindService(conn);
	}




	@Override
	public void unregisterReceiver(BroadcastReceiver receiver) {
		// TODO Auto-generated method stub
		super.unregisterReceiver(receiver);
	}




	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}




	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return super.equals(o);
	}




	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}




	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}




	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}




	public void put(String key, Object values){
    	if(values ==null){
    		Log.d("value null!", "values is null");
    		return;
    	}
    	MessageDecodeAndEncode.put(sharedPreferences, key, values);
    }
 
    public ContactData getContact(){
        return con;
    }
    
    public void setContact(ContactData con){
    	this.con = con;
  
    }
    
    
    @Override
    public void onCreate() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        super.onCreate();
       
    }
   
   

}
