package sample;


import shared.Flat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Random;

public class FlatDaoImpl extends AbstractDAO<Flat> {
    private static final String[] DISTRICTS = {"Shevchenkivskyi", "Pecherskyi", "Holosiivskyi", "Darnytskyi", "Obolonskyi",};
    private static final Method GET_ALL_WHERE = getMethodFromSuper();

    public FlatDaoImpl(Connection conn, String table) {
        super(conn, table);
    }

    public void addRandom(int quantity) {
        for (int i = 1; i <= quantity; i++) {
            try {
                add(getRandomFlat());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Flat> getByDistrict(String district) {
        List<Flat> res = null;
        Method m;
        try {
            m = AbstractDAO.class.getDeclaredMethod("getAllWhere", Class.class, String.class, String.class);
            m.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        try {
            res = (List<Flat>) m.invoke(this, Flat.class, "district", district);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public List<Flat> getByArea(int area) {
        List<Flat> res = null;

        int minParameter = area - 5;
        int maxParameter = area + 5;

        try {
            Method m = AbstractDAO.class.getDeclaredMethod("getAllWhere", Class.class,
                    String.class, int.class, int.class);
            m.setAccessible(true);

            res = (List<Flat>) m.invoke(this, Flat.class, "area",
                    minParameter, maxParameter);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public List<Flat> getByRooms(int rooms) {
        List<Flat> res = null;
        String parameter = String.valueOf(rooms);
        try {
            res = (List<Flat>) GET_ALL_WHERE.invoke(this, Flat.class, "rooms", parameter);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public List<Flat> getByPrice(int price) {
        List<Flat> res = null;

        int minParameter = price - 20000;
        int maxParameter = price + 20000;

        try {
            Method m = AbstractDAO.class.getDeclaredMethod("getAllWhere", Class.class,
                    String.class, int.class, int.class);
            m.setAccessible(true);

            res = (List<Flat>) m.invoke(this, Flat.class, "price", minParameter, maxParameter);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return res;
    }
    
    private Flat getRandomFlat() {
        Random rnd = new Random();
        return new Flat(DISTRICTS[rnd.nextInt(DISTRICTS.length)], rnd.nextInt(30, 80),
                rnd.nextInt(1, 4), rnd.nextInt(200000, 400000));
    }

    private static Method getMethodFromSuper() {
        Method m = null;
        try {
            m = AbstractDAO.class.getDeclaredMethod("getAllWhere", Class.class, String.class, String.class);
            m.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return m;
    }
}
