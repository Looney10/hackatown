package com.example.ada.myapplicationforhackaton;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ada.myapplicationforhackaton.entities.Comment;
import com.example.ada.myapplicationforhackaton.entities.Message;
import com.example.ada.myapplicationforhackaton.entities.MyUsername;
import com.example.ada.myapplicationforhackaton.entities.Pending;
import com.example.ada.myapplicationforhackaton.entities.User;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PersonalChatActivity extends AppCompatActivity {
    private TextView cine;
    private Button sendmsg;
    private ListView listMessages;
    private EditText msgText;
    private String magazin;

    private String user;
    private String maga;

    private MobileServiceClient mClient;
    private MobileServiceTable<Message> mToDoTable;
    private MobileServiceTable<Pending> mToDoTable2;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat);

        magazin=getIntent().getStringExtra("nume");

        sendmsg=(Button)findViewById(R.id.sendMessagebtn);
        listMessages=(ListView)findViewById(R.id.msgLIST);
        msgText=(EditText)findViewById(R.id.messageET);

        adapter=new ArrayAdapter<String>(PersonalChatActivity.this,android.R.layout.simple_list_item_1);
        listMessages.setAdapter(adapter);

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

            mToDoTable = mClient.getTable("conversatii", Message.class);
            mToDoTable2=mClient.getTable("pending2", Pending.class);


        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e) {
            createAndShowDialog(e, "Error");
        }

        sendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMesaj(msgText.getText().toString());
            }
        });

        if(MyUsername.getInstance().isMagazin()==false){
            user=MyUsername.getInstance().getName();
            maga=magazin;
        }
        else
        {
            maga=MyUsername.getInstance().getName();
            user=magazin;
        }

        getAllMessages();

        listMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getAllMessages();
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
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    public void getAllMessages(){
        adapter.clear();
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final List<Message> mylist = mToDoTable
                            .where()
                            .field("user").eq(user)
                            .execute()
                            .get();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Message p : mylist) {
                                if(p.getMagazin().equals(maga))
                                    adapter.add(p.getText());
                                else
                                if(p.getUser().equals(user))
                                    adapter.add(p.getText());
                            }
                        }
                    });
                    Log.d("why4",maga);

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error!");
                }
                return null;
            }
        };
        runAsyncTask(task);
    }

    public void sendMesaj(String text){
        Message m=new Message();

        String tt=MyUsername.getInstance().getName()+": "+msgText.getText().toString();

        m.setText(tt);
        m.setMagazin(maga);
        m.setUser(user);
        if(MyUsername.getInstance().isMagazin()==false) {
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Log.d("llalskd", "djsfj");
                        List<Pending> result = mToDoTable2
                                .where()
                                .field("user").eq(user)
                                .execute()
                                .get();
                        boolean ok = false;
                        if (!result.isEmpty())
                            for (Pending p : result) {
                                if (p.getMagazin().equals(maga))
                                    ok = true;
                            }
                        if (ok == false) {
                            Pending pend = new Pending();
                            pend.setMagazin(maga);
                            pend.setUser(user);
                            mToDoTable2.insert(pend);
                        }
                    } catch (final Exception e) {
                        createAndShowDialogFromTask(e, "Error!");
                    }

                    return null;
                }
            };
            runAsyncTask(task);
        }

        mToDoTable.insert(m);
        msgText.setText("");

        getAllMessages();
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null);
        } else {
            return task.execute();
        }
    }
}
