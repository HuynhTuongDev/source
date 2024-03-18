package view;

import control.Connector;
import control.Managerment;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Menu {

    public static void main(String[] args) throws SQLException {
        Menu m = new Menu();
        m.showMenu();
    }

    public void showMenu() {
        try {
            Managerment ma = new Managerment();
            Connection conn = Connector.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs1 = stmt.executeQuery("SELECT * FROM menu WHERE Menu_index = 1 OR Menu_index = 0");
            // In ra menu lựa chọn từ ResultSet rs1
            checkPosition(rs1);

            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.print("Enter your choice: ");
                int choose = sc.nextInt();
                sc.nextLine();
                switch (choose) {
                    case 1:
                        // Bạn cần thêm in ra menu lựa chọn ở đây nếu cần
                        int check_login = ma.login();
                        if (check_login == 1 || check_login == 2) {
                            while (true) {
                                if (check_login == 1) {
                                    ResultSet rs2 = stmt.executeQuery("SELECT * FROM menu WHERE Menu_index = 2 OR Menu_index = 0 OR Menu_index = 3");
                                    // In ra menu lựa chọn từ ResultSet rs2
                                    checkPosition(rs2);
                                    System.out.println("Enter your choose: ");
                                    int choose2 = sc.nextInt();
                                    sc.nextLine();
                                    switch (choose2) {
                                        case 1:
                                            System.out.println("Goodbye!");
                                            return;
                                        case 2:
                                            ma.addTeacher();
                                            break;
                                        case 3:
                                            ma.deleteTeacher();
                                            break;
                                        case 4:
                                            ma.updateTeacher();
                                            break;
                                        case 5:
                                            ma.addStudent();
                                            break;
                                        case 6:
                                            ma.deleteStudent();
                                            break;
                                        case 7:
                                            ma.updateStudent();
                                    }
                                }
                                if (check_login == 2) {
                                    ResultSet rs2 = stmt.executeQuery("SELECT * FROM menu WHERE Menu_index = 3 OR Menu_index = 0  ");
                                    // In ra menu lựa chọn từ ResultSet rs2
                                    checkPosition(rs2);
                                    System.out.println("Enter your choose: ");
                                    int choose2 = sc.nextInt();
                                    sc.nextLine();
                                    switch (choose2) {
                                        case 1:
                                            System.out.println("Goodbye!");
                                        case 2:
                                            ma.addStudent();
                                            break;
                                        case 3:
                                            ma.deleteStudent();
                                            break;
                                        case 4:
                                            ma.updateStudent();
                                    }
                                }
                            }
                        }
                        break;
                    case 2:
                        // Xử lý lựa chọn 2 ở đây nếu cần
                        ma.addAdmin();
                        break;
                    case 0:
                        System.out.println("Goodbye!");
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

    public void checkPosition(ResultSet rs) throws SQLException {
        int index = 1;
        while (rs.next()) {
            String name = rs.getString("function_name");
            System.out.println(index + ". " + name);
            index++;
        }
    }
}
