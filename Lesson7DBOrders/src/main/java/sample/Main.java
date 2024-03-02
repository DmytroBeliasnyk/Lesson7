package sample;

import shared.Client;
import shared.ConnectionFactory;
import shared.Order;
import shared.Product;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Main {
    private static final String TABLE_CLIENTS = "Clients";
    private static final String TABLE_PRODUCTS = "Products";
    private static final String TABLE_ORDERS = "Orders";

    public static void main(String[] args) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            try (Statement st = conn.createStatement()) {
                st.execute("DROP TABLE IF EXISTS " + TABLE_CLIENTS);
                st.execute("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
                st.execute("DROP TABLE IF EXISTS " + TABLE_ORDERS);
            }
            ClientsDaoImpl clDao = new ClientsDaoImpl(conn, TABLE_CLIENTS);
            clDao.createTable(Client.class);
            ProductsDaoImpl prDao = new ProductsDaoImpl(conn, TABLE_PRODUCTS);
            prDao.createTable(Product.class);

            Client cl1 = new Client("Client1", "Client1", 123123123, "adress1");
            Client cl2 = new Client("Client2", "Client2", 234234234, "adress2");
            Client cl3 = new Client("Client3", "Client3", 345345345, "adress3");
            Client cl4 = new Client("Client4", "Client4", 456456456, "adress4");
            Client cl5 = new Client("Client5", "Client5", 567567567, "adress5");
            clDao.add(cl1);
            clDao.add(cl2);
            clDao.add(cl3);
            clDao.add(cl4);
            clDao.add(cl5);

            Product pr1 = new Product("Product1", 10);
            Product pr2 = new Product("Product2", 20);
            Product pr3 = new Product("Product3", 30);
            Product pr4 = new Product("Product4", 40);
            Product pr5 = new Product("Product5", 50);
            prDao.add(pr1);
            prDao.add(pr2);
            prDao.add(pr3);
            prDao.add(pr4);
            prDao.add(pr5);

            List<Client> clients = clDao.getAll(Client.class);
            for (var cl : clients) {
                System.out.println(cl);
            }
            System.out.println();

            List<Product> products = prDao.getAll(Product.class);
            for (var pr : products) {
                System.out.println(pr);
            }
            System.out.println();

            OrdersDaoImpl orDao = new OrdersDaoImpl(conn, TABLE_ORDERS);
            orDao.createTable(Order.class);

            Order or1 = new Order(cl1, pr3, 2);
            Order or2 = new Order(cl4, pr1, 7);
            orDao.add(or1);
            orDao.add(or2);

            List<Order> orders = orDao.getAll(Order.class);
            for (var or : orders) {
                System.out.println(or);
            }
        } catch (SQLException | ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
