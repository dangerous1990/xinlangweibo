package com.limeng.xinlangweibo;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.limeng.xinlangweibo.pojo.Status;
import com.limeng.xinlangweibo.share.util.InfoHelper;
import com.limeng.xinlangweibo.share.util.StringUtils;
import com.limeng.xinlangweibo.util.AccessTokenKeeper;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.CommentsAPI;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.api.WeiboAPI.COMMENTS_TYPE;
import com.weibo.sdk.android.net.RequestListener;

/**
 * 转发微博界面
 * 
 * @author limeng
 */
public class RepostActivity extends Activity {
    
    private static final String TAG = "RepostActivity";
    
    private Button share_button;
    
    private ImageButton imgChooseBtn;
    
    private ImageView imgView;
    
    private TextView wordCounterTextView;
    
    private EditText contentEditText;
    
    private ProgressDialog dialog;
    
    private Status status;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_main);
        // 设置标题头
        ((TextView) findViewById(R.id.tv_title)).setText("转发内容");
        ((Button) findViewById(R.id.tv_back)).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        share_button = (Button) findViewById(R.id.btn_share);
        imgChooseBtn = (ImageButton) findViewById(R.id.share_imagechoose);
        imgChooseBtn.setVisibility(View.GONE);
        imgView = (ImageView) findViewById(R.id.share_image);
        imgView.setVisibility(View.GONE);
        wordCounterTextView = (TextView) findViewById(R.id.share_word_counter);
        contentEditText = (EditText) findViewById(R.id.share_content);
        Bundle bundle = getIntent().getExtras();
        status = (Status) bundle.get("status");
        // 分享微博dialog
        dialog = new ProgressDialog(this);
        dialog.setMessage("转发中...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        share_button.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                if (!InfoHelper.checkNetWork(RepostActivity.this)) {
                    Toast.makeText(RepostActivity.this, "网络连接失败，请检查网络设置！", Toast.LENGTH_LONG).show();
                } else {
                    if (isChecked()) {
                        dialog.show();
                        repostStatus(contentEditText.getText().toString(), Long.valueOf(status.getId()));
                    }
                }
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
     * 发表评论后的处理
     */
    Handler handler = new Handler() {
        
        @Override
        public void handleMessage(Message msg) {
            if (dialog != null) {
                dialog.dismiss();
            }
            
            contentEditText.setText("");
            imgView.setImageBitmap(null);
            
            if (msg.what > 0) {
                Toast.makeText(RepostActivity.this, "转发成功", Toast.LENGTH_SHORT).show();
                RepostActivity.this.finish();
            } else {
                Toast.makeText(RepostActivity.this, "转发失败", Toast.LENGTH_SHORT).show();
            }
        }
    };
    
    private void repostStatus(String text, Long id) {
        final Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this);
        StatusesAPI statusesAPI = new StatusesAPI(token);
        statusesAPI.repost(id, text, COMMENTS_TYPE.NONE, new RequestListener() {
            
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
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
