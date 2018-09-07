package com.reb.light;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String result = "\"BgcNfc\">Current Version</div><span class=\"htlgb\"><div><span class=\"htlgb\">10.1.28.560</span></div></span></div><div class=\"hAyfc\"><div class=\"BgcNfc\">Requires Android</div><span class=\"htlgb\">";
        Pattern p = Pattern.compile("Current Version[\\s\\S>]*?");//([\d\.]+)
        Matcher matcher = p.matcher(result);
        //获取Googleplay上的版本号
        String mLastVersion = matcher.group(1);
        System.out.println("mLastVersion:" + mLastVersion);
    }
}