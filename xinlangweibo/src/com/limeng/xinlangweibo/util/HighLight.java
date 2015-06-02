package com.limeng.xinlangweibo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.widget.TextView;


public class HighLight {
    // 匹配@+人名
    public static final Pattern NAME_Pattern = Pattern.compile(
            "@([\\u4e00-\\u9fa5\\w\\-\\—]{2,30})", Pattern.CASE_INSENSITIVE);

    // 匹配话题#...#
    public static final Pattern TOPIC_PATTERN = Pattern
            .compile("#([^\\#|^\\@|.]+)#");

    // 匹配网址
    public final static Pattern URL_PATTERN = Pattern
            .compile("http://([\\w-]+\\.)+[\\w-]+(/[\\w-\\./?%&=]*)?");
    
    public static void setHighLightText(TextView textView, String text) {
        SpannableStringBuilder style = new SpannableStringBuilder(text);

         Matcher nameMatcher = NAME_Pattern.matcher(text);
         while (nameMatcher.find()) {
         style.setSpan(new ForegroundColorSpan(Color.BLUE),
         nameMatcher.start(), nameMatcher.end(),
         Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
         }

        Matcher topicMatcher = TOPIC_PATTERN.matcher(text);
        while (topicMatcher.find()) {
            style.setSpan(new ForegroundColorSpan(Color.BLUE),
                    topicMatcher.start(), topicMatcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        Matcher urlMatcher = URL_PATTERN.matcher(text);
        while (urlMatcher.find()) {
            style.setSpan(new URLSpan(urlMatcher.group()), urlMatcher.start(), urlMatcher.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
      
        textView.setText(style);
    }
}
