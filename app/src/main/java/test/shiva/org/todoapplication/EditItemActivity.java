package test.shiva.org.todoapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends Activity {

    private static final String EXTRA_INDEX = "index";
    private static final String EXTRA_ITEM = "item";
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        mEditText = (EditText) findViewById(R.id.editText2);
        String item = getIntent().getStringExtra(EXTRA_ITEM);
        mEditText.setText(item);
    }

    /**
     * This method is invoked on click of button (id:button2)
     * @param v
     */
    public void onSubmit(View v) {
        String item = mEditText.getText().toString();
        if (TextUtils.isEmpty(item)) {return;}
        Intent data = new Intent();
        data.putExtra(EXTRA_ITEM, item);
        data.putExtra(EXTRA_INDEX, getIntent().getIntExtra(EXTRA_INDEX, -1));
        setResult(RESULT_OK, data);
        finish();
    }
}
