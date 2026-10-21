/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.monster.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author chris
 */
public class AccesoDB {
    public static Connection getConnection() throws SQLException {
           try {
               Class.forName("com.mysql.cj.jdbc.Driver");
           } catch (ClassNotFoundException e) {
               throw new SQLException("No se encontró el driver de MySQL", e);
           }
           String url = "jdbc:mysql://localhost:3306/eureka";
           String user = "root";
           String password = "tu_password";
           return DriverManager.getConnection(url, user, password);
    }
}
