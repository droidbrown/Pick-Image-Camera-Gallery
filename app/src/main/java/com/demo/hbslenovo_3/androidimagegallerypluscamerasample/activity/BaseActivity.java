package com.demo.hbslenovo_3.androidimagegallerypluscamerasample.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.demo.hbslenovo_3.androidimagegallerypluscamerasample.utility.MarshMallowPermission;
import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by hbslenovo-3 on 6/14/2016.
 */

public class BaseActivity extends AppCompatActivity{




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*init fresco for image*/
        Fresco.initialize(this);


    }


}
