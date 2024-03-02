package sample;

import com.mysql.cj.jdbc.result.ResultSetMetaData;
import shared.Client;
import shared.Order;
import shared.Product;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdersDaoImpl extends AbstractDAO<Order> {

    private final Connection conn;
    private final String table;

    public OrdersDaoImpl(Connection conn, String table) {
        super(conn, table);
        this.conn = conn;
        this.table = table;
    }

    @Override
    public void add(Order order) throws IllegalAccessException {
        Field[] fields = order.getClass().getDeclaredFields();
        Field id = getPrimaryKeyField(fields);

        StringBuilder names = new StringBuilder();
        StringBuilder values = new StringBuilder();

        names.append('(');
        values.append('(');

        for (var f : fields) {
            if (f != id) {
                f.setAccessible(true);

                names.append(f.getName())
                        .append(',');
                if (f.getType() == Client.class || f.getType() == Product.class) {
                    Field[] thisFields = f.get(order).getClass().getDeclaredFields();
                    Field idField = getPrimaryKeyField(thisFields);
                    idField.setAccessible(true);
                    values.append('"')
                            .append(idField.get(f.get(order)))
                            .append('"')
                            .append(',');
                } else {
                    values.append('"')
                            .append(f.get(order))
                            .append('"')
                            .append(',');
                }
            }
        }
        names.deleteCharAt(names.length() - 1)
                .append(')');
        values.deleteCharAt(values.length() - 1)
                .append(')');

        String sql = "INSERT INTO " + table + " " + names.toString() + " VALUES " + values.toString();

        try {
            int primaryKey = -1;
            try (Statement st = conn.createStatement()) {
                st.execute(sql);
                ResultSet rs = st.executeQuery("SELECT LAST_INSERT_ID()");
                if (rs.next()) {
                    primaryKey = rs.getInt(1);
                }
                if (primaryKey != -1) {
                    for (Field f : fields) {
                        if (f == id) {
                            f.setAccessible(true);
                            f.setInt(order, primaryKey);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Order> getAll(Class<Order> cls) throws SQLException, ReflectiveOperationException {
        List<Order> result = new ArrayList<>();
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM " + table);
            ResultSetMetaData md = (ResultSetMetaData) rs.getMetaData();

            int columnCount = md.getColumnCount();
            while (rs.next()) {
                Order order = cls.getDeclaredConstructor().newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = md.getColumnName(i);
                    Field field = cls.getDeclaredField(columnName);
                    field.setAccessible(true);

                    if (field.getType() == Client.class) {
                        field.set(order, getClient(rs.getInt(columnName)));
                    } else if (field.getType() == Product.class) {
                        field.set(order, getProduct(rs.getInt(columnName)));
                    } else {
                        field.set(order, rs.getObject(columnName));
                    }
                }
                result.add(order);
            }
        }
        return result;
    }

    @Override
    public Field getPrimaryKeyField(Field[] fields) {
        return super.getPrimaryKeyField(fields);
    }

    private Client getClient(int id) {
        Client cl = null;
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Clients WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData md = (ResultSetMetaData) rs.getMetaData();

            int columnCount = md.getColumnCount();
            while (rs.next()) {
                cl = Client.class.getDeclaredConstructor().newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = md.getColumnName(i);
                    Field field = cl.getClass().getDeclaredField(columnName);

                    field.setAccessible(true);
                    field.set(cl, rs.getObject(columnName));
                }
            }
        } catch (SQLException | ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        if (cl == null) throw new RuntimeException();
        return cl;
    }

    private Product getProduct(int id) {
        Product pr = null;
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Products WHERE id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData md = (ResultSetMetaData) rs.getMetaData();

            int columnCount = md.getColumnCount();
            while (rs.next()) {
                pr = Product.class.getDeclaredConstructor().newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = md.getColumnName(i);
                    Field field = pr.getClass().getDeclaredField(columnName);

                    field.setAccessible(true);
                    field.set(pr, rs.getObject(columnName));
                }
            }
        } catch (SQLException | ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        if (pr == null) throw new RuntimeException();
        return pr;
    }
}
