package test.shiva.org.todoapplication.db;

/**
 * Created by Sandeep on 8/24/2014.
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import test.shiva.org.todoapplication.pojo.TodoItem;

public class TodoItemDatabaseHelper extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "TodoItems";

    // TodoItems table name
    private static final String TABLE_TODO = "todo";

    // TodoItems Table Columns names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DUE_DATE = "due_date";
    private static final String COLUMN_PRIORITY = "priority";
    private static final String COLUMN_DATE_CREATED = "date_created";

    public TodoItemDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODO_ITEMS_TABLE = "CREATE TABLE " + TABLE_TODO + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_NAME + " TEXT, "
                + COLUMN_DUE_DATE + " TEXT, "
                + COLUMN_PRIORITY + " TEXT, "
                + COLUMN_DATE_CREATED + " INTEGER" + ")";
        db.execSQL(CREATE_TODO_ITEMS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    public void addTodo(TodoItem todoItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, todoItem.getName());
        values.put(COLUMN_DUE_DATE, todoItem.getDate());
        values.put(COLUMN_PRIORITY, todoItem.getPriority());
        values.put(COLUMN_DATE_CREATED, todoItem.getCreation_date());

        // Inserting Row
        db.insert(TABLE_TODO, null, values);
        db.close(); // Closing database connection
    }

    // Getting single Item
    public TodoItem getTodoItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TODO, new String[]{COLUMN_ID,
                        COLUMN_NAME, COLUMN_DUE_DATE, COLUMN_PRIORITY, COLUMN_DATE_CREATED}, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        TodoItem todoItem = new TodoItem(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getLong(4));
        // return Item
        return todoItem;
    }

    // Getting All Items
    public ArrayList<TodoItem> getAllTodoItems() {
        ArrayList<TodoItem> todoList = new ArrayList<TodoItem>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TODO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TodoItem todoItem = new TodoItem(Integer.parseInt(cursor.getString(0)),
                        (cursor.getString(1)),
                        (cursor.getString(2)),
                        (cursor.getString(3)),
                        (cursor.getLong(4)));
                todoList.add(todoItem);
            } while (cursor.moveToNext());
        }
        return todoList;
    }

    // Updating single Item
    public int updateTodoItem(TodoItem todoItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, todoItem.getName());
        values.put(COLUMN_DUE_DATE, todoItem.getDate());
        values.put(COLUMN_PRIORITY, todoItem.getPriority());

        // updating row
        return db.update(TABLE_TODO, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(todoItem.getId())});
    }

    // Deleting single Item
    public void deleteTodoItem(TodoItem todoItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, COLUMN_ID + " = ?",
                new String[]{String.valueOf(todoItem.getId())});
        db.close();
    }

    // Getting Items Count
    public int getTodoItemsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TODO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
