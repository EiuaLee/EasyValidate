package com.eiualee.easyvalidate_compiler.utils;

/**
 * Created by liweihua on 2018/12/26.
 */

public interface C {

    String IVALIDATE = "com.eiualee.easyvalidate.impl.IValidate";
    String LISTENER = "com.eiualee.easyvalidate.impl.IValidate.OnViewUnValidateListener";

    String EASY_VALIDATE = "com.eiualee.easyvalidate.EasyValidate";
    String ACTIVITY = "android.app.Activity";
    String TEXTUTILS = "android.text.TextUtils";
    String VIEW = "android.view.View";
    String UITHREAD = "android.support.annotation.UiThread";



    //自动编译的代码
    String VALIDATE_CHECK_CODE = "     if (target.$N != null?$Ltarget.$N.isChecked():$L(($T)sourse.findViewById($L)).isChecked()){" +
            "\n       if(listener == null)return false;" +
            "\n       listener.unValidate($L,$S);" +
            "\n       return false;" +
            "\n     }\n";

    String VALIDATE_NULL_CODE =  "     if ($T.isEmpty($T.getText(target.$N != null?target.$N:(($T)sourse.findViewById($L))))){" +
            "\n       if(listener == null)return false;" +
            "\n       listener.unValidate($L,$S);" +
            "\n       return false;" +
            "\n     }\n";

    String VALIDATE_REGULAR_CODE = "     if(!$T.isMatch($S,$T.getText(target.$N != null?target.$N:($T)sourse.findViewById($L)))){" +
            "\n       if(listener == null)return false;" +
            "\n       listener.unValidate($L,$S);" +
            "\n       return false;" +
            "\n     }\n";
}
