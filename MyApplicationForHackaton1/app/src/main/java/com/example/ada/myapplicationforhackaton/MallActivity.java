package com.example.ada.myapplicationforhackaton;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ada.myapplicationforhackaton.entities.Article;
import com.example.ada.myapplicationforhackaton.entities.Magazin;
import com.example.ada.myapplicationforhackaton.entities.NewsResponse;
import com.example.ada.myapplicationforhackaton.entities.ToDoItem;
import com.example.ada.myapplicationforhackaton.entities.User;
import com.example.ada.myapplicationforhackaton.service.NewsService;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MallActivity extends AppCompatActivity {
    private Button btnSearch,tdbtn;
    private EditText magazinSearch;
    private ListView list;
    private MobileServiceClient mClient;
    private MobileServiceTable<Magazin> mToDoTable;
    private ArrayAdapter<Article> adapter;
    private static boolean gasit=false;
    private static Magazin instance=null;
    private NewsService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall);
        btnSearch=(Button)findViewById(R.id.btnsearchmagazine);
        magazinSearch=(EditText)findViewById(R.id.searchmagazineet);
        tdbtn=(Button)findViewById(R.id.tdbtn);
        list=(ListView) findViewById(R.id.listOFNEWS);
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

            mToDoTable = mClient.getTable("magazine",Magazin.class);


        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error");
        } catch (Exception e){
            createAndShowDialog(e, "Error");
        }

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String magazin=magazinSearch.getText().toString();
                findMagazinInDatabase(magazin);
            }
        });

        adapter=new ArrayAdapter<Article>(this, android.R.layout.simple_list_item_1);
        list.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://newsapi.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service=retrofit.create(NewsService.class);

        Call<NewsResponse> news=service.getNews();
        news.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                List<Article> articles=response.body().getArticles();
                adapter.clear();
                for (Article a:articles)
                    adapter.add(a);
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Log.e("MAIN","ERROR",t);
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article=adapter.getItem(position);
                Intent i=new Intent(MallActivity.this,NewsViewActivity.class);
                i.putExtra("url",article.getUrl());
                startActivity(i);
            }
        });
        tdbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MallActivity.this, ToDoActivity.class);
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

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Log.d("lala","lala");
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null);
        } else {
            Log.d("lala33","lala33");
            return task.execute();
        }
    }

    private void findMagazinInDatabase(String mag){
        final String nume=mag;
        Log.d("hello1","hello1");
        try{
            AsyncTask<Void, Void, Void> task2 = new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    Log.d("dzvvvvvlasdddddla","LALFdssssssssDZ");
                    try {
                        Log.d("dzvvvvvlala","LALFDZ");
                        List<Magazin> result = mToDoTable
                                .where()
                                .field("nume").eq(nume)
                                .execute()
                                .get();
                        if(result.isEmpty())
                        {
                            gasit=false;
                        }
                        else
                        {
                            gasit=true;
                            instance=result.get(0);
                        }
                    } catch (final Exception e) {
                        createAndShowDialogFromTask(e, "Error!");
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(gasit==false&&instance==null)
                                Toast.makeText(MallActivity.this,"Magazinul nu exista!",Toast.LENGTH_LONG).show();
                            else
                            {
                                Intent i=new Intent(MallActivity.this,UserSeesMagazinActivity.class);
                                i.putExtra("instanta",instance);
                                startActivity(i);
                                instance=null;
                            }
                        }
                    });
                    return null;
                }
            };
            runAsyncTask(task2);
            //task2.execute();

        }catch (Exception e){
            createAndShowDialogFromTask(e, "Error!");
        }
    }
}