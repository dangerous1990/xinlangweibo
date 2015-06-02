package com.limeng.xinlangweibo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.limeng.xinlangweibo.share.util.InfoHelper;

public class HighLightTextView extends TextView {

	public static final String PARAM_UID = "uid";
	public static final String MENTIONS_SCHEMA = "tofuweibo://sina_profile";

	// 匹配@+人名
	public static final Pattern NAME_Pattern = Pattern.compile(
			"@([\\u4e00-\\u9fa5\\w\\-\\—]{2,30})", Pattern.CASE_INSENSITIVE);

	// 匹配话题#...#
	public static final Pattern TOPIC_PATTERN = Pattern
			.compile("#([^\\#|^\\@|.]+)#");

	// 匹配网址
	public final static Pattern URL_PATTERN = Pattern
			.compile("http://([\\w-]+\\.)+[\\w-]+(/[\\w-\\./?%&=]*)?");

	// 匹配表情[...]
	public static Pattern EMOTION_PATTERN = Pattern.compile(
			"\\[([\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]+)\\]",
			Pattern.CASE_INSENSITIVE);

	public HighLightTextView(Context context) {
		this(context, null);
	}

	public HighLightTextView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.textViewStyle);
	}

	public HighLightTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setFocusable(false);
	}

	public void setText(String text) {
		setHighLightText(this, text);
	}

	/**
	 * 实现人名/话题/连接等高亮，以及显示表情
	 * 
	 * @param textView
	 * @param text
	 */
	public static void setHighLightText(TextView textView, String text) {
		SpannableStringBuilder style = new SpannableStringBuilder(text);

		// Matcher nameMatcher = NAME_Pattern.matcher(text);
		// while (nameMatcher.find()) {
		// style.setSpan(new ForegroundColorSpan(Color.BLUE),
		// nameMatcher.start(), nameMatcher.end(),
		// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		// }

		Matcher topicMatcher = TOPIC_PATTERN.matcher(text);
		while (topicMatcher.find()) {
			style.setSpan(new ForegroundColorSpan(Color.BLUE),
					topicMatcher.start(), topicMatcher.end(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		// Matcher urlMatcher = URL_PATTERN.matcher(text);
		// while (urlMatcher.find()) {
		// style.setSpan(new URLSpan(urlMatcher.group()), urlMatcher.start(),
		// urlMatcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		// }

		Matcher emotionMatcher = EMOTION_PATTERN.matcher(text);
		while (emotionMatcher.find()) {
			try {

				Bitmap bitmap = BitmapFactory.decodeFile(InfoHelper
						.getEmotionPath() + emotionMatcher.group());
				if (bitmap == null)
					throw new NullPointerException();

				Drawable drawable = new BitmapDrawable(bitmap);

				drawable.setBounds(0, 0, 30, 30);
				ImageSpan span = new ImageSpan(drawable,
						ImageSpan.ALIGN_BASELINE);
				style.setSpan(span, emotionMatcher.start(),
						emotionMatcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} catch (Exception e) {
				e.printStackTrace();
				style.setSpan(new ForegroundColorSpan(Color.BLUE),
						emotionMatcher.start(), emotionMatcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		textView.setText(style);

		if (!(textView instanceof EditText)) {
			textView.setAutoLinkMask(0);
			Linkify.addLinks(textView, NAME_Pattern, String.format("%s/?%s=",
					HighLightTextView.MENTIONS_SCHEMA,
					HighLightTextView.PARAM_UID), null, new TransformFilter() {

				@Override
				public String transformUrl(Matcher match, String url) {
					return match.group(1);
				}
			});
			textView.setMovementMethod(null);
		}
	}

	/**
	 * 给文字加了超链接以后，listview的onItemClickListener就会失效，自己重写onTouchEvent方法可以解决这问题
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		TextView widget = (TextView) this;
		Object text = widget.getText();
		if (text instanceof Spanned) {
			Spannable buffer;
			try {
				buffer = (Spannable) text;
			} catch (ClassCastException cce) {
				cce.printStackTrace();
				return false;
			}

			int action = event.getAction();

			if (action == MotionEvent.ACTION_UP
					|| action == MotionEvent.ACTION_DOWN) {
				int x = (int) event.getX();
				int y = (int) event.getY();

				x -= widget.getTotalPaddingLeft();
				y -= widget.getTotalPaddingTop();

				x += widget.getScrollX();
				y += widget.getScrollY();

				Layout layout = widget.getLayout();
				int line = layout.getLineForVertical(y);
				int off = layout.getOffsetForHorizontal(line, x);

				ClickableSpan[] link = buffer.getSpans(off, off,
						ClickableSpan.class);
				if (link.length != 0) {
					if (action == MotionEvent.ACTION_UP) {
						link[0].onClick(widget);
					} else if (action == MotionEvent.ACTION_DOWN) {
						Selection.setSelection(buffer,
								buffer.getSpanStart(link[0]),
								buffer.getSpanEnd(link[0]));
					}
					return true;
				}
			}

		}

		return false;
	}
}
