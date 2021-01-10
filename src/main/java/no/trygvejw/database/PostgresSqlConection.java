package no.trygvejw.database;

import no.trygvejw.debugLogger.DebugLogger;
import no.trygvejw.util.ThrowingConsumer;

import java.sql.*;


/**
 * QOL object for interacting with a postgres sql database.
 * Has the option to toggle whether or not to keep the connection open.
 */
public class PostgresSqlConection {

    /**
     * Creates a connection object.
     * @param keepalive whether or not to keep the connection open after a query.
     */
    public PostgresSqlConection(boolean keepalive){
        this.keepalive = keepalive;
    }

    private boolean keepalive = false;
    private Connection connection;

    private static final String url = System.getenv("SQLURL");
    private static final String dbUser = System.getenv("POSGRESS_USER");
    private static final String dbPassword = System.getenv("POSGRESS_PASSWORD");

    protected static final DebugLogger allQueries = new DebugLogger(false);
    protected static final DebugLogger errorQueries = new DebugLogger(true);


    /**
     * Try to connect to the db
     * @return a connection object, null if unsuccessful.
     */
    private Connection tryConnectToDB() throws SQLException{
        allQueries.log("try connect to db", "url", url, "user", dbUser, "passwd", dbPassword);
        Connection connection = null;

        try{
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(url, dbUser, dbPassword);
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        return connection;
    }

    private Connection getConnection() throws SQLException {


        if (keepalive){


            if (connection == null){
                // if first time connect make the connection
                connection = this.tryConnectToDB();

            } else if(connection.isClosed()){
                // if the connection is closed open it again
                connection = this.tryConnectToDB();
            }
            return connection;

        } else {
            return this.tryConnectToDB();
        }

    }


    /**
     * Runs a query to the database and passes the resulting data to the provided consumer.
     * @param query the query to run.
     * @param rowHandler the Throwing consumer to handle the output.
     * @throws SQLException if the query returns an error.
     */
    public void sqlQuery(String query, ThrowingConsumer<ResultSet, SQLException> rowHandler) throws SQLException{

        Connection connection = this.getConnection();
        Statement statement = connection.createStatement();

        allQueries.log("making SQL query:\n", query);
        ResultSet resultSet = statement.executeQuery(query);


        while (resultSet.next()){
            rowHandler.accept(resultSet);
        }

        resultSet.close();
        statement.close();
        if (!this.keepalive){
            connection.close();
        }


    }

    /**
     * Runs an sql operation that does not return any data.
     * @param query the query to run
     * @throws SQLException if the query returns an error.
     */
    public void sqlUpdate(String query) throws SQLException{
        Connection connection = tryConnectToDB();
        Statement statement = connection.createStatement();

        allQueries.log("making SQL update:\n", query);
        statement.executeUpdate(query);


        statement.close();
        if (!this.keepalive){
            connection.close();
        }

    }
}
