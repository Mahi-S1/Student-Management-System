
package com.example.studentmanagement;

import java.sql.*;
import java.util.Scanner;

public class StudentManagementJDBC {

    static final String URL =
            "jdbc:mysql://localhost:3306/student_management";
    static final String USER = "root";
    static final String PASSWORD = "mahi";

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        try (Connection con =
                     DriverManager.getConnection(URL, USER, PASSWORD)) {

            System.out.println("Connected to MySQL successfully!");

            int choice;

            do {
                System.out.println("\n===== STUDENT MANAGEMENT SYSTEM =====");
                System.out.println("1. Display Average Scores");
                System.out.println("2. Display Performance Category");
                System.out.println("3. Find Stronger Subject");
                System.out.println("4. Search Student");
                System.out.println("5. Display Merit List");
                System.out.println("6. Find Students with Same Skills");
                System.out.println("7. Find Students Needing Improvement");
                System.out.println("8. Add New Student");
                System.out.println("9. Exit");
                System.out.print("Enter your choice: ");

                choice = sc.nextInt();

                switch (choice) {

                    case 1:
                        display(con,
                                "SELECT student_id, student_name, " +
                                        "(programming_score + mathematics_score)/2 AS average_score " +
                                        "FROM students");
                        break;

                    case 2:
                        display(con,
                                "SELECT student_name, " +
                                        "(programming_score + mathematics_score)/2 AS average_score, " +
                                        "CASE " +
                                        "WHEN (programming_score + mathematics_score)/2 >= 85 THEN 'Excellent' " +
                                        "WHEN (programming_score + mathematics_score)/2 >= 70 THEN 'Good' " +
                                        "WHEN (programming_score + mathematics_score)/2 >= 50 THEN 'Satisfactory' " +
                                        "ELSE 'Needs Improvement' END AS performance " +
                                        "FROM students");
                        break;

                    case 3:
                        display(con,
                                "SELECT student_name, " +
                                        "CASE " +
                                        "WHEN programming_score > mathematics_score THEN 'Programming' " +
                                        "WHEN mathematics_score > programming_score THEN 'Mathematics' " +
                                        "ELSE 'Both Equal' END AS stronger_subject " +
                                        "FROM students");
                        break;

                    case 4:
                        System.out.println("1. Search by ID");
                        System.out.println("2. Search by Email");
                        System.out.println("3. Search by Course");
                        System.out.print("Enter search choice: ");

                        int searchChoice = sc.nextInt();
                        sc.nextLine();

                        String sql = "";
                        String value = "";

                        if (searchChoice == 1) {
                            sql = "SELECT * FROM students WHERE student_id = ?";
                            System.out.print("Enter Student ID: ");
                            value = sc.nextLine();

                        } else if (searchChoice == 2) {
                            sql = "SELECT * FROM students WHERE email = ?";
                            System.out.print("Enter Email: ");
                            value = sc.nextLine();

                        } else if (searchChoice == 3) {
                            sql = "SELECT * FROM students WHERE course_name = ?";
                            System.out.print("Enter Course: ");
                            value = sc.nextLine();

                        } else {
                            System.out.println("Invalid search choice!");
                            break;
                        }

                        try (PreparedStatement ps = con.prepareStatement(sql)) {
                            ps.setString(1, value);
                            try (ResultSet rs = ps.executeQuery()) {
                                printResult(rs);
                            }
                        }
                        break;

                    case 5:
                        display(con,
                                "SELECT student_id, student_name, " +
                                        "(programming_score + mathematics_score)/2 AS average_score " +
                                        "FROM students ORDER BY average_score DESC");
                        break;

                    case 6:
                        display(con,
                                "WITH skill_counts AS (" +
                                        "SELECT student_id, COUNT(DISTINCT skill) AS total_skills " +
                                        "FROM student_skills GROUP BY student_id), " +

                                        "common_skills AS (" +
                                        "SELECT a.student_id AS id1, b.student_id AS id2, " +
                                        "COUNT(DISTINCT a.skill) AS common_count, " +
                                        "GROUP_CONCAT(DISTINCT a.skill ORDER BY a.skill) AS skills " +
                                        "FROM student_skills a JOIN student_skills b " +
                                        "ON a.skill = b.skill AND a.student_id < b.student_id " +
                                        "GROUP BY a.student_id, b.student_id) " +

                                        "SELECT s1.student_name AS Student1, " +
                                        "s2.student_name AS Student2, c.skills AS Common_Skills " +
                                        "FROM common_skills c " +
                                        "JOIN skill_counts sc1 ON c.id1 = sc1.student_id " +
                                        "JOIN skill_counts sc2 ON c.id2 = sc2.student_id " +
                                        "JOIN students s1 ON c.id1 = s1.student_id " +
                                        "JOIN students s2 ON c.id2 = s2.student_id " +
                                        "WHERE c.common_count = sc1.total_skills " +
                                        "AND c.common_count = sc2.total_skills");
                        break;

                    case 7:
                        display(con,
                                "SELECT * FROM students " +
                                        "WHERE programming_score < 50 " +
                                        "AND mathematics_score >= 75");
                        break;

                    case 8:
                        addStudent(con);
                        break;

                    case 9:
                        System.out.println("Exiting program...");
                        break;

                    default:
                        System.out.println("Invalid choice!");
                }

            } while (choice != 9);

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    static void display(Connection con, String sql)
            throws SQLException {

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            printResult(rs);
        }
    }

    static void printResult(ResultSet rs) throws SQLException {

        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();

        boolean found = false;

        while (rs.next()) {
            found = true;

            for (int i = 1; i <= columns; i++) {
                System.out.print(md.getColumnLabel(i) + ": "
                        + rs.getString(i) + " | ");
            }

            System.out.println();
        }

        if (!found) {
            System.out.println("No records found.");
        }
    }

    static void addStudent(Connection con) throws SQLException {

        sc.nextLine();

        System.out.print("Enter Student ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Student Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        System.out.print("Enter Course Name: ");
        String course = sc.nextLine();

        System.out.print("Enter Programming Score: ");
        double programming = sc.nextDouble();

        System.out.print("Enter Mathematics Score: ");
        double mathematics = sc.nextDouble();
        sc.nextLine();

        if (programming < 0 || programming > 100 ||
                mathematics < 0 || mathematics > 100) {
            System.out.println("Scores must be between 0 and 100!");
            return;
        }

        System.out.print("Enter skills separated by commas: ");
        String[] skills = sc.nextLine().split(",");

        String sql = "INSERT INTO students VALUES (?, ?, ?, ?, ?, ?)";

        try {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setString(3, email);
                ps.setString(4, course);
                ps.setDouble(5, programming);
                ps.setDouble(6, mathematics);

                ps.executeUpdate();
            }

            String skillSQL =
                    "INSERT INTO student_skills VALUES (?, ?)";

            try (PreparedStatement ps =
                         con.prepareStatement(skillSQL)) {

                for (String skill : skills) {
                    ps.setInt(1, id);
                    ps.setString(2, skill.trim());
                    ps.addBatch();
                }

                ps.executeBatch();
            }

            con.commit();
            System.out.println("Student added successfully!");

        } catch (SQLException e) {
            con.rollback();
            System.out.println("Could not add student: "
                    + e.getMessage());

        } finally {
            con.setAutoCommit(true);
        }
    }
}
