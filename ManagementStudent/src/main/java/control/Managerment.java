package control;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import model.Admin;
import model.Teacher;

public class Managerment {

    Scanner sc = new Scanner(System.in);
    Admin currentAdmin;
    Teacher currentTeacher;
    int teacherID = 0;

    public void createAdminAccount(String username, String password) {
        if (!checkAdminExists(username)) {
            String query = "INSERT INTO Admin (Username, Password) VALUES (?, ?)";
            try ( Connection connection = Connector.getConnection();  PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                preparedStatement.executeUpdate();
                System.out.println("Admin account created successfully!");
            } catch (SQLException e) {
            }
        } else {
            System.out.println("Admin account already exists!");
        }
    }

    private static boolean checkAdminExists(String username) {
        String query = "SELECT * FROM Admin WHERE Username = ?";
        try ( Connection connection = Connector.getConnection();  PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try ( ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Trả về true nếu tài khoản admin tồn tại, ngược lại trả về false
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public void addAdmin() {
        Managerment m = new Managerment();
        String newUsername = sc.nextLine();
        String newpassword = sc.nextLine();
        m.createAdminAccount(newUsername, newpassword);
    }

    public int login() {
        int check_login = 0;
        System.out.println("Enter account number: ");
        String accountNumber = sc.nextLine();
        System.out.println("Enter password: ");
        String password = sc.nextLine();
        try {
            Connection conn = Connector.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM [dbo].[account] WHERE Username = ? AND Password = ?");
            stmt.setString(1, accountNumber);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                currentAdmin = new Admin(rs.getString("Username"), rs.getString("Password"));
                System.out.println("Login successful. Welcome admin, " + accountNumber + "!");
                check_login = 1;
            } else {
                if (checkTeacherExists(accountNumber, password)) {
                    currentTeacher = new Teacher(accountNumber, password);
                    System.out.println("Login successful. Welcome teacher, " + accountNumber + "!");
                    teacherID = getTeacherID(accountNumber);
                    System.out.println("Teacher ID: " + teacherID);
                    check_login = 2;
                } else {
                    System.out.println("Login failed. Incorrect account number or password.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Login failed due to an error: " + e.getMessage());
        }
        return check_login;
    }

    private int getTeacherID(String accountNumber) {
        int teacherID = 0;
        String query = "SELECT ID FROM Teacher WHERE Account_number = ?";
        try ( Connection connection = Connector.getConnection();  PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, accountNumber);
            try ( ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    teacherID = resultSet.getInt("ID");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while getting teacher ID: " + e.getMessage());
        }
        return teacherID;
    }

    public void addTeacher() {
        System.out.println("Enter teacher's account number: ");
        String accountNumber = sc.nextLine();
        if (!checkAccount_number_Teacher(accountNumber)) { // Kiểm tra xem giáo viên đã tồn tại chưa
            System.out.println("Enter teacher's name: ");
            String name = sc.nextLine();
            System.out.println("Enter teacher's address: ");
            String address = sc.nextLine();
            System.out.println("Enter teacher's password: ");
            String password = sc.nextLine();
            System.out.println("Enter teacher's phone number: ");
            String phoneNumber = sc.nextLine();
            System.out.println("Enter teacher's course: ");
            String course = sc.nextLine();
            String query = "INSERT INTO Teacher ([Name_teacher], [Address_teacher],[pass], [phone_number], [course], [Account_number]) VALUES ( ?, ?, ?, ?, ?, ?)";
            try ( Connection connection = Connector.getConnection();  PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, address);
                preparedStatement.setString(3, password);
                preparedStatement.setString(4, phoneNumber);
                preparedStatement.setString(5, course);
                preparedStatement.setString(6, accountNumber);
                preparedStatement.executeUpdate();
                System.out.println("Teacher added successfully!");
            } catch (SQLException e) {
                System.out.println("Failed to add teacher.");
            }
        } else {
            System.out.println("Teacher already exists!");
        }
    }

    public void deleteTeacher() {
        System.out.println("Nhập số tài khoản của giáo viên: ");
        String accountNumber = sc.nextLine();
        if (checkAccount_number_Teacher(accountNumber)) {
            // Kiểm tra xem có sinh viên nào được giao cho giáo viên này không
            if (checkAssignedStudents(accountNumber)) {
                System.out.println("Trước khi xóa giáo viên, bạn cần phải xử lý thông tin sinh viên được giao cho giáo viên này.");
                return;
            }

            try ( Connection connection = Connector.getConnection()) {
                // Bắt đầu một giao dịch để đảm bảo tính nhất quán của dữ liệu
                connection.setAutoCommit(false);

                // Cập nhật T_ID của tất cả sinh viên liên kết với giáo viên này thành null
                updateStudentsTIdToNull(connection, accountNumber);

                // Xóa giáo viên khỏi cơ sở dữ liệu
                deleteTeacherFromDatabase(connection, accountNumber);

                // Commit giao dịch nếu mọi thứ thành công
                connection.commit();
                System.out.println("Xóa giáo viên thành công!");
            } catch (SQLException e) {
                System.out.println("Lỗi khi xóa giáo viên: ");
//                try {
//                    // Rollback giao dịch nếu có lỗi xảy ra
//                    connection.rollback();
//                } catch (SQLException rollbackException) {
//                    System.out.println("Lỗi khi rollback giao dịch: " + rollbackException.getMessage());
//                }
            }
        } else {
            System.out.println("Giáo viên không tồn tại!");
        }
    }

    private void updateStudentsTIdToNull(Connection connection, String accountNumber) throws SQLException {
        String updateQuery = "UPDATE Student SET T_ID = NULL WHERE T_ID = (SELECT ID FROM Teacher WHERE Account_number = ?)";
        try ( PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, accountNumber);
            preparedStatement.executeUpdate();
        }
    }

    private void deleteTeacherFromDatabase(Connection connection, String accountNumber) throws SQLException {
        String deleteQuery = "DELETE FROM Teacher WHERE Account_number = ?";
        try ( PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setString(1, accountNumber);
            preparedStatement.executeUpdate();
        }
    }

    private boolean checkAssignedStudents(String accountNumber) {
        String query = "SELECT COUNT(*) FROM [dbo].[Student] WHERE [teacher_account_number] = ?";
        try ( Connection connection = Connector.getConnection();  PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, accountNumber);
            try ( ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {

        }
        return false;
    }

    public void updateTeacher() {
        try {
            System.out.println("Enter teacher's account number: ");
            String accountNumber = sc.nextLine();
            if (checkAccount_number_Teacher(accountNumber)) {
                System.out.println("Enter new teacher's name: ");
                String newName = sc.nextLine();
                System.out.println("Enter new teacher's address: ");
                String newAddress = sc.nextLine();
                System.out.println("Enter new teacher's password: ");
                String newPass = sc.nextLine();
                System.out.println("Enter new teacher's phone number: ");
                String newPhoneNumber = sc.nextLine();
                System.out.println("Enter new teacher's course: ");
                String newCourse = sc.nextLine();

                String query = "UPDATE Teacher SET Name_teacher = ?, Address_teacher = ?, pass = ?, phone_number = ?, course = ? WHERE Account_number = ?";
                try ( Connection connection = Connector.getConnection();  PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, newName);
                    preparedStatement.setString(2, newAddress);
                    preparedStatement.setString(3, newPass);
                    preparedStatement.setString(4, newPhoneNumber);
                    preparedStatement.setString(5, newCourse);
                    preparedStatement.setString(6, accountNumber);
                    preparedStatement.executeUpdate();
                    System.out.println("Teacher update successful!");
                } catch (SQLException e) {
                    System.out.println("Failed to update teacher.");
                }
            } else {
                System.out.println("Teacher does not exist!");
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter valid data.");
        }
    }

    public void addStudent() {
        System.out.println("Enter student's name: ");
        String name = sc.nextLine();
        System.out.println("Enter student's address: ");
        String address = sc.nextLine();
        System.out.println("Enter student's phone number: ");
        String phoneNumber = sc.nextLine();
        System.out.println("Enter student's tearcherID: ");
        teacherID = sc.nextInt();
        sc.nextLine();
        System.out.println("Enter student's course: ");
        String course = sc.nextLine();
        String query = "INSERT INTO Student ( [Name_student], [address_student],[phone_number], [T_ID], [Course]) VALUES ( ?, ?, ?, ?, ?)";
        try ( Connection connection = Connector.getConnection();  PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, address);
            preparedStatement.setString(3, phoneNumber);
            preparedStatement.setInt(4, teacherID);
            preparedStatement.setString(5, course);
            preparedStatement.executeUpdate();
            System.out.println("Student added successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to add student.");
        }
    }

    public void updateStudent() {
        try {
            System.out.println("Enter student's ID: ");
            int studentID = Integer.parseInt(sc.nextLine());
            if (checkStudentExists(studentID)) {
                System.out.println("Enter new student's name: ");
                String newName = sc.nextLine();
                System.out.println("Enter new student's address: ");
                String newAddress = sc.nextLine();
                System.out.println("Enter new student's phone number: ");
                String newPhoneNumber = sc.nextLine();
                System.out.println("Enter new Student's TeacherID: ");
                teacherID = sc.nextInt();
                sc.nextLine();
                System.out.println("Enter new student's course: ");
                String newCourse = sc.nextLine();

                String query = "UPDATE Student SET Name_student = ?, address_student = ?, phone_number = ?, Course = ?,[T_ID] = ? WHERE ID = ?";
                try ( Connection connection = Connector.getConnection();  PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, newName);
                    preparedStatement.setString(2, newAddress);
                    preparedStatement.setString(3, newPhoneNumber);
                    preparedStatement.setString(4, newCourse);
                    preparedStatement.setInt(5, teacherID);
                    preparedStatement.setInt(6, studentID);
                    preparedStatement.executeUpdate();
                    System.out.println("Student update successful!");
                } catch (SQLException e) {
                    System.out.println("Failed to update student.");
                }
            } else {
                System.out.println("Student does not exist!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid data.");
        }
    }

    public void deleteStudent() {
        try {
            System.out.println("Enter student's ID: ");
            int studentID = Integer.parseInt(sc.nextLine());
            if (checkStudentExists(studentID)) {
                String query = "DELETE FROM Student WHERE ID = ? ";
                try ( Connection connection = Connector.getConnection();  PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, studentID);
                    preparedStatement.executeUpdate();
                    System.out.println("Student deleted successfully!");
                } catch (SQLException e) {
                    System.out.println("Failed to delete student.");
                }
            } else {
                System.out.println("Student does not exist!");
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter valid data.");
        }
    }

    private boolean checkIDTeacher(int id) {
        String query = "SELECT * FROM Teacher WHERE ID = ?";
        try ( Connection connection = Connector.getConnection();  PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            try ( ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean checkTeacherExists(String accountNumber, String password) {
        String query = "SELECT * FROM [dbo].[Teacher] WHERE [Account_number] = ? AND  [pass]= ?";
        try ( Connection connection = Connector.getConnection();  PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, accountNumber);
            preparedStatement.setString(2, password);
            try ( ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println("Error while checking teacher existence: " + e.getMessage());
            return false;
        }
    }

    public boolean checkStudentExists(int id) {
        String query = "SELECT * FROM Student WHERE ID = ?";
        try ( Connection connection = Connector.getConnection();  PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            try ( ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean checkAccount_number_Teacher(String account_number) {
        String query = "SELECT * FROM Teacher WHERE Account_number = ?";
        try ( Connection connection = Connector.getConnection();  PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, account_number);
            try ( ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Trả về true nếu giáo viên tồn tại trong cơ sở dữ liệu, ngược lại trả về false
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
