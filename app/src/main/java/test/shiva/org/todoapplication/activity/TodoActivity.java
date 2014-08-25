package test.shiva.org.todoapplication.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import test.shiva.org.todoapplication.R;
import test.shiva.org.todoapplication.db.TodoItemDatabaseHelper;
import test.shiva.org.todoapplication.fragments.EditItemDialogFragment;
import test.shiva.org.todoapplication.pojo.TodoItem;

public class TodoActivity extends Activity implements EditItemDialogFragment.OnEditItemListener {

    private ArrayList<TodoItem> mListItems;
    private ArrayAdapter<TodoItem> mListAdapter;
    private ListView mListView;
    private TodoItemDatabaseHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDBHelper = new TodoItemDatabaseHelper(this);

        mListView = (ListView) findViewById(R.id.listView2);
        mListView.setEmptyView(findViewById(android.R.id.empty));

        readItems();
        mListAdapter = new TodoItemsAdapter(getBaseContext(), mListItems);

        mListView.setAdapter(mListAdapter);
        setupListViewListener();
    }

    private void readItems() {
        mListItems = mDBHelper.getAllTodoItems();
    }

    private void setupListViewListener() {
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                mDBHelper.deleteTodoItem(mListItems.get(i));
                refreshList();
                return true;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                launchEditActivity(i, mListItems.get(i));
            }
        });
    }

    private void launchEditActivity(int index, TodoItem item) {
        DialogFragment dialogFrag = EditItemDialogFragment.
                getInstance(item, index);
        dialogFrag.show(getFragmentManager(), "dialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            launchEditActivity(mListItems.size(), null);
            return true;
        }
        if (id == R.id.clear) {
            for (TodoItem todoitem : mListItems) {
                mDBHelper.deleteTodoItem(todoitem);
            }
            refreshList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addNewTodoItem(View v) {
        launchEditActivity(mListItems.size(), null);
    }

    @Override
    public void refreshList() {
        mListItems.clear();
        mListItems.addAll(mDBHelper.getAllTodoItems());
        mListAdapter.notifyDataSetChanged();
    }

    public class TodoItemsAdapter extends ArrayAdapter<TodoItem> {
        // View lookup cache
        private class ViewHolder {
            TextView name;
            TextView desc;
            ImageButton priority;
            TextView color_code;
        }

        public TodoItemsAdapter(Context context, ArrayList<TodoItem> items) {
            super(context, R.layout.item_row, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            TodoItem item = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_row, parent, false);
                viewHolder.name = (TextView) convertView.findViewById(R.id.textView);
                viewHolder.desc = (TextView) convertView.findViewById(R.id.textView2);
                viewHolder.priority = (ImageButton) convertView.findViewById(R.id.imageButton);
                viewHolder.color_code = (TextView) convertView.findViewById(R.id.color_code);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data into the template view using the data object
            viewHolder.name.setText(item.getName());
            if(!item.getDate().equals("No Due")) {
                long days = daysBetween(parseStringDate(item.getDate()));
                if (days < 0) {
                    viewHolder.desc.setText(getString(R.string.due_expired));
                } else {
                    viewHolder.desc.setText((MessageFormat.format(getString(R.string.due_days), days)));
                }
            }else{
                viewHolder.desc.setText(item.getDate());
            }

            int priority = android.R.drawable.star_off;
            viewHolder.priority.setVisibility(View.VISIBLE);

            if (item.getPriority().equals("High")) {
                priority = android.R.drawable.star_on;
            } else if (item.getPriority().equals("Low")) {
                priority = android.R.drawable.star_off;
            } else {
                viewHolder.priority.setVisibility(View.INVISIBLE);
            }
            viewHolder.priority.setImageResource(priority);
            viewHolder.priority.setFocusable(false);

            int color = getColorCode(item.getDate());
            viewHolder.color_code.setBackgroundColor(color);
            // Return the completed view to render on screen
            return convertView;
        }

        private int getColorCode(String date) {

            switch (getDayofWeek(date)) {
                case 1:
                    return Color.YELLOW;
                case 2:
                    return Color.BLUE;
                case 3:
                    return Color.GREEN;
                case 4:
                    return Color.RED;
                case 5:
                    return Color.GRAY;
                case 6:
                    return Color.DKGRAY;
                case 7:
                    return Color.LTGRAY;
            }
            return Color.WHITE;
        }

        private int getDayofWeek(String duedate) {
            try {
                final Calendar c = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = format.parse(duedate);
                System.out.println(date);
                c.setTime(date);
                return c.get(Calendar.DAY_OF_WEEK);
            } catch (ParseException e) {
            }
            return 0;
        }

        private Calendar parseStringDate(String duedate) {
            try {
                final Calendar c = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = format.parse(duedate);
                System.out.println(date);
                c.setTime(date);
                return c;
            } catch (ParseException e) {
            }
            return null;
        }
    }

    public static long daysBetween(Calendar endDate) {
        Calendar date = Calendar.getInstance();
        long daysBetween = 0;
        while (date.before(endDate)) {
            date.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }
}
