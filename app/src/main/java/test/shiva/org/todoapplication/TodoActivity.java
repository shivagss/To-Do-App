package test.shiva.org.todoapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TodoActivity extends Activity {

    private static final String TODO_ITEMS_FILE = "todo.txt";
    private static final String EXTRA_INDEX = "index";
    private static final String EXTRA_ITEM = "item";
    private static final int REQUEST_CODE = 1;

    private ArrayList<String> mListItems;
    private ArrayAdapter<String> mListAdapter;
    private ListView mListView;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mEditText = (EditText) findViewById(R.id.editText5);
        mListView = (ListView) findViewById(R.id.listView2);

        readItems();
        mListAdapter = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_list_item_1, mListItems);

        mListView.setAdapter(mListAdapter);
        setupListViewListener();
    }

    private void readItems() {
        File fileDir = getFilesDir();
        File todoFile = new File(fileDir, TODO_ITEMS_FILE);

        try {
            mListItems = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            mListItems = new ArrayList<String>();
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

    /**
     * This method is invoked on click of button (id:button)
     * @param v
     */
    public void onAddItem(View v) {
        String item = mEditText.getText().toString();
        if (!TextUtils.isEmpty(item)) {
            mListAdapter.add(item);
            mEditText.setText("");
            writeItems();
        }
    }

    private void launchEditActivity(int index, String item) {
        Intent i = new Intent(this, EditItemActivity.class);
        i.putExtra(EXTRA_ITEM, item);
        i.putExtra(EXTRA_INDEX, index);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String name = data.getExtras().getString("item");
            int index = data.getIntExtra("index", -1);

            if (index >= 0 && index < mListItems.size()) {
                mListItems.set(index, name);
                updateListAndPersist();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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
}
