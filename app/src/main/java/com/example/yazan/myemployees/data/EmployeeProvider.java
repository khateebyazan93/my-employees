package com.example.yazan.myemployees.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.yazan.myemployees.data.EmployeeContract.EmployeeEntry;

/**
 * Created by yazan on 2/28/17.
 */

/**
 * {@link ContentProvider} for My Employees app.
 */
public class EmployeeProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = EmployeeProvider.class.getSimpleName();


    /**
     * URI matcher code for the content URI for the employees table
     */
    private static final int EMPLOYEES = 100;

    /**
     * URI matcher code for the content URI for a single employee in the employees table
     */
    private static final int EMPLOYEES_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // This URI is used to provide access to MULTIPLE rows of the employees table.
        sUriMatcher.addURI(EmployeeContract.CONTENT_AUTHORITY, EmployeeContract.PATH_EMPLOYEES, EMPLOYEES);

        //This URI is used to provide access to ONE single row of the employees table.
        sUriMatcher.addURI(EmployeeContract.CONTENT_AUTHORITY, EmployeeContract.PATH_EMPLOYEES + "/#", EMPLOYEES_ID);

    }

    /**
     *   Database helper object
     */
    private EmployeeDbHelper mEmployeeDbHelper;

    @Override
    public boolean onCreate() {
        mEmployeeDbHelper = new EmployeeDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Get readable database
        SQLiteDatabase sqLiteDatabase = mEmployeeDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match){
            case EMPLOYEES:
                cursor = sqLiteDatabase.query(EmployeeContract.EmployeeEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,
                            sortOrder);
                break;

            case EMPLOYEES_ID:
                selection = EmployeeContract.EmployeeEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = sqLiteDatabase.query(EmployeeContract.EmployeeEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the Cursor,
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }



    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EMPLOYEES:
                return insertEmployee(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Helper method for {@link #insert} method
     * */
    private Uri insertEmployee(Uri uri, ContentValues values) {
        Log.d("insertEmployee","insertEmployee");
        if(values.containsKey(EmployeeEntry.COLUMN_EMPLOYEE_FIRST_NAME)) {
            // Check If the employee first name not null
            String employeeFirstName = values.getAsString(EmployeeEntry.COLUMN_EMPLOYEE_FIRST_NAME);
            if (employeeFirstName == null) {
                throw new IllegalArgumentException("null employee first name");
            }

        }

        if(values.containsKey(EmployeeEntry.COLUMN_EMPLOYEE_LAST_NAME)) {
            // Check If the employee last name not null
            String employeeLastName = values.getAsString(EmployeeEntry.COLUMN_EMPLOYEE_LAST_NAME);
            if (employeeLastName == null) {
                throw new IllegalArgumentException("null employee last name");
            }

        }

        if(values.containsKey(EmployeeEntry.COLUMN_EMPLOYEE_GENDER)) {
            // Check employee gender is valid
            Integer employeeGender = values.getAsInteger(EmployeeEntry.COLUMN_EMPLOYEE_GENDER);
            if (employeeGender == null || !EmployeeEntry.isValidGender(employeeGender)) {
                throw new IllegalArgumentException(" employee gender is invalid");
            }
        }

        if(values.containsKey(EmployeeEntry.COLUMN_EMPLOYEE_MOBILE_NUMBER)) {
            // Check If the employee mobile number is valid
            Integer employeeMobileNumber = values.getAsInteger(EmployeeEntry.COLUMN_EMPLOYEE_MOBILE_NUMBER);
            if (employeeMobileNumber == null || employeeMobileNumber < 0) {
                throw new IllegalArgumentException("employee mobile number is invalid");
            }
        }
        if(values.containsKey(EmployeeEntry.COLUMN_EMPLOYEE_POSITION)) {

            // Check If the employee position not null
            String employeePosition = values.getAsString(EmployeeEntry.COLUMN_EMPLOYEE_POSITION);
            if (employeePosition == null) {
                throw new IllegalArgumentException("null employee Position");
            }
        }

        // Get writeable database
        SQLiteDatabase sqLiteDatabase = mEmployeeDbHelper.getWritableDatabase();

        long id = sqLiteDatabase.insert(EmployeeEntry.TABLE_NAME,null,values);

        // If the ID is -1, then the insertion failed.
        if(id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the employee content URI
        getContext().getContentResolver().notifyChange(uri, null);


        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EMPLOYEES:
                return updateEmployee(uri, values, selection, selectionArgs);
            case EMPLOYEES_ID:

                selection = EmployeeEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateEmployee(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     *  Helper method for {@link #update} method
     */
    private int updateEmployee(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(values.containsKey(EmployeeEntry.COLUMN_EMPLOYEE_FIRST_NAME)) {
            // Check If the employee first name not null
            String employeeFirstName = values.getAsString(EmployeeEntry.COLUMN_EMPLOYEE_FIRST_NAME);
            if (employeeFirstName == null) {
                throw new IllegalArgumentException("null employee first name");
            }

        }

        if(values.containsKey(EmployeeEntry.COLUMN_EMPLOYEE_LAST_NAME)) {
            // Check If the employee last name not null
            String employeeLastName = values.getAsString(EmployeeEntry.COLUMN_EMPLOYEE_LAST_NAME);
            if (employeeLastName == null) {
                throw new IllegalArgumentException("null employee last name");
            }

        }

        if(values.containsKey(EmployeeEntry.COLUMN_EMPLOYEE_GENDER)) {
            // Check employee gender is valid
            Integer employeeGender = values.getAsInteger(EmployeeEntry.COLUMN_EMPLOYEE_GENDER);
            if (employeeGender == null || !EmployeeEntry.isValidGender(employeeGender)) {
                throw new IllegalArgumentException(" employee gender is invalid");
            }
        }

        if(values.containsKey(EmployeeEntry.COLUMN_EMPLOYEE_MOBILE_NUMBER)) {
            // Check If the employee mobile number is valid
            Integer employeeMobileNumber = values.getAsInteger(EmployeeEntry.COLUMN_EMPLOYEE_MOBILE_NUMBER);
            if (employeeMobileNumber == null || employeeMobileNumber < 0) {
                throw new IllegalArgumentException("employee mobile number is invalid");
            }
        }
        if(values.containsKey(EmployeeEntry.COLUMN_EMPLOYEE_POSITION)) {

            // Check If the employee position not null
            String employeePosition = values.getAsString(EmployeeEntry.COLUMN_EMPLOYEE_POSITION);
            if (employeePosition == null) {
                throw new IllegalArgumentException("null employee Position");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }



        // Get writeable database
        SQLiteDatabase sqLiteDatabase = mEmployeeDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = sqLiteDatabase.update(EmployeeEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;

    }

        @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
            // Get writeable database
            SQLiteDatabase database = mEmployeeDbHelper.getWritableDatabase();

            // Track the number of rows that were deleted
            int rowsDeleted;

            final int match = sUriMatcher.match(uri);
            switch (match) {
                case EMPLOYEES:
                    // Delete all rows that match the selection and selection args
                    rowsDeleted = database.delete(EmployeeEntry.TABLE_NAME, selection, selectionArgs);
                    break;

                case EMPLOYEES_ID:
                    // Delete a single row given by the ID in the URI
                    selection = EmployeeEntry._ID + "=?";
                    selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                    rowsDeleted = database.delete(EmployeeEntry.TABLE_NAME, selection, selectionArgs);
                    break;

                default:
                    throw new IllegalArgumentException("Deletion is not supported for " + uri);
            }

            // If 1 or more rows were deleted, then notify all listeners that the data at the
            // given URI has changed
            if (rowsDeleted != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }

            // Return the number of rows deleted
            return rowsDeleted;

    }




    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EMPLOYEES:
                return EmployeeEntry.CONTENT_LIST_TYPE;
            case EMPLOYEES_ID:
                return EmployeeEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
