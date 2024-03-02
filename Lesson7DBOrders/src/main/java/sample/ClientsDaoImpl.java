package sample;

import shared.Client;

import java.sql.Connection;

public class ClientsDaoImpl extends AbstractDAO<Client> {
    public ClientsDaoImpl(Connection conn, String table) {
        super(conn, table);
    }
}
