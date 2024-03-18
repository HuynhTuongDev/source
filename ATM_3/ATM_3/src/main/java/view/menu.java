package view;

import control.DatabaseConnector;
import control.Management;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class menu {

    public static void main(String[] args) throws SQLException {
        menu m = new menu();
        m.showMenu();
    }

    public void showMenu() {
        try {
            Management ma = new Management();
            Connection conn = DatabaseConnector.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM menu WHERE position = 1");
            checkPosition(rs);
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.print("Enter your choice: ");
                int choose = sc.nextInt();
                sc.nextLine();
                switch (choose) {
                    case 1:
                        ma.login();
                        ResultSet rs2 = stmt.executeQuery("SELECT * FROM menu WHERE position = 2");
                        break;
                    case 2:
                        ma.addAccount();
                        break;
                    case 0:
                        System.out.println("Exiting ATM Application. Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option. Please choose again.");
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve menu items from the database.");
        }
    }

    public void checkPosition(ResultSet rs2) throws SQLException {
        while (rs2.next()) {
            String name = rs2.getString("name");
            System.out.println(name);
        }
    }
}
