package com.dzy.onedriveclient.transfer;

import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by dzysg on 2017/4/29 0029.
 */

public final class DLHelper {
    private DLHelper(){}

    public static SimpleDateFormat simpleDateFormat;

    public static void checkNull(Object o,String msg){
        if (o==null){
            throw new NullPointerException(msg+" is null");
        }
    }

    private static void initFormat(){
        if (simpleDateFormat==null){
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
    }

    public static Date parseDate(String time) throws ParseException {
        initFormat();
        return simpleDateFormat.parse(time);
    }

    public static String dateToString(Date date){
        initFormat();
        return simpleDateFormat.format(date);
    }

    public static boolean isExpire(Date date){
        Calendar expire = Calendar.getInstance();
        expire.setTime(date);
        expire.setTime(date);
        Calendar now = Calendar.getInstance();
        return now.after(expire);
    }

    public static UploadSession parseUploadSession(String json,UploadSession session) throws IOException{
        JsonReader reader =new JsonReader(new StringReader(json));
        reader.beginObject(); // throws IOException
        if (session==null){
            session = new UploadSession();
        }
        while (reader.hasNext()) {
            String s = reader.nextName();
            switch (s) {
                case "uploadUrl":
                    session.setUploadUrl(reader.nextString());
                    break;
                case "expirationDateTime":
                    session.setExpirationDateTime(reader.nextString());
                    break;
                case "nextExpectedRanges":
                    parseRange(reader,session);
                    break;
                default:
                    reader.nextString();
                    break;
            }
        }
        reader.endObject(); // throws IOException
        return session;
    }

    private static void parseRange(JsonReader reader,UploadSession session) throws IOException{
        reader.beginArray();
        List<String> list = new ArrayList<>();
        while (reader.hasNext()){
            list.add(reader.nextString());
        }
        reader.endArray();
        session.setNextExpectedRange(list);
    }

    public  static void checkExistFile(String path) throws FileNotFoundException {
        File file = new File(path);
        if (!file.exists()){
            throw new FileNotFoundException("file not found!,path "+path);
        }
        if (file.isDirectory()){
            throw new IllegalArgumentException("can not upload a directory");
        }
    }

}
