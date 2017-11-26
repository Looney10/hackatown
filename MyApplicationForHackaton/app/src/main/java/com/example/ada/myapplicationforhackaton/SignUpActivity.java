package com.example.ada.myapplicationforhackaton;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ada.myapplicationforhackaton.entities.User;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {
    private EditText username, parola;
    private Button btnregister;

    private MobileServiceClient mClient;
    private MobileServiceTable<User> mToDoTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = (EditText) findViewById(R.id.useret);
        parola = (EditText) findViewById(R.id.parolaet);
        btnregister = (Button) findViewById(R.id.registerbtn);

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = username.getText().toString();
                searchNameInDatabase(s);
            }
        });

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

            mToDoTable = mClient.getTable("users", User.class);


        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e) {
            createAndShowDialog(e, "Error");
        }
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

    private void searchNameInDatabase(String name) {
        final String n = name;
        try {
            Log.d("hello", "hello");
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        List<User> result = mToDoTable
                                .where()
                                .field("USERNAME").eq(n)
                                .execute()
                                .get();
                        if (result.isEmpty()) {
                            User user = new User();
                            user.setUsername(username.getText().toString());
                            user.setPassword( parola.getText().toString());

                            addItemInTable(user);
                            Intent i=new Intent(SignUpActivity.this,LoginActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Contul exista deja", Toast.LENGTH_LONG).show();
                        }
                        Log.d("hello again", "again");
                    } catch (final Exception e) {
                        createAndShowDialogFromTask(e, "Error!");
                    }
                    return null;
                }
            };
            runAsyncTask(task);

        } catch (Exception e) {
            createAndShowDialogFromTask(e, "Error!");
        }

    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null);
        } else {
            return task.execute();
        }
    }

    private void addItemInTable(User item) throws ExecutionException, InterruptedException {
        mToDoTable.insert(item);
    }


}

