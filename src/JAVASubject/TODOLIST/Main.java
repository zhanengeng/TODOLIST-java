package JAVASubject.TODOLIST;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * DB:SQLite.実行する前に、SQLiteーjdbc -3.xx.jarの導入を確認してください。
 */

public class Main {
    public static void main(String[] args) {

        ToDoList tdl = new ToDoList();
        // SQLiteを使うため、SQLiteControllerをdbのコントローラーとする。
        SQLiteController sqlcontroller = new SQLiteController();

        // tableの新規作成がSQLiteControllerのstaticブロック内で実施するため、以下のコードを不要。
        /*
        // 1. TODOLISTが存在しない場合、新しく作る。（存在判別はDBControllerクラスで行う。）
        String tableName = "TODOLIST"; // dbのtableの名前。
        try {
            ToDoList.createNewList(tableName);
            // System.out.println("test@Main createNewList OK");
        } catch (SQLException e) {
            e.printStackTrace();
        }*/


        // main logic
        while (true) {
            // 2.予定一覧画面を示す
            try {
                tdl.showUncompSch(sqlcontroller);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 3.ユーザー入力:メニュー選択
            Scanner sc = new Scanner(System.in);
            String select = sc.next();
            try {
                tdl.menuSelect(select, sqlcontroller);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

}
