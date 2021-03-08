package JAVASubject.TODOLIST;

import java.io.FileReader;
import java.net.URL;
import java.sql.*;
import java.util.Properties;

/**
 * JDBC工具類
 * 目的：データベースへの接続(getConnection)、切断(close)機能を簡略化するため。
 * データベースへ接続情報が別ファイルjdbc.propertiesで保存
 */
public class JDBCUtils {
    private static String url, user, password, driver;

    /*
     * dbへ接続する必要情報をjdbc.propertiesから取得
     */
    static {
        try {
            // 1.jdbc.propertiesのパースを取得
            ClassLoader classLoader = JDBCUtils.class.getClassLoader();
            URL resource = classLoader.getResource("jdbc.properties");
            String path = resource.getPath();

            // 2.配列propertiesを生成
            Properties pro = new Properties();
            pro.load(new FileReader(path));


            // 3.jdbc.propertiesの中身を配列proへ格納
            driver = pro.getProperty("driver");
            url = pro.getProperty("url");
            user = pro.getProperty("user");
            password = pro.getProperty("password");

//            System.out.println("test@JDBCUtils// driver:"+driver+" OK");

            // 4.register driver
            Class.forName(driver);
//            System.out.println("test@JDBCUtils// driver registered OK");

        } catch (Exception e) {
            throw new RuntimeException(e + "properties取得失敗。");
        }
    }

    /**
     * @return instance of Connection
     */
    public static Connection getConnection() {
        Connection conn;
        try {
            conn = DriverManager.getConnection(url, user, password);
//            System.out.println("test@JDBCUtils// データベース接続 OK");
        } catch (Exception e) {
            throw new RuntimeException(e + "データベースへの接続失敗。");
        }

        return conn;
    }

    /**
     * close
     *
     * @param stmt
     * @param conn
     */
    public static void close(Statement stmt, Connection conn) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * close
     *
     * @param rs
     * @param stmt
     * @param conn
     */
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
