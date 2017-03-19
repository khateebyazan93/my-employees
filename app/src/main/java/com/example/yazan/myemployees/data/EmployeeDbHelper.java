package com.example.yazan.myemployees.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import  com.example.yazan.myemployees.data.EmployeeContract.EmployeeEntry;
/**
 * Created by yazan on 2/27/17.
 */

public class EmployeeDbHelper extends SQLiteOpenHelper {

    /**
     * TAG For Log messages*/
    private static final String LOG_TAG = EmployeeDbHelper.class.getName();

    /**\
     * Name of database file
     * */
    private static final String DATABASE_NAME = "contacts_list.db";

    /**
     * Database version
     * */
    private static final int DATABASE_VERSION = 1;


/**
 * Constructs a new instance of {@link EmployeeDbHelper}
 *
 * @param context of the app
 * */
    public EmployeeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * called the first time when the database is created  */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the employees table
        String SQL_CREATE_EMPLOYEES_TABLE = "CREATE TABLE " + EmployeeEntry.TABLE_NAME + "("
                + EmployeeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EmployeeEntry.COLUMN_EMPLOYEE_FIRST_NAME +  " TEXT NOT NULL,"
                + EmployeeEntry.COLUMN_EMPLOYEE_LAST_NAME +  " TEXT NOT NULL, "
                + EmployeeEntry.COLUMN_EMPLOYEE_GENDER + " INTEGER NOT NULL, "
                + EmployeeEntry.COLUMN_EMPLOYEE_MOBILE_NUMBER + " INTEGER NOT NULL, "
                + EmployeeEntry.COLUMN_EMPLOYEE_POSITION + " TEXT NOT NULL);" ;


// Execute the SQL statement
        db.execSQL(SQL_CREATE_EMPLOYEES_TABLE);




    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
