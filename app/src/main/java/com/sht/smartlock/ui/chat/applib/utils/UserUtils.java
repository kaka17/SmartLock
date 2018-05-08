package com.sht.smartlock.ui.chat.applib.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sht.smartlock.R;
import com.sht.smartlock.api.base.HttpCallBack;
import com.sht.smartlock.api.base.HttpClient;
import com.sht.smartlock.api.base.ResponseBean;
import com.sht.smartlock.base.BaseApplication;
import com.sht.smartlock.helper.MySQLiteOpenHelper;
import com.sht.smartlock.helper.PwdGenHelper;
import com.sht.smartlock.ui.chat.applib.controller.HXSDKHelper;
import com.sht.smartlock.ui.chat.applib.domain.User;
import com.sht.smartlock.ui.chat.applib.uidemo.DemoHXSDKHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class UserUtils {

    private static List<Integer> list = new ArrayList<>();
    private static List<String> list_NickName = new ArrayList<>();
    private static int n = -1;
    private static MySQLiteOpenHelper mydbHelper = null;

    //数据库
    private static MySQLiteOpenHelper getMydbHelper(Context context) {
        if (mydbHelper == null) {
            mydbHelper = new MySQLiteOpenHelper(context);
        }
        return mydbHelper;
    }

    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     *
     * @param username
     * @return
     */
    public static User getUserInfo(String username) {
        User user = null;
        try {
            user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(username);
        }catch (Exception e){
        }
        if (user == null) {
            user = new User(username);
        }

        if (user != null) {
            //demo没有这些数据，临时填充
            if (TextUtils.isEmpty(user.getNick()))
                user.setNick(username);
        }
        return user;
    }

    /**
     * 设置用户头像
     *
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        User user = getUserInfo(username);
        int img_Num = -1;
        list.clear();
        list.add(R.drawable.hend_01);
        list.add(R.drawable.hend_02);
        list.add(R.drawable.hend_03);
        list.add(R.drawable.hend_04);
        list.add(R.drawable.hend_05);
        list.add(R.drawable.hend_06);
        list.add(R.drawable.hend_07);
        list.add(R.drawable.hend_08);
        list.add(R.drawable.hend_09);
        list.add(R.drawable.hend_10);

        list_NickName.clear();
        list_NickName.add("游客");
        list_NickName.add("游客");
        list_NickName.add("游客");
        list_NickName.add("游客");
        list_NickName.add("游客");
        list_NickName.add("游客");
        list_NickName.add("游客");
        list_NickName.add("游客");
        list_NickName.add("游客");
        list_NickName.add("游客");
        String sqlString = "select * from huanxinuser where username=?";
        int count = getMydbHelper(context).selectCount(
                sqlString,
                new String[]{username});
        if (count > 0) {//用户已存在已存在
            List<Map<String, Object>> list = getMydbHelper(context).selectList(sqlString, new String[]{username});
            img_Num=Integer.parseInt(String.valueOf(list.get(0).get("img_Num")));
        } else {
            // 先插入数据再查询数据
            String a = String.valueOf((int) (Math.random() * 10));//存图片
            String b = String.valueOf((int) (Math.random() * 100));//随机获取nickName
            //随机生成环信密码
            PwdGenHelper pwdGenHelper=new PwdGenHelper();
            b=pwdGenHelper.getpwd(4,true,true,true);
            n = Integer.parseInt(a);
            String sql = "insert into huanxinuser (username ,nick,img_Num) values (?,?,?)";
            boolean flag = getMydbHelper(context).execData(
                    sql,
                    new Object[]{username, list_NickName.get(n)+b, n});
            //BaseApplication.toast(flag+"数据存储");
        }

        //
        if (user != null && user.getAvatar() != null) {
            if (img_Num != -1) {//从数据库里找到数据设置上去
                Picasso.with(context).load(user.getAvatar()).placeholder(list.get(img_Num)).into(imageView);
            } else {    //通过随机数设置上去
                Picasso.with(context).load(user.getAvatar()).placeholder(list.get(n)).into(imageView);
            }
//			Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
        } else {
            if (img_Num != -1) {//从数据库里找到数据设置上去
                Picasso.with(context).load(user.getAvatar()).placeholder(list.get(img_Num)).into(imageView);
            } else {    //通过随机数设置上去
                Picasso.with(context).load(user.getAvatar()).placeholder(list.get(n)).into(imageView);
            }
        }
    }

    /**
     * 设置当前用户头像
     */
    public static void setCurrentUserAvatar(Context context, ImageView imageView) {
        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
        int img_Num = -1;
        if (list.size()>0){
        }else {
            list.clear();
            list.add(R.drawable.hend_01);
            list.add(R.drawable.hend_02);
            list.add(R.drawable.hend_03);
            list.add(R.drawable.hend_04);
            list.add(R.drawable.hend_05);
            list.add(R.drawable.hend_06);
            list.add(R.drawable.hend_07);
            list.add(R.drawable.hend_08);
            list.add(R.drawable.hend_09);
            list.add(R.drawable.hend_10);

            list_NickName.clear();
            list_NickName.add("游客");
            list_NickName.add("游客");
            list_NickName.add("游客");
            list_NickName.add("游客");
            list_NickName.add("游客");
            list_NickName.add("游客");
            list_NickName.add("游客");
            list_NickName.add("游客");
            list_NickName.add("游客");
            list_NickName.add("游客");
        }
        String myuserName= (String) imageView.getTag();
        if (myuserName!=null&&!myuserName.equals("null")){
            String sqlString = "select * from huanxinuser where username=?";
            int count = getMydbHelper(context).selectCount(
                    sqlString,
                    new String[]{myuserName});
            if (count > 0) {//用户已存在已存在
                List<Map<String, Object>>  list = getMydbHelper(context).selectList(sqlString, new String[]{myuserName});
                img_Num=Integer.parseInt(String.valueOf(list.get(0).get("img_Num")));
            } else {
                // 先插入数据再查询数据
                String a = String.valueOf((int) (Math.random() * 10));//存图片
                String b = String.valueOf((int) (Math.random() * 100));//随机获取nickName
                //随机生成环信密码
                PwdGenHelper pwdGenHelper=new PwdGenHelper();
                b=pwdGenHelper.getpwd(4,true,true,true);
                n = Integer.parseInt(a);
                String sql = "insert into huanxinuser (username ,nick,img_Num) values (?,?,?)";
//                BaseApplication.toast(n+"插入");
                boolean flag = getMydbHelper(context).execData(
                        sql,
                        new Object[]{myuserName, list_NickName.get(n) + b, n});
                //BaseApplication.toast(flag+"数据存储");
            }
        }

        if (user != null && user.getAvatar() != null) {
            if (img_Num!=-1) {
                Picasso.with(context).load(user.getAvatar()).placeholder(list.get(img_Num)).into(imageView);
            }else {
                Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.hend_01).into(imageView);
            }
        } else {
            if (img_Num!=-1) {
                Picasso.with(context).load(user.getAvatar()).placeholder(list.get(img_Num)).into(imageView);
            }else {
                Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.hend_01).into(imageView);
            }
        }
    }

    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username, TextView textView) {
        User user = getUserInfo(username);
        String nickName="";
        if (mydbHelper!=null) {
            String sqlString = "select * from huanxinuser where username=?";
            int count = mydbHelper.selectCount(
                    sqlString,
                    new String[]{username});
            if (count > 0) {//用户已存在已存在
                List<Map<String, Object>> list = mydbHelper.selectList(sqlString, new String[]{username});
                //
                String name = (String) textView.getTag();
                if (name!=null&&!name.equals("null")){//更改为好友的真实昵称
                    String sqlUpda="update huanxinuser set nick=? where username='"+username+"'";
                    boolean isfale=mydbHelper.updateData(sqlUpda,new String[] {name});
                }
                //
                nickName= (String) list.get(0).get("nick");
                Log.e("TAG", "-------->" + "2222");
                if (nickName.contains("小二")||nickName.contains("客服")||nickName.contains("前台")){
                //已经存在
                }else {
                    //没有存在
                     getNickName(username,nickName,n+"");
                }
            } else {
                // 先插入数据再查询数据
                String a = String.valueOf((int) (Math.random() * 10));
                String b = String.valueOf((int) (Math.random() * 100));//随机获取nickName
                //随机数
                PwdGenHelper pwdGenHelper=new PwdGenHelper();
                b=pwdGenHelper.getpwd(4, true, true, true);
                n = Integer.parseInt(a);
                String name = (String) textView.getTag();
                String saveName="";
                if (name!=null&&!name.equals("null")){
                    saveName=name;
                }else {
                    if(user != null&& !user.getNick().equals(user.getUsername())){
                        saveName=user.getNick();//真实昵称
                    }else {
                        saveName=list_NickName.get(n)+b;//随机昵称
                    }

                }
                String sql = "insert into huanxinuser (username ,nick,img_Num) values (?,?,?)";
                boolean flag = mydbHelper.execData(
                        sql,
                        new Object[]{username, saveName, n});
                //				BaseApplication.toast(flag+"数据存储");
                Log.e("TAG","-------->"+"111");
                getNickName(username, saveName, n + "");

            }
        }
        if (user != null) {
            Log.e("UseNickName","---------->"+user.getNick());
            if (!nickName.equals("")){
                if (username.equals("admin")||username.equals("service")){
                    textView.setText("本宝宝");
                }else {
                     textView.setText(nickName);
                    if (!user.getNick().equals(user.getUsername())){
                        textView.setText(user.getNick());
                    }
                }
//           textView.setText(user.getNick());
            }else {
                if (username.equals("admin")||username.equals("service")){
                    textView.setText("本宝宝");
                }else {
                    textView.setText(user.getNick());
                }
            }
        } else {
            if (!nickName.equals("")){

                if (username.equals("admin")||username.equals("service")){
                    textView.setText("本宝宝");
                }else {
                    textView.setText(nickName);
                }
//           textView.setText(user.getNick());
            }else {
                if (username.equals("admin")||username.equals("service")){
                    textView.setText("本宝宝");
                }else {
                    textView.setText(username);
                }

            }
        }
    }

    /**
     * 设置当前用户昵称
     */
    public static void setCurrentUserNick(TextView textView) {
        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
        if (textView != null) {
            textView.setText(user.getNick());
        }
    }

    /**
     * 保存或更新某个用户
     *
     * @param newUser
     */
    public static void saveUserInfo(User newUser) {
        if (newUser == null || newUser.getUsername() == null) {
            return;
        }
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveContact(newUser);
    }

    private static void getNickName(final String username, final String saveName, final String n){
        StringBuffer sb = new StringBuffer();//转换成小写，账号只支持小写
		if(username!=null){
			for(int i=0;i<username.length();i++){
				char c = username.charAt(i);
				if(Character.isUpperCase(c)){
					sb.append(Character.toLowerCase(c));
				}else{
					sb.append(c);
				}
			}
		}
       final String  myusername=sb.toString();
        HttpClient.instance().is_Receptionist(myusername, new HttpCallBack() {
            @Override
            public void onSuccess(ResponseBean responseBean) {
                try {
                    JSONObject jsonObject=new JSONObject(responseBean.toString());
                    String result = jsonObject.getString("result");
                    Log.e("USER","----------->username=="+username+"myusername=="+myusername+"==="+responseBean.toString());
                    if (result.equals(myusername)){
                        //不是店小二不处理
                        String sql = "insert into huanxinuser (username ,nick,img_Num) values (?,?,?)";
                        Log.e("TAG","-------->"+"true");

                        boolean flag = mydbHelper.execData(
                                sql,
                                new Object[]{username, saveName, n});
                    }else {
                        Log.e("TAG","-------->"+"false");

                        String sqlString = "select * from huanxinuser where username=?";
                        int count = mydbHelper.selectCount(
                                sqlString,
                                new String[]{myusername});
                        if (count > 0) {//用户已存在已存在，更改数据库
                            List<Map<String, Object>> list = mydbHelper.selectList(sqlString, new String[]{myusername});
                            //
                            Log.e("TAG","-------->"+"count>0");

                            if (result != null && !result.equals("null")) {//更改为好友的真实昵称
                                String sqlUpda = "update huanxinuser set nick=? where username='" + myusername + "'";
                                boolean isfale = mydbHelper.updateData(sqlUpda, new String[]{result});
                            }
                        }else {//存入数据库
                            String sql = "insert into huanxinuser (username ,nick,img_Num) values (?,?,?)";
                            boolean flag = mydbHelper.execData(
                                    sql,
                                    new Object[]{myusername, result, 0});
                            Log.e("TAG","-------->"+"count<0"+flag);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("TAG", "-------->" + "JSONException" + e.toString());
                }
            }
        });
    }

}
