package edu.sdust.moon.Core;

import com.alibaba.fastjson.JSON;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigRead {
    private File ConfigFile;
    private static final ConfigRead configRead = new ConfigRead();

    private ConfigRead() {
    }

    public static ConfigRead createReader() {
        return configRead;
    }


    public boolean ReadStart(String local) {
        try {
            ConfigFile = new File(local + "/config/config.json");
            if (!ConfigFile.exists()) {
                if (!ConfigFile.getParentFile().exists()) {
                    ConfigFile.getParentFile().mkdirs();
                }
                ConfigFile.createNewFile();
                Start.setConfig(new ConfigObj());
                Save();
                return true;
            } else {
                var reader = new InputStreamReader(
                        new FileInputStream(ConfigFile), StandardCharsets.UTF_8);
                var bf = new BufferedReader(reader);
                StringBuilder stringBuilder = new StringBuilder();
                String str = null;
                while ((str = bf.readLine()) != null) {
                    stringBuilder.append(str);
                }
                Start.setConfig(JSON.parseObject(stringBuilder.toString(), ConfigObj.class));
                if (Start.getConfig().getNodes() == null) {
                    Start.setConfig(new ConfigObj());
                    Save();
                }
                bf.close();
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void Save() {
        try {
            var out = new FileOutputStream(ConfigFile);
            var write = new OutputStreamWriter(
                    out, StandardCharsets.UTF_8);
            write.write(JSON.toJSONString(Start.getConfig()));
            write.close();
            out.close();
        } catch (Exception e) {
            Start.getLogger().error("Failed to save configuration file.", e);
            e.printStackTrace();
        }
    }
}
