package JAVASubject.TODOLIST;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * インターフェースDBContollerのSQLite実装類。
 * データベースに対する操作機能（増加、修正、検索）実装
 */
public class SQLiteController implements DBController {
    /**
     * SQLiteControllerがJVMに初めてロードされる時、1回だけ実施。
     */
    static{
        Connection conn;
        Statement stmt;
        conn = JDBCUtils.getConnection();
        // System.out.println("Opened database successfully");

        String sql = "CREATE TABLE IF NOT EXISTS TODOLIST" +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "CONTENTS TEXT NOT NULL," +
                "DEADLINE DATE, " +
                "ISDELETED INTEGER DEFAULT 0)";

        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            // close
            JDBCUtils.close(stmt, conn);

            //        System.out.println("created new table successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }



    }

    /**
     * 未完成予定を取得
     *
     * @param a ; 未完成予定、b＝0; 完成済予定、b＝1;
     * @return ArrayList<HashMap> [{ID=xx, CONTENTS=xx, DEADLINE=xx},{..,..,..}...]
     * @throws SQLException
     */
    @Override
    public ArrayList<HashMap> search(int a) throws SQLException {
        // 1.dbコネクト対象を取得
        Connection conn = JDBCUtils.getConnection();
        // 2.sql実行対象を取得
        Statement stmt = conn.createStatement();
        // 3.sqlを定義
        String sql = "SELECT * FROM TODOLIST WHERE ISDELETED=" + a;
        // 4.sqlを実行
        ResultSet rs = stmt.executeQuery(sql);

        ArrayList<HashMap> list = new ArrayList<>();


        while (rs.next()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("ID", rs.getInt("ID"));
            map.put("CONTENTS", rs.getString("CONTENTS"));
            map.put("DEADLINE", rs.getString("DEADLINE"));
            list.add(map);
        }

        // db link 解放
        JDBCUtils.close(rs, stmt, conn);

        // ソート
        mySort(list);


        return list;
    }

    private void mySort(ArrayList<HashMap> list) {
        // sort by deadline
        Collections.sort(list, new Comparator<HashMap>() {
            @Override
            public int compare(HashMap o1, HashMap o2) {
                Date date1 = null;
                Date date2 = null;

                // java.sql.Date(String) を利用して、String=>Date
                try{
                    date1 = Date.valueOf((String) o1.get("DEADLINE"));
                }catch (Exception e){
                    // o1.get("DEADLINE") == null の場合
                    return 1;
                }

                try{
                    date2 = Date.valueOf((String) o2.get("DEADLINE"));
                }catch (Exception e){
                    // o2.get("DEADLINE") == null の場合
                    return -1;
                }

                return date1.compareTo(date2);
            }
        });
    }


    /**
     * 新規Schedule追加
     *
     * @param sch
     * @return boolean
     */
    @Override
    public boolean insert(Schedule sch) {
        try {
            Connection conn = JDBCUtils.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO TODOLIST(CONTENTS, DEADLINE) " +
                    "VALUES('" + sch.getContents() + "','" + sch.getDeadline() + "')";
            stmt.executeUpdate(sql);
            JDBCUtils.close(stmt, conn);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

    /**
     * Scheduleを修正
     *
     * @param sch
     * @return boolean
     */
    @Override
    public boolean update(int id, Schedule sch) {
        try {
            Connection conn = JDBCUtils.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "UPDATE TODOLIST SET CONTENTS='" + sch.getContents() +
                    "',DEADLINE='" + sch.getDeadline() + "' where ID=" + id;
            stmt.executeUpdate(sql);
            JDBCUtils.close(stmt, conn);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * @param id
     * @return boolean
     */
    @Override
    public boolean delete(int id) {
        try {
            Connection conn = JDBCUtils.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "UPDATE TODOLIST SET ISDELETED=1 where ID=" + id;
            stmt.executeUpdate(sql);
            JDBCUtils.close(stmt, conn);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
