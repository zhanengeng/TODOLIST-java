package JAVASubject.DeveloperFilder;

import JAVASubject.TODOLIST.*;

import java.sql.*;

/**
 * 開発者用db内容確認クラス、プログラムと関係なし。
 */
public class CheckSql {
    public static void main(String[] args) {
        Connection conn = JDBCUtils.getConnection();
        try {
            Statement stmt = conn.createStatement();
            String sql = "select * from TODOLIST";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("ID");
                String contents = rs.getString("CONTENTS");
                String date = rs.getString("DEADLINE");

                int isDeleted = rs.getInt("ISDELETED");
                System.out.println("ID:" + id + "; Contents:" + contents + "; deadline:" + date + "; isDeleted:" + isDeleted);
            }
            JDBCUtils.close(rs, stmt, conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
