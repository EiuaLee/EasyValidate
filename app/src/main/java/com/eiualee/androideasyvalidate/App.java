package com.eiualee.androideasyvalidate;

import android.app.Application;

import com.eiualee.androideasyvalidate.utils.Utils;

/**
 * Created by liweihua on 2018/12/27.
 */

public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
