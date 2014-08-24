package test.shiva.org.todoapplication.pojo;

/**
 * Created by Sandeep on 8/23/2014.
 */
public class TodoItem {

    private int id;
    private String name;
    private String date;
    private String priority;
    private long creation_date;

    private boolean isHeader;

    public boolean isHeader() {
        return isHeader;
    }

    public TodoItem(String title) {
        this.name = title;
        isHeader = true;

    }

    public TodoItem(String name, String date, String priority, long creation_date) {
        this.name = name;
        this.date = date;
        this.priority = priority;
        this.creation_date = creation_date;
    }

    public TodoItem(int id, String name, String date, String priority, long creation_date) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.priority = priority;
        this.creation_date = creation_date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name + " "+ date + " "+ priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreation_date() {
        return creation_date;
    }
}
