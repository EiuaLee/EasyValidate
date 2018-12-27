package com.eiualee.androideasyvalidate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.eiualee.androideasyvalidate.utils.ToastUtils;
import com.eiualee.easyvalidate.EasyValidate;
import com.eiualee.easyvalidate.impl.IValidate;
import com.eiualee.easyvalidate_annotations.Plan;
import com.eiualee.easyvalidate_annotations.ValidateNull;

public class MainActivity extends AppCompatActivity {

    private IValidate IVALIDATE;

    @ValidateNull(id = R.id.et_activityInput,toast = "Activity中的输入框为空")
    EditText et_activityInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindEasyValidate();

        et_activityInput = findViewById(R.id.et_activityInput);

        findViewById(R.id.btn_validate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!IVALIDATE.isValidatePass(Plan.DEFAULT)){
                    return;
                }
                ToastUtils.showLongToast("验证通过");
            }
        });

        getSupportFragmentManager().beginTransaction().add(R.id.rlay_fragmentReplace,new MainFragment()).commitAllowingStateLoss();
    }

    /**
     * 绑定
     */
    private void bindEasyValidate() {
        IVALIDATE = EasyValidate.bind(this);
        IVALIDATE.setUnValidateListener(new IValidate.OnViewUnValidateListener() {
            @Override
            public void unValidate(int viewId, String toast) {
                ToastUtils.showLongToast(toast);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IVALIDATE.unBind();
    }
}
