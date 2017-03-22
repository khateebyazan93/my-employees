package com.example.yazan.myemployees.activities;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.yazan.myemployees.R;
import com.example.yazan.myemployees.data.EmployeeContract.EmployeeEntry;

/**
 * Created by yazan on 2/27/17.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * TAG FOR LOG Messages
     */
    private static final String LOG_TAG = EditorActivity.class.getName();

    /**
     * Loader ID
     */
    private static final int EMPLOYEE_LOADER_ID = 2;

    /**
     * Content URI for the existing Employee (null if it's a new Employee)
     */
    private Uri mCurrentEmployeeUri;


    /**
     * EditText field to enter the employee first name
     */
    private EditText mEmployeeFirstNameEditText;

    /**
     * EditText field to enter the employee last name
     */
    private EditText mEmployeeLastNameEditText;

    /**
     * EditText field to enter the employee's gender
     */
    private Spinner mEmployeeGenderSpinner;

    /**
     * EditText field to enter the employee mobile number
     */
    private EditText mEmployeeMobileNumberEditText;

    /**
     * EditText field to enter the employee position
     */
    private EditText mEmployeePositionEditText;

    /**
     * Gender of the employee
     */
    private int mEmployeeGender = EmployeeEntry.GENDER_UNKNOWN;

    /**
     * Boolean flag that keeps track of whether the employee has been edited (true) or not (false)
     */
    private boolean mEmployeeHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view
     */
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            mEmployeeHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("onCreate","onCreate onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        // check the intent that was used to launch this activity,
        // in order to figure out if we're creating a new employee or editing an existing one.
        Intent intent = getIntent();
        mCurrentEmployeeUri = intent.getData();

        if (mCurrentEmployeeUri == null) {

            // This is a new Employee, so change the app bar to "Add new Employee"
            setTitle("Add new Employee");

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing pet, so change app bar to say "Edit Pet"
            setTitle("Edit the Employee");

            // Initialize a loader to read the Employee data from the database
            // and display the current values in the EditTexts
           getLoaderManager().initLoader(EMPLOYEE_LOADER_ID, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mEmployeeFirstNameEditText = (EditText) findViewById(R.id.first_name_edit_text);
        mEmployeeLastNameEditText = (EditText) findViewById(R.id.last_name_edit_text);
        mEmployeeGenderSpinner = (Spinner) findViewById(R.id.gender_spinner);
        mEmployeeMobileNumberEditText = (EditText) findViewById(R.id.mobile_number_edit_text);
        mEmployeePositionEditText = (EditText) findViewById(R.id.position_edit_text);

        //setup touch listener to all  relevant views that we will need to read user input from
        mEmployeeFirstNameEditText.setOnTouchListener(mOnTouchListener);
        mEmployeeLastNameEditText.setOnTouchListener(mOnTouchListener);
        mEmployeeGenderSpinner.setOnTouchListener(mOnTouchListener);
        mEmployeeMobileNumberEditText.setOnTouchListener(mOnTouchListener);
        mEmployeePositionEditText.setOnTouchListener(mOnTouchListener);


        setupEmployeeGenderSpinner();
    }

    /**
     * Helper method for {@link #onCreate}
     * <p>
     * Setup the dropdown spinner that allows the user to select the gender of the employee.
     */
    private void setupEmployeeGenderSpinner() {

        // Create adapter for spinner
        ArrayAdapter employeeGenderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_employee_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        employeeGenderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // setup the adapter to the spinner
        mEmployeeGenderSpinner.setAdapter(employeeGenderSpinnerAdapter);

        mEmployeeGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //get correct choice(String) in the correct position
                String selection = (String) parent.getItemAtPosition(position);

                //check user selection
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals("male")) {
                        mEmployeeGender = EmployeeEntry.GENDER_MALE;
                    } else if (selection.equals("female")) {
                        mEmployeeGender = EmployeeEntry.GENDER_FEMALE;

                    } else {
                        mEmployeeGender = EmployeeEntry.GENDER_UNKNOWN;

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mEmployeeGender = EmployeeEntry.GENDER_UNKNOWN;
            }
        });

    }

    /**
     * Get user input from EditText and save Employee data into database.
     */
    private void saveEmployee() {

        //read user input
        String employeeFirstName = mEmployeeFirstNameEditText.getText().toString().trim();
        String employeeLastName = mEmployeeLastNameEditText.getText().toString().trim();
        String employeeMobileNumber = mEmployeeMobileNumberEditText.getText().toString().trim();
        String employeePosition = mEmployeePositionEditText.getText().toString().trim();

        //check if all the fields in the EditText are blank, then exit
        if ((TextUtils.isEmpty(employeeFirstName) || TextUtils.isEmpty(employeeLastName) ||
                TextUtils.isEmpty(employeeMobileNumber) || TextUtils.isEmpty(employeePosition))&&
                mEmployeeGender == EmployeeEntry.GENDER_UNKNOWN) {

        Toast.makeText(this,"Some Fields Empty",Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and Employee attributes from the TextEditor are the values.
        ContentValues employeeData = new ContentValues();
        employeeData.put(EmployeeEntry.COLUMN_EMPLOYEE_FIRST_NAME, employeeFirstName);
        employeeData.put(EmployeeEntry.COLUMN_EMPLOYEE_LAST_NAME, employeeLastName);
        employeeData.put(EmployeeEntry.COLUMN_EMPLOYEE_GENDER, mEmployeeGender);
        employeeData.put(EmployeeEntry.COLUMN_EMPLOYEE_POSITION, employeePosition);

        // If the mobile number is not provided by the user Use 0 by default.
        int mobileNumber = 0;
        if (!TextUtils.isEmpty(employeeMobileNumber)) {
            mobileNumber = Integer.parseInt(employeeMobileNumber);
        }
        employeeData.put(EmployeeEntry.COLUMN_EMPLOYEE_MOBILE_NUMBER, mobileNumber);

        // check if this is a new or existing employee by checking if mCurrentEmployeeUri is null or not
        if (mCurrentEmployeeUri == null) {
            Uri newUri = getContentResolver().insert(EmployeeEntry.CONTENT_URI, employeeData);

            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Error with save new Employee", Toast.LENGTH_SHORT).show();
            } else {

                // Otherwise, the insertion was successful
                Toast.makeText(this, "new Employee saved", Toast.LENGTH_SHORT).show();

            }
        } else {
            // Otherwise this is an EXISTING employee, so update the current employee with content URI: mCurrentEmployeeUri
            int rowsAffected = getContentResolver().update(mCurrentEmployeeUri, employeeData, null, null);

            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Error with updating Current Employee", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful
                Toast.makeText(this, "Employee updated", Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    private boolean isEditTextHaschanged() {
        //read user input
        String employeeFirstName = mEmployeeFirstNameEditText.getText().toString().trim();
        String employeeLastName = mEmployeeLastNameEditText.getText().toString().trim();
        String employeeMobileNumber = mEmployeeMobileNumberEditText.getText().toString().trim();
        String employeePosition = mEmployeePositionEditText.getText().toString().trim();

        //check if all the fields in the EditText are blank, then return false
        if (mCurrentEmployeeUri == null &&
                TextUtils.isEmpty(employeeFirstName) && TextUtils.isEmpty(employeeLastName) &&
                TextUtils.isEmpty(employeeMobileNumber) && TextUtils.isEmpty(employeePosition) &&
                mEmployeeGender == EmployeeEntry.GENDER_UNKNOWN) {


            return false;
        } else {
            return true;
        }

    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new employee, hide the "Delete" menu item.
        if (mCurrentEmployeeUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                //save new employee
                saveEmployee();
                // Exit EditorActivity

                return true;

            case R.id.action_delete:
                // Pop up/show confirmation dialog for deleting
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!mEmployeeHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this Employee");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the employee.
                deleteEmployee();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    private void showUnsavedChangesDialog() {
        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // User clicked "Discard" button, navigate to parent activity.
                NavUtils.navigateUpFromSameTask(EditorActivity.this);

            }
        });
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void deleteEmployee() {
        // Only perform the delete if this is an existing employee.
        if (mCurrentEmployeeUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentEmployeeUri, null, null);

            if (rowsDeleted == 0) {
                // If no rows were deleted
                Toast.makeText(this, "Deleting failed", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful
                Toast.makeText(this, "Deleting successful", Toast.LENGTH_SHORT).show();
            }
        }

        // Exit EditorActivity
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                EmployeeEntry._ID,
                EmployeeEntry.COLUMN_EMPLOYEE_FIRST_NAME,
                EmployeeEntry.COLUMN_EMPLOYEE_LAST_NAME,
                EmployeeEntry.COLUMN_EMPLOYEE_GENDER,
                EmployeeEntry.COLUMN_EMPLOYEE_MOBILE_NUMBER,
                EmployeeEntry.COLUMN_EMPLOYEE_POSITION,
        };

        return new CursorLoader(this,
                mCurrentEmployeeUri,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //  if the cursor is null or there is less than 1 row in the cursor , then exit
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int employeeFirstNameColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_EMPLOYEE_FIRST_NAME);
            int employeeLastNameColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_EMPLOYEE_LAST_NAME);
            int employeeGenderColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_EMPLOYEE_GENDER);
            int employeeMobileNumberColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_EMPLOYEE_MOBILE_NUMBER);
            int employeePositionColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_EMPLOYEE_POSITION);


            // Extract out the value from the Cursor for the given column index
            String employeeFirstName = cursor.getString(employeeFirstNameColumnIndex);
            String employeeLastName = cursor.getString(employeeLastNameColumnIndex);
            int employeeGender = cursor.getInt(employeeGenderColumnIndex);
            int employeeMobileNumber = cursor.getInt(employeeMobileNumberColumnIndex);
            String employeePosition = cursor.getString(employeePositionColumnIndex);


            // Update the views on the screen with the values from the database
            mEmployeeFirstNameEditText.setText(employeeFirstName);
            mEmployeeLastNameEditText.setText(employeeLastName);
            mEmployeeMobileNumberEditText.setText(Integer.toString(employeeMobileNumber));
            mEmployeePositionEditText.setText(employeePosition);


            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (employeeGender) {
                case EmployeeEntry.GENDER_MALE:
                    mEmployeeGenderSpinner.setSelection(1);
                    break;
                case EmployeeEntry.GENDER_FEMALE:
                    mEmployeeGenderSpinner.setSelection(2);
                    break;
                default:
                    mEmployeeGenderSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // If the loader is invalidated, clear out all the data from the input fields.
        mEmployeeFirstNameEditText.setText("");
        mEmployeeLastNameEditText.setText("");
        mEmployeeGenderSpinner.setSelection(0);
        mEmployeeMobileNumberEditText.setText("");
        mEmployeePositionEditText.setText(""); // Select "Unknown" gender


    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {

        // If the pet hasn't changed, continue with handling back button press
        if (mEmployeeHasChanged) {
            showUnsavedChangesDialog();
            //super.onBackPressed();
            return;
        }
        super.onBackPressed();
}


}









