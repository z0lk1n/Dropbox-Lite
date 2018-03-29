public class Data {
    private String name;
    private String size;
    private String date;

    public Data(String name, String size, String date) {
        this.name = name;
        this.size = size;
        this.date = date;
    }

    public Data() {
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
