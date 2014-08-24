package test.shiva.org.todoapplication.pojo;

/**
 * Created by Sandeep on 8/23/2014.
 */
public class TodoItem {

    private String name;
    private String date;
    private String priority;
    private boolean isHeader;

    public boolean isHeader() {
        return isHeader;
    }

    public TodoItem(String title) {
        this.name = title;
        isHeader = true;

    }

    public TodoItem(String name, String date, String priority) {
        this.name = name;
        this.date = date;
        this.priority = priority;
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

    public static TodoItem readFromFile(String line){
        String[] arr = line.split(" ");
        int length = arr.length;
        if(length >= 3){
            String priority = arr[length - 1];
            String date = arr[length - 2];
            String name = "";
            for(int i = 0; i<= length - 3; i++){
                name += arr[i];
                if(i < length - 3){
                    name += " ";
                }
            }
            return new TodoItem(name,date,priority);
        }
        return null;
    }
}
