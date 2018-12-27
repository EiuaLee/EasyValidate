package com.eiualee.androideasyvalidate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.eiualee.androideasyvalidate.utils.ToastUtils;
import com.eiualee.easyvalidate.EasyValidate;
import com.eiualee.easyvalidate.impl.IValidate;
import com.eiualee.easyvalidate_annotations.Plan;
import com.eiualee.easyvalidate_annotations.ValidateNull;
import com.eiualee.easyvalidate_annotations.ValidateRegular;

/**
 * Created by liweihua on 2018/12/26.
 */

public class MainFragment extends Fragment {

    private IValidate IVALIDATE;

    //18位身份证号码
    public static final String REGEX_ID_CARD = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$";

    @ValidateNull(id = R.id.et_input1, toast = "输入框1为空")
    EditText et_input1;
    @ValidateNull(id = R.id.et_input2, toast = "输入框2为空", plan = Plan.A)
    EditText et_input2;
    @ValidateNull(id = R.id.et_input3, toast = "输入框3为空", plan = {Plan.A, Plan.B})
    @ValidateRegular(id = R.id.et_input3, toast = "输入框3内容不符合18位身份证", regular = REGEX_ID_CARD, plan = Plan.B)
    EditText et_input3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        bindEasyValidate(fragmentView);
        initView(fragmentView);
        initEvent(fragmentView);
        return fragmentView;
    }

    /**
     * 绑定
     * @param fragmentView
     */
    private void bindEasyValidate(View fragmentView) {
        IVALIDATE = EasyValidate.bind(this, fragmentView);
        IVALIDATE.setUnValidateListener(new IValidate.OnViewUnValidateListener() {
            @Override
            public void unValidate(int viewId, String toast) {
                ToastUtils.showLongToast(toast);
            }
        });
    }

    private void initEvent(final View fragmentView) {
        fragmentView.findViewById(R.id.btn_commit1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!IVALIDATE.isValidatePass(Plan.DEFAULT)) {
                    return;
                }
                ToastUtils.showLongToast("计划DEFAULT通过");
            }
        });

        fragmentView.findViewById(R.id.btn_commit2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!IVALIDATE.isValidatePass(Plan.A)) {
                    return;
                }
                ToastUtils.showLongToast("计划A通过");
            }
        });

        fragmentView.findViewById(R.id.btn_commit3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!IVALIDATE.isValidatePass(Plan.B)) {
                    return;
                }
                ToastUtils.showLongToast("计划B通过");
            }
        });
    }

    private void initView(View fragmentView) {
        et_input1 = fragmentView.findViewById(R.id.et_input1);
        et_input2 = fragmentView.findViewById(R.id.et_input2);
        et_input3 = fragmentView.findViewById(R.id.et_input3);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IVALIDATE.unBind();
    }
}
