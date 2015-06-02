package com.limeng.xinlangweibo.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.limeng.xinlangweibo.db.DBHelper;
import com.limeng.xinlangweibo.db.DBInfo;
import com.limeng.xinlangweibo.pojo.User;

public class UserDao {
    
    private DBHelper dbHelper = null;
    
    private SQLiteDatabase db = null;
    
    public UserDao(Context context) {
        dbHelper = new DBHelper(context);
    }
    
    /**
     * 新增用户
     * 
     * @return
     */
    public long insertUser(User user) {
        db = dbHelper.getWritableDatabase();
        // 数据绑定
        ContentValues values = new ContentValues();
        values.put(DBInfo.Table.USER_ID, user.getUser_id());
        values.put(DBInfo.Table.USER_NAME, user.getUser_name());
        values.put(DBInfo.Table.TOKEN, user.getToken());
        values.put(DBInfo.Table.TOKEN_SECRET, user.getToken_secret());
        values.put(DBInfo.Table.DESCRIPTION, user.getDescription());
        // 处理头像将Drawable类型文件存储成字节码
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BitmapDrawable newHead = (BitmapDrawable) user.getUser_head();
        newHead.getBitmap().compress(CompressFormat.PNG, 100, os);
        values.put(DBInfo.Table.USER_HEAD, os.toByteArray());
        long rowId = db.insert(DBInfo.Table.USER_TABLE, DBInfo.Table.USER_NAME, values);
        db.close();
        return rowId;
        
    }
    
    public int updateUser(User user) {
        return 1;
    }
    
    public int deleteUser(String user_id) {
        return 1;
    }
    
    public User findUserById(String user_id) {
        return null;
    }
    
    String[] columns = { DBInfo.Table._ID, DBInfo.Table.USER_ID, DBInfo.Table.USER_NAME, DBInfo.Table.TOKEN,
            DBInfo.Table.TOKEN_SECRET, DBInfo.Table.DESCRIPTION, DBInfo.Table.USER_HEAD };
    
    public List<User> findAllUsers() {
        db = dbHelper.getReadableDatabase();
        List<User> userList = new ArrayList<User>();
        User user = null;
        Cursor cursor = db.query(DBInfo.Table.USER_TABLE, columns, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                user = new User();
                
                user.setId(cursor.getLong(cursor.getColumnIndex(DBInfo.Table._ID)));
                user.setUser_id(cursor.getString(cursor.getColumnIndex(DBInfo.Table.USER_ID)));
                user.setUser_name((cursor.getString(cursor.getColumnIndex(DBInfo.Table.USER_NAME))));
                user.setToken((cursor.getString(cursor.getColumnIndex(DBInfo.Table.TOKEN))));
                user.setToken_secret((cursor.getString(cursor.getColumnIndex(DBInfo.Table.TOKEN_SECRET))));
                user.setDescription((cursor.getString(cursor.getColumnIndex(DBInfo.Table.DESCRIPTION))));
                
                byte[] byteHead = cursor.getBlob(cursor.getColumnIndex(DBInfo.Table.USER_HEAD));
                
                ByteArrayInputStream is = new ByteArrayInputStream(byteHead);
                Drawable userHead = Drawable.createFromStream(is, "image");
                user.setUser_head(userHead);
                
                Log.d("user", user.toString());
                userList.add(user);
                
            }
        }
        
        cursor.close();
        db.close();
        return userList;
    }
}
