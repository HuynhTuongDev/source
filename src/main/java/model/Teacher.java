package model;


public class Teacher {
    private int id;
    protected String name;
    protected String address;
    protected String password;
    protected String phone_number;
    protected String course;

    public Teacher(int id, String name, String address, String password, String phone_number, String course) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.password = password;
        this.phone_number = phone_number;
        this.course = course;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Teacher(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public String toString() {
        return "Teacher{" + "id=" + id + ", name=" + name + ", address=" + address + ", password=" + password + ", phone_number=" + phone_number + ", course=" + course + '}';
    }
    
}
