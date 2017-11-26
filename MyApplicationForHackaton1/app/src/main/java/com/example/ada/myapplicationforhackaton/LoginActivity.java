package com.example.ada.myapplicationforhackaton;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ada.myapplicationforhackaton.entities.MyUsername;
import com.example.ada.myapplicationforhackaton.entities.User;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private EditText username, parola;
    private Button btnlogin, btnsignin;

    private MobileServiceClient mClient;
    private MobileServiceTable<User> mToDoTable;

    private static String passResult=null;
    private static boolean isMagazin=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username=(EditText)findViewById(R.id.useret);
        parola=(EditText)findViewById(R.id.parolaet);
        btnlogin=(Button)findViewById(R.id.loginbtn);
        btnsignin=(Button)findViewById(R.id.signupbtn);

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

            mToDoTable = mClient.getTable("users",User.class);


        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            createAndShowDialog(e, "Error");
        }

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryLogin();
            }
        });
        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void tryLogin(){
        String nume=username.getText().toString();
        foundInDatabase(nume);
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


    private void foundInDatabase(String name){
        final String n=name;

        try{
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Log.d("iii","iii");
                        List<User> result = mToDoTable
                                    .where()
                                    .field("USERNAME").eq(n)
                                    .execute()
                                    .get();
                        if(!result.isEmpty())
                        {
                            passResult= result.get(0).getPassword();
                            isMagazin=result.get(0).isMagazin();
                        }
                    } catch (final Exception e) {
                        createAndShowDialogFromTask(e, "Error!");
                    }
                    Log.d("mmmm","mmmm");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(passResult==null)
                            {
                                Toast.makeText(LoginActivity.this,"Parola sau utilizatorul sunt gresite!",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                if(parola.getText().toString().equals(passResult))
                                {
                                    Toast.makeText(LoginActivity.this,"Succes",Toast.LENGTH_LONG).show();
                                    if(isMagazin==false)
                                    {
                                        Log.d("yas","is user");
                                        Intent i=new Intent(LoginActivity.this,MallActivity.class);
                                        MyUsername.getInstance().setName(username.getText().toString());
                                        MyUsername.getInstance().setMagazin(false);
                                        startActivity(i);
                                        finish();
                                    }
                                    else
                                    {
                                        MyUsername.getInstance().setMagazin(true);
                                        MyUsername.getInstance().setName(username.getText().toString());
                                        Log.d("no","is magazin");
                                        Intent i=new Intent(LoginActivity.this,MagazinSeesChatListActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                                else
                                    Toast.makeText(LoginActivity.this,"Parola sau utilizatorul sunt gresite!",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    return null;
                }
            };
            runAsyncTask(task);
        }catch (Exception e){
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
}
