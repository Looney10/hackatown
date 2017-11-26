package com.example.ada.myapplicationforhackaton;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.ada.myapplicationforhackaton.entities.Message;
import com.example.ada.myapplicationforhackaton.entities.MyUsername;
import com.example.ada.myapplicationforhackaton.entities.Pending;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MagazinSeesChatListActivity extends AppCompatActivity {
    private ListView list;
    private MobileServiceClient mClient;
    private MobileServiceTable<Pending> mToDoTable2;
    private static String cauta;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onResume() {
        super.onResume();
        queryForAll();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazin_sees_chat_list);
        list=(ListView)findViewById(R.id. mesajemagazinList);
        adapter=new ArrayAdapter<String>(MagazinSeesChatListActivity.this,android.R.layout.simple_list_item_1);
        list.setAdapter(adapter);

        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient("http://ourmobileappforhackaton.azurewebsites.net", this);

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });

            // Get the Mobile Service Table instance to use

            mToDoTable2=mClient.getTable("pending2", Pending.class);


        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e) {
            createAndShowDialog(e, "Error");
        }

        queryForAll();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(MagazinSeesChatListActivity.this,PersonalChatActivity.class);
                i.putExtra("nume",adapter.getItem(position));
                cauta=adapter.getItem(position);
                removeFromDatabase();
                startActivity(i);
            }
        });
    }
    private void createAndShowDialogFromTask(final Exception exception, String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error");
            }
        });
    }

    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if (exception.getCause() != null) {
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Userul exista deja!");
        builder.setTitle(title);
        builder.create().show();
    }

    private void queryForAll(){
        adapter.clear();
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final List<Pending> mylist = mToDoTable2
                            .where()
                            .field("magazin").eq(MyUsername.getInstance().getName())
                            .execute()
                            .get();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(Pending p:mylist)
                                adapter.add(p.getUser());
                        }
                    });

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error!");
                }
                return null;
            }
        };
        runAsyncTask(task);
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null);
        } else {
            return task.execute();
        }
    }

    private void removeFromDatabase(){
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final List<Pending> mylist = mToDoTable2
                            .where()
                            .field("magazin").eq(MyUsername.getInstance().getName())
                            .execute()
                            .get();
                    Pending p = null;
                    for (Pending pp : mylist)
                        if (pp.getUser().equals(cauta))
                        {
                            mToDoTable2
                                    .delete(pp.getId());
                        }

                    cauta=null;
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error!");
                }
                return null;
            }
        };
        runAsyncTask(task);
    }
}
