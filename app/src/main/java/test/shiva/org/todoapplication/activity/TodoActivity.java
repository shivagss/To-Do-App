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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import test.shiva.org.todoapplication.R;
import test.shiva.org.todoapplication.fragments.EditItemDialogFragment;
import test.shiva.org.todoapplication.pojo.TodoItem;

public class TodoActivity extends Activity implements EditItemDialogFragment.OnEditItemListener {

    private static final String TODO_ITEMS_FILE = "todo.txt";

    private ArrayList<TodoItem> mListItems;
    private ArrayAdapter<TodoItem> mListAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mListView = (ListView) findViewById(R.id.listView2);

        readItems();
        mListAdapter = new TodoItemsAdapter(getBaseContext(), mListItems);

        mListView.setAdapter(mListAdapter);
        setupListViewListener();
    }

    private void readItems() {
        File fileDir = getFilesDir();
        File todoFile = new File(fileDir, TODO_ITEMS_FILE);

        mListItems = new ArrayList<TodoItem>();

        try {
            ArrayList<String> list = new ArrayList<String>(FileUtils.readLines(todoFile));
            for(String s : list){
                mListItems.add(TodoItem.readFromFile(s));
            }
        } catch (IOException e) {
            //DO NOTHING
        }
    }

    private void writeItems() {
        File fileDir = getFilesDir();
        File todoFile = new File(fileDir, TODO_ITEMS_FILE);

        try {
            FileUtils.writeLines(todoFile, mListItems);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupListViewListener() {
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                mListItems.remove(i);
                updateListAndPersist();
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
            mListItems.clear();
            updateListAndPersist();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateListAndPersist() {
        mListAdapter.notifyDataSetChanged();
        writeItems();
    }

    @Override
    public void onItemEdited(TodoItem item, int index) {
        if (index >= 0 && index < mListItems.size()) {
            mListItems.set(index, item);
            updateListAndPersist();
        }

        if (index >= mListItems.size()) {
            mListAdapter.add(item);
            updateListAndPersist();
        }
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
            viewHolder.desc.setText(item.getDate());

            int priority = android.R.drawable.star_off;
            viewHolder.priority.setVisibility(View.VISIBLE);

            if(item.getPriority().equals("High")){
                priority = android.R.drawable.star_on;
            }else if(item.getPriority().equals("Low")){
                priority = android.R.drawable.star_off;
            }else{
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

            switch (getDayofWeek(date)){
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
                e.printStackTrace();
            }
            return 1;
        }
    }
}
