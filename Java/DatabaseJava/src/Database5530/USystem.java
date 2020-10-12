package Database5530;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class USystem {

    public USystem() {

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

    private static void displayFeedback(String excludeUser, Statement stmt) throws SQLException {
        String sqlQuery = MessageFormat.format("SELECT * FROM Feedback where UU_login != \"{0}\"", excludeUser);
        ResultSet rs = stmt.executeQuery(sqlQuery);

        int fid;
        int score;
        String text;
        String fbdate;
        int vin;
        String UU_login;
        String finalPrint;

        while (rs.next()) {
            fid = rs.getInt("fid");
            score = rs.getInt("score");
            text = rs.getString("text");
            fbdate = rs.getString("fbdate");
            vin = rs.getInt("UC_vin");
            UU_login = rs.getString("UU_login");

            finalPrint = MessageFormat.format("fid: {0}, score: {1}, feedback date: {2}, vin: {3}, feedback owner: {4}, text: {5}", fid, score, fbdate, vin, UU_login, text);
            System.out.println(finalPrint);
        }
    }

    private static void displayUU(String excludeUser, Statement stmt) throws SQLException {
        System.out.println("Some information on the UUser has been hidden for their protection");
        String sqlQuery = MessageFormat.format("SELECT login, name FROM UU where login != \"{0}\"", excludeUser);
        ResultSet rs = stmt.executeQuery(sqlQuery);

        String login;
        String name;
        while (rs.next()) {
            login = rs.getString("login");
            name = rs.getString("name");

            System.out.println("login: " + login + " user's name: " + name);
        }
    }

    private static void displayUC(Statement stmt) throws SQLException {
        String UCSimpleQuery = "SELECT vin, category, year, owner_login, make, model FROM UC c, IsCtypes i, Ctypes t WHERE c.vin = i.UC_vin AND t.tid = i.Ctypes_tid";
        ResultSet rs = stmt.executeQuery(UCSimpleQuery);

        int vin;
        String category;
        String make;
        String model;
        int year;
        String owner_login;
        String finalPrint;

        while (rs.next()) {
            vin = rs.getInt("vin");
            category = rs.getString("category");
            make = rs.getString("make");
            model = rs.getString("model");
            year = rs.getInt("year");
            owner_login = rs.getString("owner_login");

            finalPrint = MessageFormat.format("vin = {0}, owner = {1}, category = {2}, make = {3}, model = {4}, year = ",
                    vin, owner_login, category, make, model);
            System.out.println(finalPrint + year);
        }
    }

    private static void displayReserveUC(Statement stmt) throws SQLException {
        System.out.println("--UCs for reservation--");

        String sqlQuery = "SELECT vin, category, year, owner_login, make, model FROM UC c, IsCtypes i, Ctypes t WHERE i.Ctypes_tid = t.tid " +
                "AND i.UC_vin = c.vin";
        ResultSet rs = stmt.executeQuery(sqlQuery);

        String owner;
        int vin;
        int pid;
        int fromHour;
        int toHour;
        String category;
        String make;
        String model;
        int year;
        String finalPrint;

        while (rs.next()) {
            vin = rs.getInt("vin");
            owner = rs.getString("owner_login");
            category = rs.getString("category");
            make = rs.getString("make");
            model = rs.getString("model");
            year = rs.getInt("year");
            finalPrint = MessageFormat.format("vin = {0}, owner = {1}, category = {2}, make = {3}, model = {4}, year = ",
                    vin, owner, category, make, model);

            System.out.println(finalPrint + year);
        }

        System.out.println();
    }

    private static void displayUD(Statement stmt) throws SQLException {
        String sqlQuery = "SELECT * FROM UD";
        ResultSet rs = stmt.executeQuery(sqlQuery);

        String login;
        String name;
        String address;
        while (rs.next()) {
            login = rs.getString("login");
            name = rs.getString("name");
            address = rs.getString("address");

            System.out.println("driver login: " + login + "   --driver's name: " + name + "   --driver's works in: " + address);
        }
    }

    private static void displayRideUC(Statement stmt) throws SQLException {

        System.out.println("--Available UCs for rides--");

        String sqlQuery = "Select vin, owner_login, pid, fromHour, toHour, category, make, model, C.year " +
                "FROM UC C, Available A, Period P, Ctypes t, IsCtypes i " +
                "WHERE A.UD_login = C.owner_login AND A.Period_pid = P.pid AND t.tid = i.Ctypes_tid AND i.UC_vin = C.vin";
        ResultSet rs = stmt.executeQuery(sqlQuery);

        String owner;
        int vin;
        int pid;
        int fromHour;
        int toHour;
        String category;
        String make;
        String model;
        int year;
        String finalPrint;

        while (rs.next()) {
            vin = rs.getInt("vin");
            owner = rs.getString("owner_login");
            pid = rs.getInt("pid");
            fromHour = rs.getInt("fromHour");
            toHour = rs.getInt("toHour");
            category = rs.getString("category");
            make = rs.getString("make");
            model = rs.getString("model");
            year = rs.getInt("year");
            finalPrint = MessageFormat.format("vin = {0}, owner = {1}, pid = {2}, Reserve during {3}:00 - {4}:00, category = {5}, make = {6}, model = {7}, year = ",
                    vin, owner, pid, fromHour, toHour, category, make, model);

            System.out.println(finalPrint + year);
        }

        System.out.println();
    }

    private static void displayBrowsingUC(Statement stmt) throws IOException, SQLException {
        System.out.println("Please enter your search terms");

        System.out.println("enter nothing if you don't want to search by city/state");
        System.out.println("Search term by city or state, for example SLC for Salt Lake City or UT for Utah?");
        String cityState;
        cityState = userInput();

        System.out.println("enter a model search term");
        System.out.println("enter nothing if you don't want to search by model");
        String model;
        model = userInput();
        System.out.println(model);

        System.out.println("Category search");
        System.out.println("1: comfort");
        System.out.println("2: luxury");
        System.out.println("3: economy");
        System.out.println("4: don't search by category");
        int categoryC = Integer.parseInt(userInput());
        String categorySearch;
        if (categoryC == 1)
            categorySearch = "comfort";
        else if (categoryC == 2)
            categorySearch = "luxury";
        else if (categoryC == 3)
            categorySearch = "economy";
        else
            categorySearch = "";

        System.out.println("1: sort results by average feedback score?");
        System.out.println("2: sort results by trusted user feedback scores?");
        System.out.println("3: no sort");

        int sortChoice = Integer.parseInt(userInput());

        if (sortChoice == 3) { //GENERAL TERM WITHOUT A SORT
            String sqlQuery = "SELECT * FROM UD d, UC c, IsCtypes i, Ctypes t WHERE d.login = c.owner_login AND c.vin = i.UC_vin AND i.Ctypes_tid = t.tid";
            if (!cityState.equals("")) {
                sqlQuery = MessageFormat.format(sqlQuery + " AND address LIKE \"%{0}%\"", cityState);
            }
            if (!categorySearch.equals("")) {
                sqlQuery = MessageFormat.format(sqlQuery + " AND category = \"{0}\"", categorySearch);
            }
            if (!model.equals("")) {
                sqlQuery = MessageFormat.format(sqlQuery + " AND model = \"{0}\"", model);
            }

            System.out.println(sqlQuery);
            ResultSet rs = stmt.executeQuery(sqlQuery);

            int vin;
            String owner_login;
            String address;
            String category;
            String make;
            String modelPrinter;
            int year;
            String finalPrint;
            while (rs.next()) {
                vin = rs.getInt("vin");
                owner_login = rs.getString("owner_login");
                address = rs.getString("address");
                category = rs.getString("category");
                make = rs.getString("make");
                modelPrinter = rs.getString("model");
                year = rs.getInt("year");

                finalPrint = MessageFormat.format("vin: {0}, owner: {1}, address: {2}, category: {3}, make: {4}, model: {5}, year: " + year, vin, owner_login, address, category, make, modelPrinter);
                System.out.println(finalPrint);
            }
            System.out.println("-----end of results-----");
            System.out.println();
        } else if (sortChoice == 1) {
            String sqlQuery = "SELECT vin, avg(score) AS averageScore FROM UD d, UC c, IsCtypes i, Ctypes t, Feedback f " +
                    "WHERE d.login = c.owner_login AND c.vin = i.UC_vin AND i.Ctypes_tid = t.tid AND f.UC_vin = c.vin";
            if (!cityState.equals("")) {
                sqlQuery = MessageFormat.format(sqlQuery + " AND address LIKE \"%{0}%\"", cityState);
            }
            if (!categorySearch.equals("")) {
                sqlQuery = MessageFormat.format(sqlQuery + " AND category = \"{0}\"", categorySearch);
            }
            if (!model.equals("")) {
                sqlQuery = MessageFormat.format(sqlQuery + " AND model = \"{0}\"", model);
            }

            sqlQuery = sqlQuery + " GROUP BY vin";
            String nestedQuery = "SELECT * FROM (" + sqlQuery + ") AS sorted, UC car, UD driver, Ctypes types, IsCtypes isT " +
                    "WHERE sorted.vin = car.vin AND driver.login = car.owner_login AND isT.UC_vin = car.vin AND isT.Ctypes_tid = types.tid ORDER BY averageScore desc";

            System.out.println(nestedQuery);
            ResultSet rs = stmt.executeQuery(nestedQuery);

            int vin;
            String owner_login;
            String address;
            String category;
            String make;
            String modelPrinter;
            int year;
            double averageS;
            String finalPrint;
            while (rs.next()) {
                vin = rs.getInt("vin");
                owner_login = rs.getString("owner_login");
                address = rs.getString("address");
                category = rs.getString("category");
                make = rs.getString("make");
                modelPrinter = rs.getString("model");
                year = rs.getInt("year");
                averageS = rs.getDouble("averageScore");

                finalPrint = MessageFormat.format("vin: {0}, owner: {1}, average feedback score: {2}, address: {3}, category: {4}, make: {5}, model: {6}, year: " + year, vin, owner_login, averageS, address, category, make, modelPrinter);
                System.out.println(finalPrint);
            }
            System.out.println("-----end of results-----");
            System.out.println();
        } else if (sortChoice == 2) {
            String sqlQuery = "SELECT vin, avg(score) AS averageScore FROM UD d, UC c, IsCtypes i, Ctypes t, Feedback f, Trust tru " +
                    "WHERE tru.login2 = f.UU_login AND tru.isTrusted = 1 AND d.login = c.owner_login AND c.vin = i.UC_vin AND i.Ctypes_tid = t.tid AND f.UC_vin = c.vin";
            if (!cityState.equals("")) {
                sqlQuery = MessageFormat.format(sqlQuery + " AND address LIKE \"%{0}%\"", cityState);
            }
            if (!categorySearch.equals("")) {
                sqlQuery = MessageFormat.format(sqlQuery + " AND category = \"{0}\"", categorySearch);
            }
            if (!model.equals("")) {
                sqlQuery = MessageFormat.format(sqlQuery + " AND model = \"{0}\"", model);
            }

            sqlQuery = sqlQuery + " GROUP BY vin";
            String nestedQuery = "SELECT * FROM (" + sqlQuery + ") AS sorted, UC car, UD driver, Ctypes types, IsCtypes isT " +
                    "WHERE sorted.vin = car.vin AND driver.login = car.owner_login AND isT.UC_vin = car.vin AND isT.Ctypes_tid = types.tid ORDER BY averageScore desc";

            //System.out.println(nestedQuery);
            ResultSet rs = stmt.executeQuery(nestedQuery);

            int vin;
            String owner_login;
            String address;
            String category;
            String make;
            String modelPrinter;
            int year;
            double averageS;
            String finalPrint;
            while (rs.next()) {
                vin = rs.getInt("vin");
                owner_login = rs.getString("owner_login");
                address = rs.getString("address");
                category = rs.getString("category");
                make = rs.getString("make");
                modelPrinter = rs.getString("model");
                year = rs.getInt("year");
                averageS = rs.getDouble("averageScore");

                finalPrint = MessageFormat.format("vin: {0}, owner: {1}, average feedback score: {2}, address: {3}, category: {4}, make: {5}, model: {6}, year: " + year, vin, owner_login, averageS, address, category, make, modelPrinter);
                System.out.println(finalPrint);
            }
            System.out.println("-----end of results-----");
            System.out.println();
        }
    }

    private static void displayReservationPeriods(Statement stmt) throws SQLException {
        String sqlQuery = "Select * From Period";
        ResultSet rs = stmt.executeQuery(sqlQuery);

        int pid;
        int fromHour;
        int toHour;

        while (rs.next()) {
            pid = rs.getInt("pid");
            fromHour = rs.getInt("fromHour");
            toHour = rs.getInt("toHour");

            System.out.println("pid: " + pid + " from hour: " + fromHour + " to hour: " + toHour);
        }
    }

    private static void systemMainMenu() {
        if (systemType == 1) {
            System.out.println("1: Reserve a UUber Car (UC)");
            System.out.println("2: Record a ride with a UUber Car (UC)");
            System.out.println("3: Declare a favorite UUber Car and the driver (UC) :D");
            System.out.println("4: Give feedback to a UUber Car (UC)");
            System.out.println("5: Look at feedback and rate them");
            System.out.println("6: Set up trust on other users");
            System.out.println("7: UC browsing");
            System.out.println("8: Get the the most useful feedbacks for a UD");
            System.out.println("9: Two degrees of separation");
            System.out.println("10: Statistics");
            if (userID.equals("admin")) {
                System.out.println("11: give admin rewards");
            }
        }
        if (systemType == 2) {
            System.out.println("1: Update a UUBer car (UC)");
            System.out.println("7: UC browsing");
            System.out.println("8: Get the the most useful feedbacks for a UD");
            System.out.println("9: Two degrees of separation");
            System.out.println("10: Statistics");
        }
        System.out.println("0: exit");
    }

    private static int systemType;
    private static String userID;

    public int startSystem(String user, int type, Statement stmt) {
        systemType = type;
        userID = user;
        try {
            while (true) { //Main system loop
                System.out.println();
                System.out.println("Welcome to UUber " + user);

                if (type == 1) {
                    System.out.println("You are logged in as a UUser");
                } else if (type == 2) {
                    System.out.println("You are logged in as a UDriver");
                } else {
                    System.out.println("ERROR ON USystem start, unrecognized type");
                }

                systemMainMenu();
                int mainSystemChoice = Integer.parseInt(userInput());

                if (mainSystemChoice == 1 && systemType == 1) { //RESERVATION
                    int vin;
                    int pid;

                    while (true) { //LOOP FOR RESERVATIONS
                        displayReserveUC(stmt);

                        System.out.println("please enter a vin to reserve");
                        vin = Integer.parseInt(userInput());

                        displayReservationPeriods(stmt);

                        System.out.println("please enter a pid to reserve");
                        pid = Integer.parseInt(userInput());

                        String sqlQuery = MessageFormat.format("SELECT vin, category, year, owner_login, pid, fromHour, toHour, make, model FROM UC c, Period p, Ctypes t, IsCtypes i " +
                                "WHERE t.tid = i.Ctypes_tid AND i.UC_vin = c.vin AND " +
                                "c.vin = {0} AND p.pid = {1}", vin, pid);
                        ResultSet reserve = stmt.executeQuery(sqlQuery);

                        int checkVin = -1;
                        int checkPid = -1;
                        int fromHour = -1;
                        int toHour = -1;
                        String owner = "error";
                        String make = "error";
                        String model = "error";
                        int year = -1;

                        if (reserve.next()) {
                            checkVin = reserve.getInt("vin");
                            checkPid = reserve.getInt("pid");
                            fromHour = reserve.getInt("fromHour");
                            toHour = reserve.getInt("toHour");
                            owner = reserve.getString("owner_login");
                            make = reserve.getString("make");
                            model = reserve.getString("model");
                            year = reserve.getInt("year");
                        }

                        if (checkVin == -1 && checkPid == -1 && owner.equals("error")) {
                            System.out.println("Input vin and pid does not exit");
                        } else {
                            int cost = toHour - fromHour;
                            cost = cost * 5;
                            System.out.println("Reserve driver: " + owner + " vin: " + checkVin + " " + make + " " + model +
                                    " " + year + " from " + fromHour + ":00 to " + toHour + ":00 for " + cost + " dollars?");
                            System.out.println("1: yes");
                            System.out.println("2: no");

                            int finalRC = Integer.parseInt(userInput());

                            if (finalRC == 1) {
                                LocalDateTime date = LocalDateTime.now();
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:ss");
                                String insertDate = date.format(formatter);

                                String reserveQuery = MessageFormat.format("INSERT INTO Reserve Values (\"{0}\", {1}, {2}, {3}, \"{4}\")", user, vin, pid, cost, insertDate);
                                boolean goodQuery = true;
                                try {
                                    stmt.execute(reserveQuery);
                                } catch (Exception e) {
                                    System.out.println("You have already reserved this vin and pid!!");
                                    goodQuery = false;
                                }

                                if (goodQuery)
                                    System.out.println("Reservation set on " + insertDate + " for UC vin: " + vin);


                                String suggestionQuery = MessageFormat.format("SELECT * FROM(SELECT counter.UC_vin, counter.RideC FROM (SELECT DISTINCT UU_login FROM Ride R1 WHERE R1.UC_vin = {0}) AS RideA, Ride r, " +
                                        "(SELECT UC_vin, count(UC_vin) as RideC FROM Ride r group by UC_vin order by RideC desc) as counter " +
                                        "WHERE r.UC_vin != {0} AND RideA.UU_login = r.UU_login AND r.UC_vin = counter.UC_vin group by counter.UC_vin order by counter.RideC desc) AS info, " +
                                        "UC c, Ctypes t, IsCtypes i WHERE i.Ctypes_tid = t.tid AND c.vin = i.UC_vin AND info.UC_vin = c.vin order by RideC desc", vin);


                                System.out.println(suggestionQuery);
                                ResultSet suggestionSet = stmt.executeQuery(suggestionQuery);

                                int vinSug;
                                int numberOfRides;
                                String owner_loginSug;
                                String categorySug;
                                String makeSug;
                                String modelSug;
                                int yearSug;
                                int periodSug;
                                int fromHourSug;
                                int toHourSUg;

                                System.out.println("People who rode with vin " + vin + " also rode with: ");
                                while (suggestionSet.next()) {
                                    vinSug = suggestionSet.getInt("vin");
                                    numberOfRides = suggestionSet.getInt("RideC");
                                    periodSug = suggestionSet.getInt("pid");
                                    fromHourSug = suggestionSet.getInt("fromHour");
                                    toHourSUg = suggestionSet.getInt("toHour");
                                    owner_loginSug = suggestionSet.getString("owner_login");
                                    categorySug = suggestionSet.getString("category");
                                    makeSug = suggestionSet.getString("make");
                                    modelSug = suggestionSet.getString("model");
                                    yearSug = suggestionSet.getInt("year");

                                    System.out.println(MessageFormat.format("vin: {0}, UC owner: {1}, pid: {2}, {3}:00 - {4}::00, category: {5}, make: {6}, model: {7}, year: {8}, popularity/number of rides: {9}", vinSug, owner_loginSug, periodSug, fromHourSug, toHourSUg, categorySug, makeSug, modelSug, yearSug, numberOfRides));
                                }

                                System.out.println("Would you like to reserve one of these UC's?");
                                System.out.println("1: yes");
                                System.out.println("2: no");
                                int suggestionC = Integer.parseInt(userInput());

                                if (suggestionC == 1) {
                                    System.out.println("Which vin would you like to reserve?");
                                    int vinSuggestion = Integer.parseInt(userInput());

                                    displayReservationPeriods(stmt);

                                    System.out.println("Which pid would you like to reserve?");
                                    int pidSuggestion = Integer.parseInt(userInput());

                                    LocalDateTime dateSug = LocalDateTime.now();
                                    DateTimeFormatter formatterSug = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:ss");
                                    String insertDateSug = dateSug.format(formatterSug);

                                    String reserveQuerySug = MessageFormat.format("INSERT INTO Reserve Values (\"{0}\", {1}, {2}, {3}, \"{4}\")", user, vinSuggestion, pidSuggestion, cost, insertDateSug);
                                    boolean goodQuerySug = true;
                                    try {
                                        stmt.execute(reserveQuerySug);
                                    } catch (Exception e) {
                                        System.out.println("You have already reserved this vin and pid!!");
                                        goodQuerySug = false;
                                    }

                                    if (goodQuerySug)
                                        System.out.println("Reservation set on " + insertDate + " for UC vin: " + vinSuggestion);
                                }

                                System.out.println("exit reservation menu?");
                                System.out.println("1: yes");
                                System.out.println("2: no");

                                int exitC = Integer.parseInt(userInput());
                                if (exitC == 1) {
                                    break;
                                } else
                                    continue;
                            } else {
                                System.out.println("exit reservation menu?");
                                System.out.println("1: yes");
                                System.out.println("2: no");

                                int exitC = Integer.parseInt(userInput());

                                if (exitC == 1) {
                                    break;
                                } else
                                    continue;
                            }
                        }
                    }
                } else if (mainSystemChoice == 2 && systemType == 1) { //RIDE
                    int vin;
                    int pid;

                    while (true) { //LOOP FOR RIDE
                        displayRideUC(stmt);

                        System.out.println("please enter a vin to reserve");
                        vin = Integer.parseInt(userInput());

                        System.out.println("please enter a pid to reserve");
                        pid = Integer.parseInt(userInput());

                        String sqlQuery = MessageFormat.format("SELECT vin, category, year, owner_login, Period_pid, fromHour, toHour, make, model FROM" +
                                " Available A, UC C, Period P, IsCtypes i, Ctypes t" +
                                " WHERE A.UD_login = C.owner_login AND A.Period_pid = P.pid AND t.tid = i.Ctypes_tid AND i.UC_vin = C.vin" +
                                " AND C.vin = {0} AND A.Period_pid = {1}", vin, pid);
                        ResultSet reserve = stmt.executeQuery(sqlQuery);

                        int checkVin = -1;
                        int checkPid = -1;
                        int fromHour = -1;
                        int toHour = -1;
                        String owner = "error";
                        String make = "error";
                        String model = "error";
                        int year = -1;

                        if (reserve.next()) {
                            checkVin = reserve.getInt("vin");
                            checkPid = reserve.getInt("Period_pid");
                            fromHour = reserve.getInt("fromHour");
                            toHour = reserve.getInt("toHour");
                            owner = reserve.getString("owner_login");
                            make = reserve.getString("make");
                            model = reserve.getString("model");
                            year = reserve.getInt("year");
                        }

                        if (checkVin == -1 && checkPid == -1 && owner.equals("error")) {
                            System.out.println("Input vin and pid does not exist");
                        } else {
                            int cost = toHour - fromHour;
                            cost = cost * 5;
                            System.out.println("Ride with driver: " + owner + " vin: " + checkVin + " " + make + " " + model +
                                    " " + year + " from " + fromHour + ":00 to " + toHour + ":00 for " + cost + " dollars?");
                            System.out.println("1: yes");
                            System.out.println("2: no");

                            int finalRC = Integer.parseInt(userInput());

                            if (finalRC == 1) {
                                LocalDateTime date = LocalDateTime.now();
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:ss");
                                String insertDate = date.format(formatter);

                                String reserveQuery = MessageFormat.format("INSERT INTO Ride (UU_login, date, fromHour, toHour, cost, UC_vin) " +
                                        "VALUES (\"{0}\", \"{1}\", {2}, {3}, {4}, {5})", user, insertDate, fromHour, toHour, cost, vin);
                                //System.out.println(reserveQuery);

                                boolean goodQuery = true;
                                try {
                                    stmt.execute(reserveQuery);
                                } catch (Exception e) {
                                    System.out.println("error processing ride request");
                                    goodQuery = false;
                                }

                                if (goodQuery)
                                    System.out.println("Ride coming on " + insertDate + ", go to meeting point!!");

                                System.out.println("exit ride menu?");
                                System.out.println("1: yes");
                                System.out.println("2: no");

                                int exitC = Integer.parseInt(userInput());
                                if (exitC == 1) {
                                    break;
                                } else
                                    continue;
                            } else {
                                System.out.println("exit ride menu?");
                                System.out.println("1: yes");
                                System.out.println("2: no");

                                int exitC = Integer.parseInt(userInput());
                                if (exitC == 1) {
                                    break;
                                } else
                                    continue;
                            }
                        }
                    }
                } else if (mainSystemChoice == 3 && systemType == 1) { //DECLARE FAVORITE
                    while (true) {
                        displayUC(stmt);

                        System.out.println("please enter a vin to favorite :D");
                        int favC = Integer.parseInt(userInput());

                        LocalDateTime date = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:ss");
                        String insertDate = date.format(formatter);

                        String sqlQuery = MessageFormat.format("INSERT INTO Favorites VALUES ({0}, \"{1}\", \"{2}\")", favC, user, insertDate);
                        boolean goodQuery = true;
                        try {
                            stmt.execute(sqlQuery);
                        } catch (Exception e) {
                            System.out.println("You have already faved this vin");
                            goodQuery = false;
                        }

                        if (goodQuery)
                            System.out.println("Favorite set for vin: " + favC);

                        System.out.println("Favorite another vin?");
                        System.out.println("1: yes");
                        System.out.println("2: no");

                        int exitChoice = Integer.parseInt(userInput());
                        if (exitChoice == 1) {
                            continue;
                        } else {
                            break;
                        }
                    }
                } else if (mainSystemChoice == 4 && systemType == 1) { //FEEDBACK
                    while (true) {
                        displayUC(stmt);

                        System.out.println("which vin are you providing feedback for?");
                        System.out.println("enter -1 if you want to exit feedback menu");

                        int vin = Integer.parseInt(userInput());
                        if (vin == -1) {
                            break;
                        }

                        String checkIfFeedExistQuery = MessageFormat.format("SELECT fid FROM Feedback WHERE UC_vin = {0} AND UU_login = \"{1}\"", vin, user);
                        ResultSet rs = stmt.executeQuery(checkIfFeedExistQuery);

                        int check = -1;
                        while (rs.next()) {
                            check = rs.getInt("fid");
                        }

                        if (check != -1) {
                            System.out.println("It appears like you have already provided feedback for this vin");
                        } else {
                            System.out.println("please provide a score of 1-10 for vin: " + vin);
                            int score = Integer.parseInt(userInput());

                            if (score > 10)
                                score = 10;
                            if (score < 1)
                                score = 1;

                            System.out.println("would you like to provide a small text with this feedback?");
                            System.out.println("1: yes");
                            System.out.println("2: no");

                            int textC = Integer.parseInt(userInput());
                            String text = "";
                            if (textC == 1) {
                                System.out.println("Please enter the text you would like to provide, press enter when finished");
                                text = userInput();
                            }

                            LocalDateTime date = LocalDateTime.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:ss");
                            String insertDate = date.format(formatter);

                            String sqlQuery = MessageFormat.format("INSERT INTO Feedback (score, text, fbdate, UC_vin, UU_login) " +
                                    "VALUES ( {0}, \"{1}\", \"{2}\", {3}, \"{4}\")", score, text, insertDate, vin, user);

                            boolean goodQuery = true;
                            try {
                                stmt.execute(sqlQuery);
                            } catch (Exception e) {
                                System.out.println("vin you provided doesn't exist");
                                goodQuery = false;
                            }

                            if (goodQuery)
                                System.out.println("Feedback recorded for vin: " + vin);

                            System.out.println("record another feedback?");
                            System.out.println("1: yes");
                            System.out.println("2: no");

                            int exitC = Integer.parseInt(userInput());
                            if (exitC == 1) {
                                continue;
                            } else {
                                break;
                            }
                        }
                    }
                } else if (mainSystemChoice == 5 && systemType == 1) { //RATE FEEDBACK
                    while (true) {
                        displayFeedback(user, stmt);

                        System.out.println("which fid would you like to rate?");
                        int fid = Integer.parseInt(userInput());

                        System.out.println("was the feedback: ");
                        System.out.println("1: useless");
                        System.out.println("2: useful");
                        System.out.println("3: very useful");
                        int useful = Integer.parseInt(userInput());

                        String sqlQuery = MessageFormat.format("INSERT INTO Rates VALUES (\"{0}\", {1}, {2})", user, fid, useful);
                        boolean goodQuery = true;
                        try {
                            stmt.execute(sqlQuery);
                        } catch (Exception e) {
                            System.out.println("Appears like you've already rated this fid");
                            goodQuery = false;
                        }

                        if (goodQuery)
                            System.out.println("Thank you for your input");

                        System.out.println("Rate another feedback?");
                        System.out.println("1: yes");
                        System.out.println("2: no");
                        int exitC = Integer.parseInt(userInput());

                        if (exitC == 1) {
                            continue;
                        } else {
                            break;
                        }
                    }
                } else if (mainSystemChoice == 6 && systemType == 1) { //TRUST
                    while (true) {
                        displayUU(user, stmt);

                        System.out.println("which login/user do you want to trust/don't trust?");
                        String trustUser = userInput();

                        System.out.println("do you trust this user?");
                        System.out.println("1: yes");
                        System.out.println("2: no");
                        int answer = Integer.parseInt(userInput());

                        boolean trust;
                        if (answer == 1) {
                            trust = true;
                        } else {
                            trust = false;
                        }

                        String sqlQuery = MessageFormat.format("INSERT INTO Trust VALUES (\"{0}\", \"{1}\", {2})", user, trustUser, trust);
                        boolean goodQuery = true;
                        try {
                            stmt.execute(sqlQuery);
                        } catch (Exception e) {
                            System.out.println("Appears like you've already set a trust on this user");
                            goodQuery = false;
                        }

                        if (goodQuery)
                            System.out.println("Thank you for your trust");

                        System.out.println("Trust another user?");
                        System.out.println("1: yes");
                        System.out.println("2: no");
                        int exitC = Integer.parseInt(userInput());

                        if (exitC == 1) {
                            continue;
                        } else {
                            break;
                        }
                    }
                } else if (mainSystemChoice == 7) { //UC BROWSING
                    while (true) {
                        displayBrowsingUC(stmt);

                        System.out.println("keep browsing?");
                        System.out.println("1: yes");
                        System.out.println("2: no");
                        int exitC = Integer.parseInt(userInput());

                        if (exitC == 1) {
                            continue;
                        } else {
                            break;
                        }
                    }
                } else if (mainSystemChoice == 8) { //GET USEFUL FEEDBACKS
                    while (true) {
                        displayUD(stmt);
                        System.out.println("Which UD login do you want the most useful feedbacks for?");
                        String udLogin = userInput();

                        System.out.println("How many results do you want? enter a number");
                        int resultCount = Integer.parseInt(userInput());

                        //SELECT f.fid, avg(rating) as avgR, f.text FROM Rates r, Feedback f, UC c
                        //WHERE r.Feedback_fid = f.fid AND c.vin = f.UC_vin AND owner_login = "hi"
                        //group by fid order by avgR desc LIMIT 2

                        String sqlQuery = MessageFormat.format("SELECT f.fid, avg(rating) as avgR, f.text FROM Rates r, Feedback f, UC c" +
                                " WHERE r.Feedback_fid = f.fid AND c.vin = f.UC_vin AND owner_login = \"{0}\" group by fid" +
                                " ORDER BY avgR desc LIMIT {1}", udLogin, resultCount);
                        ResultSet rs = stmt.executeQuery(sqlQuery);

                        int fid;
                        int average;
                        String text;
                        while (rs.next()) {
                            fid = rs.getInt("fid");
                            average = rs.getInt("avgR");
                            text = rs.getString("text");

                            System.out.println("feedback fid: " + fid + " average score for feedback: " + average + " text: " + text);
                            System.out.println();
                        }

                        System.out.println("keep browsing useful feedbacks?");
                        System.out.println("1: yes");
                        System.out.println("2: no");
                        int exitC = Integer.parseInt(userInput());

                        if (exitC == 1) {
                            continue;
                        } else {
                            break;
                        }
                    }
                } else if (mainSystemChoice == 9) {
                    while (true) {

                        String quickUU = "Select * FROM UU";
                        ResultSet rsUU = stmt.executeQuery(quickUU);

                        String UU;
                        String name;
                        String phone;
                        while (rsUU.next()) {
                            UU = rsUU.getString("login");
                            name = rsUU.getString("name");
                            phone = rsUU.getString("phone");

                            System.out.println("login: " + UU + " name: " + name + " phone: " + phone);
                        }

                        System.out.println("First login?");
                        String login1 = userInput();

                        System.out.println("Second login?");
                        String login2 = userInput();

                        String oneDegree = MessageFormat.format("Select f1.UU_login, f2.UU_login From Favorites f1, Favorites f2 WHERE f1.UU_login = \"{0}\" AND f2.UU_login = \"{1}\" AND f1.UC_vin = f2.UC_vin", login1, login2);

                        String twoDegree = MessageFormat.format(" SELECT f3.UU_login FROM (SELECT f2.UU_login FROM Favorites f1, Favorites f2 WHERE f1.UU_login = \"{0}\" AND f1.UU_login != f2.UU_login AND f1.UC_vin = f2.UC_vin) AS degree, Favorites f3 " +
                                "WHERE f3.UU_login != degree.UU_login AND f3.UU_login != \"{0}\"", login1);

                        String check1 = "";
                        String check2 = "";
                        ResultSet rs = stmt.executeQuery(oneDegree);
                        while (rs.next()) {
                            check1 = rs.getString("f1.UU_login");
                            check2 = rs.getString("f2.UU_login");
                        }

                        boolean oneDegreeConfirm = false;
                        if (login1.equals(check1) && login2.equals(check2)) {
                            oneDegreeConfirm = true;
                        }

                        String check3 = "";
                        ResultSet rs2 = stmt.executeQuery(twoDegree);
                        while (rs2.next()) {
                            check3 = rs2.getString("UU_login");
                        }

                        boolean twoDegreeConfirm = false;
                        if (check3.equals(login2)) {
                            twoDegreeConfirm = true;
                        }

                        if (!oneDegreeConfirm && twoDegreeConfirm) {
                            System.out.println("The two logins provided are two degrees apart");
                        } else if (oneDegreeConfirm && !twoDegreeConfirm) {
                            System.out.println("The two logins provided are one degree apart");
                        } else {
                            System.out.println("Nothing in common");
                        }

                        System.out.println();

                        System.out.println("keep browsing degree?");
                        System.out.println("1: yes");
                        System.out.println("2: no");
                        int exitC = Integer.parseInt(userInput());

                        if (exitC == 1) {
                            continue;
                        } else {
                            break;
                        }
                    }
                } else if (mainSystemChoice == 10) {
                    System.out.println("How many items do you want list in the stats query?");
                    int m = Integer.parseInt(userInput());

                    System.out.println("1: most popular UCs (in terms of total rides) for each category");
                    System.out.println("2: most expensive UC's (defined by the average cost of all rides on a UC) for each category)");
                    System.out.println("3: highly rated UDs (defined by the average scores from all feedbacks a UD has received for all of his/her UCs) for each category");
                    int statChoice = Integer.parseInt(userInput());
                    if (statChoice == 1) {
                        String sqlQuery = MessageFormat.format("(Select UC_vin, count(UC_vin) as counter, category FROM Ride r, UC c WHERE c.vin = r.UC_vin AND category = \"economy\" " +
                                "group by UC_vin, category order by counter desc LIMIT {0}) UNION ", m);
                        String sqlQuery2 = MessageFormat.format("(Select UC_vin, count(UC_vin) as counter, category FROM Ride r, UC c WHERE c.vin = r.UC_vin AND category = \"comfort\" " +
                                "group by UC_vin, category order by counter desc LIMIT {0}) UNION ", m);
                        String sqlQuery3 = MessageFormat.format("(Select UC_vin, count(UC_vin) as counter, category FROM Ride r, UC c WHERE c.vin = r.UC_vin AND category = \"luxury\" " +
                                "group by UC_vin, category order by counter desc LIMIT {0})", m);
                        String finalQuery = sqlQuery + sqlQuery2 + sqlQuery3;

                        //System.out.println(finalQuery);
                        ResultSet rs = stmt.executeQuery(finalQuery);
                        int vin;
                        int count;
                        String category;
                        while (rs.next()) {
                            vin = rs.getInt("UC_vin");
                            count = rs.getInt("counter");
                            category = rs.getString("category");

                            System.out.println("UC vin: " + vin + " ride count: " + count + " category: " + category);
                        }
                    } else if (statChoice == 2) {
                        String sqlQuery = MessageFormat.format("(SELECT vin, category, avg(cost) as avger From Ride r, UC c " +
                                "WHERE r.UC_vin = c.vin AND category = \"economy\" group by vin, category order by avger desc LIMIT {0}) UNION", m);
                        String sqlQuery2 = MessageFormat.format("(SELECT vin, category, avg(cost) as avger From Ride r, UC c " +
                                "WHERE r.UC_vin = c.vin AND category = \"luxury\" group by vin, category order by avger desc LIMIT {0}) UNION", m);
                        String sqlQuery3 = MessageFormat.format("(SELECT vin, category, avg(cost) as avger From Ride r, UC c " +
                                "WHERE r.UC_vin = c.vin AND category = \"comfort\" group by vin, category order by avger desc LIMIT {0})", m);
                        String finalQuery = sqlQuery + sqlQuery2 + sqlQuery3;

                        //System.out.println(finalQuery);
                        ResultSet rs = stmt.executeQuery(finalQuery);
                        int vin;
                        double count;
                        String category;
                        while (rs.next()) {
                            vin = rs.getInt("vin");
                            count = rs.getDouble("avger");
                            category = rs.getString("category");

                            //System.out.println("vin: " + vin + " average cost: " + count + " category: " + category);
                            System.out.println(MessageFormat.format("UC vin: {1}, average score: {2}, category: {3}", vin, count, category));
                        }
                    } else if (statChoice == 3) {
                        String sqlQuery = MessageFormat.format("(SELECT avger, vin, category, owner_login FROM (SELECT avg(score) as avger, UC_vin FROM Feedback group by UC_vin order by avger desc) AS fd, UC c WHERE c.vin = fd.UC_vin AND category = \"luxury\" LIMIT {0}) UNION", m);
                        String sqlQuery2 = MessageFormat.format("(SELECT avger, vin, category, owner_login FROM (SELECT avg(score) as avger, UC_vin FROM Feedback group by UC_vin order by avger desc) AS fd, UC c WHERE c.vin = fd.UC_vin AND category = \"economy\" LIMIT {0}) UNION", m);
                        String sqlQuery3 = MessageFormat.format("(SELECT avger, vin, category, owner_login FROM (SELECT avg(score) as avger, UC_vin FROM Feedback group by UC_vin order by avger desc) AS fd, UC c WHERE c.vin = fd.UC_vin AND category = \"comfort\" LIMIT {0})", m);
                        String finalQuery = sqlQuery + sqlQuery2 + sqlQuery3;

                        //System.out.println(finalQuery);
                        ResultSet rs = stmt.executeQuery(finalQuery);
                        int vin;
                        double count;
                        String category;
                        String owner;
                        while (rs.next()) {
                            vin = rs.getInt("vin");
                            count = rs.getDouble("avger");
                            category = rs.getString("category");
                            owner = rs.getString("owner_login");

                            //System.out.println("owner_login: " + owner + " vin: " + vin + " average score: " + count + " category: " + category);
                            System.out.println(MessageFormat.format("owner: {0}, UC vin: {1}, average score: {2}, category: {3}", owner, vin, count, category));
                        }
                    }
                } else if (mainSystemChoice == 11 && userID.equals("admin") && systemType == 1) {
                    System.out.println("1: Reward trusted users");
                    System.out.println("2: Reward useful users");
                    int adminC = Integer.parseInt(userInput());

                    System.out.println("How many do users do you want listed?");
                    int m = Integer.parseInt(userInput());

                    if (adminC == 1) {
                        System.out.println("Negative scores are not even displayed");
                        String notT = "Select count(isTrusted) as counter, login2 From Trust WHERE isTrusted = 0 group by login2 order by counter desc";
                        String t = "Select count(isTrusted) as counter, login2 From Trust WHERE isTrusted = 1 group by login2 order by counter desc";

                        ResultSet tRS = stmt.executeQuery(t);
                        String login2T;
                        int countT;
                        List<String> logins = new ArrayList<>();
                        List<Integer> counts = new ArrayList<>();
                        while (tRS.next()) {
                            login2T = tRS.getString("login2");
                            countT = tRS.getInt("counter");

                            logins.add(login2T);
                            counts.add(countT);
                        }

                        ResultSet notTRS = stmt.executeQuery(notT);
                        String login2NT;
                        int countNT;
                        while (notTRS.next()) {
                            login2NT = notTRS.getString("login2");
                            countNT = notTRS.getInt("counter");

                            for(int i = 0; i < logins.size(); i++) {
                                if(logins.get(i).equals(login2NT)) {
                                    int temp = counts.get(i);
                                    temp = temp - countNT;
                                    counts.set(i, temp);
                                }
                            }
                        }

                        if(m > logins.size()) {
                            for(int i = 0; i < logins.size(); i++) {
                                System.out.println("login: " + logins.get(i) + " trust score: " + counts.get(i));
                            }
                        } else {
                            for(int i = 0; i < m; i++) {
                                System.out.println("login: " + logins.get(i) + " trust score: " + counts.get(i));
                            }
                        }
                    } else if (adminC == 2) {
                        String sqlQuery = "SELECT count(rating) as ratingS, f.UU_login From Rates r, Feedback f WHERE f.fid = r.Feedback_fid group by f.UU_login order by ratingS desc LIMIT " + m;
                        ResultSet rs = stmt.executeQuery(sqlQuery);

                        int rating;
                        String login;
                        while(rs.next()) {
                            rating = rs.getInt("ratingS");
                            login = rs.getString("UU_login");

                            System.out.println("rating score: " + rating + " UUser: " + login);
                        }
                    }
                } else if (mainSystemChoice == 1 && systemType == 2) { //UPDATE A UC
                    while (true) {
                        System.out.println("Registered UCs for user: " + user);
                        String sqlQuery = MessageFormat.format("SELECT vin, category, year, make, model FROM UC c, IsCtypes i, Ctypes t " +
                                "WHERE i.Ctypes_tid = t.tid AND i.UC_vin = c.vin AND owner_login = \"{0}\"", user);
                        ResultSet rs = stmt.executeQuery(sqlQuery);

                        int vin;
                        String category;
                        String make;
                        String model;
                        int year;

                        while (rs.next()) {
                            vin = rs.getInt("vin");
                            category = rs.getString("category");
                            make = rs.getString("make");
                            model = rs.getString("model");
                            year = rs.getInt("year");

                            String print = MessageFormat.format("vin: {0}, category: {1}, make: {2}, model: {3}, year: " + year, vin, category, make, model);
                            System.out.println(print);
                        }

                        System.out.println();
                        System.out.println("1: Register a new UC");
                        System.out.println("2: Update a exiting UC");
                        System.out.println("3: return main menu");

                        int userC = Integer.parseInt(userInput());
                        if (userC == 1) { //REGISTER NEW UC
                            System.out.println("enter a category: ");
                            System.out.println("1: comfort");
                            System.out.println("2: luxury");
                            System.out.println("3: economy");

                            int categoryC = Integer.parseInt(userInput());
                            String userCategory;
                            if (categoryC == 1)
                                userCategory = "comfort";
                            else if (categoryC == 2)
                                userCategory = "luxury";
                            else
                                userCategory = "economy";

                            System.out.println("please enter the car make (manufacturer)");
                            String userMake = userInput();

                            System.out.println("please enter the car model");
                            String userModel = userInput();

                            System.out.println("please enter the year");
                            int userYear = Integer.parseInt(userInput());

                            String UCInsert = MessageFormat.format("INSERT INTO UC (category, owner_login, year) " +
                                    "VALUES (\"{0}\", \"{1}\", ", userCategory, user);
                            UCInsert = UCInsert + "" + userYear + ")";
                            stmt.executeUpdate(UCInsert);
                            ResultSet vinKey = stmt.getGeneratedKeys();

                            int returnedVin = -1;
                            while (vinKey.next()) {
                                returnedVin = vinKey.getInt(1);
                            }

                            if (returnedVin == -1) {
                                System.out.println("error in returnedVin");
                                break;
                            }

                            String CheckCType = MessageFormat.format("SELECT tid FROM Ctypes WHERE make = \"{0}\" AND model = \"{1}\"", userMake, userModel);
                            ResultSet checkType = stmt.executeQuery(CheckCType);

                            int tidCheck = -1;
                            while (checkType.next()) {
                                tidCheck = checkType.getInt("tid");
                            }

                            if (tidCheck == -1) { //make-model doesn't exist in CTypes
                                String CTypesInsert = MessageFormat.format("INSERT INTO Ctypes (make, model) VALUES (\"{0}\", \"{1}\")", userMake, userModel);
                                stmt.executeUpdate(CTypesInsert);
                                ResultSet tidKey = stmt.getGeneratedKeys();

                                int returnTid = -1;
                                while (tidKey.next()) {
                                    returnTid = tidKey.getInt(1);
                                }

                                if (returnTid == -1) {
                                    System.out.println("error in getting keys");
                                    break;
                                } else {
                                    String IsCTypeInsert = MessageFormat.format("INSERT INTO IsCtypes VALUES ({0}, {1})", returnedVin, returnTid);
                                    stmt.execute(IsCTypeInsert);
                                }
                            } else { //exist in CTypes
                                String IsCTypeInsert = MessageFormat.format("INSERT INTO IsCtypes VALUES ({0}, {1})", returnedVin, tidCheck);
                                stmt.execute(IsCTypeInsert);
                            }

                            System.out.println("UC registered!!");
                            System.out.println();

                        } else if (userC == 2) { //UPDATE
                            System.out.println("which vin would you like to update?");
                            int userUpdateVin = Integer.parseInt(userInput());

                            System.out.println("enter a category: ");
                            System.out.println("1: comfort");
                            System.out.println("2: luxury");
                            System.out.println("3: economy");

                            int categoryC = Integer.parseInt(userInput());
                            String userCategory;
                            if (categoryC == 1)
                                userCategory = "comfort";
                            else if (categoryC == 2)
                                userCategory = "luxury";
                            else
                                userCategory = "economy";

                            System.out.println("please enter the car make (manufacturer)");
                            String userMake = userInput();

                            System.out.println("please enter the car model");
                            String userModel = userInput();

                            System.out.println("please enter the year");
                            int userYear = Integer.parseInt(userInput());

                            String UCInsert = MessageFormat.format("UPDATE UC " +
                                    "SET category = \"{0}\", year = " + userYear + " WHERE owner_login = \"{1}\" AND vin = {2}", userCategory, user, userUpdateVin);
                            stmt.executeUpdate(UCInsert);

                            String deleteIsType = "DELETE FROM IsCtypes WHERE UC_vin = " + userUpdateVin; //Ensures that there isn't a weird double key listing in IsCtypes
                            stmt.execute(deleteIsType);

                            String CheckCType = MessageFormat.format("SELECT tid FROM Ctypes WHERE make = \"{0}\" AND model = \"{1}\"", userMake, userModel);
                            ResultSet checkType = stmt.executeQuery(CheckCType);

                            int tidCheck = -1;
                            while (checkType.next()) {
                                tidCheck = checkType.getInt("tid");
                            }

                            if (tidCheck == -1) { //make-model doesn't exist in CTypes
                                String CTypesInsert = MessageFormat.format("INSERT INTO Ctypes (make, model) VALUES (\"{0}\", \"{1}\")", userMake, userModel);
                                stmt.executeUpdate(CTypesInsert);
                                ResultSet tidKey = stmt.getGeneratedKeys();

                                int returnTid = -1;
                                while (tidKey.next()) {
                                    returnTid = tidKey.getInt(1);
                                }

                                if (returnTid == -1) {
                                    System.out.println("error in getting keys");
                                    break;
                                } else {
                                    String IsCTypeInsert = MessageFormat.format("INSERT INTO IsCtypes VALUES ({0}, {1})", userUpdateVin, returnTid);
                                    stmt.execute(IsCTypeInsert);
                                }
                            } else { //exist in CTypes
                                String IsCTypeInsert = MessageFormat.format("INSERT INTO IsCtypes VALUES ({0}, {1})", userUpdateVin, tidCheck);
                                stmt.execute(IsCTypeInsert);
                            }

                            System.out.println("UC updated");
                            System.out.println();
                        } else {
                            break;
                        }
                    }
                } else if (mainSystemChoice == 0) { //EXIT
                    break;
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Either connection error or query execution error!");
        }

        return 0;
    }
}