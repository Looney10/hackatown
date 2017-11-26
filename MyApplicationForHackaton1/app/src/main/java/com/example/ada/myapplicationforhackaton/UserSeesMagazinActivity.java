package com.example.ada.myapplicationforhackaton;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ada.myapplicationforhackaton.entities.Comment;
import com.example.ada.myapplicationforhackaton.entities.Magazin;
import com.example.ada.myapplicationforhackaton.entities.MyUsername;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UserSeesMagazinActivity extends AppCompatActivity {
    private TextView descriere,titlu;
    private Button addCommBtn,chatBtn;
    private EditText commentText;
    private ListView commentList;
    private ArrayAdapter<String> adapter;
    private Magazin magazin;
    private MobileServiceClient mClient;
    private MobileServiceTable<Comment> mToDoTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sees_magazin);
        descriere=(TextView)findViewById(R.id.descriereet);
        addCommBtn=(Button)findViewById(R.id.btnComment);
        titlu=(TextView)findViewById(R.id.titlutv);
        commentList=(ListView)findViewById(R.id.commlist);
        commentText=(EditText)findViewById(R.id.edittextAddComment);
        adapter=new ArrayAdapter<String>(UserSeesMagazinActivity.this,android.R.layout.simple_list_item_1);
        commentList.setAdapter(adapter);

        chatBtn=(Button) findViewById(R.id.chatBtn);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(UserSeesMagazinActivity.this,PersonalChatActivity.class);
                i.putExtra("nume",magazin.getNume());
                startActivity(i);
            }
        });


        Intent i=getIntent();
        magazin=(Magazin) i.getSerializableExtra("instanta");

        descriere.setText(magazin.getDescriere());
        titlu.setText(magazin.getNume());


        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient("http://ourmobileappforhackaton.azurewebsites.net",this);

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

            mToDoTable = mClient.getTable("comment2",Comment.class);


        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            createAndShowDialog(e, "Error");
        }

        addCommBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t=commentText.getText().toString();
                adaugaComment(t);
            }
        });

        getListOfComments();
        commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getListOfComments();
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

    public void adaugaComment(String textul){
        String text=commentText.getText().toString();
        String magaz=magazin.getNume();
        String sender= MyUsername.getInstance().getName();
        Comment com=new Comment();
        com.setMagazin(magaz);
        com.setSender(sender);
        com.setText(text);
        mToDoTable.insert(com);
        commentText.setText("");
        getListOfComments();
    }

    public void getListOfComments(){
            adapter.clear();
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        final List<Comment> mylist = mToDoTable
                                .select("id","magazin","text","sender")
                                .execute()
                                .get();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for(Comment p:mylist)
                                    adapter.add(p.toString());
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

}
