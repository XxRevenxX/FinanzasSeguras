package Controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import Vista.FrmConfiguracion;
import Vista.FrmPrincipal;
import Vista.FrmIngreso;
import Vista.FrmGasto;
import Vista.FrmReporte;
import Vista.FrmAdministrarUsuario;
import Vista.InicioSesion;
import Conexion.ConexionDB;
import Vista.FrmMetas;

public class ConfiguracionController implements ActionListener {
    private FrmConfiguracion ventana;
    private int idUsuario;
    private String rutaFotoTemporal = null; 

    public ConfiguracionController(FrmConfiguracion ventana, int idUsuario) {
        this.ventana = ventana;
        this.idUsuario = idUsuario;
        
        //Botones del CRUD
        this.ventana.btnEditarPerfil.addActionListener(this);
        
        //Botonoes panel lateral
        this.ventana.btnPrincipal.addActionListener(this);
        this.ventana.btnIngreso.addActionListener(this);
        this.ventana.btnEgreso.addActionListener(this);
        this.ventana.btnReporte.addActionListener(this);
        this.ventana.btnMeta.addActionListener(this); 
        this.ventana.btnConfiguracion.addActionListener(this);
        this.ventana.btnCerrarSesion.addActionListener(this);

        if (this.idUsuario == 1) {
            this.ventana.btnAdminUsuarios.setVisible(true);
            this.ventana.btnAdminUsuarios.addActionListener(this);
        } else {
            this.ventana.btnAdminUsuarios.setVisible(false);
        }

        // Hacemos que al dar clic en el panel de la foto se abra el explorador de archivos
        this.ventana.panel_foto.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarImagenPerfil();
            }
        });

        cargarDatosUsuario();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ventana.btnEditarPerfil) {
            actualizarDatos();
        }
        
        
        if (e.getSource() == ventana.btnPrincipal) {
            abrirMenuPrincipal();
        }
        if (e.getSource() == ventana.btnIngreso) {
            abrirVentanaIngreso();
        }
        if (e.getSource() == ventana.btnEgreso) {
            abrirVentanaGasto();
        }
        if (e.getSource() == ventana.btnReporte) {
            abrirVentanaReporte();
        }
        if (e.getSource() == ventana.btnConfiguracion) {
            JOptionPane.showMessageDialog(ventana, "Ya te encuentras en Configuración.");
        }
        if (e.getSource() == ventana.btnMeta) {
            abrirVentanaMetas();
        }
        if (e.getSource() == ventana.btnAdminUsuarios) {
            abrirVentanaAdmin();
        }
        if (e.getSource() == ventana.btnCerrarSesion) {
            cerrarSesion();
        }
    }

    // Seleccion imagen de la PC
    private void seleccionarImagenPerfil() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "png", "jpeg");
        fileChooser.setFileFilter(filtro);
        
        int seleccion = fileChooser.showOpenDialog(ventana);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            rutaFotoTemporal = archivo.getAbsolutePath();
            
            // Mostramos la imagen seleccionada de forma temporal en el panel
            mostrarImagenEnPanel(rutaFotoTemporal);
        }
    }

    // Mostrar imagen en el panel
    private void mostrarImagenEnPanel(Object fuenteImagen) {
        ventana.panel_foto.removeAll();
        JLabel lblImagen = new JLabel();
        lblImagen.setBounds(0, 0, 150, 150); // Tamaño estandarizado del panel
        
        ImageIcon iconoOriginal;
        if (fuenteImagen instanceof String) {
            iconoOriginal = new ImageIcon((String) fuenteImagen);
        } else if (fuenteImagen instanceof byte[]) {
            iconoOriginal = new ImageIcon((byte[]) fuenteImagen);
        } else {
            return;
        }

        Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        lblImagen.setIcon(new ImageIcon(imagenEscalada));
        
        ventana.panel_foto.add(lblImagen);
        ventana.panel_foto.repaint();
        ventana.panel_foto.revalidate();
    }

    private void cargarDatosUsuario() {
        try {
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            String sql = "SELECT nombre, fecha_nac, contrasena, correo, telefono, foto_perfil FROM Usuario WHERE id_usuario = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, this.idUsuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                ventana.txtNombre.setText(rs.getString("nombre"));
                ventana.txtFecha.setText(rs.getString("fecha_nac"));
                ventana.txtContrasena.setText(rs.getString("contrasena"));
                ventana.txtCorreo.setText(rs.getString("correo"));
                ventana.txtTelefono.setText(rs.getString("telefono"));
                
                byte[] bytesImagen = rs.getBytes("foto_perfil");
                if (bytesImagen != null) {
                    mostrarImagenEnPanel(bytesImagen);
                }
            }
            con.close();
        } catch (SQLException ex) {
            System.out.println("Error al cargar datos: " + ex.getMessage());
        }
    }

    // --- ACTUALIZAR DATOS Y FOTO EN LA BD ---
    private void actualizarDatos() {
        String nombre = ventana.txtNombre.getText().trim();
        String fecha = ventana.txtFecha.getText().trim();
        String contrasena = ventana.txtContrasena.getText().trim();
        String correo = ventana.txtCorreo.getText().trim();
        String telefono = ventana.txtTelefono.getText().trim();

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(ventana, "Nombre, Contraseña y Correo son obligatorios.");
            return;
        }

        try {
            ConexionDB conDb = new ConexionDB();
            Connection con = conDb.conectar();
            
            String sql;
            PreparedStatement ps;

            // Si el usuario seleccionó una nueva foto, la incluimos en el UPDATE
            if (rutaFotoTemporal != null) {
                sql = "UPDATE Usuario SET nombre = ?, fecha_nac = ?, contrasena = ?, correo = ?, telefono = ?, foto_perfil = ? WHERE id_usuario = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, nombre);
                ps.setString(2, fecha);
                ps.setString(3, contrasena);
                ps.setString(4, correo);
                ps.setString(5, telefono);
                
                FileInputStream fis = new FileInputStream(new File(rutaFotoTemporal));
                ps.setBinaryStream(6, fis, (int) new File(rutaFotoTemporal).length());
                ps.setInt(7, this.idUsuario);
            } else {
                // Si no cambió la foto, actualizamos solo los textos para no sobreescribirla con nulos
                sql = "UPDATE Usuario SET nombre = ?, fecha_nac = ?, contrasena = ?, correo = ?, telefono = ? WHERE id_usuario = ?";
                ps = con.prepareStatement(sql);
                ps.setString(1, nombre);
                ps.setString(2, fecha);
                ps.setString(3, contrasena);
                ps.setString(4, correo);
                ps.setString(5, telefono);
                ps.setInt(6, this.idUsuario);
            }
            
            int filas = ps.executeUpdate();
            con.close();

            if (filas > 0) {
                JOptionPane.showMessageDialog(ventana, "¡Perfil actualizado con éxito!");
                rutaFotoTemporal = null; // Reiniciamos la variable temporal
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(ventana, "Error al actualizar: " + ex.getMessage());
        }
    }

    private void abrirMenuPrincipal() {
        FrmPrincipal frm = new FrmPrincipal();
        new PrincipalController(frm, this.idUsuario);
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);
        ventana.dispose();
    }

    private void abrirVentanaIngreso() {
        FrmIngreso frm = new FrmIngreso();
        new IngresoController(frm, this.idUsuario);
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);
        ventana.dispose();
    }

    private void abrirVentanaGasto() {
        FrmGasto frm = new FrmGasto();
        new GastoController(frm, this.idUsuario);
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);
        ventana.dispose();
    }

    private void abrirVentanaReporte() {
        FrmReporte frm = new FrmReporte();
        new ReporteController(frm, this.idUsuario);
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);
        ventana.dispose();
    }
    private void abrirVentanaMetas() {
        FrmMetas frm = new FrmMetas();
        new MetasController(frm, this.idUsuario);
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);
        ventana.dispose();
    }

    private void abrirVentanaAdmin() {
        FrmAdministrarUsuario frm = new FrmAdministrarUsuario();
        new AdminUsuariosController(frm, this.idUsuario);
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);
        ventana.dispose();
    }

    private void cerrarSesion() {
        InicioSesion ventanaLogin = new InicioSesion();
        new InicioSesionController(ventanaLogin);
        ventanaLogin.setLocationRelativeTo(null);
        ventanaLogin.setVisible(true);
        ventana.dispose();
    }
}