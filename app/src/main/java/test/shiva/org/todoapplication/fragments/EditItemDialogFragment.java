package test.shiva.org.todoapplication.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import test.shiva.org.todoapplication.R;
import test.shiva.org.todoapplication.pojo.TodoItem;

public class EditItemDialogFragment extends DialogFragment {

    private EditText mEditText;
    private static TextView mDatePicker;
    private static DatePickerDialog mDatePickerDialog;
    private Spinner mPriority;
    private static int sIndex;

    private static TodoItem sItem;
    private static int year;
    private static int month;
    private static int day;
    private OnEditItemListener mCallback;
    private Button mButton;
    private ImageButton mDatePickerButton;
    private Button mReset;

    public static EditItemDialogFragment getInstance(TodoItem item, int index){
        sIndex = index;
        sItem = item;
        return new EditItemDialogFragment();
    }

    // Container Activity must implement this interface
    public interface OnEditItemListener {
        public void onItemEdited(TodoItem item, int index);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnEditItemListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEditItemListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_edit_item, container, false);

        mDatePicker = (TextView)v.findViewById(R.id.date_picker_label);
        mPriority = (Spinner)v.findViewById(R.id.spinner1);
        mButton = (Button)v.findViewById(R.id.button2);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmit(view);
            }
        });


        mReset = (Button)v.findViewById(R.id.reset);
        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onReset(view);
            }
        });

        mDatePickerButton = (ImageButton)v.findViewById(R.id.date_picker);
        mDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

        mEditText = (EditText)v.findViewById(R.id.editText2);

        getDialog().setTitle(getString(R.string.add_item_title));

        if(sItem != null) {
            getDialog().setTitle(getString(R.string.edit_item_title));
            mEditText.setText(sItem.getName());
            ArrayAdapter myAdap = (ArrayAdapter) mPriority.getAdapter();
            int spinnerPosition = myAdap.getPosition(sItem.getPriority());
            mPriority.setSelection(spinnerPosition);
            String[] date = sItem.getDate().split("-");
             year = Integer.parseInt(date[0]);
             month = Integer.parseInt(date[1]) - 1;
             day = Integer.parseInt(date[2]);
            mDatePicker.setText(sItem.getDate());

        }
        return v;
    }

    private void onReset(View view) {
        mEditText.setText("");
        mDatePicker.setText(getString(R.string.date_picker_label));
        mPriority.setSelection(0);
    }

    /**
     * This method is invoked on click of button (id:button2)
     * @param v
     */
    public void onSubmit(View v) {
        String item = mEditText.getText().toString();
        if (TextUtils.isEmpty(item)) {return;}

        if(mCallback != null){
            TodoItem todo = new TodoItem(item, mDatePicker.getText().toString(),
                    mPriority.getSelectedItem().toString());
            mCallback.onItemEdited(todo, sIndex);
        }
        this.dismiss();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            mDatePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            mDatePickerDialog.getDatePicker().setMinDate(new Date().getTime());
            return mDatePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            final Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            CharSequence s = DateFormat.format("yyyy-MM-dd", c.getTime());
            mDatePicker.setText(s);
        }
    }
}
