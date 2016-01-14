package com.mobiwebcode.Rodeo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLitedbHelper extends SQLiteOpenHelper {
	
	 private static final int DATABASE_VERSION = 1;
	    private static final String DATABASE_PATH = "/data/data/com.Syneotek.Rodeo/databases/";
	    // Database Name
	    private static final String DATABASE_NAME = "Rodeo.sqlite";
	 // Contacts table name
	    private static final String TABLE_RODEO = "rodeodetails";
	    private static final String TABLE_EVENTS = "events";
	    private static final String TABLE_CONTESTANTS="contestants";
	    
	 private SQLiteDatabase db;
	    // Contacts Table Columns names
	    private static final String R_ID = "rodeoid";
	    private static final String R_NAME = "rodeoname";
	    private static final String R_LOCATION = "location";
	    private static final String R_DATE = "rodeostartdate";
	    private static final String R_ROUNDS = "numberofrounds";
	    private static final String R_STARTED = "isstarted";
	    private static final String R_SERVERRODEO = "serverrodeo";
	    Context ctx;
		
	    public SQLitedbHelper(Context context) {
	       super(context, DATABASE_NAME, null, DATABASE_VERSION);
	       ctx = context;
	    }
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}
	  
	   

public void create() throws IOException {
	 boolean check = checkDataBase();

	 SQLiteDatabase db = null;

	 // Creates empty database default system path
	 db = this.getWritableDatabase();
	 db.close();
	 try {
	      if (!check) {
	  copyDataBase();
	   }
	      } catch (IOException e) {
	  throw new Error("Error copying database");
	 }
	}



private boolean checkDataBase() {
	 SQLiteDatabase checkDB = null;
	 try {
	  String myPath = DATABASE_PATH + DATABASE_NAME;
	  checkDB = SQLiteDatabase.openDatabase(myPath, null,
	   SQLiteDatabase.OPEN_READWRITE);
	 } catch (SQLiteException e) {
	  // database does't exist yet.
	 }

	 if (checkDB != null) {
	  checkDB.close();
	 }
	 return checkDB != null ? true : false;
	}
private void copyDataBase() throws IOException {

// Open your local db as the input stream
InputStream myInput = ctx.getAssets().open(DATABASE_NAME);

// Path to the just created empty db
String outFileName = DATABASE_PATH + DATABASE_NAME;

// Open the empty db as the output stream
OutputStream myOutput = new FileOutputStream(outFileName);

// transfer bytes from the inputfile to the outputfile
byte[] buffer = new byte[1024];
int length;
while ((length = myInput.read(buffer)) > 0) {
 myOutput.write(buffer, 0, length);
}

// Close the streams
myOutput.flush();
myOutput.close();
myInput.close();

}
/** open the database */
public void open() throws SQLException {
 String myPath = DATABASE_PATH + DATABASE_NAME;
 db = SQLiteDatabase.openDatabase(myPath, null,
  SQLiteDatabase.OPEN_READWRITE);
}

/** close the database */
@Override
public synchronized void close() {
 if (db != null)
  db.close();
 super.close();
}
public long insertUser(long rodeoid,String rodeoname,String location,String rodeostartdate, String numberofrounds, String isstarted,String serverrodeo) {
	 ContentValues initialValues = new ContentValues();
	 initialValues.put(R_ID, rodeoid);
	 initialValues.put(R_NAME, rodeoname);
	 initialValues.put(R_LOCATION, location);
	 initialValues.put(R_DATE, rodeostartdate);
	 initialValues.put(R_ROUNDS, numberofrounds);
	 initialValues.put(R_STARTED, isstarted);
	 initialValues.put(R_SERVERRODEO, serverrodeo);
	 return db.insert(TABLE_RODEO, null, initialValues);
	}
//updates a user
public boolean updateUser(long rodeoid,String rodeoname,String location,String rodeostartdate, String numberofrounds, String isstarted,String serverrodeo) {
ContentValues args = new ContentValues();
args.put(R_ID, rodeoid);
args.put(R_NAME, rodeoname);
args.put(R_LOCATION, location);
args.put(R_DATE, rodeostartdate);
args.put(R_ROUNDS, numberofrounds);
args.put(R_STARTED, isstarted);
args.put(R_SERVERRODEO, serverrodeo);

return db.update(TABLE_RODEO, args, R_ID + "=" + rodeoid, null) > 0;
}
//retrieves a particular user
public Cursor getUser(long rowId) throws SQLException {
Cursor mCursor = db.query(true, TABLE_RODEO, new String[] {
R_ID, R_NAME, R_LOCATION, R_DATE,R_ROUNDS,R_STARTED,R_SERVERRODEO },
R_ID + " = " + rowId, null, null, null, null, null);
if (mCursor != null) {
mCursor.moveToFirst();
}

return mCursor;
}

//retrieves all users
public Cursor getAllUsers() {
return db.query(TABLE_RODEO, new String[] { R_ID, R_NAME, R_LOCATION, R_DATE,R_ROUNDS,R_STARTED,R_SERVERRODEO
 }, null, null,
null, null, null);
}
public boolean deleteContact(int i) {
	// TODO Auto-generated method stub
	return false;
}
public void updateUser(int i, String string, String string2, String string3) {
	// TODO Auto-generated method stub
	
}
}