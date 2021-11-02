package lab.server;

import lab.Hat;
import lab.Message;

import java.math.BigInteger;
import java.sql.*;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import static lab.server.ManyHats.collection;
public class DataBaseHandler {
    private final static byte[] SALT = "HF2Ddf3s436".getBytes();
    private String url = "jdbc:postgresql://localhost:5432/mydb";
    private String login = "postgres";
    private String pass = "genm00";
    DataBaseHandler db;
    private Connection connection = null;
    private AtomicInteger hatindex = new AtomicInteger(Math.round((ZonedDateTime.now()).getNano()));
    private AtomicInteger userindex = new AtomicInteger(Math.round((ZonedDateTime.now()).getNano()));
   // private Hashtable<String, Hat> collection = new Hashtable<>();

    {

        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Installed Driver");
            connection = DriverManager.getConnection(url, login, pass);
            System.out.println("The Connection is successfully established\n");
            autoCreateTables();
        } catch (Exception e) {
            e.printStackTrace(); //todo remove
            System.out.println("Can't connect to the database");
        }
    }
    void autoCreateTables(){

        try {
            Statement statement = connection.createStatement();
            statement.execute("create table if not exists Hats " +
                    "(hitindex serial primary key not null, key text, size integer, color text, name text," +
                    " things text, createddate text, username text)"
            );
            statement.execute("create table if not exists users (" +
                    "userindex serial primary key not null, login text, email text unique, pass text)"
            );
        } catch (SQLException e) {
            System.out.println("Не получилось создать таблицы: " + e.toString());
            System.exit(-1);
        }
    }

    public int loadHats(ManyHats hats) {
        try {
            int i = 0;
            ZonedDateTime time = ZonedDateTime.now();
            PreparedStatement preStatement = connection.prepareStatement("SELECT * FROM Hats;");
            ResultSet result = preStatement.executeQuery();
            while (result.next()) {
                String username = result.getString("username");
                String key = result.getString("key");
                int size = result.getInt("size");
                String color = result.getString("color");
                String name = result.getString("name");
                String date = result.getString("createddate");
                if (date != null) {
                    time = ZonedDateTime.parse(result.getString("createddate"));
                }
                Hat h = new Hat(size, color, name);
                h.setCreatedDate(time);
                h.setUser(username);
                hats.addNewHat(key,h);
                i++;

            }
            return i;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error whilst adding Hats");
            return -1;
        }
    }

    public void addToDB(String key, Hat h, String username, String pass) {

        try {
            addHat(key, h, username, pass);
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Error while adding a hat to a DataBase");
        }
    }

    private void addHat(String key, Hat h, String username, String password) throws SQLException {
        hatindex.incrementAndGet();
        if (this.executeLogin(username, password) == 0) {
            PreparedStatement preStatement = connection.prepareStatement("INSERT INTO Hats VALUES (?, ?, ?, ?, ?, ?,?,?);");
            preStatement.setInt(1, new Integer(String.valueOf(hatindex)));
            preStatement.setString(2, key);
            preStatement.setInt(3, h.getSize());
            preStatement.setString(4, h.getColor());
            preStatement.setString(5, h.getName());
            preStatement.setString(6, h.contentlist());
            preStatement.setString(7, h.getCreatedDate().toString());
            preStatement.setString(8, username);
            preStatement.executeUpdate();
        }
    }

    public void clear() throws SQLException {
        PreparedStatement preStatement = connection.prepareStatement("DELETE FROM Hats;");
        preStatement.executeUpdate();
    }

    public void saveHats(String filename) {
        try {
            if (collection.size()!=0) {
                for (Map.Entry<String, Hat> entry : collection.entrySet()) {
                      entry.getValue();
                    Loader.writeFile(filename);
                }
                System.out.println("База данных была обновлена");
            } else {
                System.out.println("Гардероб пуст.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при сохранении в базу данных");
        }
    }

    boolean removeHat(Hat hat, String username, String password) {
        if (this.executeLogin(username, password)==0) {
            try {
                PreparedStatement preStatement = connection.prepareStatement("SELECT FROM hats WHERE size=? AND color=? AND name=? AND username=?;");
                preStatement.setInt(1, hat.getSize());
                preStatement.setString(2,hat.getColor());
                preStatement.setString(3,hat.getName());
                preStatement.setString(4,username);
                ResultSet result = preStatement.executeQuery();
                if (result.next()) {
                    try {
                        PreparedStatement ppreStatement = connection.prepareStatement("DELETE FROM hats WHERE size=? AND color=? AND name=? AND username=?;");
                        ppreStatement.setInt(1, hat.getSize());
                        ppreStatement.setString(2,hat.getColor());
                        ppreStatement.setString(3,hat.getName());
                        ppreStatement.setString(4,username);
                        ppreStatement.executeUpdate();
                        return true;
                    } catch (Exception e) {
                        System.out.println("Ошибка при удалении из базы данных");
                    }
                }
            } catch (Exception e) {
                System.out.println("Ошибка при удалении из базы данных");
                return false;}
        }
        return false;
    }



    public int executeLogin(String login, String pass) {
        try {
            PreparedStatement preStatement = connection.prepareStatement("SELECT * FROM Users WHERE login=? and pass=?;");
            String hash = DataBaseHandler.computeSaltedBase64Hash(pass, SALT, "SHA-224");
            preStatement.setString(1, login);
            preStatement.setString(2, hash);
            ResultSet result = preStatement.executeQuery();
            if (result.next()) return 0;
            else {
                PreparedStatement preStatement2 = connection.prepareStatement("SELECT * FROM Users WHERE login=?;");
                preStatement2.setString(1, login);
                ResultSet result2 = preStatement2.executeQuery();
                if (result2.next()) return 2;
                else return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Login error");
            return -1;
        }
    }

    public int executeRegister(String login, String mail, String pass) {
        userindex.incrementAndGet();

        try {


            PreparedStatement ifLog = connection.prepareStatement("SELECT * FROM Users WHERE login=?;");

            ifLog.setString(1, login);
            ResultSet result = ifLog.executeQuery();
            if (result.next()) {
                return 0;
            }
            String hash = DataBaseHandler.computeSaltedBase64Hash(pass, SALT, "SHA-224");
            PreparedStatement statement = connection.prepareStatement("INSERT INTO Users VALUES (?, ?, ?,?);");
            statement.setInt(1, new Integer(String.valueOf(userindex)));
            statement.setString(2, login);
            statement.setString(3, mail);
            statement.setString(4, hash);
            statement.executeUpdate();
            new Thread(() -> JavaMail.registration(mail, pass)).start();
            return 1;
        } catch (Exception e) {
           // e.printStackTrace();
            System.out.println("Error whilst registration");
            return -1;
        }
    }



    public static String computeSaltedBase64Hash(String password, // the password you want to hash
                                                 byte[] salt, // the salt you want to use (uses random salt if null).
                                                 String hashAlgorithm// the algorithm you want to use.
                                                 ) throws NoSuchAlgorithmException // the delimiter that will be used to delimit the salt and the hash.
    {
        // transform the password string into a byte[]. we have to do this to work with it later.
        byte[] passwordBytes = password.getBytes();
        byte[] saltBytes;

        if(salt != null)
        {
            saltBytes = salt;
        }
        else
        {
            // if null has been provided as salt parameter create a new random salt.
            saltBytes = new byte[64];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(saltBytes);
        }

        // MessageDigest converts our password and salt into a hash.
        MessageDigest md= MessageDigest.getInstance(hashAlgorithm);
        // concatenate the salt byte[] and the password byte[].
        // digest() method is called
        // to calculate message digest of the input string
        // returned as array of byte
        byte[] messageDigest = md.digest(passwordBytes);

        // Convert byte array into signum representation
        BigInteger no = new BigInteger(1, messageDigest);
        // Convert message digest into hex value
        String hashtext = no.toString(16);

        // Add preceding 0s to make it 32 bit
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }

        // return the HashText
        return hashtext;
    }


}