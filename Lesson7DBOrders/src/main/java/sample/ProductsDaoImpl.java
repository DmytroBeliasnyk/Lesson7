package sample;

import shared.Product;

import java.sql.Connection;

public class ProductsDaoImpl extends AbstractDAO<Product> {
    public ProductsDaoImpl(Connection conn, String table) {
        super(conn, table);
    }
}
