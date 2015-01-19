package com.vismay.android.blogreader;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;


public class BlogWebView extends ActionBarActivity {

    public String mUrl=null;
    public static final String TAG = BlogWebView.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_web_view);
        Intent intent = getIntent();
        Uri uri = intent.getData();
        mUrl=uri.toString();
        Log.d(TAG,mUrl);
        WebView webview = (WebView) findViewById(R.id.webView);
        webview.loadUrl(mUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_blog_web_view,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
      if(itemId==R.id.share){
            Intent intent = new Intent(Intent.ACTION_SEND);
          intent.setType("text/plain");
          intent.putExtra(Intent.EXTRA_TEXT,mUrl);
          startActivity(Intent.createChooser(intent,getString(R.string.share_title)));
      }
        return super.onOptionsItemSelected(item);

    }

}
