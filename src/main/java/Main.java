import java.sql.*;

public class Main{

    private final String url = "jdbc:postgresql://localhost/vehicles";
    private final String user = "";
    private final String password = "";

    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    public void getVehicles() {
        String SQL = "SELECT veh_id, veh_name FROM vehicle";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            displayVehicle(rs);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void displayVehicle(ResultSet rs) throws SQLException {
        while (rs.next()) {
            System.out.println(rs.getString("veh_id") + "\t"
                    + rs.getString("veh_name"));
        }
    }

    public void findVehicleByID(int vehID) {
        String SQL = "SELECT veh_id, veh_name FROM vehicle WHERE veh_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, vehID);
            ResultSet rs = pstmt.executeQuery();
            displayVehicle(rs);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public long insertVehicle(Vehicle vehicle) {
        String SQL = "INSERT INTO vehicle(veh_name) VALUES(?)";
        long id = 0;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, vehicle.getName());
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Vehicle added to database.");
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return id;
    }

    public int deleteVehicle(int id) {
        String SQL = "DELETE FROM vehicle WHERE veh_id = ?";
        int affectedrows = 0;
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, id);
            affectedrows = pstmt.executeUpdate();
            System.out.println("Vehicle deleted.");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return affectedrows;
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.connect();
        Vehicle veh1 = new Vehicle("kia");
        main.insertVehicle(veh1);
        main.getVehicles();
        main.findVehicleByID(1);
        main.deleteVehicle(1);
        main.getVehicles();
    }
}
