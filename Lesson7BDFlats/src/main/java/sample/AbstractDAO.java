package sample;

import com.mysql.cj.jdbc.result.ResultSetMetaData;
import shared.Id;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbstractDAO<T> {
    private Connection conn;
    private String table;

    public AbstractDAO(Connection conn, String table) {
        this.conn = conn;
        this.table = table;
    }

    public void createTable(Class<T> cls) {
        Field[] fields = cls.getDeclaredFields();
        Field id = getPrimaryKeyField(fields);

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ")
                .append(table)
                .append('(')
                .append(id.getName())
                .append(" INT NOT NULL AUTO_INCREMENT PRIMARY KEY")
                .append(',');

        for (var f : fields) {
            if (f != id) {
                sql.append(f.getName())
                        .append(" ");
                if (f.getType() == String.class) {
                    sql.append("VARCHAR(128)")
                            .append(',');
                } else if (f.getType() == int.class) {
                    sql.append("INT")
                            .append(',');
                } else {
                    throw new RuntimeException("Wrong type");
                }
            }
        }
        sql.deleteCharAt(sql.length() - 1)
                .append(")");

        try {
            try (Statement st = conn.createStatement()) {
                st.execute(sql.toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void add(T t) throws IllegalAccessException {
        Field[] fields = t.getClass().getDeclaredFields();
        Field id = getPrimaryKeyField(fields);

        StringBuilder names = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (var f : fields) {
            if (f != id) {
                f.setAccessible(true);

                names.append(f.getName())
                        .append(',');
                values.append('"')
                        .append(f.get(t))
                        .append('"')
                        .append(',');
            }
        }
        names.deleteCharAt(names.length() - 1);
        values.deleteCharAt(values.length() - 1);

        String sql = "INSERT INTO " + table + "(" + names.toString()
                + ") VALUES (" + values.toString() + ")";

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
                            f.setInt(t, primaryKey);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(T t) throws IllegalAccessException {
        Field[] fields = t.getClass().getDeclaredFields();
        Field id = getPrimaryKeyField(fields);

        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ")
                .append(table)
                .append(" SET ");

        for (var f : fields) {
            if (f != id) {
                f.setAccessible(true);

                sql.append(f.getName())
                        .append('=')
                        .append('"')
                        .append(f.get(t))
                        .append('"')
                        .append(',');
            }
        }
        sql.deleteCharAt(sql.length() - 1)
                .append(" WHERE ")
                .append(id.getName())
                .append('=')
                .append(id.get(t));

        try {
            try (Statement st = conn.createStatement()) {
                st.execute(sql.toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(T t) throws IllegalAccessException {
        Field[] fields = t.getClass().getDeclaredFields();
        Field id = getPrimaryKeyField(fields);
        id.setAccessible(true);

        String sql = "DELETE " + "FROM " + table + " WHERE "
                + id.getName() + "=" + id.get(t);

        try {
            try (Statement st = conn.createStatement()) {
                st.execute(sql.toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> getAll(Class<T> cls) throws SQLException, ReflectiveOperationException {
        List<T> result = new ArrayList<>();
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM " + table);
            ResultSetMetaData md = (ResultSetMetaData) rs.getMetaData();
            result = createResultList(cls, rs, md);
        }
        return result;
    }

    private List<T> getAllWhere(Class<T> cls, String columnName, String parameter) {
        List<T> result = new ArrayList<>();
        String sql = "SELECT * FROM " + table + " WHERE " + columnName + "= ?";
        try {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, parameter);
                ResultSet rs = ps.executeQuery();
                ResultSetMetaData md = (ResultSetMetaData) rs.getMetaData();

                result = createResultList(cls, rs, md);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<T> getAllWhere(Class<T> cls, String columnName, int minParameter, int maxParameter) {
        List<T> result = new ArrayList<>();
        String sql = "SELECT * FROM " + table + " WHERE " + columnName
                + " BETWEEN ? AND ?";
        try {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, minParameter);
                ps.setInt(2, maxParameter);
                ResultSet rs = ps.executeQuery();
                ResultSetMetaData md = (ResultSetMetaData) rs.getMetaData();

                result = createResultList(cls, rs, md);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Field getPrimaryKeyField(Field[] fields) {
        Field id = null;
        for (var f : fields) {
            if (f.isAnnotationPresent(Id.class)) {
                f.setAccessible(true);
                id = f;
                break;
            }
        }
        if (id == null) throw new RuntimeException("No id field found");
        return id;
    }

    private List<T> createResultList(Class<T> cls, ResultSet rs, ResultSetMetaData md) {
        List<T> result = new ArrayList<>();
        try {
            int columnCount = md.getColumnCount();
            while (rs.next()) {
                T t = cls.getDeclaredConstructor().newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = md.getColumnName(i);
                    Field field = cls.getDeclaredField(columnName);

                    field.setAccessible(true);
                    field.set(t, rs.getObject(columnName));
                }
                result.add(t);
            }
        } catch (SQLException | ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}

