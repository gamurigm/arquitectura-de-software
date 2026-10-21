/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.monster.service;

import ec.edu.monster.db.AccesoDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author chris
 */
public class EurekaService {
    public int regDeposito(String cuenta, double importe) {
        int estado = -1;
        Connection cn = null;
        try {
            cn = AccesoDB.getConnection();
            String sql = "INSERT INTO deposito (cuenta, importe) VALUES (?, ?)";
            PreparedStatement pstm = cn.prepareStatement(sql);
            pstm.setString(1, cuenta);
            pstm.setDouble(2, importe);
            int filas = pstm.executeUpdate();
            if (filas > 0) {
                estado = 1;
            }
            cn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estado;
    }
}
