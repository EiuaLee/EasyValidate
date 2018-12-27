package com.eiualee.easyvalidate.impl;

import android.view.View;

/**
 * Created by liweihua on 2018/12/26.
 */

public interface IValidate {

    boolean isEmptyValidate(int plan);//空验证是否通过
    boolean isCheckValidate(int plan);//勾选验证是否通过
    boolean isRegularValidate(int plan);//正则验证是否通过
    boolean isValidatePass(int plan); //是否验证通过
    void unBind();
    void setUnValidateListener(OnViewUnValidateListener listener);

    interface OnViewUnValidateListener{
        void unValidate(int viewId,String toast);
    }

    IValidate EMPTY = new IValidate() {
        @Override
        public void unBind() {
        }

        @Override
        public void setUnValidateListener(OnViewUnValidateListener listener) {

        }

        @Override
        public boolean isEmptyValidate(int plan) {
            return false;
        }

        @Override
        public boolean isCheckValidate(int plan) {
            return false;
        }

        @Override
        public boolean isRegularValidate(int plan) {
            return false;
        }

        @Override
        public boolean isValidatePass(int plan) {
            return isEmptyValidate(plan) && isCheckValidate(plan) && isRegularValidate(plan);
        }
    };
}
