package JAVASubject.TODOLIST;

import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * ToDoListクラス
 *
 * ToDoListに対する操作(未完成予定一覧、完成予定、予定追加、予定編集)機能実装。
 * データベースに対する操作がDBControllerの実装クラス(SQLiteController)を通じて実現
 */
public class ToDoList {
    // SQLiteControllerのインスタンスを生成
    //! private SQLiteController sqLiteController = new SQLiteController();
    // (ディカプリングのため、直接的にSQLiteControllerの使用をやめ、インスタンスを引数として方法に引き渡す)

    // 予定の画面上のNo.とdb中のNo.の関係集
    private ArrayList<Object> numIndex;

    public static void createNewList(String tableName) throws SQLException {
        // create a new table
        DBController.createNewTableIfNotExists(tableName);
    }


    /**
     * 完成済予定を取得し、画面へ出力
     * @param controller
     * @throws SQLException
     */
    public void showCompSch(DBController controller) throws SQLException {
//        System.out.println("test@ToDoList// showCompSch ok");
        // 検索結果集取得
        ArrayList<HashMap> rs = controller.search(1);
        // rs = [{ID:xx, CONTENTS:xx, DEADLINE:xx},{}]

        // 画面へ出力

        System.out.println("");
        System.out.println(String.format("%50s", "完成済予定一覧"));
        System.out.println(String.format("%5s | %-79s | %-10s", "No.", "Contents", "Deadline"));
        System.out.println("=============================================================================================================");
        for (int i = 0; i < rs.size(); i++) {
            Object contents = rs.get(i).get("CONTENTS");
            Object deadline = rs.get(i).get("DEADLINE");
            // 出力
            System.out.println(String.format("%5s | %-80s | %-10s", i + 1, contents, deadline));
        }

        // メニューを出力
        System.out.println("=============================================================================================================");

        // 確認待ち
        System.out.println(String.format("%50s", "確認(ENTER)"));
        Scanner sc = new Scanner(System.in);
        sc.nextLine();
    }


    /**
     * 未完成予定を取得し、画面へ出力
     * @param controller
     * @throws SQLException
     */
    public void showUncompSch(DBController controller) throws SQLException {
//        System.out.println("test@ToDoList// showUncompSch OK");

        // 未完成予定の配列を取得(b=0)
        ArrayList<HashMap> rs = controller.search(0);

        // Scheduleが示される度に、numIndexがリフレッシュウする必要がある。
        numIndex = new ArrayList<>();
        // 使用上の便利のため、numIndexの0番をnullにする。
        numIndex.add(null);

        // 画面へ出力
        System.out.println("");
        System.out.println(String.format("%50s", "TODOLIST"));
        System.out.println(String.format("%5s | %-80s | %-10s", "No.", "Contents", "Deadline"));
        System.out.println("=============================================================================================================");
        for (int i = 0; i < rs.size(); i++) {
            Object id = rs.get(i).get("ID");
            Object contents = rs.get(i).get("CONTENTS");
            Object deadline = rs.get(i).get("DEADLINE");
            numIndex.add(id);

            // 出力
            System.out.println(String.format("%5s | %-80s | %-10s", i + 1, contents, deadline));
        }

        // メニューを出力
        System.out.println("=============================================================================================================");
        System.out.println("[MENU] a:予定追加  e:予定修正　d:予定削除　c:完成済予定一覧");
//        System.out.println("test@ToDoList.show"+ numIndex.toString());
        System.out.print("=> ");
    }


