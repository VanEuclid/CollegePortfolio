package Database5530;


import java.lang.*;
import java.sql.*;
import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class UUber {

    /**
     * Display menu from start
     */
    public static void displayMenu() {
        System.out.println("        Welcome to UUber System     ");
        System.out.println("1. sign into UUber system:");
        System.out.println("2. enter your own query:");
        System.out.println("3. exit:");
        //System.out.println("please enter your choice:");
    }

    /**
     * Get user input helper
     *
     * @return string that user inputs
     * @throws IOException
     */
    private static String userInput() throws IOException {

        String choice;
        String c = "error";

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while ((choice = in.readLine()) == null && choice.length() == 0) ; //Gets user inputs
        try {
            c = choice;
        } catch (Exception e) {
            System.out.println("exception in userInput function");
        }

        if (c.equals("error")) {
            return null;
        } else {
            return c;
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        System.out.println("Starting Connection");
        Connector2 con = null;
        String choice;
        String sql;
        int c;
        try {
            //remember to replace the password
            con = new Connector2();
            System.out.println("Database connection established");

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("NOTE: admin account/password is: admin/1234");

            while (true) {
                displayMenu();

                while ((choice = in.readLine()) == null && choice.length() == 0) ; //Gets user inputs for main menu
                try {
                    c = Integer.parseInt(choice);
                } catch (Exception e) {
                    continue;
                }

                if (c < 1 | c > 3)
                    continue;
                if (c == 1) //Main system
                {
                    while (true) {
                        System.out.println("1. login with existing account");
                        System.out.println("2. create a new account");
                        System.out.println("3. back to main menu");

                        String loginChoice = userInput();
                        int intLoginChoice = Integer.parseInt(loginChoice);

                        boolean AsUDriver = false;
                        boolean AsUUser = false;
                        String userLogin;
                        String userPassword;

                        if (intLoginChoice == 1) {
                            while (true) {
                                System.out.println("Enter 'back' to return to previous menu");
                                System.out.println("Enter your user login");
                                userLogin = userInput().toLowerCase();

                                if (userLogin.equals("back"))
                                    break;

                                System.out.println("Enter password");
                                userPassword = userInput().toLowerCase();

                                if (userPassword.equals("back"))
                                    break;

                                //System.out.println("User input: " + userLogin + " and " + userPassword);

                                String sqlQueryUU = MessageFormat.format("SELECT login, password FROM UU WHERE login=\"{0}\" AND password=\"{1}\"", userLogin, userPassword);
                                //System.out.println(sqlQueryUU);

                                String sqlQueryUD = MessageFormat.format("SELECT login, password FROM UD WHERE login=\"{0}\" AND password=\"{1}\"", userLogin, userPassword);
                                //System.out.println(sqlQueryUD);

                                ResultSet rsUU = con.stmt.executeQuery(sqlQueryUU);

                                String retrievedLoginUU = null;
                                String retrievedPasswordUU = null;
                                String retrievedLoginUD = null;
                                String retrievedPasswordUD = null;

                                while (rsUU.next()) {
                                    retrievedLoginUU = rsUU.getString("login").toLowerCase();
                                    retrievedPasswordUU = rsUU.getString("password").toLowerCase();
                                }

                                ResultSet rsUD = con.stmt.executeQuery(sqlQueryUD);

                                while (rsUD.next()) {
                                    retrievedLoginUD = rsUD.getString("login").toLowerCase();
                                    retrievedPasswordUD = rsUD.getString("password").toLowerCase();
                                }

                                if (retrievedLoginUU == null && retrievedPasswordUU == null && retrievedLoginUD == null && retrievedPasswordUD == null) {
                                    System.out.println("User login and password incorrect, please try again!!");
                                } else {
                                    if (retrievedLoginUD == null && retrievedPasswordUD == null) {
                                        //System.out.println(retrievedLogin + " " + retrievedPassword);

                                        if (retrievedLoginUU.equals(userLogin) && retrievedPasswordUU.equals(userPassword)) {
                                            AsUUser = true;
                                            break;
                                        }
                                    } else {
                                        //System.out.println(retrievedLogin + " " + retrievedPassword);

                                        if (retrievedLoginUD.equals(userLogin) && retrievedPasswordUD.equals(userPassword)) {
                                            AsUDriver = true;
                                            break;
                                        }
                                    }
                                }
                            }

                        } else if (intLoginChoice == 2) {

                            while (true) { //Main loop
                                while (true) { //Login choice loop
                                    boolean availableLogin = true;

                                    System.out.println("please enter your desired username/login");
                                    userLogin = userInput().toLowerCase();

                                    String sqlQuery = "SELECT login FROM UU UNION SELECT login from UD";
                                    //System.out.println(sqlQuery);

                                    ResultSet rs = con.stmt.executeQuery(sqlQuery);

                                    List<String> result = new ArrayList<>();
                                    while (rs.next()) {
                                        result.add(rs.getString("login").toLowerCase());
                                    }

                                    for (int i = 0; i < result.size(); i++) {
                                        if (userLogin.equals(result.get(i))) {
                                            System.out.println("This login/username is taken");
                                            availableLogin = false;
                                        }
                                    }

                                    if (availableLogin) {
                                        break;
                                    }
                                }

                                System.out.println("please enter a desired password");
                                userPassword = userInput().toLowerCase();

                                System.out.println("please enter your name");
                                String userName = userInput();

                                System.out.println("please enter address (Just city/state (in a X/X format))");
                                String userAddress = userInput();

                                System.out.println("please enter your phone number");
                                String userPhone = userInput();

                                System.out.println("please designate if this is a UUser account or UDriver account");
                                System.out.println("1: create UUser account");
                                System.out.println("2: create UDriver account");
                                int accountChoice = Integer.parseInt(userInput());

                                if (accountChoice == 1) {
                                    System.out.println("Create account: " + userLogin + " password: " + userPassword + " as a UUser?");
                                    System.out.println("1: yes");
                                    System.out.println("2: no");

                                    int insideChoice = Integer.parseInt(userInput());
                                    if (insideChoice == 1) {
                                        String sqlQuery = MessageFormat.format("INSERT INTO UU VALUES (\"{0}\", \"{1}\", \"{2}\", \"{3}\", \"{4}\")", userLogin, userPassword, userName, userAddress, userPhone);
                                        //System.out.println(sqlQuery);
                                        con.stmt.execute(sqlQuery);

                                        AsUUser = true;
                                        break;
                                    } else {
                                        continue;
                                    }
                                } else if (accountChoice == 2) {
                                    System.out.println("Create account: " + userLogin + " password: " + userPassword + " as a UDriver?");
                                    System.out.println("1: yes");
                                    System.out.println("2: no");

                                    int insideChoice = Integer.parseInt(userInput());
                                    if (insideChoice == 1) {
                                        String sqlQuery = MessageFormat.format("INSERT INTO UD VALUES (\"{0}\", \"{1}\", \"{2}\", \"{3}\", \"{4}\", NULL)", userLogin, userPassword, userName, userAddress, userPhone);
                                        //System.out.println(sqlQuery);
                                        con.stmt.execute(sqlQuery);

                                        AsUDriver = true;
                                        break;
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }
                            }

                        } else {
                            System.out.println("returning to main menu");
                            break;
                        }

                        int type = 0;
                        if (AsUUser) {
                            type = 1;
                        } else if (AsUDriver) {
                            type = 2;
                        }

                        USystem system = new USystem();
                        int integrity = system.startSystem(userLogin, type, con.stmt); //MAIN SYSTEM

                        if (integrity != 0) {
                            System.out.println("ERROR ON EXIT FROM MAIN SYSTEM"); //POTENTIAL PROBLEMS
                        } else {
                            System.out.println("Exit number: " + integrity);
                        }

                    }
                } else if (c == 2) {
                    System.out.println("please enter your query below:");
                    while ((sql = in.readLine()) == null && sql.length() == 0)
                        System.out.println(sql);
                    ResultSet rs = con.stmt.executeQuery(sql);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int numCols = rsmd.getColumnCount();
                    while (rs.next()) {
                        //System.out.print("cname:");
                        for (int i = 1; i <= numCols; i++)
                            System.out.print(rs.getString(i) + "  ");
                        System.out.println("");
                    }
                    System.out.println(" ");
                    rs.close();
                } else {
                    System.out.println("EoM");
                    con.stmt.close();

                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Either connection error or query execution error!");
        } finally {
            if (con != null) {
                try {
                    con.closeConnection();
                    System.out.println("Database connection terminated");
                } catch (Exception e) { /* ignore close errors */ }
            }
        }
    }
}