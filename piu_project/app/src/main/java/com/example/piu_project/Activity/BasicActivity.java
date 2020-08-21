package com.example.piu_project.Activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.example.piu_project.R;

public class BasicActivity extends AppCompatActivity {
    private TextView tv_title;
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        tv_title = (TextView)findViewById(R.id.toolbar_title);
        setSupportActionBar(myToolbar);
    }

    public void setToolbarTitle(String title){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(title);
            tv_title.setText(title);
            Typeface typeface = getResources().getFont(R.font.nanumsquareroundeb);
            tv_title.setTypeface(typeface);

        }
    }
}
