
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Scanner;
import java.sql.Array;

public class Comp3005FinalProject {

    //gloabl variables
    static Connection conn;
    static Scanner scanner = new Scanner(System.in);
    //Keeps track of whether the user is logged in or not
    static boolean loggedIn = false;
    //keeps track of if the admin has already processed payments for this week
    static boolean billedMembers = false;
    //Holds the id of the user that is logged in
    static int userId = 0;
    //Used to help print times out in a nicer format
    static String[] times = {
            "9:05 AM - 9:55 AM",
            "10:05 AM - 10:55 AM",
            "11:05 AM - 11:55 AM",
            "12:05 PM - 12:55 PM",
            "1:05 PM - 1:55 PM",
            "2:05 PM - 2:55 PM",
            "3:05 PM - 3:55 PM",
            "4:05 PM - 4:55 PM"
    };
    //same as above but for days
    static String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };

    public static void main(String[] args) {
        // Connecting to DB code from slides
        String url = "jdbc:postgresql://localhost/FinalProject";
        String user = "armaan";
        String password = "password";
        try {
            Class.forName("org.postgresql.Driver");

            conn = DriverManager.getConnection(url, user, password);
            if (conn != null) {
                // If it connects it prints a message to the user and then calls the controlFlow
                // method
                System.out.println("Connected to PostgreSQL successfully!");

                //createRoom method is used to create rooms in the database
                //However since in real life the admin cannot just make a new room, they have not been given that ability
                // createRoom("RoomOne", 15);
                // createRoom("RoomTwo", 20);
                // createRoom("RoomThree", 10);
                // createRoom("RoomFour", 25);
                // createRoom("RoomFive", 15);

                controlFlow();
            } else {
                System.out.println("Failed to establish connection.");
            }
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method takes a PersonID and returns the MemberID
     * @param personId The PersonID of the person
     * @return The MemberID of the person
     */
    private static int getMemberId(int personId) {

        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT MemberID FROM Member WHERE PersonID = " + personId + ";";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getInt("MemberID");
            } else {
                return -1;
            }

        } catch (SQLException e) {
            System.out.println("This person is not a member.");
            return -1;
        }

    };

    /**
     * This method takes a PersonID and returns the TrainerID
     * @param personId The PersonID of the person
     * @return The TrainerID of the person
     */
    private static int getTrainerId(int personId) {

        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT TrainerID FROM Trainer WHERE PersonID = " + personId + ";";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getInt("TrainerID");
            } else {
                return 1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }

    };

    /**
     * This method takes a memberID and then checks if they can update their calendar at the given time
     * @param memberID The MemberID of the member
     * @param day Integer representing the day of the week
     * @param time Integer representing the time of the day
     * @param valueOne If the value at the given time is this, then the will update the value to valueTwo
     * @param valueTwo The value that the value at the given time will be updated to
     * @return
     */
    private static boolean checkMemberAvailability(int memberID, int day, int time, int valueOne, int valueTwo) {

        try {

            //Get the schedule of the member
            Integer[][] memberSchedule = getSchedule(memberID);

            if (memberSchedule[day][time] != valueOne) {

                return false;

            }

            memberSchedule[day][time] = valueTwo;

            //Converts the array into something that can be sent to the database
            Array memberAva = conn.createArrayOf("integer", memberSchedule);

            Statement stmt = conn.createStatement();

            String sql = "UPDATE Person SET Schedule = '" + memberAva + "' WHERE PersonID = " + memberID + ";";

            stmt.executeUpdate(sql);

            return true;

        } catch (Exception e) {
            System.err.println("There was an error checking the member's availability.");
            return false;

        }

    };

    /**
     * This method takes the ID of the user that is logged in and returns which type of account they have
     * @return  The type of account the user has
     */
    private static String getTypes() {

        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT AccountType FROM Person WHERE PersonID = " + userId + ";";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getString("AccountType");
            } else {
                return "Unknown";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Unknown";
        }

    };

    /**
     * This method takes the ID of the user that is logged in and returns their username
     //* @param personId The ID of the person
     * @return The username of the person
     */
    private static String getUsername() {

        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT Name FROM Person WHERE PersonID = " + userId + ";";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getString("Name");
            } else {
                return "Unknown";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Unknown";
        }

    };

    /**
     * This method takes the ID of the user that is logged in and allows them to edit their personal information
     */
    private static void updateUser() {

        int choice = 99;

        while (choice != 8) {

            System.out.println("1. Update Name");
            System.out.println("2. Update password");
            System.out.println("3. Update height");
            System.out.println("4. Update weight");
            System.out.println("5. Update age");
            System.out.println("6. Update fitness goals");
            System.out.println("7. Add an Achievement");
            System.out.println("8. Exit");

            System.out.println("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            //Switches through the different options
            //Gathers the required data then calls the corresponding methods
            switch (choice) {
                case 1:
                    System.out.println("Please enter your new name: ");
                    String name = scanner.nextLine();
                    try {
                        Statement stmt = conn.createStatement();
                        String sql = "UPDATE Person SET Name = '" + name + "' WHERE PersonID = " + userId + ";";
                        stmt.executeUpdate(sql);
                        System.out.println("Name updated successfully!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    System.out.println("Please enter your new password: ");
                    String password = scanner.nextLine();

                    try {
                        Statement stmt = conn.createStatement();
                        String sql = "UPDATE Person SET Password = '" + password + "' WHERE PersonID = " + userId + ";";
                        stmt.executeUpdate(sql);
                        System.out.println("Password updated successfully!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    System.out.println("Please enter your new height: ");
                    String height = scanner.nextLine();

                    try {
                        Statement stmt = conn.createStatement();
                        String sql = "UPDATE Person SET Height = '" + height + "' WHERE PersonID = " + userId + ";";
                        stmt.executeUpdate(sql);
                        System.out.println("Height updated successfully!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    System.out.println("Please enter your new weight: ");
                    String weight = scanner.nextLine();

                    try {
                        Statement stmt = conn.createStatement();
                        String sql = "UPDATE Person SET Weight = '" + weight + "' WHERE PersonID = " + userId + ";";
                        stmt.executeUpdate(sql);
                        System.out.println("Weight updated successfully!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    System.out.println("Please enter your new age: ");
                    String age = scanner.nextLine();

                    try {
                        Statement stmt = conn.createStatement();
                        String sql = "UPDATE Person SET Age = '" + age + "' WHERE PersonID = " + userId + ";";
                        stmt.executeUpdate(sql);
                        System.out.println("Age updated successfully!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 6:
                    System.out.println("Please enter your new fitness goals: ");
                    String fitnessGoals = scanner.nextLine();

                    try {
                        Statement stmt = conn.createStatement();
                        String sql = "UPDATE Member SET FitnessGoals = '" + fitnessGoals + "' WHERE PersonID = "
                                + userId + ";";
                        stmt.executeUpdate(sql);
                        System.out.println("Fitness goals updated successfully!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 7:
                    System.out.println(
                            "Congratulation on your achievement! Please enter what the achievement is in one line: ");
                    String achievement = scanner.nextLine();

                    try {

                        Statement stmt = conn.createStatement();
                        String sql = "SELECT FitnessAchievements FROM Member WHERE PersonID = " + userId + ";";
                        ResultSet rs = stmt.executeQuery(sql);

                        if (rs.next()) {
                            String achievements = rs.getString("FitnessAchievements");
                            if (achievements.equals("No achievements yet")) {

                                achievements = achievement;

                            } else {

                                achievements += "\n" + achievement;

                            }

                            Statement stmt2 = conn.createStatement();
                            String sql2 = "UPDATE Member SET FitnessAchievements = '" + achievements
                                    + "' WHERE PersonID = " + userId + ";";
                            stmt2.executeUpdate(sql2);
                            System.out.println("Achievement added successfully!");
                        }

                    } catch (Exception e) {

                        System.out.println("There was an error adding the achievement.");
                    }

                    break;
                default:
                    break;
            }

        }

    };

    /**
     * This method prints the dashboard of the user that is currently logged in
     */
    private static void displayMemberDashboard() {

        String name = "";
        double height = 0.0;
        double weight = 0.0;
        int age = 0;
        String fitnessGoals = "";
        String fitnessAchievements = "";
        double bill = 0.0;
        double unprocessedPayments = 0.0;

        try {

            Statement stmt = conn.createStatement();
            String sql = "SELECT Name, Height, Weight, Age FROM Person WHERE PersonID = " + userId + ";";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                name = rs.getString("Name");
                height = rs.getDouble("Height");
                weight = rs.getDouble("Weight");
                age = rs.getInt("Age");
            }

        } catch (Exception e) {
            System.out.println("There was an error gathering data from the Person Table");
        }

        try {

            Statement stmt = conn.createStatement();
            String sql = "SELECT FitnessGoals, FitnessAchievements FROM Member WHERE PersonID = "
                    + userId + ";";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                fitnessGoals = rs.getString("FitnessGoals");
                fitnessAchievements = rs.getString("FitnessAchievements");

            }

        } catch (Exception e) {
            System.out.println("There was an error gathering data from the Member Table");
        }

        System.out.println("Welcome to your dashboard, " + name + "!");

        System.out.println("Height: " + height);
        System.out.println("Weight: " + weight);
        System.out.println("BMI : " + (weight / (height * height)));
        System.out.println("Age: " + age);
        System.out.println("\nFitness Goals: \n" + fitnessGoals);
        System.out.println("\nFitness Achievements: \n" + fitnessAchievements);
        System.out.println("Your routines are : ");
        viewRoutines();
        System.out.println("Bill: $" + bill);
        System.out.println("Unprocessed Payments: $" + unprocessedPayments);

    };

    /**
     * This method allows the user that is currently logged in to make a payment
     */
    private static void makePayment() {

        try {

            Statement stmt = conn.createStatement();
            String sql = "SELECT OutstandingBalance, PendingPayments FROM Billing WHERE MemberID = " + getMemberId(userId) + ";";
            ResultSet rs = stmt.executeQuery(sql);

            double outstandingBalance;
            double pendingPayments;

            if (rs.next()) {
                
                outstandingBalance = rs.getDouble("OutstandingBalance");
                pendingPayments = rs.getDouble("PendingPayments");

                if (outstandingBalance - pendingPayments == 0) {
                    System.out.println("You have no outstanding balance!");

                } else if (outstandingBalance - pendingPayments < 0) {
                    System.out.println("You have overpaid by: $" + (pendingPayments - outstandingBalance));
                } else {

                    System.out.println("You have an outstanding balance of: $" + (outstandingBalance - pendingPayments));

                }

                System.out.println("Please enter the amount you would like to pay: ");

                double payment = scanner.nextDouble();
                scanner.nextLine();

                if (payment < 0) {
                    System.out.println("Invalid payment amount.");
                    return;
                }

                pendingPayments += payment;

                Statement stmt2 = conn.createStatement();
                String sql2 = "UPDATE Billing SET PendingPayments = '" + pendingPayments + "' WHERE MemberID = " + getMemberId(userId) + ";";

                stmt2.executeUpdate(sql2);

                System.out.println("Payment made successfully!");

            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    };

    /**
     * This method allows the user that is currently logged in to join a class if one is available
     */
    private static void joinClass() {

        viewClasses();

        System.out.println("Please enter the ID of the class you would like to join: ");
        int classID = scanner.nextInt();
        scanner.nextLine();

        try {

            Statement stmt = conn.createStatement();

            String sql = "SELECT NumberOfParticipants, Weekday, Time, RoomID FROM Class WHERE ClassID = " + classID
                    + ";";

            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {

                int roomID = rs.getInt("RoomID");
                int numParticipants = rs.getInt("NumberOfParticipants");
                int day = rs.getInt("Weekday");
                int time = rs.getInt("Time");

                if (numParticipants >= getRoomCapacity(roomID)) {

                    System.out.println("The class is full!");
                    return;

                }

                //takes the if the member was able to book off the given date then it continues the code
                if (!checkMemberAvailability(userId, day, time, 0, 2)) {

                    return;

                }

                Statement stmt2 = conn.createStatement();

                String sql2 = "INSERT INTO ClassMemberBooking (MemberID, ClassID, Weekday, Time) VALUES ('"
                        + getMemberId(userId)
                        + "', '" + classID + "', '" + day + "', '" + time + "');";

                stmt2.executeUpdate(sql2);

                Statement stmt3 = conn.createStatement();

                String sql3 = "UPDATE Class SET NumberOfParticipants = '" + (numParticipants + 1) + "' WHERE ClassID = "
                        + classID + ";";

                stmt3.executeUpdate(sql3);

                System.out.println("You have successfully joined the class!");

            } else {
                System.out.println("Class not found!");
            }


            stmt = conn.createStatement();

            sql = "SELECT PaymentDue FROM Billing WHERE MemberID = " + getMemberId(userId) + ";";

            rs = stmt.executeQuery(sql);

            if (rs.next()) {

                double paymentDue = rs.getDouble("PaymentDue");

                paymentDue += 10;

                Statement stmt4 = conn.createStatement();

                String sql4 = "UPDATE Billing SET PaymentDue = '" + paymentDue + "' WHERE MemberID = "
                        + getMemberId(userId) + ";";

                stmt4.executeUpdate(sql4);

            }




        } catch (Exception e) {
            System.out.println("There was an error joining the class.");
        }

    };

    /**
      * This method allows the user to leave a class that they are currently in 
      * @param ID ID of the user that is currently logged in
      * @param classID ID of the class that the user wants to leave
      */
    private static void leaveClass(int ID, int classID) {

        try {

            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM ClassMemberBooking WHERE MemberID = " + getMemberId(ID) + " AND ClassID = "
                    + classID + " RETURNING Weekday, Time;";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {

                int day = rs.getInt("Weekday");
                int time = rs.getInt("Time");

                if (!checkMemberAvailability(ID, day, time, 2, 0)) {

                    return;

                }

                Statement stmt2 = conn.createStatement();

                String sql2 = "UPDATE Class SET NumberOfParticipants = (NumberOfParticipants - 1) WHERE ClassID = "
                        + classID + ";";

                stmt2.executeUpdate(sql2);

                System.out.println("You have successfully left the class!");

            }


            stmt = conn.createStatement();

            sql = "SELECT PaymentDue FROM Billing WHERE MemberID = " + getMemberId(ID) + ";";

            rs = stmt.executeQuery(sql);

            if (rs.next()) {

                double paymentDue = rs.getDouble("PaymentDue");

                paymentDue -= 10;

                Statement stmt3 = conn.createStatement();

                String sql3 = "UPDATE Billing SET PaymentDue = '" + paymentDue + "' WHERE MemberID = "
                        + getMemberId(ID) + ";";

                stmt3.executeUpdate(sql3);

            }


        } catch (Exception e) {
            System.out.println("There was an error leaving the class.");
            e.printStackTrace();
        }

    };

    /**
     * This method takes a roomID and returns the capacity of the room
     * @param roomID The ID of the room
     * @return The capacity of the room
     */
    private static int getRoomCapacity(int roomID) {

        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT Capacity FROM Room WHERE RoomID = " + roomID + ";";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return rs.getInt("Capacity");
            } else {
                return 0;
            }

        } catch (SQLException e) {
            System.out.println("There was an error getting the room capacity.");
            return 0;
        }

    };

    /**
     * This method prints all the rooms that are present in the database
     */
    public static void printRooms() {

        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT RoomID, RoomName FROM Room";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                System.out.println("Room ID: " + rs.getInt("RoomID") + " | Room Name: " + rs.getString("RoomName"));
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        ;

    };

    /**
     * This method allows trainers to set their availability
     */
    public static void setAvailability(Integer[][] schedule) {

        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };
        System.out.println("Please enter the day you want to set availability for");
        System.out.println("1: Monday\n2: Tuesday\n3: Wednesday\n4: Thursday\n5: Friday");
        int day = scanner.nextInt() - 1;
        scanner.nextLine();

        if (day < 0 || day >= days.length) {
            System.out.println("Invalid day. Please enter a number between 1 and 5.");
            return;
        }

        System.out.println("Enter the starting block (1-8, corresponding to 9:05 AM - 4:05 PM):");
        int startBlock = scanner.nextInt() - 1;
        scanner.nextLine();
        System.out.println("Enter the ending block (1-8, corresponding to 9:55 AM - 4:55 PM):");
        int endBlock = scanner.nextInt() - 1;
        scanner.nextLine();

        // Check for valid block range
        if (startBlock < 0 || endBlock >= 8 || startBlock > endBlock) {
            System.out.println("Invalid block range. Start should be less than end, and within 1-8.");
            return;
        }

        boolean canUpdate = true;
        for (int i = startBlock; i <= endBlock; i++) {
            if (schedule[day][i] != 0) {
                canUpdate = false;
                break;
            }
        }

        if (canUpdate) {
            Arrays.fill(schedule[day], startBlock, endBlock + 1, 4);
            System.out.println("Availability updated for " + days[day] + ".");
        } else {
            System.out.println("Cannot update availability. One or more blocks already have events in them.");
            return;
        }

        // Update the database

        try {
            Array scheduleArray = conn.createArrayOf("integer", schedule);
            Statement stmt = conn.createStatement();
            String sql = "UPDATE Person SET Schedule = '" + scheduleArray + "' WHERE PersonID = " + userId + ";";
            stmt.executeUpdate(sql);
            System.out.println("Schedule updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method checks if the given room is available at the given time and then updates its schedule
     * @param roomID The ID of the room
     * @param day An integer representing the day of the week
     * @param time An integer representing the time of the day
     * @param deleteOrAdd A boolean that is true if the room is being deleted from the schedule and false if it is being added
     * @return
     */
    private static boolean checkRoomAvailability(int roomID, int day, int time, boolean deleteOrAdd) {

        int baseValue;
        int newValue;

        if (deleteOrAdd) {

            baseValue = 2;
            newValue = 0;

        } else {

            baseValue = 0;
            newValue = 2;

        }

        try {

            Integer[][] roomSchedule = getRoomSchedule(roomID);

            if (roomSchedule[day][time] != baseValue) {

                return false;

            } else {

                roomSchedule[day][time] = newValue;

                Array roomAva = conn.createArrayOf("integer", roomSchedule);

                Statement stmt = conn.createStatement();

                String sql = "UPDATE Room SET Schedule = '" + roomAva + "' WHERE RoomID = " + roomID + ";";

                stmt.executeUpdate(sql);

                return true;

            }

        } catch (Exception e) {
            System.out.println("There was an error checking the room availability.");
            return false;

        }

    };

    /**
     * This method allows the admin to create a room
     * @param name The name of the room 
     * @param capacity The capacity of the room
     */
    private static void createRoom(String name, int capacity) {

        try {
            Statement stmt = conn.createStatement();
            Integer[][] availability = new Integer[5][8];
            for (int i = 0; i < availability.length; i++) {
                for (int j = 0; j < availability[i].length; j++) {
                    availability[i][j] = 0;
                }
            }

            Array schedule = conn.createArrayOf("integer", availability);

            String sql = "INSERT INTO Room (RoomName, Capacity, Schedule) VALUES ('" + name + "', '" + capacity + "','"
                    + schedule + "');";

            stmt.executeUpdate(sql);

            System.out.println("Room created successfully!");

        } catch (SQLException e) {

            System.out.println("There was an error creating the room. Please try again.");

        }

    };

    /**
     * This method allows the admin to create a class
     */
    private static void createClass() {

        System.out.println("Please enter the name of the class: ");
        String name = scanner.nextLine();

        System.out.println("Please enter the day of the week the class is on: ");
        System.out.println("1. Monday");
        System.out.println("2. Tuesday");
        System.out.println("3. Wednesday");
        System.out.println("4. Thursday");
        System.out.println("5. Friday");

        int day = scanner.nextInt();
        scanner.nextLine();
        day--;

        System.out.println("Please enter the time the class is at: ");
        System.out.println("1. 9:05 AM - 9:55 AM");
        System.out.println("2. 10:05 AM - 10:55 AM");
        System.out.println("3. 11:05 AM - 11:55 AM");
        System.out.println("4. 12:05 PM - 12:55 PM");
        System.out.println("5. 1:05 PM - 1:55 PM");
        System.out.println("6. 2:05 PM - 2:55 PM");
        System.out.println("7. 3:05 PM - 3:55 PM");
        System.out.println("8. 4:05 PM - 4:55 PM");

        int time = scanner.nextInt();
        scanner.nextLine();
        time--;

        printRooms();
        System.out.println("Please enter the ID of the room the class is in: ");
        int roomID = scanner.nextInt();
        scanner.nextLine();

        printTrainers();

        System.out.println("Please enter the ID of the trainer for the class: ");
        int trainerID = scanner.nextInt();
        scanner.nextLine();


        if (!bookTrainer(trainerID, day, time, 0, 2, true, roomID)) {
            System.out.println("The trainer or room is not available at that time.");
            return;

        }

        try {
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO Class (ClassName, Weekday, Time, RoomID, PersonID) VALUES ('" + name + "', '"
                    + day + "', '"
                    + time + "', '" + roomID + "', '" + trainerID + "');";
            stmt.executeUpdate(sql);
            System.out.println("Class created successfully!");
        } catch (SQLException e) {
            
            System.out.println("There was an error creating the class.");

        }

    };

    /**
     * This method allows the admin to delete a class that has been created
     */
    private static void deleteClass() {

        viewClasses();

        System.out.println("Please enter the ID of the class you would like to delete: ");
        int classID = scanner.nextInt();
        scanner.nextLine();

        try {

            Statement stmt = conn.createStatement();
            String sql = "SELECT PersonID, RoomID, Weekday, Time, NumberOfParticipants  FROM Class WHERE ClassID = "
                    + classID
                    + ";";
            ;

            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {

                int trainersID = rs.getInt("PersonID");
                int roomID = rs.getInt("RoomID");
                int day = rs.getInt("Weekday");
                int time = rs.getInt("Time");
                int number = rs.getInt("NumberOfParticipants");

                if (number != 0) {

                    if (!checkRoomAvailability(roomID, day, time, true)) {

                        return;

                    }
                    System.out.println(758);
                    Statement stmt2 = conn.createStatement();

                    String sql2 = "SELECT MemberID FROM ClassMemberBooking WHERE ClassID = " + classID + ";";

                    rs = stmt2.executeQuery(sql2);

                    while (rs.next()) {

                        int memberID = rs.getInt("MemberID");

                        Statement stmt3 = conn.createStatement();

                        String sql3 = "SELECT PersonID FROM Member WHERE MemberID = " + memberID + ";";

                        ResultSet rs2 = stmt3.executeQuery(sql3);

                        if (rs2.next()) {

                            int personID = rs2.getInt("PersonID");

                            leaveClass(personID, classID);

                        }

                        Statement stmt4 = conn.createStatement();

                        String sql4 = "DELETE FROM ClassMemberBooking WHERE MemberID = " + memberID + " AND ClassID = "
                                + classID + ";";

                        stmt4.executeUpdate(sql4);

                    }

                }

                Integer[][] trainerSchedule = getSchedule(trainersID);

                trainerSchedule[day][time] = 0;

                Array trainerAva = conn.createArrayOf("integer", trainerSchedule);

                Statement stmt5 = conn.createStatement();

                String sql5 = "UPDATE Person SET Schedule = '" + trainerAva + "' WHERE PersonID = " + trainersID + ";";

                stmt5.executeUpdate(sql5);

            }

            Statement stmt5 = conn.createStatement();
            System.out.println("classID: " + classID);
            String sql5 = "DELETE FROM Class WHERE ClassID = " + classID + ";";

            stmt5.executeUpdate(sql5);

            System.out.println("Class deleted successfully!");
        } catch (Exception e) {
            System.out.println("There was an error deleting the class.");
            e.printStackTrace();
        }

    };

    /**
     * This method prints all the classes that are present in the database
     */
    private static void viewClasses() {

        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT ClassName, Weekday, Time, RoomID, ClassID FROM Class";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                System.out.println("Class Name: " + rs.getString("ClassName"));
                System.out.println("Day: " + days[rs.getInt("Weekday")]);
                System.out.println("Time: " + times[rs.getInt("Time")]);
                System.out.println("Room ID: " + rs.getInt("RoomID"));
                System.out.println("Class ID: " + rs.getInt("ClassID"));
                System.out.println("\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        ;

    };

    /**
     * This method prints the classes that the user is currently in
     */
    private static void viewMyClasses() {

        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT ClassID FROM ClassMemberBooking WHERE MemberID = " + getMemberId(userId)+ ";";

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int classID = rs.getInt("ClassID");
                Statement stmt2 = conn.createStatement();
                String sql2 = "SELECT ClassName, Weekday, Time, RoomID FROM Class WHERE ClassID = " + classID + ";";

                ResultSet rs2 = stmt2.executeQuery(sql2);

                if (rs2.next()) {

                    System.out.println("Class Name: " + rs2.getString("ClassName"));
                    System.out.println("Day: " + days[rs2.getInt("Weekday")]);
                    System.out.println("Time: " + times[rs2.getInt("Time")]);
                    System.out.println("Room ID: " + rs2.getInt("RoomID"));
                    System.out.println("ClassID: " + classID);
                    System.out.println("\n");

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    };

    /**
     * This method allows the user to create a routine
     */
    private static void createRoutine() {

        try {

            System.out.println("Please enter the name of the routine: ");
            String name = scanner.nextLine();

            System.out.println("Please enter a one line description of the routine: ");
            String desc = scanner.nextLine();

            System.out.println("Please enter the duration of the routine: ");
            String dur = scanner.nextLine();

            Statement stmt = conn.createStatement();

            String sql = "INSERT INTO Routine (MemberID, RoutineName, Description, Duration) VALUES ('"
                    + getMemberId(userId) + "', '" + name + "', '" + desc + "', '" + dur + "');";

            stmt.executeUpdate(sql);

            System.out.println("Routine created successfully!");

        } catch (Exception e) {

            System.out.println("Please ensure that you enter all the information correctly.");

        }

    };

    /**
     * This method allows the user to perform a routine that they have created
     */
    private static void performRoutine() {

        System.out.println("Please enter the name of the routine that you made: ");
        String name = scanner.nextLine();

        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT TimesCompleted FROM Routine WHERE RoutineName = '" + name + "' AND MemberID = '"
                    + getMemberId(userId) + "';";

            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                int timesCompleted = rs.getInt("TimesCompleted");
                timesCompleted++;
                Statement stmt2 = conn.createStatement();
                String sql2 = "UPDATE Routine SET TimesCompleted = '" + timesCompleted + "' WHERE RoutineName = '"
                        + name + "' AND MemberID = '" + getMemberId(userId) + "';";
                stmt2.executeUpdate(sql2);

                if (timesCompleted == 1) {
                    System.out.println("Routine completed successfully for the first time!");
                } else {
                    System.out.println("Routine completed successfully for the " + timesCompleted + " times!");
                }

            } else {
                System.out.println("Routine not found!");
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        ;

    }


    /**
     * This method prints all the routines that the user has created
     */
    private static void viewRoutines() {

        try {
            Statement stmt = conn.createStatement();

            String sql = "SELECT RoutineName, Description, Duration, TimesCompleted FROM Routine WHERE MemberID = '"
                    + getMemberId(userId) + "';";

            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {

                System.out.println("Routine Name: " + rs.getString("RoutineName"));
                System.out.println("Description: " + rs.getString("Description"));
                System.out.println("Duration: " + rs.getString("Duration"));
                System.out.println("Times Completed: " + rs.getInt("TimesCompleted"));

                while (rs.next()) {
                    System.out.println("\n");
                    System.out.println("Routine Name: " + rs.getString("RoutineName"));
                    System.out.println("Description: " + rs.getString("Description"));
                    System.out.println("Duration: " + rs.getString("Duration"));
                    System.out.println("Times Completed: " + rs.getInt("TimesCompleted"));

                }

            } else {
                System.out.println("No routines found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        ;

    }

    /**
     * This method prints the members based off of the name that the user has entered
     */
    private static void viewMembers() {


        System.out.println("Please enter the name of the member you would like to search up : ");
        String name = scanner.nextLine();

        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT PersonID FROM Person WHERE AccountType = 'member' AND Name = '" + name + "';";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                System.out.println(
                        "Member's ID: " + getMemberId(rs.getInt("PersonID")) + " | Name: " + name);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        ;

    }

    /**
     * This method allows the user to log into their account
     */
    private static void login() {

        try {

            Statement stmt = conn.createStatement();

            System.out.println("Please enter your username: ");
            String un = scanner.nextLine();

            System.out.println("Please enter your password: ");
            String pw = scanner.nextLine();

            String sql = "SELECT * FROM Person WHERE Username = '" + un + "' AND Password = '" + pw + "';";

            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                loggedIn = true;
                userId = rs.getInt("PersonID");
                System.out.println("Login successful!");
            } else {
                System.out.println("Login failed!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    };

    /**
     * This method allows the user to create a trainer account
     */
    private static void registerTrainer() {

        try {

            System.out.println("Please enter your username: ");
            String un = scanner.nextLine();

            System.out.println("Please enter your password: ");
            String pw = scanner.nextLine();

            System.out.println("Please enter your first name: ");
            String fn = scanner.nextLine();

            System.out.println("Please enter your height (In meters): ");
            String ht = scanner.nextLine();

            System.out.println("Please enter your weight (In kilograms): ");
            String wt = scanner.nextLine();

            System.out.println("Please enter your age: ");
            String age = scanner.nextLine();

            System.out.println("What do you specialize in: ");
            String spec = scanner.nextLine();

            Integer[][] availability = new Integer[5][8];
            for (int i = 0; i < availability.length; i++) {
                for (int j = 0; j < availability[i].length; j++) {
                    availability[i][j] = 0;
                }
            }

            Array schedule = conn.createArrayOf("integer", availability);

            Statement stmt = conn.createStatement();

            String sql = "INSERT INTO Person (Username, Password, AccountType,  Name, Height, Weight, Age, Schedule) VALUES ('"
                    + un + "', '" + pw + "', 'trainer', '" + fn + "', '" + ht + "', '" + wt + "', '" + age + "', '"
                    + schedule + "') RETURNING  PersonID;";
            int personID = 0;
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                personID = rs.getInt(1);
            }

            Statement stmt2 = conn.createStatement();

            String sql2 = "INSERT INTO Trainer (PersonID, Specialization) VALUES ('" + personID + "', '" + spec + "');";

            stmt2.executeUpdate(sql2);

            System.out.println("added new trainer");

        } catch (SQLException e) {
            System.out.println("Username may already be taken. Please try again.");
        }
    };

    /**
     * This method allows the user to create a user account
     * @param type the type of account that the user is creating. Either member or admin
     */
    private static void registerUser(String type) {

        try {

            // Using the global connection object to create a statement and execute a query
            // to update a student's email
            Statement stmt = conn.createStatement();

            System.out.println("Please enter your username: ");
            String un = scanner.nextLine();

            System.out.println("Please enter your password: ");
            String pw = scanner.nextLine();

            System.out.println("Please enter your first name: ");
            String fn = scanner.nextLine();

            System.out.println("Please enter your height (In meters): ");
            String ht = scanner.nextLine();

            System.out.println("Please enter your weight (In kilograms): ");
            String wt = scanner.nextLine();

            System.out.println("Please enter your age: ");
            String age = scanner.nextLine();

            Integer[][] availability = new Integer[5][8];
            for (int i = 0; i < availability.length; i++) {
                for (int j = 0; j < availability[i].length; j++) {
                    availability[i][j] = 0;
                }
            }

            Array schedule = conn.createArrayOf("integer", availability);

            String sql = "INSERT INTO Person (Username, Password, AccountType,  Name, Height, Weight, Age, Schedule) VALUES ('"
                    + un + "', '" + pw + "', '" + type + "', '" + fn + "', '" + ht + "', '" + wt + "', '" + age + "', '"
                    + schedule + "') RETURNING  PersonID;";

            int personID = 0;
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                personID = rs.getInt(1);
            }

            if (type.equals("admin")) {

                return;

            }

            Statement stmt2 = conn.createStatement();

            System.out.println("Please enter your fitness goals: ");
            String fg = scanner.nextLine();

            String sql2 = "INSERT INTO Member (PersonID, FitnessGoals) VALUES ('" + personID + "', '" + fg + "');";

            stmt2.executeUpdate(sql2);

            Statement stmt3 = conn.createStatement();


            String sql3 = "INSERT INTO Billing (MemberID) VALUES ('" + getMemberId(personID) + "');";

            stmt3.executeUpdate(sql3);


        } catch (SQLException e) {

            System.out.println("Username may already be taken. Please try again.");
        }

    };

    /**
     * This method prints out all of the trainers that are in the database
     */
    private static void printTrainers() {

        try {
            Statement stmt = conn.createStatement();
            // String sql = "SELECT t.TrainerID, p.Name FROM Trainer t JOIN Person p ON
            // t.PersonID = p.PersonID";
            String sql = "Select PersonID, Name FROM Person WHERE AccountType = 'trainer'";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                System.out.println("\nTrainer's ID: " + rs.getInt("PersonID") + " | Name: " + rs.getString("Name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        ;

    }

    /**
     * This method prints out all of the personal sessions that a user has with a trainer
     */
    private static void printPS() {

        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT PersonalSessionID, Weekday, Time FROM PersonalSession WHERE MemberID = "
                    + getMemberId(userId) + ";";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                System.out.println("PersonalSessionID's ID: " + rs.getInt("PersonalSessionID"));
                System.out.println("Day: " + days[rs.getInt("Weekday")]);
                System.out.println("Time: " + times[rs.getInt("Time")]);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
        ;

    };

    /**
     * This method allows the user to book a trainer for a personal session or a class
     * @param trainerID The PersonID of the trainer
     * @param day  An integer representing the day of the week
     * @param time An integer representing the time of the day
     * @param baseValue If the value in the trainers schedule is this value then it will be changed to newValue
     * @param newValue The value that the baseValue will be changed to
     * @param roomOrMember A boolean that is true if the trainer is being booked for a room and false if it is being booked for a member
     * @param roomID The ID of the room
     * @return A boolean that is true if the trainer was successfully booked and false if they were not
     */
    private static boolean bookTrainer(int trainerID, int day, int time, int baseValue, int newValue, boolean roomOrMember, int roomID) {


        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT AccountType FROM Person WHERE PersonID = " + trainerID + ";";
    
            ResultSet rs = stmt.executeQuery(sql);
    
            if(rs.next()){
                if(rs.getString("AccountType").equals("trainer")){
                } else {
                    System.out.println("The person is not a trainer!");
                    return false;
                }
            }
        } catch (Exception e) {

        }

        Integer[][] trainerSchedule = getSchedule(trainerID);
        Integer[][] scheduleTwo;

        if (roomOrMember) {
            scheduleTwo = getRoomSchedule(roomID);
        } else {
            scheduleTwo = getSchedule(userId);
        }


        if (trainerSchedule[day][time] != baseValue) {
            System.out.println("The trainer is not available at that time! int he method call");
            return false;

        } else {



        }


        if (scheduleTwo[day][time] != baseValue) {

            return false;

        }

        trainerSchedule[day][time] = newValue;
        scheduleTwo[day][time] = newValue;

        try {

            Array trainerAva = conn.createArrayOf("integer", trainerSchedule);
            Array twoAva = conn.createArrayOf("integer", scheduleTwo);

            Statement stmt = conn.createStatement();

            String sql = "UPDATE Person SET Schedule = '" + trainerAva + "' WHERE PersonID = " + trainerID + ";";

            stmt.executeUpdate(sql);

            Statement stmt2 = conn.createStatement();

            if (roomOrMember) {
                String sql2 = "UPDATE Room SET Schedule = '" + twoAva + "' WHERE RoomID = " + roomID + ";";
                stmt2.executeUpdate(sql2);

            } else {

                String sql2 = "UPDATE Person SET Schedule = '" + twoAva + "' WHERE PersonID = " + userId + ";";
                stmt2.executeUpdate(sql2);

            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    };

    /**
     * This method returns the schedule of the room
     * @param roomID The ID of the room
     * @return The schedule of the room
     */
    private static Integer[][] getRoomSchedule(int roomID) {

        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT Schedule FROM Room WHERE RoomID = " + roomID + ";";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return (Integer[][]) rs.getArray("Schedule").getArray();
            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    };

    /**
     * This method returns the schedule of the person
     * @param personID The ID of the person
     * @return The schedule of the person
     */
    private static Integer[][] getSchedule(int personID) {

        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT Schedule FROM Person WHERE PersonID = " + personID + ";";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                return (Integer[][]) rs.getArray("Schedule").getArray();
            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    };

    /**
     * This method prints the schedule of the person
     //* @param personID The ID of the person
     */
    public static void printSchedule(Integer[][] schedule) {

        int columnWidth = 20;

        // Print the header
        System.out.printf("%-" + columnWidth + "s", "Time Range");
        for (String day : days) {
            System.out.printf("%-" + columnWidth + "s", day);
        }
        System.out.println();

        // Iterate over each time slot
        for (int slot = 0; slot < times.length; slot++) {
            // Print the time range
            System.out.printf("%-" + columnWidth + "s", times[slot]);

            // Iterate over each day
            for (int day = 0; day < 5; day++) {
                if (/** schedule[day][slot] != null && **/
                schedule[day][slot] == 1) {
                    System.out.printf("%-" + columnWidth + "s", "Private Session"); // Print "PS" if the slot is booked
                } else if (schedule[day][slot] == 2) {

                    System.out.printf("%-" + columnWidth + "s", "Class");

                } else if (schedule[day][slot] == 3) {

                    System.out.printf("%-" + columnWidth + "s", "Maintenance");

                } else if (schedule[day][slot] == 4) {

                    System.out.printf("%-" + columnWidth + "s", "Not Available");

                } else {
                    System.out.printf("%-" + columnWidth + "s", "-"); // Print "-" if the slot is free
                }
            }
            System.out.println();
        }
    }

    /**
     * this method allows the user to book a personal session with a trainer
     */
    private static void bookPS() {

        try {

            printTrainers();

            System.out.println("Please enter the trainer's ID: ");
            int trainersID = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Please enter the day of the week you would like to book: ");
            System.out.println("1. Monday");
            System.out.println("2. Tuesday");
            System.out.println("3. Wednesday");
            System.out.println("4. Thursday");
            System.out.println("5. Friday");

            int date = 0;
            System.out.println("Enter your choice: ");

            while (date < 1 || date > 5) {
                System.out.println("Please ensure you enter a number between 1 and 5: ");
                date = scanner.nextInt();
                scanner.nextLine();

            }
            date--;

            System.out.println("Please enter the time you would like to book: ");
            System.out.println("1. 9:05 AM - 9:55 AM");
            System.out.println("2. 10:05 AM - 10:55 AM");
            System.out.println("3. 11:05 AM - 11:55 AM");
            System.out.println("4. 12:05 PM - 12:55 PM");
            System.out.println("5. 1:05 PM - 1:55 PM");
            System.out.println("6. 2:05 PM - 2:55 PM");
            System.out.println("7. 3:05 PM - 3:55 PM");
            System.out.println("8. 4:05 PM - 4:55 PM");

            int time = 0;
            System.out.println("Enter your choice: ");
            while (time < 1 || time > 8) {
                System.out.println("Please ensure you enter a number between 1 and 8: ");
                time = scanner.nextInt();
                scanner.nextLine();

            }
            time--;

            if (bookTrainer(trainersID, date, time, 0, 1, false, 0)) {

                Statement stmt = conn.createStatement();

                String sql = "INSERT INTO PersonalSession (MemberID, TrainerID, Weekday, Time) VALUES ('"
                        + getMemberId(userId) + "', '" + getTrainerId(trainersID) + "', '" + date + "', '" + time
                        + "');";

                stmt.executeUpdate(sql);

                System.out.println("Personal training session booked successfully!");

            } else {

                System.out.println("Trainer is not available at that time!");

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {

            Statement stmt = conn.createStatement();
            String sql = "SELECT PaymentDue FROM Billing WHERE MemberID = " + getMemberId(userId) + ";";

            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {

                double paymentDue = rs.getDouble("PaymentDue");

                paymentDue += 10;

                Statement stmt2 = conn.createStatement();
                String sql2 = "UPDATE Billing SET PaymentDue = " + paymentDue + " WHERE MemberID = "
                        + getMemberId(userId) + ";";

                stmt2.executeUpdate(sql2);

            }

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    /**
     * this method allows the user to delete a personal session with a trainer
     */
    private static void cancelPS() {

        printPS();
        System.out.println("NOTE : CANCELING A PERSONAL SESSION WILL NOT REFUND THE AMOUNT PAID FOR THE SESSION");
        System.out.println("Please enter the ID of the personal session you would like to cancel: ");
        int psID = scanner.nextInt();
        scanner.nextLine();

        try {

            Statement stmt = conn.createStatement();
            String sql = "SELECT Weekday, Time, TrainerID  FROM PersonalSession WHERE PersonalSessionID = " + psID
                    + ";";

            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {

                int day = rs.getInt("Weekday");
                int time = rs.getInt("Time");
                int trainerID = rs.getInt("TrainerID");

                // get the trainers personid

                Statement stmt2 = conn.createStatement();

                String sql2 = "SELECT PersonID FROM Trainer WHERE TrainerID = " + trainerID + ";";

                ResultSet rs2 = stmt2.executeQuery(sql2);

                trainerID = 0;

                if (rs2.next()) {

                    trainerID = rs2.getInt("PersonID");

                }

                if (!bookTrainer(trainerID, day, time, 1, 0, false, 0)) {

                    return;

                }

                Statement stmt3 = conn.createStatement();
                String sql3 = "DELETE FROM PersonalSession WHERE PersonalSessionID = " + psID + ";";

                stmt3.executeUpdate(sql3);

                System.out.println("Personal session cancelled successfully!");

            }

        } catch (Exception e) {
            System.out.println("There was an error cancelling the personal session.");
            e.printStackTrace();
        }

    };

    /**
     * This method allows the user to book a class
     */
    private static void bookMaintenance() {

        Integer[][] schedule = getSchedule(userId);

        System.out.println("Please enter the day of the week you would like to book maintenance: ");
        System.out.println("1. Monday");
        System.out.println("2. Tuesday");
        System.out.println("3. Wednesday");
        System.out.println("4. Thursday");
        System.out.println("5. Friday");

        System.out.println("Enter your choice: ");
        int date = scanner.nextInt();
        scanner.nextLine();

        date--;

        System.out.println("Please enter the time you would like to book maintenance: ");
        System.out.println("1. 9:05 AM - 9:55 AM");
        System.out.println("2. 10:05 AM - 10:55 AM");
        System.out.println("3. 11:05 AM - 11:55 AM");
        System.out.println("4. 12:05 PM - 12:55 PM");
        System.out.println("5. 1:05 PM - 1:55 PM");
        System.out.println("6. 2:05 PM - 2:55 PM");
        System.out.println("7. 3:05 PM - 3:55 PM");
        System.out.println("8. 4:05 PM - 4:55 PM");

        System.out.println("Enter your choice: ");
        int time = scanner.nextInt();
        scanner.nextLine();

        time--;

        if (schedule[date][time] != 0) {

            System.out.println("You are not available at that time!");

            return;

        }

        schedule[date][time] = 3;

        try {
            Array scheduleArray = conn.createArrayOf("integer", schedule);
            Statement stmt = conn.createStatement();
            String sql = "UPDATE Person SET Schedule = '" + scheduleArray + "' WHERE PersonID = " + userId + ";";
            stmt.executeUpdate(sql);
            System.out.println("Maintenance booked successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method allows the user to delete maintenance
     */
    private static void deleteMaintenance() {

        Integer[][] schedule = getSchedule(userId);

        System.out.println("Please enter the day of the week you would like to delete maintenance: ");
        System.out.println("1. Monday");
        System.out.println("2. Tuesday");
        System.out.println("3. Wednesday");
        System.out.println("4. Thursday");
        System.out.println("5. Friday");

        System.out.println("Enter your choice: ");
        int date = scanner.nextInt();
        scanner.nextLine();

        date--;

        System.out.println("Please enter the time you would like to delete maintenance: ");
        System.out.println("1. 9:05 AM - 9:55 AM");
        System.out.println("2. 10:05 AM - 10:55 AM");
        System.out.println("3. 11:05 AM - 11:55 AM");
        System.out.println("4. 12:05 PM - 12:55 PM");
        System.out.println("5. 1:05 PM - 1:55 PM");
        System.out.println("6. 2:05 PM - 2:55 PM");
        System.out.println("7. 3:05 PM - 3:55 PM");
        System.out.println("8. 4:05 PM - 4:55 PM");

        System.out.println("Enter your choice: ");
        int time = scanner.nextInt();
        scanner.nextLine();

        time--;

        if (schedule[date][time] != 3) {

            System.out.println("You do not have maintenance booked at that time!");

            return;

        }

        schedule[date][time] = 0;

        try {
            Array scheduleArray = conn.createArrayOf("integer", schedule);
            Statement stmt = conn.createStatement();
            String sql = "UPDATE Person SET Schedule = '" + scheduleArray + "' WHERE PersonID = " + userId + ";";
            stmt.executeUpdate(sql);
            System.out.println("Maintenance deleted successfully!");
        } catch (SQLException e) {
            System.out.println("There was an error deleting maintenance.");
        }

    };

    /**
     * This method allows the admin to bill the members
     */
    private static void billMembers() {

        if (billedMembers) {

            System.out.println("You have already processed payments for this week!");
            return;

        }

        try {
            
            Statement stmt = conn.createStatement();
            String sql = "SELECT MemberID, PaymentDue, OutstandingBalance FROM Billing";

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                double paymentDue = rs.getDouble("PaymentDue");
                double outstandingBalance = rs.getDouble("OutstandingBalance");

                outstandingBalance += paymentDue;
                paymentDue = 0.0;

                Statement stmt2 = conn.createStatement();
                String sql2 = "UPDATE Billing SET PaymentDue = '" + paymentDue + "', OutstandingBalance = '"
                        + outstandingBalance + "' WHERE MemberID = " + rs.getInt("MemberID") + ";";

                stmt2.executeUpdate(sql2);

                System.out.println("Member with ID " + rs.getInt("MemberID") + " has been billed.");

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    };

    /**
     * This method allows the admin to process the payments of the members
     */
    private static void processPayments() {

        try {

            Statement stmt = conn.createStatement();
            String sql = "SELECT MemberID, PendingPayments, OutstandingBalance FROM Billing";

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                double pendingPayments = rs.getDouble("PendingPayments");
                double outstandingBalance = rs.getDouble("OutstandingBalance");

                outstandingBalance -= pendingPayments;
                pendingPayments = 0.0;

                Statement stmt2 = conn.createStatement();
                String sql2 = "UPDATE Billing SET PendingPayments = '" + pendingPayments + "', OutstandingBalance = '"
                        + outstandingBalance + "' WHERE MemberID = " + rs.getInt("MemberID") + ";";

                stmt2.executeUpdate(sql2);

                System.out.println("Member with ID " + rs.getInt("MemberID") + " has paid their dues.");

            }


        } catch (Exception e) {
            System.out.println("There was an error processing payments.");
        }

    };

    /**
     * This method allows the admin to start a new week
     * It resets the booked off time of trainers and members and allows the admin to bill the members again
     */
    private static void newWeek() {

        billedMembers = false;

        try {
            Statement stmt = conn.createStatement();
            String sql = "SELECT PersonID, Schedule FROM Person;";

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                Integer[][] schedule = (Integer[][]) rs.getArray("Schedule").getArray();

                for (int i = 0; i < schedule.length; i++) {
                    for (int j = 0; j < schedule[i].length; j++) {

                        if(schedule[i][j] == 4){
                            schedule[i][j] = 0;
                        }

                    }
                }

                Array scheduleArray = conn.createArrayOf("integer", schedule);

                Statement stmt2 = conn.createStatement();
                String sql2 = "UPDATE Person SET Schedule = '" + scheduleArray + "' WHERE PersonID = "
                        + rs.getInt("PersonID") + ";";
                stmt2.executeUpdate(sql2);

            }

        } catch (Exception e) {
            System.out.println("There was an error updating the schedules for the members");
        }


        try {

            Statement stmt = conn.createStatement();

            String sql = "SELECT MemberID, PaymentDue FROM Billing";

            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                double paymentDue = rs.getDouble("PaymentDue");

                paymentDue += 15;

                Statement stmt2 = conn.createStatement();
                String sql2 = "UPDATE Billing SET PaymentDue = " + paymentDue + " WHERE MemberID = " + rs.getInt("MemberID") + ";";

                stmt2.executeUpdate(sql2);

            }


        } catch (Exception e) {
        }

    };

    /**
     * Prints the views for each type of user
     * @param type The type of user
     */ 
    private static void showMenu(String type) {



        int choice = 99;

        if (type.equals("admin")) {

            while (choice != 10) {

                System.out.println("\n\n1. Create a Class");
                System.out.println("2. Delete a Class");
                System.out.println("3. View Classes");
                System.out.println("4. Book Equipment Maintenance");
                System.out.println("5. Delete Equipment Maintenance");
                System.out.println("6. View your Schedule");
                System.out.println("7. Bill Members");
                System.out.println("8. Process Payments");
                System.out.println("9. New Week");
                System.out.println("10. Exit");

                System.out.println("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        createClass();
                        break;
                    case 2:
                        deleteClass();
                        break;
                    case 3:
                        viewClasses();
                        break;
                    case 4:
                        bookMaintenance();
                        break;
                    case 5:
                        deleteMaintenance();
                        break;
                    case 6:
                        Integer[][] schedule = getSchedule(userId);
                        printSchedule(schedule);
                        break;
                    case 7:
                        billMembers();
                        break;
                    case 8:
                        processPayments();
                        break;
                    case 9:
                        newWeek();
                        break;
                    case 10:
                        break;
                    default:
                        System.out.println("Invalid choice");
                        break;
                }

            }

            choice = 99;

        } else if (type.equals("trainer")) {

            while (choice != 4) {

                System.out.println("\n\n1. Set availability");
                System.out.println("2. View members");
                System.out.println("3. View schedule");
                System.out.println("4. Exit");

                System.out.println("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        setAvailability(getSchedule(userId));
                        break;
                    case 2:
                        viewMembers();
                        break;
                    case 3:
                        Integer[][] schedule = getSchedule(userId);
                        printSchedule(schedule);
                        break;
                    case 4:
                        break;
                    default:
                        System.out.println("Invalid choice");
                        break;
                }
            }

            choice = 99;

        } else if (type.equals("member")) {

            while (choice != 13) {

                System.out.println("\n\n1. View Dashboard");
                System.out.println("2. Update your Information");
                System.out.println("3. Book Personal Training");
                System.out.println("4. Cancel Personal Training");
                System.out.println("5. Book a Class");
                System.out.println("6. Drop out of a Class");
                System.out.println("7. View Schedule");
                System.out.println("8. Create a Routine");
                System.out.println("9. Perform a Routine");
                System.out.println("10. View your Routines");
                System.out.println("11. View your Classes");
                System.out.println("12. Make a Payment");
                System.out.println("13. Exit");

                System.out.println("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        displayMemberDashboard();
                        break;
                    case 2:
                        updateUser();
                        break;
                    case 3:
                        bookPS();
                        break;
                    case 4:
                        cancelPS();
                        break;
                    case 5:
                        joinClass();
                        break;
                    case 6:
                        viewMyClasses();

                        System.out.println("Please enter the ID of the class you would like to leave: ");
                        int classID = scanner.nextInt();
                        scanner.nextLine();
                        leaveClass(getMemberId(userId), classID);
                        break;
                    case 7:
                        Integer[][] schedule = getSchedule(userId);
                        printSchedule(schedule);
                        break;
                    case 8:
                        createRoutine();
                        break;
                    case 9:
                        performRoutine();
                        break;
                    case 10:
                        viewRoutines();
                        break;
                    case 11:
                        viewMyClasses();
                        break;
                    case 12:
                        makePayment();
                        break;
                    case 13:
                        break;
                    default:
                        System.out.println("Invalid choice");
                        break;
                }

            }

            choice = 99;

        }

    }

    /**
     * This method starts off the control flow of the program
     * Also initializes the schedules of the rooms and people if they are null
     */
    private static void controlFlow() {
        try {
            Statement stmt = conn.createStatement();
            //getting all schedules in person regardless of user
            String sql = "SELECT Schedule, PersonID FROM Person;";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String ID = rs.getString("PersonID");
                if(rs.getArray("Schedule") == null){
                    Integer[][] availability = new Integer[5][8];
                    for (int i = 0; i < availability.length; i++) {
                        for (int j = 0; j < availability[i].length; j++) {
                            availability[i][j] = 0;
                        }
                    }
                    Array schedule = conn.createArrayOf("integer", availability);
                    Statement stmt2 = conn.createStatement();
                    String sql2 = "UPDATE Person SET Schedule = '" + schedule + "' WHERE PersonID = '" + ID + "';";
                    stmt2.executeUpdate(sql2);
                }
            }

            sql = "SELECT Schedule, RoomID FROM Room;";

            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String ID = rs.getString("RoomID");
                if(rs.getArray("Schedule") == null){
                    Integer[][] availability = new Integer[5][8];
                    for (int i = 0; i < availability.length; i++) {
                        for (int j = 0; j < availability[i].length; j++) {
                            availability[i][j] = 0;
                        }
                    }
                    Array schedule = conn.createArrayOf("integer", availability);
                    Statement stmt2 = conn.createStatement();
                    String sql2 = "UPDATE Room SET Schedule = '" + schedule + "' WHERE RoomID = '" + ID + "';";
                    stmt2.executeUpdate(sql2);
                }
            }


        } catch (Exception e) {

        }
        int choice = 99;
        System.out.println("\n\nWelcome to the Health and Fitness Center!\n\n");

        while (!loggedIn) {

            System.out.println("You are not currently logged in");
            System.out.println("1. Register");
            System.out.println("2. Login");

            System.out.println("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine();
            while (choice != 1 && choice != 2) {
                System.out.println("Invalid choice");
                System.out.println("Enter your choice: ");
                choice = scanner.nextInt();
                scanner.nextLine();

            }

            if (choice == 1) {

                System.out.println("Which type of account would you like to create?");
                System.out.println(
                        "By creating a Member account, you are agreeing to our terms and conditions and purchasing a membership plan.");
                System.out.println(
                        "The Membership plan is $15/week and includes access to all facilities, classes and personal training sessions.");
                System.out.println("Booking a personal training session is $10/session and you do not get refunded if you cancel.");
                System.out.println("Booking a class is $10/class and you do get refunded if you cancel.");

                choice = 99;

                while (choice != 1 && choice != 2 && choice != 3) {
                    System.out.println("1. Member");
                    System.out.println("2. Trainer");
                    System.out.println("3. Admin");
                    System.out.println("Enter your choice: ");
                    choice = scanner.nextInt();
                    scanner.nextLine();
                }

                switch (choice) {
                    case 1:
                        registerUser("member");
                        break;
                    case 2:
                        registerTrainer();
                        break;
                    case 3:
                        registerUser("admin");
                        break;
                    default:
                        break;
                }

            } else {
                login();
            }

        }

        System.out.println("You are currently logged in as " + getUsername());

        if (getTypes().equals("admin")) {

            showMenu("admin");

        } else if (getTypes().equals("trainer")) {

            showMenu("trainer");

        } else if (getTypes().equals("member")) {


            showMenu("member");

        } else {
            System.out.println("Please make sure to check out our plans!");

        }

    }

}