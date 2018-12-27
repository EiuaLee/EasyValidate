package com.eiualee.easyvalidate;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;


import com.eiualee.easyvalidate.impl.IValidate;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by liweihua on 2018/12/25.
 * PS:大部分代码来源于ButterKnife
 */

public final class EasyValidate {


    private static Application sApplication;
    public static WeakReference<Activity> sTopActivityWeakRef;
    static List<Activity> sActivityList = new LinkedList<>();

    private static Map<Class, Constructor<? extends IValidate>> VALIDATE = new LinkedHashMap<>();

    /**
     * 绑定Activity
     * @param target
     * @return
     */
    public final static IValidate bind(Activity target) {
        return createValidate(target, null);
    }

    /**
     * 绑定Dialog
     * @param target
     * @return
     */
    public final static IValidate bind(Dialog target) {
        return createValidate(target, target.getWindow().getDecorView());
    }

    /**
     * 绑定Fragment等等?
     * @param target
     * @return
     */
    public final static IValidate bind(Object target, View source) {
        return createValidate(target, source);
    }

    public final static IValidate createValidate(@NonNull Object target, @NonNull View source) {

        if(getApp() == null){
            throw new NullPointerException("u should init first");
        }

        Class<?> targetClass = target.getClass();
        Constructor<? extends IValidate> constructor = findValidateConstructorForClass(targetClass);

        if (constructor == null) {
            return IValidate.EMPTY;
        }
        //noinspection TryWithIdenticalCatches Resolves to API 19+ only type.
        try {
            return constructor.newInstance(target, source);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create binding instance.", cause);
        }
    }


    @Nullable
    @CheckResult
    @UiThread
    private final static Constructor<? extends IValidate> findValidateConstructorForClass(Class<?> cls) {
        Constructor<? extends IValidate> validateCor = VALIDATE.get(cls);
        if (validateCor != null) {
            return validateCor;
        }
        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            return null;
        }
        try {
            Class<?> validateClass = Class.forName(clsName + "_Validate");
            //noinspection unchecked
            validateCor = (Constructor<? extends IValidate>) validateClass.getConstructor(cls, View.class);
        } catch (ClassNotFoundException e) {
            validateCor = findValidateConstructorForClass(cls.getSuperclass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find validate constructor for " + clsName, e);
        }
        VALIDATE.put(cls, validateCor);
        return validateCor;
    }

    /**
     * 获取文本
     * @param view
     * @return
     */
    public final static String getText(View view) {
        if (view != null && view instanceof TextView) {
            return ((TextView) view).getText().toString().trim();
        }
        return "";
    }

    public final static boolean isMatch(final String regex, final CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }


    private final static Application.ActivityLifecycleCallbacks mCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            sActivityList.add(activity);
            setTopActivityWeakRef(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            setTopActivityWeakRef(activity);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            setTopActivityWeakRef(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            sActivityList.remove(activity);
        }
    };

    private EasyValidate() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param app 应用
     */
    public final static void init(@NonNull final Application app) {
        sApplication = app;
        app.registerActivityLifecycleCallbacks(mCallbacks);
    }

    /**
     * 获取Application
     *
     * @return Application
     */
    public final static Application getApp() {
        if (sApplication != null) return sApplication;
        throw new NullPointerException("u should init first");
    }

    private final static void setTopActivityWeakRef(Activity activity) {
        if (sTopActivityWeakRef == null || !activity.equals(sTopActivityWeakRef.get())) {
            sTopActivityWeakRef = new WeakReference<>(activity);
        }
    }

}
