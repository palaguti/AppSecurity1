package esfe.persistencia;

import java.sql.Connection; // Importar Connection
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import esfe.dominio.Herramienta;

public class HerramientaDAO {
    private ConnectionManager connManager; // Renombrado a connManager para mayor claridad

    public HerramientaDAO() {
        connManager = ConnectionManager.getInstance(); // Obtener la instancia del gestor de conexiones
    }

    /**
     * Crea una nueva herramienta en la base de datos.
     *
     * @param herramienta El objeto Herramienta que contiene la información de la nueva herramienta a crear.
     * @return El objeto Herramienta recién creado, incluyendo el ID generado por la base de datos,
     * o null si ocurre algún error durante la creación.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public Herramienta create(Herramienta herramienta) throws SQLException {
        Herramienta res = null;
        Connection connection = null; // Declarar la conexión localmente
        try {
            connection = connManager.connect(); // Obtener la conexión
            // Usar try-with-resources para PreparedStatement y ResultSet
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO Herramientas (nombre, tipo, uso_principal) VALUES (?, ?, ?)",
                    java.sql.Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, herramienta.getNombre());
                ps.setString(2, herramienta.getTipo());
                ps.setString(3, herramienta.getUso_principal());

                int affectedRows = ps.executeUpdate();

                if (affectedRows != 0) {
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int idGenerado = generatedKeys.getInt(1);
                            // Llamar a getById para obtener la herramienta completa con su nuevo ID
                            res = getById(idGenerado); // Esto abrirá/cerrará su propia conexión
                        } else {
                            throw new SQLException("La creación de la herramienta falló, no se obtuvo ID.");
                        }
                    }
                }
            } // ps se cierra automáticamente aquí
        } catch (SQLException ex) {
            System.err.println("Error al crear la herramienta: " + ex.getMessage()); // Imprimir el error para depuración
            throw new SQLException("Error al crear la herramienta: " + ex.getMessage(), ex);
        } finally {
            // Asegurarse de cerrar la conexión si se abrió
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    /**
     * Actualiza la información de una herramienta existente en la base de datos.
     *
     * @param herramienta El objeto Herramienta que contiene la información actualizada de la herramienta.
     * @return true si la actualización de la herramienta fue exitosa, false en caso contrario.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public boolean update(Herramienta herramienta) throws SQLException {
        boolean res = false;
        Connection connection = null; // Declarar la conexión localmente
        try {
            connection = connManager.connect(); // Obtener la conexión
            try (PreparedStatement ps = connection.prepareStatement(
                    "UPDATE Herramientas SET nombre = ?, tipo = ?, uso_principal = ? WHERE id = ?")) {

                ps.setString(1, herramienta.getNombre());
                ps.setString(2, herramienta.getTipo());
                ps.setString(3, herramienta.getUso_principal());
                ps.setInt(4, herramienta.getId());

                if (ps.executeUpdate() > 0) {
                    res = true;
                }
            } // ps se cierra automáticamente aquí
        } catch (SQLException ex) {
            System.err.println("Error al modificar la herramienta: " + ex.getMessage()); // Imprimir el error
            throw new SQLException("Error al modificar la herramienta: " + ex.getMessage(), ex);
        } finally {
            // Asegurarse de cerrar la conexión
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    /**
     * Elimina una herramienta de la base de datos basándose en su ID.
     *
     * @param herramienta El objeto Herramienta que contiene el ID de la herramienta a eliminar.
     * @return true si la eliminación de la herramienta fue exitosa, false en caso contrario.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public boolean delete(Herramienta herramienta) throws SQLException {
        boolean res = false;
        Connection connection = null; // Declarar la conexión localmente
        try {
            connection = connManager.connect(); // Obtener la conexión
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM Herramientas WHERE id = ?")) {

                ps.setInt(1, herramienta.getId());

                if (ps.executeUpdate() > 0) {
                    res = true;
                }
            } // ps se cierra automáticamente aquí
        } catch (SQLException ex) {
            System.err.println("Error al eliminar la herramienta: " + ex.getMessage()); // Imprimir el error
            throw new SQLException("Error al eliminar la herramienta: " + ex.getMessage(), ex);
        } finally {
            // Asegurarse de cerrar la conexión
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    /**
     * Busca herramientas en la base de datos cuyo nombre contenga la cadena de búsqueda proporcionada.
     *
     * @param nombre La cadena de texto a buscar.
     * @return Un ArrayList de objetos Herramienta que coinciden con el criterio de búsqueda.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public ArrayList<Herramienta> search(String nombre) throws SQLException {
        ArrayList<Herramienta> records = new ArrayList<>();
        Connection connection = null; // Declarar la conexión localmente
        try {
            connection = connManager.connect(); // Obtener la conexión
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT id, nombre, tipo, uso_principal FROM Herramientas WHERE nombre LIKE ?")) {

                ps.setString(1, "%" + nombre + "%");

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Herramienta herramienta = new Herramienta();
                        herramienta.setId(rs.getInt("id")); // Usar nombre de columna para mayor claridad
                        herramienta.setNombre(rs.getString("nombre"));
                        herramienta.setTipo(rs.getString("tipo"));
                        herramienta.setUso_principal(rs.getString("uso_principal"));
                        records.add(herramienta);
                    }
                } // rs se cierra automáticamente aquí
            } // ps se cierra automáticamente aquí
        } catch (SQLException ex) {
            System.err.println("Error al buscar herramientas: " + ex.getMessage()); // Imprimir el error
            throw new SQLException("Error al buscar herramientas: " + ex.getMessage(), ex);
        } finally {
            // Asegurarse de cerrar la conexión
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return records;
    }

    /**
     * Obtiene una herramienta de la base de datos basada en su ID.
     *
     * @param id El ID de la herramienta que se desea obtener.
     * @return Un objeto Herramienta si se encuentra, null si no se encuentra.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public Herramienta getById(int id) throws SQLException {
        Herramienta herramienta = null;
        Connection connection = null; // Declarar la conexión localmente
        try {
            connection = connManager.connect(); // Obtener la conexión
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT id, nombre, tipo, uso_principal FROM Herramientas WHERE id = ?")) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        herramienta = new Herramienta();
                        herramienta.setId(rs.getInt("id"));
                        herramienta.setNombre(rs.getString("nombre"));
                        herramienta.setTipo(rs.getString("tipo"));
                        herramienta.setUso_principal(rs.getString("uso_principal"));
                    }
                } // rs se cierra automáticamente aquí
            } // ps se cierra automáticamente aquí
        } catch (SQLException ex) {
            System.err.println("Error al obtener una herramienta por id: " + ex.getMessage()); // Imprimir el error
            throw new SQLException("Error al obtener una herramienta por id: " + ex.getMessage(), ex);
        } finally {
            // Asegurarse de cerrar la conexión
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return herramienta;
    }
}