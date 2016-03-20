package com.kevin.futuremeet.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by carver on 2016/3/19.
 */
public class CitiesDBHelper extends SQLiteOpenHelper {

    /**
     * I wanted to set the version to be 1,but if so,when i run the code , there is always a error
     * says that "can not downgrade the database from 3 to 1", I can not figure it out, so here we are
     */
    private static final int DB_VERSION = 3;

    private final Context mContext;


    public CitiesDBHelper(Context context) {
        super(context, CitiesContract.DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        copyDataBase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     */
    private void copyDataBase() {
        Log.i("meet database","here is copy data");
//        // Path to the just created empty db
        String adFileName = CitiesContract.DB_PATH + CitiesContract.DB_NAME;

        //code below create a database file with no data
        File dir = new File(CitiesContract.DB_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dbFile = new File(adFileName);
        if (dbFile.exists())
            dbFile.delete();

        SQLiteDatabase.openOrCreateDatabase(dbFile, null);

        try {
            // code below copy data from assets database to the database just created
            InputStream myInput = mContext.getAssets().open(CitiesContract.DB_ASSETS_NAME);
            OutputStream myOutput = new FileOutputStream(adFileName);

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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
