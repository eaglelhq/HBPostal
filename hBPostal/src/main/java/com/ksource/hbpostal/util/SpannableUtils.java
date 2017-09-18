package com.ksource.hbpostal.util;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

/**
 * Spannable工具类，用于设置文字的前景色、背景色、Typeface、粗体、斜体、字号、超链接、删除线、下划线、上下标等
 * 
 * */
public class SpannableUtils {
    private SpannableUtils( ){
         
    }
     
    /**
     * 改变字符串中某一段文字的字号
     * setTextSize("",24,0,2) = null;
     * setTextSize(null,24,0,2) = null;
     * setTextSize("abc",-2,0,2) = null;
     * setTextSize("abc",24,0,4) = null;
     * setTextSize("abc",24,-2,2) = null;
     * setTextSize("abc",24,0,2) = normal string
     * */
	public static SpannableString setTextSize( String content, int startIndex, int endIndex, int fontSize ){
        if( TextUtils.isEmpty( content ) || fontSize <= 0 || startIndex >= endIndex || startIndex < 0 || endIndex >= content.length( ) ){
            return null;
        }
         
        SpannableString spannableString = new SpannableString( content );
        spannableString.setSpan( new AbsoluteSizeSpan( fontSize ), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
         
        return spannableString;
    }
     
    public static SpannableString setTextSub( String content, int startIndex, int endIndex ){
        if( TextUtils.isEmpty( content ) || startIndex < 0 || endIndex >= content.length( ) || startIndex >= endIndex ){
            return null;
        }
         
        SpannableString spannableString = new SpannableString( content );
        spannableString.setSpan( new SubscriptSpan( ), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
         
        return spannableString;
    }
     
    public static SpannableString setTextSuper( String content, int startIndex, int endIndex ){
        if( TextUtils.isEmpty( content ) || startIndex < 0 || endIndex >= content.length( ) || startIndex >= endIndex ){
            return null;
        }
         
        SpannableString spannableString = new SpannableString( content );
        spannableString.setSpan( new SuperscriptSpan( ), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
         
        return spannableString;
    }
     
    public static SpannableString setTextStrikethrough( String content, int startIndex, int endIndex ){
        if( TextUtils.isEmpty( content ) || startIndex < 0 || endIndex >= content.length( ) || startIndex >= endIndex ){
            return null;
        }
         
        SpannableString spannableString = new SpannableString( content );
        spannableString.setSpan(new StrikethroughSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
         
        return spannableString;
    }
     
    public static SpannableString setTextUnderline( String content, int startIndex, int endIndex ){
        if( TextUtils.isEmpty( content ) || startIndex < 0 || endIndex >= content.length( ) || startIndex >= endIndex ){
            return null;
        }
         
        SpannableString spannableString = new SpannableString( content );
        spannableString.setSpan(new UnderlineSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
         
        return spannableString;
    }
     
    public static SpannableString setTextBold( String content, int startIndex, int endIndex ){
        if( TextUtils.isEmpty( content ) || startIndex < 0 || endIndex >= content.length( ) || startIndex >= endIndex ){
            return null;
        }
         
        SpannableString spannableString = new SpannableString( content );
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
         
        return spannableString;
    }
     
    public static SpannableString setTextItalic( String content, int startIndex, int endIndex ){
        if( TextUtils.isEmpty( content ) || startIndex < 0 || endIndex >= content.length( ) || startIndex >= endIndex ){
            return null;
        }
         
        SpannableString spannableString = new SpannableString( content );
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
         
        return spannableString;
    }
     
    public static SpannableString setTextBoldItalic( String content, int startIndex, int endIndex ){
        if( TextUtils.isEmpty( content ) || startIndex < 0 || endIndex >= content.length( ) || startIndex >= endIndex ){
            return null;
        }
         
        SpannableString spannableString = new SpannableString( content );
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
         
        return spannableString;
    }
     
    public static SpannableString setTextForeground( String content, int startIndex, int endIndex, int foregroundColor ){
        if( TextUtils.isEmpty( content ) || startIndex < 0 || endIndex >= content.length( ) || startIndex >= endIndex ){
            return null;
        }
         
        SpannableString spannableString = new SpannableString( content );
        spannableString.setSpan(new ForegroundColorSpan( foregroundColor ), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
         
        return spannableString;
    }
     
    public static SpannableString setTextBackground( String content, int startIndex, int endIndex, int backgroundColor ){
        if( TextUtils.isEmpty( content ) || startIndex < 0 || endIndex >= content.length( ) || startIndex >= endIndex ){
            return null;
        }
         
        SpannableString spannableString = new SpannableString( content );
        spannableString.setSpan(new BackgroundColorSpan( backgroundColor ), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
         
        return spannableString;
    }
     
    /**
     * 设置文本的超链接
     * @param content 需要处理的文本
     * @param startIndex
     * @param endIndex 被处理文本中需要处理字串的开始和结束索引
     * @param url 文本对应的链接地址，需要注意格式：
     * （1）电话以"tel:"打头，比如"tel:02355692427"
     * （2）邮件以"mailto:"打头，比如"mailto:zmywly8866@gmail.com"
     * （3）短信以"sms:"打头，比如"sms:02355692427"
     * （4）彩信以"mms:"打头，比如"mms:02355692427"
     * （5）地图以"geo:"打头，比如"geo:68.426537,68.123456"
     * （6）网络以"http://"打头，比如"http://www.google.com"
     * */
    public static SpannableString setTextURL( String content, int startIndex, int endIndex, String url ){
        if( TextUtils.isEmpty( content ) || startIndex < 0 || endIndex >= content.length( ) || startIndex >= endIndex ){
            return null;
        }
         
        SpannableString spannableString = new SpannableString( content );
        spannableString.setSpan(new URLSpan( url ), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
         
        return spannableString;
    }
     
    public static SpannableString setTextImg( String content, int startIndex, int endIndex, Drawable drawable ){
        if( TextUtils.isEmpty( content ) || startIndex < 0 || endIndex >= content.length( ) || startIndex >= endIndex ){
            return null;
        }
         
        SpannableString spannableString = new SpannableString( content );
        spannableString.setSpan(new ImageSpan(drawable), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
         
        return spannableString;
    }
    /**     * 创建一个Spannable对象
    *
    * @param string
    * @return
    */
   public static Spannable createSpannable(String string) {
       Spannable WordtoSpan = new SpannableString(string);
       return WordtoSpan;
   }

   /**
    * FOR: textview.setText(WordtoSpan)
    * Textview文字设置俩种不同颜色
    *
    * @param context
    * @param string
    * @param tag           从哪个索引开始分割
    * @param colorFirstId
    * @param colorSecondId
    * @return
    */
   private static Spannable setTextColor(Context context, String string, int tag, int colorFirstId, int colorSecondId) {
       Spannable WordtoSpan = createSpannable(string);
       WordtoSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(colorFirstId)), 0, tag, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
       WordtoSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(colorSecondId)), tag, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
       return WordtoSpan;
   }

   /**
    * 设置字符串中部分串的字体颜色
    *
    * @param sp
    * @param start
    * @param end
    * @param color
    */
   public static void setTextColor(Spannable sp, int color, int start, int end) {
       if (sp != null) {
           sp.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
       }
   }

   /**
    * 设置字符串中部分串的字体大小
    *
    * @param sp
    * @param textSize
    * @param start
    * @param end
    */    
public static void setTextSize(Spannable sp, int textSize, int start, int end) {        if (sp != null) {
           //设置字体大小,第二个参数boolean dip，如果为true，表示前面的字体大小单位为dip，否则为像素，
           sp.setSpan(new AbsoluteSizeSpan(textSize, true), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
       }
   }

   /**
    * 设置不同的样式
    *
    * @param sp
    * @param textStyle
    * @param start
    * @param end
    */
   public static void setTextStyle(Spannable sp, int textStyle, int start, int end) {
       if (sp != null) {
           sp.setSpan(new StyleSpan(textStyle), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
       }
   }

   /**
    * FOR:
    * SpannableString spStr = new SpannableString(str);
    * SpannableUtils.setTextClickable(tv_1, spStr, start, end);
    * tv_1.setText(spStr);
    * 设置字符串的可点击部分
    *
    * @param textView
    * @param sp
    * @param start
    * @param end
    */
   public static void setTextClickable(ClickableSpan what,TextView textView, Spannable sp, int start, int end) {
       if (sp != null) {
           
		sp.setSpan(what, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
           //设置点击后的颜色为透明，否则会一直出现高亮            
           textView.setHighlightColor(Color.TRANSPARENT); 
           //开始响应点击事件
           textView.setMovementMethod(LinkMovementMethod.getInstance());
       }
   }

   /**
    * 设置中划线
    *
    * @param sp
    * @param start
    * @param end
    */
   public static void setTextMiddleLine(Spannable sp, int start, int end) {
       if (sp != null) {
           StrikethroughSpan stSpan = new StrikethroughSpan();  //设置删除线样式
           sp.setSpan(stSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
       }
   }
}