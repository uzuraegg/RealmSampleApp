package com.diesen.realmsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class MainActivity extends AppCompatActivity {

    private Realm realm;

    private EditText et_id;
    private EditText et_data;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);  //最初に初期化

        try {
            realm = Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException r) {
            Realm.deleteRealm(Realm.getDefaultConfiguration());
            realm = Realm.getDefaultInstance();
        }

        et_id = (EditText)findViewById(R.id.edit_id);
        et_data = (EditText)findViewById(R.id.edit_data);
        tv = (TextView)findViewById(R.id.textView);

        /* ボタンのリスナー登録 */
        findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id_str = et_id.getText().toString();
                String data_str = et_data.getText().toString();

                if (!TextUtils.isEmpty(id_str) && !TextUtils.isEmpty(data_str)){  //未記入を検出
                    addData(Integer.parseInt(id_str),data_str);
                    clearEditTexts();
                    read();
                }
            }
        });
        findViewById(R.id.button_del_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id_str = et_id.getText().toString();

                if (!TextUtils.isEmpty(id_str)) {  //未記入を検出
                    delData(Integer.parseInt(et_id.getText().toString()));
                    clearEditTexts();
                }
                read();
            }
        });
        findViewById(R.id.button_del_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dellAll();
                read();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        read();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();  //最後にクローズ
    }

    /* テキスト入力欄をクリア */
    private void clearEditTexts(){
        et_id.getEditableText().clear();
        et_data.getEditableText().clear();
    }


    /* データを追加(IDが同じデータが既に存在する場合、データを更新) */
    private void addData(int id,String data) {
        Log.d("tes","onAddData");
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Model model = new Model(); //realm.createObject()を使うと、Realmオブジェクト生成時にPrimaryKeyが重複しているとエラーが出た
                model.setPk(id);
                model.setData(data);
                realm.copyToRealmOrUpdate(model);
            }
        });
    }

    /* idで指定したデータを削除 */
    private void delData(int id) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Model> r = realm.where(Model.class).equalTo("pk", id).findAll();
                r.deleteAllFromRealm();
            }
        });
    }

    /* 全データ削除 */
    private void dellAll(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(Model.class);
            }
        });
    }

    /* 全てのデータを読み込みテキストビューに出力 */
    private void read() {
        RealmQuery<Model> query = realm.where(Model.class);
        RealmResults<Model> models = query.findAll();
        tv.setText("");
        for (Model model : models) {
            tv.append(String.valueOf(model.getPk()) + ": " +model.getData() + "\n");
        }
    }
}
