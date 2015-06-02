package com.limeng.xinlangweibo.db;

public class DBInfo {
    
    public static class DB {
        
        /**
         * 数据库名称
         */
        public static final String DB_NAME = "crazyit_weibo.db";
        
        /**
         * 数据库版本
         */
        public static final int VERSION = 1;
    }
    
    public static class Table {
        
        /**
         * 用户表名称
         */
        public static final String USER_TABLE = "userinfo";
        
        /**
         * 主键
         */
        public static final String _ID = "_id";
        
        /**
         * 用户编号
         */
        public static final String USER_ID = "user_id";
        
        /**
         * 用户名称
         */
        public static final String USER_NAME = "user_name";
        
        /**
         * token
         */
        public static final String TOKEN = "token";
        
        /**
         * token_secret
         */
        public static final String TOKEN_SECRET = "token_secret";
        
        /**
         * 用户的描述
         */
        public static final String DESCRIPTION = "description";
        
        /**
         * 用户头像
         */
        public static final String USER_HEAD = "user_head";
        
        /**
         * 创建用户表语
         */
        public static final String CREATE_USER_TABLE = "create table if not exists " + USER_TABLE + "(" + _ID
                + " integer primary key autoincrement, " + USER_ID + " text, " + USER_NAME + " text, " + TOKEN
                + " text, " + TOKEN_SECRET + " text, " + DESCRIPTION + " text, " + USER_HEAD + " BLOB);";
        
        /**
         * 删除用户表空间语
         */
        public static final String DROP_USER_TABLE = "drop table " + USER_TABLE;
        
    }
    
}
