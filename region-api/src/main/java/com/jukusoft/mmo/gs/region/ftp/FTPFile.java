package com.jukusoft.mmo.gs.region.ftp;

import com.jukusoft.mmo.engine.shared.logger.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FTPFile {

    private static Pattern filere = Pattern.compile("(.).........+(\\d+) ([A-Za-z0-9@\\.]+) +([A-Za-z0-9]+) +(\\d+) ([A-Z][a-z][a-z]) +(\\d+) +(\\d+):(\\d+) +([A-Za-z0-9@\\.\\-_]+)");

    protected String type;
    protected String name;
    protected long size;
    protected String date;


    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getDate() {
        return date;
    }

    public boolean isDirectory() {
        return type.equals("d");
    }

    public static FTPFile from(String line) {
        Matcher m = filere.matcher(line);

        if (!m.matches()) {
            Log.e("FTPFile", "line " + line + " does not match dir re");
            return null;
        } else {
            String size = m.group(5);
            String date = m.group(6) + " " + m.group(7) + " " + m.group(8) + ":" + m.group(9);
            String fname = m.group(10);

            FTPFile f = new FTPFile();
            f.type = m.group(1);
            f.size = Long.parseLong(size);
            f.name = fname;
            f.date = date;
            return f;
        }
    }

    public static List<FTPFile> listing(String listing) {
        List<FTPFile> ret  = new ArrayList<>();

        for (String f : listing.split("\n")) {
            if (f.endsWith("\r")) {
                f = f.substring(0, f.length() - 1);
            }
            FTPFile file = FTPFile.from(f.toString());

            if (file == null) {
                continue;
            }
            if (file.name.equals(".") || file.name.equals("..")) {
                continue;
            }

            ret.add(file);
        }
        return ret;
    }

    @Override
    public String toString() {
        return "FTPFile [type=" + type + ", name=" + name + ", size=" + size + ", date=" + date + "]";
    }

}
