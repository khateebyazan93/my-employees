package com.example.yazan.myemployees.activities;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.yazan.myemployees.R;
import com.example.yazan.myemployees.adapters.EmployeeCursorAdapter;
import com.example.yazan.myemployees.data.EmployeeContract.EmployeeEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener {

    /**
     * TAG for LOG Messages
     */
    private static final String LOG_TAG = MainActivity.class.getName();

    /**
     * Employee Loader ID
     */
    private static final int EMPLOYEE_LOADER_ID = 1;

    /**
     * Employee cursor adapter
     */
    private EmployeeCursorAdapter mEmployeeCursorAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        //find the ListView which will be populated with employee data
        ListView employeeListView = (ListView) findViewById(R.id.list);

        // show the empty view when the list view is empty
        View emptyView = findViewById(R.id.empty_view);
        employeeListView.setEmptyView(emptyView);

        //setup an adapter
        mEmployeeCursorAdapter = new EmployeeCursorAdapter(this, null);
        employeeListView.setAdapter(mEmployeeCursorAdapter);

        mEmployeeCursorAdapter.setOnItemClickListener(new EmployeeCursorAdapter.OnItemClickListener() {
            @Override
            public void onRowClick(int rowId) {
                // create new Intent to go {@link EditorActivity}
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                Uri currentEmployeeUri = ContentUris.withAppendedId(EmployeeEntry.CONTENT_URI, rowId);

                //set the URI ON THE DATA field of the intent
                intent.setData(currentEmployeeUri);

                //launch the {@link EditorActivity} to display the data for current employee
                startActivity(intent);
            }

            @Override
            public void onCallClick(int employeeMobileNumber) {

                String mobileNumber = String.valueOf(employeeMobileNumber);
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mobileNumber, null));
                startActivity(intent);

            }
        });

        //kick off the loader
        getLoaderManager().initLoader(EMPLOYEE_LOADER_ID, null, this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         //inflate  Menu Options from layout
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        //SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

/**
 *Confirmation Dialog for Deleting all Employees
 *  */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete All Employees");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the employee.
                deleteAllPets();
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

    /**
     * Helper method for {@link #showDeleteConfirmationDialog}
     * <p>
     * delete all employees in the database.
     */
    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(EmployeeEntry.CONTENT_URI, null, null);
        Log.v(LOG_TAG, rowsDeleted + " Rows deleted from Employees database");
    }

    /**
     * Create new Loader
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Define a projection that specifies the  columns from the table we care about
        String[] projection = {
                EmployeeEntry._ID,
                EmployeeEntry.COLUMN_EMPLOYEE_FIRST_NAME,
                EmployeeEntry.COLUMN_EMPLOYEE_GENDER,
                EmployeeEntry.COLUMN_EMPLOYEE_MOBILE_NUMBER,
                EmployeeEntry.COLUMN_EMPLOYEE_POSITION
        };


        // this loader will execute the contentProvider's query method on a background thread
        return new CursorLoader(this,
                EmployeeEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    /**
     * get loaded data from cursor
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link EmployeeCursorAdapter} with  this new cursor containing updated Employees data
        mEmployeeCursorAdapter.swapCursor(data);

        // mEmployeeCursorAdapter.convertToString(data);
    }

    /**
     * reset and delete loader data
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Update {@link EmployeeCursorAdapter} to null cursor when when data need to ne deleted
        mEmployeeCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mEmployeeCursorAdapter.getFilter().filter(newText);
        return true;
    }
}
