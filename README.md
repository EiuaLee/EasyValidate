# EasyValidate
* 使用方法，在module的build.gradle中添加
```
    implementation 'com.eiualee:easyvalidate:1.0.0'
    annotationProcessor 'com.eiualee:easyvalidate-compiler:1.0.0'
```
>一、用法
---
`EasyValidate`提供了3种注解验证，注：Plan字段等下再说

* ① `ValidateNull (控件空判断，当控件为空时，提示toast中填写的内容)`
 
```
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface ValidateNull {

    int id();//控件ID

    String toast();//不合法时提示的内容

    int[] plan() default {Plan.DEFAULT};//校验计划
}
```
* ②`ValidateCheck (判断控件是否选中状态, 当控件选中的状态与validateState字段的值相同时会提示toast中的内容)`

```
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface ValidateCheck {

    int id();//控件ID

    String toast();//不合法时提示的内容

    int[] plan() default {Plan.DEFAULT};//校验计划

    boolean validateState() default false;//勾选的值不能与此相同,相同的话提示错误
}
```
* ③`ValidateRegular(判断控件内容是否符合正则表达式)`
```
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface ValidateRegular {

    int id();//控件ID

    String toast();//不合法时提示的内容

    int[] plan() default {Plan.DEFAULT};//校验计划

    String regular();

}
```
>当了解完上面3中注解后，我们就可以开始愉快的编程了。试着在控件上面这样子使用，噢不，先得调用一个方法，使用与`Butternife`一致，毕竟是基于它写出来的。以下为初始化时调用的代码：
* Activity：
```
 IValidate IVALIDATE = EasyValidate.bind(this);
 IVALIDATE.setUnValidateListener(new IValidate.OnViewUnValidateListener() {
        //失败时的回调（viewid：验证失败View的id,toast：注解上的内容）
        @Override
        public void unValidate(int viewId, String toast) {        
             ToastUtils.showLongToast(toast);
        }
 });
```
&emsp;&emsp;调用`EasyVlidate.bind();`方法并返回一个`IValidate`,用`IValidate`实现
一个接口。这个接口主用于校验失败时回调，毕竟失败时不一定都是`Toast`内容是吧！这样子便于拓展。
* Fragment
```
IValidate  IVALIDATE = EasyValidate.bind(this, fragmentView);
IVALIDATE.setUnValidateListener(new IValidate.OnViewUnValidateListener() {
       //失败时的回调（viewid：验证失败View的id,toast：注解上的内容）
       @Override
       public void unValidate(int viewId, String toast) {
            ToastUtils.showLongToast(toast);
       }
});
```
与Activity的使用方法差不多，只是`EasyValidate.bind(this, fragmentView);`需要变化一下
* 释放资源
```
 IVALIDATE.unBind();
```
> 现在为注解使用事项
* 注解的使用
```
@ValidateNull(id = R.id.et_input1, toast = "输入框1为空")
EditText et_input1;
@ValidateCheck(id = R.id.cb_check. toast = "请勾选xxxx注意事项后重新提交")
CheckBox cb_check;
//18位身份证号码
public static final String REGEX_ID_CARD = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$";
@ValidateRegular(id = R.id.et_input3, toast = "输入框3内容不符合18位身份证", regular = REGEX_ID_CARD, plan = Plan.B)
EditText et_input3;
```
* 调用验证的方法
```
if(!IVALIDATE.isValidatePass(Plan.DEFAULT)){
     //Todo 验证不通过
     return;
}
```
以上就是绑定界面、使用注解、开始验证、解绑界面一整套的流程了，是不是很简单。。。哦对了，在上面调用验证方法是会有一个`Plan.DEFAULT`这个是干嘛的呢？请接着看。
>Plan的使用（注解中默认的Plan为DEFAULT）
* 当我们在开发的时候。假设会有以下这么一种需求：
 界面有4个输入框，分别为 `手机号码` `验证码` `用户名` `密码`
①当用户输入`手机号码`时，只要`验证码`不为空就可以请求登录接口了。
②当用户输入`用户名`时,只要`密码`不为空就可以请求登录接口了。
那我们要怎么做呢？这下子就会用到Plan这个字段了，请看代码
```
@ValidateNull(id = R.id.et_phoneNo,toast = "手机号码不能为空",plan = Plan.A)
EditText et_phoneNo;
@ValidateNull(id = R.id.et_checkNo,toast = "手机验证码不能为空",plan = Plan.A)
EditText et_checkNo;
@ValidateNull(id = R.id.et_userName,toast = "手机用户名不能为空",plan = Plan.B)
EditText et_userName;
@ValidateNull(id = R.id.et_pw,toast = "手机密码不能为空",plan = Plan.B)
EditText et_pw;
```
在调用时分别传入Plan即可
```
if(!IVALIDATE.isValidatePass(Plan.A)){
     //Todo 验证不通过
     return;
}
```
```
if(!IVALIDATE.isValidatePass(Plan.B)){
     //Todo 验证不通过
     return;
}
```
* 那当我需求中的判断都需要用到这个控件去判断可咋办呢？
```
@ValidateNull(id = R.id.et_pw,toast = "手机密码不能为空",plan = {Plan.A, Plan.B})
EditText et_pw;
```
`plan = {Plan.A, Plan.B}`就这么简单，我既参加计划A的校验，也参加计划B的校验，这下可没毛病了吧！
>使用的注意事项
* `组件化`开发时要配合Butternife使用，我懒得去生成R2文件了，毕竟重复造轮子没意义是吧。
>简书地址:<https://www.jianshu.com/p/311ab5be9e2e>
