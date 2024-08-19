package com.example.ddns.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GetIpByCmdUtils {

    public static String runSh(String bashPath, String cmd){
        try {
            // 执行命令并获取输出流
            Process process = Runtime.getRuntime().exec(new String[]{bashPath, "-c", cmd});
            BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s="";
            // 读取输出流
            String line;
            while ((line = output.readLine()) != null) {
                s+=line+"\n";
            }

            // 关闭输出流和进程
            output.close();
            process.destroy();
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }




    public static String runCmd(String cmd) throws IOException {
        try {
            String s= "";
            // 调用CMD命令
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", cmd); // /c参数表示执行后关闭CMD窗口
            processBuilder.redirectErrorStream(true); // 将错误输出流与标准输出流合并
            Process process = processBuilder.start();

            // 获取命令输出结果
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "GBK")); // 设置编码为GBK
            String line;
            while ((line = reader.readLine()) != null) {
                s+=line+"\n";
            }

            // 等待命令执行完成
            process.waitFor(10, TimeUnit.SECONDS);
            reader.close();
            inputStream.close();;
            process.destroy();

            return s;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }
    public static List<String> findPublicIpv6Addresses(String text) {
        // 公网IPv6地址通常不以fe80开头，这是链路本地地址
        // 正则表达式解释：
        // (\A|[^:]): 非捕获组，匹配字符串开始或非冒号字符后的位置
        // ([0-9A-Fa-f]{1,4}:){7}(?:[0-9A-Fa-f]{1,4}|:): 匹配标准的1200:0000:...形式的地址
        // |: 逻辑或
        // ([0-9A-Fa-f]{1,4}:){1,7}: 匹配可能的缩写形式，如2001:db8::
        // ([0-9A-Fa-f]{1,4}|:): 匹配地址的最后一个部分或冒号
        // \b: 单词边界，确保我们匹配的是完整单词

        String regex = "(?<!fe80:)\\b(?:[0-9A-Fa-f]{1,4}:){7}[0-9A-Fa-f]{1,4}\\b" +
                "|" +
                "(?<!ff\\d{2}:)(?:[0-9A-Fa-f]{1,4}:){1,7}:(?:[0-9A-Fa-f]{1,4})?" +
                "|" +
                "::(?:[0-9A-Fa-f]{1,4}:){0,5}[0-9A-Fa-f]{1,4}" +
                "|" +
                "([0-9A-Fa-f]{1,4}:){1,6}::[0-9A-Fa-f]{1,4}";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        List<String> addresses = new ArrayList<>();
        while (matcher.find()) {
            addresses.add(matcher.group());
        }

        List<String> realAddress = new ArrayList<>();
        for(String add:addresses){
            String[] ss=add.split(":");
            if(ss.length==8){
                for(int i=0;i<ss.length;i++){
                    if(i==0) if(ss[i].equals("fe80")) break;
                    if(i==ss.length-1){
                        realAddress.add(add);
                    }
                }
            }
        }
        // 将找到的地址数组化
        return realAddress;
    }


    public static List<String> findPublicIPv4(String text) {
        // 正则表达式匹配IPv4地址，排除私有地址
        String regex = "(?<!10\\.|172\\.1[6-9]\\.|172\\.2[0-9]\\.|172\\.3[01]\\.|192\\.168\\.|127\\.)" +
                "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
                "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        List<String> ips = new ArrayList<>();
        while (matcher.find()) {
            String ip = matcher.group();
            // 验证IP地址是否为公网地址，这里简化处理，只排除127.0.0.1
            if (!ip.equals("127.0.0.1")) {

                // 判断IP地址是内网还是外网
                if (ip.startsWith("10.") || ip.startsWith("172.16.") || ip.startsWith("192.168.")) {
                    continue;
                } else {
                    if(ip.endsWith("255")||ip.endsWith("0"))continue;

                    ips.add(ip);
                }


            }
        }


        return ips;
    }

}
