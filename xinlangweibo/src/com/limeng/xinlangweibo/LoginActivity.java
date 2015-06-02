package com.limeng.xinlangweibo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.limeng.xinlangweibo.dao.UserDao;
import com.limeng.xinlangweibo.pojo.User;

public class LoginActivity extends Activity implements OnClickListener {
    ImageView user_head;
    ImageView adduser;
    TextView user_slogans;
    Spinner spinner_user;
    Button login;
    Button reset;
    List<User> userData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        user_head = (ImageView) findViewById(R.id.user_head);
        adduser = (ImageView) findViewById(R.id.adduser);
        user_slogans = (TextView) findViewById(R.id.user_slogans);

        spinner_user = (Spinner) findViewById(R.id.auto_user);

        login = (Button) findViewById(R.id.login);
        reset = (Button) findViewById(R.id.reset);
        login.setOnClickListener(this);
        reset.setOnClickListener(this);
        adduser.setOnClickListener(this);
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.login:
            startActivity(new Intent(LoginActivity.this,FragmentTabs.class));
            finish();
            break;
        case R.id.reset:
            Toast.makeText(this, "取消", Toast.LENGTH_LONG).show();
            break;
        case R.id.adduser:
            startActivity(new Intent(this, OAuthActivity.class));
            break;
        }
    }

    public void init() {
        UserDao dao = new UserDao(this);
        userData = dao.findAllUsers();
       
        if (userData.isEmpty()) {
            Intent intent = new Intent(this, OAuthActivity.class);
            startActivity(intent);
            finish();
        } else {
            List<HashMap<String, Object>> userList = new ArrayList<HashMap<String, Object>>();
            for (User u : userData) {
                HashMap<String, Object> userMap = new HashMap<String, Object>();
                userMap.put("name", u.getUser_name());
                userList.add(userMap);
            }
            spinner_user.setAdapter(new SimpleAdapter(this, userList,
                    R.layout.login_user_item,
                    new String[] {"name" }, new int[] {R.id.username }));

            
            spinner_user.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                        int position, long id) {
                    User u = userData.get(position);
                    user_head.setImageDrawable(u.getUser_head());
                    user_slogans.setText(u.getDescription());
                    //保存当前登录的用户
                    UserSession.nowUser = u;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Toast.makeText(LoginActivity.this, "ttt", Toast.LENGTH_LONG).show();
                } 
            });
        }
    }
    
    /**
     * 保存当前登录的用户
     * @author hanfei.li
     *
     */
    public static class UserSession{
        public static User nowUser;
    }

}