    /**
     * 予定編集メソッド
     * @param controller
     * @param schNo
     * @throws SQLException
     */
    private void editSch(int schNo, DBController controller) throws SQLException {
//        System.out.println("test@ToDoList.editSch// editSch:"+schNo);
        Schedule sch = new Schedule();
        Scanner sc = new Scanner(System.in);
        // schNoからdb中のidを取得, obj->int型転換
        int id = (int) numIndex.get(schNo);


        // 未完成予定の配列を取得(b=0)
        ArrayList<HashMap> rs = controller.search(0);
        // 配列[{},{}]からcontentsとdeadlineを取得
        Object contents = rs.get(schNo - 1).get("CONTENTS");
        Object deadline = rs.get(schNo - 1).get("DEADLINE");

        //　予定内容を修正
        System.out.println("修正前の内容：" + contents);
        System.out.println("新たな内容(変更なし:ENTER)：");
        System.out.print("=> ");
        String newContents = sc.nextLine();
        // 予定入力ぜず、enterをおす場合、予定追加モード中止、メイン画面に戻る。
        if (newContents.length() == 0) {
            newContents = (String) contents;
        }
        sch.setContents(newContents);

        // 締め切り修正
        System.out.println("");
        System.out.println("修正前の〆切：" + deadline);
        while (true) {
            System.out.println("新たな〆切(書式:yyyy-MM-dd　〆切なし:ENTER):");
            System.out.print("=> ");
            String s = sc.nextLine();
            if (s.length() == 0) {
                break;
            }
            try {
                Date newDeadline = Date.valueOf(s); // Date書式チェック
                sch.setDeadline(newDeadline);
                break;
            } catch (Exception ex) {
                System.out.println("日付の書式が間違っている。");
            }
        }

        // qLiteController.updateにid,schを引き渡し、update実行
        boolean succeeded = controller.update(id, sch);
        if (succeeded) {
            System.out.println(String.format("%50s", "予定修正成功!"));
        } else {
            System.out.println(String.format("%50s", "予定修正失敗!"));
        }
    }


    /**
     * 予定削除メソッド
     * @param controller
     * @param schNo
     */
    private void delete(int schNo, DBController controller) {
        // schNoからdb中のidを取得, obj->int型転換
        int id = (int) numIndex.get(schNo);
        // dbからロジック削除
        boolean succeeded = controller.delete(id);
        if (succeeded) {
            System.out.println("No." + schNo + "の予定が削除されました。");
        } else {
            System.out.println(String.format("%50s", "削除失敗！"));
        }
    }


    /**
     * 予定追加メソッド
     * @param controller
     */
    private void addSch(DBController controller) throws SQLException {
//        System.out.println("test@ToDoList.addSch// addSch ok");
        // Scheduleインスタンスschを作成
        Schedule sch = new Schedule();
        Scanner sc = new Scanner(System.in);

        //　予定内容を入力
        System.out.println("予定内容：");
        System.out.print("=> ");
        String contents = sc.nextLine();
        // 予定入力ぜず、enterをおす場合、予定追加モード中止、メイン画面に戻る。
        if (contents.length() == 0) {
            System.out.println(String.format("%50s", "修正なし。"));
            return;
        }
        sch.setContents(contents);

        // 締め切りを入力
        while (true) {
            System.out.println("");
            System.out.println("締め切り:\n" +
                    "(書式:yyyy-MM-dd　〆切なし:ENTER)");
            System.out.print("=> ");
            String s = sc.nextLine();
            if (s.length() == 0) {
                break;
            }
            try {
                Date dl = Date.valueOf(s); // Date書式チェック
                sch.setDeadline(dl);
                break;
            } catch (Exception ex) {
                System.out.println("日付の書式が間違っている。");
            }
        }

        // 予定schをdbへ入力
        Boolean succeeded = controller.insert(sch);
        if (succeeded) {
            System.out.println(String.format("%50s", "予定追加成功"));
        } else {
            System.out.println(String.format("%50s", "予定追加失敗"));
        }
    }


    /**
     * menu選択メソッド
     *
     * @param select
     */
    public void menuSelect(String select, DBController controller) throws SQLException {
        if (select.equals("a")) {
            addSch(controller);
        } else if (select.equals("c")) {
            showCompSch(controller);
        } else if (select.equals("e")) {
            while (true) {
                System.out.print("No.(キャンセル:0)=>");
                try{
                    Scanner sc = new Scanner(System.in);
                    int schNo = sc.nextInt();
                    // No有効性チェック
                    if (schNo > 0 && schNo < numIndex.size()) {
                        editSch(schNo, controller);
                        break;
                    } else if (schNo == 0) {
                        break;
                    } else {
                        System.out.println("無効No.");
                    }
                }catch (InputMismatchException ex){
                    System.out.println("無効No.");
                }

            }
        } else if (select.equals("d")) {
            while (true) {
                System.out.print("No.(キャンセル:0)=>");
                try{
                    Scanner sc = new Scanner(System.in);
                    int schNo = sc.nextInt();
                    // No有効性チェック
                    if (schNo > 0 && schNo < numIndex.size()) {
                        delete(schNo, controller);
                        break;
                    } else if (schNo == 0) {
                        break;
                    } else {
                        System.out.println("無効No.");
                    }
                }catch (InputMismatchException ex){
                    System.out.println("無効No.");
                }
            }
        } else {
            System.out.println("無効命令");
        }
    }

}
