package org.ezdevgroup.ezapm.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by ddakker on 2016-02-26.
 */
public class Conf {
    private static Properties prop = new Properties();

    static {
        String conf = System.getProperty("ezapm.conf");
        InputStream input = null;

        try {
            File file = new File(conf);
            if (!file.exists()) {
                System.err.println("설정 파일이 존재하지 않습니다.");
            }

            input = new FileInputStream(file);

            prop.load(input);

            prop.list(System.out);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }
}
