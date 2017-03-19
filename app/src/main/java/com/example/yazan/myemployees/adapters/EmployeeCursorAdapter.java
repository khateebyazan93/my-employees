package com.example.yazan.myemployees.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yazan.myemployees.R;
import com.example.yazan.myemployees.data.EmployeeContract.EmployeeEntry;

/**
 * Created by yazan on 2/27/17.
 */

public class EmployeeCursorAdapter extends CursorAdapter implements Filterable {
    /**
     * Listener for list item
     */
    private static OnItemClickListener mListener;
    /**
     * App Context
     */
    Context mContext;
    /**
     * Filter list of Employees based on first name position
     */
    EmployeeFilter mEmployeeFilter;
    FilterQueryProvider mFilterQueryProvider = new FilterQueryProvider() {
        /**
         * Helper method for {@link EmployeeCursorAdapter#runQueryOnBackgroundThread(CharSequence)}
         *
         * perform user query on the database
         *
         * @param constraint is a word that user search for
         * @return Cursor contain rows relevant to user query
         * */
        @Override
        public Cursor runQuery(CharSequence constraint) {
            if (constraint == null || constraint.length() == 0) {
                return mContext.getContentResolver().query(
                        EmployeeEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                );
            } else {
                return mContext.getContentResolver().query(
                        EmployeeEntry.CONTENT_URI,
                        null,
                        EmployeeEntry.COLUMN_EMPLOYEE_FIRST_NAME + " LIKE ? OR " + EmployeeEntry.COLUMN_EMPLOYEE_POSITION + " LIKE ? ",
                        new String[]{"%" + constraint + "%", "%" + constraint + "%"},
                        null
                );
            }
        }

    };

    /**
     * Constructs a new {@link EmployeeCursorAdapter} object.
     *
     * @param context The context
     * @param cursor  The cursor from which to get the data.
     */
    public EmployeeCursorAdapter(Context context, Cursor cursor) {

        super(context, cursor, 0 /* flags */);
        mContext = context;
    }

    // Define the method that allows to define the listener in activity class
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    /**
     * Makes a new blank list item view
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_employee, parent, false);
    }

    /**
     * binds the employee data (in the current row pointed to by cursor) to the given
     * list item layout
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        // Find individual views that we want to modify in the list item layout
        ImageView employeeIconImageView = (ImageView) view.findViewById(R.id.employee_icon_image_view);
        TextView employeeFirstNameTextView = (TextView) view.findViewById(R.id.first_name_text_view);
        TextView employeePositionTextView = (TextView) view.findViewById(R.id.position_text_view);
        View rowContainerView = view.findViewById(R.id.row_container);
        ImageView callIconImageView = (ImageView) view.findViewById(R.id.call_icon);

        employeeIconImageView.setTag(1);

        // Find the columns of employee attributes that we're interested in
        int _idColumnIndex = cursor.getColumnIndex(EmployeeEntry._ID);
        int genderColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_EMPLOYEE_GENDER);
        int firstNameColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_EMPLOYEE_FIRST_NAME);
        int mobileNumberColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_EMPLOYEE_MOBILE_NUMBER);
        int positionColumnIndex = cursor.getColumnIndex(EmployeeEntry.COLUMN_EMPLOYEE_POSITION);

        // Read the employee attributes from the Cursor for the current employee
        final int _id = cursor.getInt(_idColumnIndex);
        String employeeFirstName = cursor.getString(firstNameColumnIndex);
        String employeePosition = cursor.getString(positionColumnIndex);
        final int employeeMobileNumber = cursor.getInt(mobileNumberColumnIndex);
        int employeeGender = cursor.getInt(genderColumnIndex);

        //set fist name and the Position for current list item
        employeeFirstNameTextView.setText(employeeFirstName);
        employeePositionTextView.setText(employeePosition);

        //check for employee gender to set proper icon
        if (employeeGender == EmployeeEntry.GENDER_MALE) {
            employeeIconImageView.setImageResource(R.drawable.male);
        } else if (employeeGender == EmployeeEntry.GENDER_FEMALE) {
            employeeIconImageView.setImageResource(R.drawable.female);

        } else {
            employeeIconImageView.setImageResource(R.drawable.unknwn);

        }

        rowContainerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRowClick(_id);
            }
        });


        callIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCallClick(employeeMobileNumber);
            }
        });


    }


    @Override
    public Filter getFilter() {
        if (mEmployeeFilter == null) {
            mEmployeeFilter = new EmployeeFilter();
        }

        return mEmployeeFilter;
    }

    /**
     * Helper method for {@link EmployeeFilter#performFiltering(CharSequence)} method
     * <p>
     * Run user query
     *
     * @param constraint is a word that user search for
     * @return Cursor contain rows relevant to user query
     */
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if (mFilterQueryProvider != null) {
            return mFilterQueryProvider.runQuery(constraint);
        }

        return getCursor();
    }

    // listener interface
    public interface OnItemClickListener {
        void onRowClick(int rowId);

        void onCallClick(int employeeMobileNumber);
    }

    /**
     * class to filter user query on the list item
     */
    private class EmployeeFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Cursor cursor = runQueryOnBackgroundThread(constraint);

            FilterResults results = new FilterResults();
            if (cursor != null) {
                results.count = cursor.getCount();
                results.values = cursor;
            } else {
                results.count = 0;
                results.values = null;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Cursor oldCursor = getCursor();

            if (results.values != null && results.values != oldCursor) {
                swapCursor((Cursor) results.values);
            }
        }
    }
}
