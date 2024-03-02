package shared;

public class Client {
    @Id
    private int id;
    private String name;
    private String lastName;
    private int phoneNUmber;
    private String adress;

    public Client() {
    }

    public Client(String name, String lastName, int phoneNUmber, String adress) {
        this.name = name;
        this.lastName = lastName;
        this.phoneNUmber = phoneNUmber;
        this.adress = adress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getPhoneNUmber() {
        return phoneNUmber;
    }

    public void setPhoneNUmber(int phoneNUmber) {
        this.phoneNUmber = phoneNUmber;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNUmber=" + phoneNUmber +
                ", adress='" + adress + '\'' +
                ", id=" + id +
                '}';
    }
}
