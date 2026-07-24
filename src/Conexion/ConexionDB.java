/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion; // Asegúrate de que este sea el nombre de tu paquete

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConexionDB {
    
    // Aquí usamos el puerto 3306 de MySQL y el nombre exacto de tu base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/Finanzas_Seguras";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = ""; // En XAMPP la contraseña va vacía

    public Connection conectar() {
        Connection conexion = null;
        try {
            // Esta línea invoca al Driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Intentamos conectar
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("¡Conexión exitosa a Finanzas_Seguras!");
            
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error: Falta el conector de MySQL en las librerías.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos: " + e.getMessage());
        }
        return conexion;
    }
}
