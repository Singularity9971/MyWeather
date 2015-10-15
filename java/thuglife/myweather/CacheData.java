package thuglife.myweather;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class CacheData {

    private static long threshold = 10800000;
    private static long MAX_FILE_SIZE = 500000;
    private static String FILENAME = "record";


    public static boolean storeData(Data data, Context context){
        File file = new File(context.getCacheDir(),FILENAME);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            Log.d("Avi", "Data has been entered into cache");
            fos.close();
            checkSize(file,context);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Data check(String location, Context context){
        File file = new File(context.getCacheDir(),FILENAME);
        if(!file.exists()) {
            Log.d("Avi", "File doesn't exist");
            return null;
        }
        Data data;
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Data check;
            while((check = (Data)ois.readObject()) != null){
                data = check;
                if(data.location.equals(location)){
                    Log.d("Avi","Data found");
                    long curTime = System.currentTimeMillis();
                    long difference = curTime - data.timestamp;
                    if(difference <= threshold) {
                        Log.d("Avi","Loading cached data");
                        return data;
                    }
                    else {
                        return null;
                    }
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void resetFile(File file,Context context){
        File second = new File(context.getCacheDir(),"dummy");
        if(!file.exists())
            return;
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            FileOutputStream fos = new FileOutputStream(second);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            Data check;
            while((check = (Data)ois.readObject()) != null)
            {
                long curTime = System.currentTimeMillis();
                long difference = curTime - check.timestamp;
                if(difference <= threshold) {
                    oos.writeObject(check);
                }
            }
            FileWriter writer = new FileWriter(file);
            writer.write("");
            writer.close();
            FileOutputStream fos1 = new FileOutputStream(file);
            ObjectOutputStream oos1 = new ObjectOutputStream(fos1);
            FileInputStream fis1 = new FileInputStream(second);
            ObjectInputStream ois1 = new ObjectInputStream(fis1);
            Data check1;
            while((check1 = (Data)ois1.readObject())!=null)
                oos1.writeObject(check1);

            second.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkSize(File file, Context context){
        if(file.length() >= MAX_FILE_SIZE)
            resetFile(file,context);
    }

}
