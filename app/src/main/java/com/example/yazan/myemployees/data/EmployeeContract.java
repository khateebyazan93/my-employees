package com.example.yazan.myemployees.data;

/**
 * Created by yazan on 2/27/17.
 */

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for My Employees App
 */
public final class EmployeeContract {
    /**
     * "Content authority"
     */
    public static final String CONTENT_AUTHORITY = "com.example.yazan.myemployees";

    /**
     * base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * represent table
     */
    public static final String PATH_EMPLOYEES = "employees";

    /**
     * private constructor
     */
    private EmployeeContract() {
    }

    /**
     * Inner class that defines constant values for the employees database table.
     * Each entry in the table represents a single employee.
     */
    public static final class EmployeeEntry implements BaseColumns {

        /**
         * The content URI to access to the employee data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EMPLOYEES);

        /**
         * the MIME type of the {@link #CONTENT_URI} for a list of Employees
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EMPLOYEES;

        /**
         * the MIME type of the {@link #CONTENT_URI} for a single Employee
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EMPLOYEES;

        /**
         * Name for the database Table for Employees
         */
        public static final String TABLE_NAME = "employees";

        /**
         * unique ID number for employee (only for use in the database table)
         * <p>
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         * First Name of employee
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_EMPLOYEE_FIRST_NAME = "first_name";

        /**
         * Last Name of employee
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_EMPLOYEE_LAST_NAME = "last_name";

        /**
         * Possible values for the gender of the employee.
         */
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

        /**
         * Gender of the employee
         * <p>
         * the only possible values are {@link #GENDER_UNKNOWN}, {@link #GENDER_MALE},
         * {@link #GENDER_FEMALE}.
         * <p>
         * Type: INTEGER
         */
        public static final String COLUMN_EMPLOYEE_GENDER = "gender";

        /**
         * mobile number of employee
         * <p>
         * Type: INTEGER
         */
        public static final String COLUMN_EMPLOYEE_MOBILE_NUMBER = "mobile_number";


        /**
         * Position of employee
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_EMPLOYEE_POSITION = "position";

        /**
         * Returns whether or not the given gender is {@link #GENDER_UNKNOWN}, {@link #GENDER_MALE},
         * or {@link #GENDER_FEMALE}.
         */
        public static boolean isValidGender(int gender) {
            if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;
        }

    }

}
