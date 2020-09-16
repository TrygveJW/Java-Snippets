package no.trygvejw.postgress;

import no.trygvejw.debugLogger.DebugLogger;
import no.trygvejw.util.ThrowingConsumer;

import java.sql.*;

abstract class PsqlDb {

    /*
    todo: dnne må bli en singelton som kan configureres med om man skal vente ved no connection om binningen skal holdes åpen osv
            - kan kansje også ha mulighet til og spinne opp en tråd med en åpen conn som kan brukes.



     */

    private static final String url = System.getenv("SQLURL");
    private static final String dbUser = System.getenv("POSGRESS_USER");
    private static final String dbPassword = System.getenv("POSGRESS_PASSWORD");

    protected static final DebugLogger allQueries = new DebugLogger(false);
    protected static final DebugLogger errorQueries = new DebugLogger(true);



    protected static Connection tryConnectToDB() throws SQLException{
        allQueries.log("try connect to db", "url", url, "user", dbUser, "passwd", dbPassword);
        Connection connection = null;

        try{
            Class.forName("org.postgresql.Driver"); // i think this is to chek if the class exists

            connection = DriverManager.getConnection(url, dbUser, dbPassword);
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        return connection;
    }




    protected static void sqlQuery(String query, ThrowingConsumer<ResultSet, SQLException> rowHandler) throws SQLException{

        Connection connection = tryConnectToDB();
        Statement statement = connection.createStatement();

        allQueries.log("making SQL query:\n", query);
        ResultSet resultSet = statement.executeQuery(query);


        while (resultSet.next()){
            rowHandler.accept(resultSet);
        }

        resultSet.close();
        statement.close();
        connection.close();

    }


    protected static void sqlUpdate(String query) throws SQLException{
        Connection connection = tryConnectToDB();
        Statement statement = connection.createStatement();

        allQueries.log("making SQL update:\n", query);
        statement.executeUpdate(query);


        statement.close();
        connection.close();

    }
}
