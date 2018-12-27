package com.eiualee.androideasyvalidate;

import android.app.Application;

import com.eiualee.easyvalidate.EasyValidate;

/**
 * Created by liweihua on 2018/12/26.
 */

public class EVApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EasyValidate.init(this);
    }
}
