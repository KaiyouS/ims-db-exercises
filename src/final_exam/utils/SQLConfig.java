package final_exam.utils;

/**
 *
 * @author kserra
 */
public class SQLConfig {
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    public static void setDbConfig(String url, String user, String password) {
        dbUrl = url;
        dbUser = user;
        dbPassword = password;
    }

    public static String getDbUrl() {
        return dbUrl;
    }

    public static String getDbUser() {
        return dbUser;
    }

    public static String getDbPassword() {
        return dbPassword;
    }
}
