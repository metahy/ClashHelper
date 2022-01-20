package io.metahy;

import org.apache.commons.io.FileUtils;
import org.ini4j.Wini;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * @author Hyleon
 */
public class ConfigUtil {

    private static Wini ini = null;
    private static final String USER_HOME = System.getProperty("user.home");
    private static final String USER_HOME_CFG_DIR = USER_HOME + File.separator + ".config";
    private static final String TARGET_DIR = USER_HOME_CFG_DIR + File.separator + "clash_helper";
    private static final String TARGET_FILE = TARGET_DIR + File.separator + "config.ini";
    private static final String DEFAULT_CLASH_DIR = USER_HOME_CFG_DIR + File.separator + "clash";

    public static void prepare() throws IOException {
        logSys();

        final File td = FileUtils.getFile(TARGET_DIR);
        if (td.exists() && td.isFile()) {
            System.err.println("ERROR-0: Target config directory is file, please remove " + TARGET_DIR);
            return;
        }

        if (!td.exists()) {
            System.out.println("INFO: Create config directory:[" + TARGET_DIR + "], result:[" + td.mkdirs() + "]");
        }

        final File tf = FileUtils.getFile(TARGET_FILE);
        if (!tf.exists()) {
            System.out.println("INFO: Create config file:[" + TARGET_FILE + "], result:[" + tf.createNewFile() + "]");
        }

        ini = new Wini(new FileInputStream(tf));
        String currCd = ini.get("Clash", "config_dir", String.class);

        System.out.println("INFO: Current Clash config directory:[" + currCd + "]");

        if (Objects.isNull(currCd) || currCd.length() == 0) {
            System.out.println("INFO: Use default Clash config directory:[" + DEFAULT_CLASH_DIR + "]");
            currCd = DEFAULT_CLASH_DIR;
        }

        ini.put("Clash", "config_dir", DEFAULT_CLASH_DIR);
        ini.store(new FileOutputStream(tf));

        MainController.DIR = currCd;
    }

    public static void setClashDir(String dir) throws IOException {
        ini.put("Clash", "config_dir", dir);
        ini.store(new FileOutputStream(FileUtils.getFile(TARGET_FILE)));
    }

    private static void logSys() {
        System.out.println();
        System.out.println("===== System =====");
        System.out.println(" => OS: " + System.getProperty("os.name"));
        System.out.println(" => User home: " + System.getProperty("user.home"));
        System.out.println("==================");
        System.out.println();
    }
}
