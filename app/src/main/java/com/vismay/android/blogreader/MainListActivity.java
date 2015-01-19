package com.vismay.android.blogreader;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class MainListActivity extends ListActivity {
    public static final int NUMBER_OF_POSTS = 20;
    public static final String TAG = MainListActivity.class.getSimpleName();
    private JSONObject mBlogData = null;
    protected ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        mProgressBar=(ProgressBar)findViewById(R.id.progressBar);
//        ArrayAdapter<String > adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.android_names));
//        setListAdapter(adapter);
            if(isNetworkAvailable()) {
                mProgressBar.setVisibility(View.VISIBLE);
                GetBlogPostsTask getBlogPosts = new GetBlogPostsTask();
                getBlogPosts.execute();
            }
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String url = null;
        try {
            JSONArray jsonArray = mBlogData.getJSONArray("posts");
            JSONObject jsonObject1 = jsonArray.getJSONObject(position);
            url = jsonObject1.getString("url");
            Log.d(TAG,url);
        } catch (JSONException e) {
            Log.d(TAG,"EXception caught",e);
            e.printStackTrace();
        }

        Intent intent = new Intent(this, BlogWebView.class);
        //Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
        //Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            return true;
        }
        (Toast.makeText(this,"Network Unavailable",Toast.LENGTH_LONG)).show();
        return  false;
    }

    private class GetBlogPostsTask extends AsyncTask<Object,Void,JSONObject>
    {

        JSONObject jsonData;

        @Override
        protected JSONObject doInBackground(Object[] params) {
            int httpResponseCode = -1;
            try {
                URL url = new URL("http://blog.teamtreehouse.com/api/get_recent_summary/?count="+NUMBER_OF_POSTS);
                //URL url = new URL("http://blog.teamtreehouse.com//?count="+NUMBER_OF_POSTS);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                 httpResponseCode = connection.getResponseCode();

                Log.d(TAG,"Response Code received"+httpResponseCode);
                if(httpResponseCode == 200)
                {
                    InputStream input = connection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(input);
                    char[] charArray=new char[connection.getContentLength()];
                    reader.read(charArray);
                    String str = new String(charArray);
                    Log.d(TAG,str);
                    jsonData = new JSONObject(str);
//                    String status = jsonResponse.getString("status");
//                    Log.d(TAG,status);
//                    JSONArray jsonArray = jsonResponse.getJSONArray("posts");
//                    for(int i=0;i<jsonArray.length();i++)
//                    {
//                        JSONObject postObj = jsonArray.getJSONObject(i);
//                        String title =  postObj.getString("title");
//                        Log.d(TAG,"post"+i+title);
//                    }
                }
//
            } catch (MalformedURLException e) {
                Log.d(TAG,"Exception caught",e);
            } catch (IOException e) {
                e.printStackTrace();
            }catch(Exception e)
            {
                Log.e(TAG,"Exception caught",e);
            }
           // return "Response Code"+httpResponseCode;
            return jsonData;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mBlogData=result;
            HandleBlogResponse();
        }
    }

    private void HandleBlogResponse() {
        ArrayList<HashMap<String,String>> row = new ArrayList<HashMap<String,String>>();


        mProgressBar.setVisibility(View.INVISIBLE);
        if(mBlogData!=null){
            try {
                JSONArray jsonArray=mBlogData.getJSONArray("posts");
               // String[] list_items=new String[jsonArray.length()];

                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    String title=jsonObject.getString("title");
                    title = Html.fromHtml(title).toString();
                    String author = jsonObject.getString("author");
                    author = Html.fromHtml(author).toString();
                    HashMap<String,String> blogPost = new HashMap<>();

                    blogPost.put("title",title);
                    blogPost.put("author",author);
                    row.add(blogPost);

                }
                String[] keys = {"title","author"};
                int[] ids = {android.R.id.text1,android.R.id.text2};
                SimpleAdapter adapter=new SimpleAdapter(this,row,android.R.layout.simple_list_item_2,keys,ids);
                setListAdapter(adapter);
//                ArrayAdapter<String > adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.android_names));
//                setListAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            //TODO handle error;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.error_title));
            builder.setMessage(getString(R.string.error_message));
            builder.setPositiveButton(android.R.string.ok,null);
            AlertDialog dialog = builder.create();
            dialog.show();

            TextView emptyTextView = (TextView)getListView().getEmptyView();
            emptyTextView.setText(getString(R.string.no_items));
        }

    }

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main_list, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
