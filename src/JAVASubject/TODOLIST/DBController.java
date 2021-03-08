package JAVASubject.TODOLIST;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 目的：拡張性を配慮し、本バージョンがSQLite用のため、、以降MySqlバージョンを開発時、
 * 増加、修正、検索機能を定義
 * tableの新規作成機能が実装
 */

public interface DBController {
    ArrayList<HashMap> search(int a) throws SQLException;

    boolean insert(Schedule sch) throws SQLException;

    boolean update(int id, Schedule sch);

    boolean delete(int id);
    // public abstract boolean isTableExist(String tableName) throws SQLException;

    /**
     * tableが存在しない場合、新規作成
     *
     * @param tableName
     * @throws SQLException
     */
    static void createNewTableIfNotExists(String tableName) throws SQLException {
        Connection conn;
        Statement stmt;
        conn = JDBCUtils.getConnection();
        // System.out.println("Opened database successfully");
        stmt = conn.createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "CONTENTS TEXT NOT NULL," +
                "DEADLINE DATE, " +
                "ISDELETED INTEGER DEFAULT 0)";

        stmt.executeUpdate(sql);
//        System.out.println("created new table successfully!");

        // close
        JDBCUtils.close(stmt, conn);
    }
}
