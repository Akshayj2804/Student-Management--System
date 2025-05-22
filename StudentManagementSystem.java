import java.sql.*;
import java.util.Scanner;

public class StudentManagementSystem {
    static final String DB_URL = "jdbc:mysql://localhost:3306/studentdb";
    static final String USER = "root";       // Replace with your MySQL username
    static final String PASS = "password";   // Replace with your MySQL password

    public static void main(String[] args) {
        try (
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Scanner sc = new Scanner(System.in)
        ) {
            while (true) {
                System.out.println("\n--- Student Management System ---");
                System.out.println("1. Add Student");
                System.out.println("2. View Students");
                System.out.println("3. Update Student");
                System.out.println("4. Delete Student");
                System.out.println("5. Search Student");
                System.out.println("6. Exit");
                System.out.print("Choose an option: ");
                int choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        addStudent(conn, sc);
                        break;
                    case 2:
                        viewStudents(conn);
                        break;
                    case 3:
                        updateStudent(conn, sc);
                        break;
                    case 4:
                        deleteStudent(conn, sc);
                        break;
                    case 5:
                        searchStudent(conn, sc);
                        break;
                    case 6:
                        System.out.println("Exiting system...");
                        return;
                    default:
                        System.out.println("Invalid option. Try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    static void addStudent(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter name: ");
        String name = sc.nextLine();
        System.out.print("Enter age: ");
        int age = sc.nextInt();
        sc.nextLine();

        String sql = "INSERT INTO students (name, age) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.executeUpdate();
            System.out.println("Student added successfully.");
        }
    }

    static void viewStudents(Connection conn) throws SQLException {
        String sql = "SELECT * FROM students";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                                   ", Name: " + rs.getString("name") +
                                   ", Age: " + rs.getInt("age"));
            }
        }
    }

    static void updateStudent(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter student ID to update: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter new name: ");
        String name = sc.nextLine();
        System.out.print("Enter new age: ");
        int age = sc.nextInt();
        sc.nextLine();

        String sql = "UPDATE students SET name = ?, age = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setInt(3, id);
            int rows = stmt.executeUpdate();
            if (rows > 0)
                System.out.println("Student updated successfully.");
            else
                System.out.println("Student not found.");
        }
    }

    static void deleteStudent(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter student ID to delete: ");
        int id = sc.nextInt();
        sc.nextLine();

        String sql = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0)
                System.out.println("Student deleted successfully.");
            else
                System.out.println("Student not found.");
        }
    }

    static void searchStudent(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter student name or ID to search: ");
        String input = sc.nextLine();
        String sql;

        if (input.matches("\\d+")) {
            sql = "SELECT * FROM students WHERE id = ?";
        } else {
            sql = "SELECT * FROM students WHERE name LIKE ?";
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (input.matches("\\d+")) {
                stmt.setInt(1, Integer.parseInt(input));
            } else {
                stmt.setString(1, "%" + input + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.println("ID: " + rs.getInt("id") +
                                       ", Name: " + rs.getString("name") +
                                       ", Age: " + rs.getInt("age"));
                }
                if (!found) {
                    System.out.println("No student found.");
                }
            }
        }
    }
}
