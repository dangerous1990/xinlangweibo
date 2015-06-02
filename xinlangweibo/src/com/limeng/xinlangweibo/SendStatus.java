package com.limeng.xinlangweibo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.limeng.xinlangweibo.share.util.InfoHelper;
import com.limeng.xinlangweibo.share.util.StringUtils;
import com.limeng.xinlangweibo.util.AccessTokenKeeper;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SendStatus extends Activity {
    
    private static final String TAG = "SendStatus";
    
    private Button share_button;
    
    private Button tv_back;
    
    private ImageButton imgChooseBtn;
    
    private ImageView imgView;
    
    private TextView wordCounterTextView;
    
    private EditText contentEditText;
    
    private ProgressDialog dialog;
    
    private Bitmap myBitmap;
    
    private byte[] mContent;
    
    private String imagePath;
    
    private static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0;
    
    private static final int REQUEST_CODE_GETIMAGE_BYCAMERA = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_main);
        share_button = (Button) findViewById(R.id.btn_share);
        imgChooseBtn = (ImageButton) findViewById(R.id.share_imagechoose);
        imgView = (ImageView) findViewById(R.id.share_image);
        wordCounterTextView = (TextView) findViewById(R.id.share_word_counter);
        contentEditText = (EditText) findViewById(R.id.share_content);
        tv_back = (Button) findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 分享微博dialog
        dialog = new ProgressDialog(this);
        dialog.setMessage("分享中...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        share_button.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                if (!InfoHelper.checkNetWork(SendStatus.this)) {
                    Toast.makeText(SendStatus.this, "网络连接失败，请检查网络设置！", Toast.LENGTH_LONG).show();
                } else {
                    if (isChecked()) {
                        dialog.show();
                        if (imagePath != null) {
                            upload(contentEditText.getText().toString(), imagePath != null ? imagePath : "");
                        } else {
                            Log.d(TAG, contentEditText.getText().toString());
                            upload(contentEditText.getText().toString());
                        }
                        
                    }
                }
            }
        });
        // 选择图片
        imgChooseBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                CharSequence[] items = { "手机相册", "手机拍照", "清除照片" };
                imageChooseItem(items);
            }
        });
        
        // 侦听EditText字数改变
        TextWatcher watcher = new TextWatcher() {
            
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textCountSet();
            }
            
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textCountSet();
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                textCountSet();
            }
        };
        contentEditText.addTextChangedListener(watcher);
        
        imgView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GETIMAGE_BYSDCARD);
            }
        });
        
    }
    
    /**
     * 设置稿件字数
     */
    private void textCountSet() {
        String textContent = contentEditText.getText().toString();
        int currentLength = textContent.length();
        if (currentLength <= 140) {
            wordCounterTextView.setTextColor(Color.BLACK);
            wordCounterTextView.setText(String.valueOf(textContent.length()));
        } else {
            wordCounterTextView.setTextColor(Color.RED);
            wordCounterTextView.setText(String.valueOf(140 - currentLength));
        }
    }
    
    /**
     * 数据合法性判断
     * 
     * @return
     */
    private boolean isChecked() {
        boolean ret = true;
        if (StringUtils.isBlank(contentEditText.getText().toString())) {
            Toast.makeText(this, "说点什么吧", Toast.LENGTH_SHORT).show();
            ret = false;
        } else if (contentEditText.getText().toString().length() > 140) {
            int currentLength = contentEditText.getText().toString().length();
            
            Toast.makeText(this, "已超出" + (currentLength - 140) + "字", Toast.LENGTH_SHORT).show();
            ret = false;
        }
        return ret;
    }
    
    /**
     * 操作选择
     * 
     * @param items
     */
    public void imageChooseItem(CharSequence[] items) {
        AlertDialog imageDialog = new AlertDialog.Builder(this).setTitle("增加图片")
                .setItems(items, new DialogInterface.OnClickListener() {
                    
                    public void onClick(DialogInterface dialog, int item) {
                        // 手机选图
                        if (item == 0) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");
                            startActivityForResult(intent, REQUEST_CODE_GETIMAGE_BYSDCARD);
                        }
                        // 拍照
                        else if (item == 1) {
                            // String camerName = InfoHelper.getFileName();
                            // String fileName = "Share" + camerName + ".jpg";
                            // File camerFile = new File(InfoHelper.getCamerPath(), fileName);
                            // Uri uri = Uri.fromFile(camerFile);
                            // imagePath = getAbsoluteImagePath(uri);
                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, 0);
                            startActivityForResult(intent, REQUEST_CODE_GETIMAGE_BYCAMERA);
                        } else if (item == 2) {
                            imagePath = null;
                            imgView.setImageBitmap(null);
                        }
                    }
                }).create();
        
        imageDialog.show();
    }
    
    /**
     * 处理返回的照片数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GETIMAGE_BYSDCARD) {
            if (resultCode != RESULT_OK) {
                return;
            }
            
            if (data == null)
                return;
            try {
                Uri uri = data.getData();
                
                imagePath = getAbsoluteImagePath(uri);
                Log.d(TAG, uri.toString());
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                imgView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 拍摄图片
        else if (requestCode == REQUEST_CODE_GETIMAGE_BYCAMERA) {
            if (resultCode != RESULT_OK) {
                return;
            }
            try {
                
                Bundle extras = data.getExtras();
                if (extras != null) {
                    imagePath = getLatestImage();
                }
                myBitmap = (Bitmap) extras.get("data");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                mContent = baos.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
            imgView.setImageBitmap(myBitmap);
            
        }
        
        imgView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GETIMAGE_BYSDCARD);
            }
        });
    }
    
    /**
     * 处理微博发送后的信息
     */
    Handler handler = new Handler() {
        
        @Override
        public void handleMessage(Message msg) {
            if (dialog != null) {
                dialog.dismiss();
            }
            
            imagePath = null;
            contentEditText.setText("");
            imgView.setImageBitmap(null);
            
            if (msg.what > 0) {
                Toast.makeText(SendStatus.this, "微博分享成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SendStatus.this, "微博分享失败，请检查网络", Toast.LENGTH_SHORT).show();
            }
        }
    };
    
    /**
     * 发布一条微博带图片
     * 
     * @param text
     *            微博内容
     * @param fileName
     *            图片绝对路径
     */
    private void upload(String text, String fileName) {
        final Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(getApplicationContext());
        StatusesAPI statusesAPI = new StatusesAPI(token);
        statusesAPI.upload(text, fileName, "0", "0", new RequestListener() {
            
            @Override
            public void onIOException(IOException arg0) {
                Log.d(TAG, arg0.getMessage() + "IOException");
                Message msg = handler.obtainMessage(-1);
                handler.sendMessage(msg);
            }
            
            @Override
            public void onError(WeiboException arg0) {
                Log.d(TAG, arg0.getMessage() + "WeiboException");
                Message msg = handler.obtainMessage(-1);
                handler.sendMessage(msg);
            }
            
            @Override
            public void onComplete(String arg0) {
                Message msg = handler.obtainMessage(1);
                handler.sendMessage(msg);
                Log.d(TAG, arg0);
                
            }
        });
    }
    
    /**
     * 发布一条微博
     * 
     * @param text
     *            微博内容
     * @param fileName
     *            图片绝对路径
     */
    private void upload(String text) {
        final Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(getApplicationContext());
        StatusesAPI statusesAPI = new StatusesAPI(token);
        statusesAPI.update(text, "0", "0", new RequestListener() {
            
            @Override
            public void onIOException(IOException arg0) {
                Log.d(TAG, arg0.getMessage() + "IOException");
                Message msg = handler.obtainMessage(-1);
                handler.sendMessage(msg);
            }
            
            @Override
            public void onError(WeiboException arg0) {
                Log.d(TAG, arg0.getMessage() + "WeiboException");
                Message msg = handler.obtainMessage(-1);
                handler.sendMessage(msg);
            }
            
            @Override
            public void onComplete(String arg0) {
                Message msg = handler.obtainMessage(1);
                handler.sendMessage(msg);
                
            }
        });
    }
    
    /**
     * 从字节流里面获取图片
     * 
     * @param bytes
     * @param opts
     * @return
     */
    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }
    
    /**
     * 读字节流
     * 
     * @param in
     * @return
     * @throws Exception
     */
    public static byte[] readStream(InputStream in) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        
        while ((len = in.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        in.close();
        return data;
    }
    
    /**
     * 通过uri获取文件的绝对路径在上传微博图片时候用
     */
    public String getAbsoluteImagePath(Uri uri) {
        // can post image
        String[] proj = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, proj, // Which columns to return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)
        
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        
        return cursor.getString(column_index);
    }
    
    /**
     * 获取图片缩略图
     * 只有Android2.1以上版本支持
     * 
     * @param imgName
     * @param kind
     *            MediaStore.Images.Thumbnails.MICRO_KIND
     * @return
     */
    protected Bitmap loadImgThumbnail(String imgName, int kind) {
        Bitmap bitmap = null;
        
        String[] proj = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME };
        
        Cursor cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,
                MediaStore.Images.Media.DISPLAY_NAME + "='" + imgName + "'", null, null);
        
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            ContentResolver crThumb = getContentResolver();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            bitmap = MediaStore.Images.Thumbnails.getThumbnail(crThumb, cursor.getInt(0), kind, options);
        }
        return bitmap;
    }
    
    /**
     * 获取SD卡中最新图片路径
     * 
     * @return
     */
    protected String getLatestImage() {
        String latestImage = null;
        String[] items = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, items, null, null,
                MediaStore.Images.Media._ID + " desc");
        
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                latestImage = cursor.getString(1);
                break;
            }
        }
        
        return latestImage;
    }
}
