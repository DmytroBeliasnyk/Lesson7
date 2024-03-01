package sample;

import shared.ConnectionFactory;
import shared.Flat;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String table = "Flats";
        try {
            try (Connection conn = ConnectionFactory.getConnection()) {
                try (Statement st = conn.createStatement()) {
                    st.execute("DROP TABLE IF EXISTS " + table);
                }
                FlatDaoImpl dao = new FlatDaoImpl(conn, table);
                dao.createTable(Flat.class);

                /*Flat flat1 = new Flat("district1", 50, 2, 340000);
                dao.add(flat1);
                Flat flat2 = new Flat("district2", 30, 1, 210000);
                dao.add(flat2);
                Flat flat3 = new Flat("district2", 75, 3, 620000);
                dao.add(flat3);

                flat1.setPrice(320000);
                dao.update(flat1);
                dao.delete(flat2);*/

                dao.addRandom(10);

                List<Flat> flats = dao.getAll(Flat.class);
                for (Flat f : flats) {
                    System.out.println(f);
                }
                System.out.println();

                System.out.println("flatsByDistricts");
                List<Flat> flatsByDistricts = dao.getByDistrict("Shevchenkivskyi");
                for (Flat f : flatsByDistricts) {
                    System.out.println(f);
                }
                System.out.println();

                System.out.println("flatsByArea");
                List<Flat> flatsByArea = dao.getByArea(30);
                for (Flat f : flatsByArea) {
                    System.out.println(f);
                }
                System.out.println();

                System.out.println("flatsByRooms");
                List<Flat> flatsByRooms = dao.getByRooms(2);
                for (Flat f : flatsByRooms) {
                    System.out.println(f);
                }
                System.out.println();

                System.out.println("flatsByPrice");
                List<Flat> flatsByPrice = dao.getByPrice(350000);
                for (Flat f : flatsByPrice) {
                    System.out.println(f);
                }
            }
        } catch (SQLException | ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
