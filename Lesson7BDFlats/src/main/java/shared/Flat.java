package shared;

public class Flat {
    @Id
    private int id;
    private String district;
    private int area;
    private int rooms;
    private int price;

    public Flat(String district, int area, int rooms, int price) {
        this.district = district;
        this.area = area;
        this.rooms = rooms;
        this.price = price;
    }

    public Flat() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Flat{" +
                "id=" + id +
                ", district='" + district + '\'' +
                ", area=" + area +
                ", rooms=" + rooms +
                ", price=" + price +
                '}';
    }
}
