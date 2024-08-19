package com.example.ddns.utils;

import java.io.*;

public class FileUtils {

    public static String readJson(String filePath) {
        String content = "";
        File file = new File(filePath);

        // 检查文件是否存在
        if (!file.exists()) {
            // 文件不存在，创建文件
            try (FileWriter writer = new FileWriter(filePath)) {
                // 可以在这里写入默认的JSON内容，如果需要的话
                // writer.write("{\"default\": \"json content\"}");
            } catch (IOException e) {
                e.printStackTrace();
                // 如果创建文件失败，返回空字符串或者抛出异常
                return content;
            }
        }

        // 文件存在，读取文件内容
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content += line + "\n";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }
    public static  void writeJson( String filePath,String content){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
