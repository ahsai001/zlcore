package com.zaitunlabs.zlcore.utils;

import android.content.Context;

/**
 * Created by ahmad s on 2019-06-30.
 */
public class Lookup {
    private static SQLiteWrapper sqLiteWrapper;

    public static void init(Context context){
        if(sqLiteWrapper == null) {
            sqLiteWrapper = SQLiteWrapper.getLookupDatabase(context);
        }
    }

    private static void checkCondition(){
        if(sqLiteWrapper == null){
            throw new IllegalStateException("you need to run init(context) first, you can put it inside oncreate of Application");
        }
    }

    //string
    public static String get(String key, String defaultValue){
        checkCondition();
        SQLiteWrapper.TLookup lookup = sqLiteWrapper.findFirstWithCriteria(null, SQLiteWrapper.TLookup.class,
                "key=?", new String[]{key});
        return lookup==null?defaultValue:lookup.getString();
    }

    public static void set(String key, String value){
        checkCondition();
        SQLiteWrapper.TLookup lookup = sqLiteWrapper.findFirstWithCriteria(null, SQLiteWrapper.TLookup.class,
                "key=?", new String[]{key});
        if(lookup != null){
            lookup.setString(value);
            lookup.update();
        } else {
            lookup = new SQLiteWrapper.TLookup();
            lookup.setKey(key);
            lookup.setString(value);
            lookup.save();
        }
    }


    //boolean
    public static boolean get(String key, boolean defaultValue){
        checkCondition();
        SQLiteWrapper.TLookup lookup = sqLiteWrapper.findFirstWithCriteria(null, SQLiteWrapper.TLookup.class,
                "key=?", new String[]{key});
        return lookup==null?defaultValue:lookup.getBoolean();
    }

    public static void set(String key, boolean value){
        checkCondition();
        SQLiteWrapper.TLookup lookup = sqLiteWrapper.findFirstWithCriteria(null, SQLiteWrapper.TLookup.class,
                "key=?", new String[]{key});
        if(lookup != null){
            lookup.setBoolean(value);
            lookup.update();
        } else {
            lookup = new SQLiteWrapper.TLookup();
            lookup.setKey(key);
            lookup.setBoolean(value);
            lookup.save();
        }
    }


    //int
    public static int get(String key, int defaultValue){
        checkCondition();
        SQLiteWrapper.TLookup lookup = sqLiteWrapper.findFirstWithCriteria(null, SQLiteWrapper.TLookup.class,
                "key=?", new String[]{key});
        return lookup==null?defaultValue:lookup.getInt();
    }

    public static void set(String key, int value){
        checkCondition();
        SQLiteWrapper.TLookup lookup = sqLiteWrapper.findFirstWithCriteria(null, SQLiteWrapper.TLookup.class,
                "key=?", new String[]{key});
        if(lookup != null){
            lookup.setInt(value);
            lookup.update();
        } else {
            lookup = new SQLiteWrapper.TLookup();
            lookup.setKey(key);
            lookup.setInt(value);
            lookup.save();
        }
    }


    //long
    public static long get(String key, long defaultValue){
        checkCondition();
        SQLiteWrapper.TLookup lookup = sqLiteWrapper.findFirstWithCriteria(null, SQLiteWrapper.TLookup.class,
                "key=?", new String[]{key});
        return lookup==null?defaultValue:lookup.getLong();
    }

    public static void set(String key, long value){
        checkCondition();
        SQLiteWrapper.TLookup lookup = sqLiteWrapper.findFirstWithCriteria(null, SQLiteWrapper.TLookup.class,
                "key=?", new String[]{key});
        if(lookup != null){
            lookup.setLong(value);
            lookup.update();
        } else {
            lookup = new SQLiteWrapper.TLookup();
            lookup.setKey(key);
            lookup.setLong(value);
            lookup.save();
        }
    }


    //float
    public static float get(String key, float defaultValue){
        checkCondition();
        SQLiteWrapper.TLookup lookup = sqLiteWrapper.findFirstWithCriteria(null, SQLiteWrapper.TLookup.class,
                "key=?", new String[]{key});
        return lookup==null?defaultValue:lookup.getFloat();
    }

    public static void set(String key, float value){
        checkCondition();
        SQLiteWrapper.TLookup lookup = sqLiteWrapper.findFirstWithCriteria(null, SQLiteWrapper.TLookup.class,
                "key=?", new String[]{key});
        if(lookup != null){
            lookup.setFloat(value);
            lookup.update();
        } else {
            lookup = new SQLiteWrapper.TLookup();
            lookup.setKey(key);
            lookup.setFloat(value);
            lookup.save();
        }
    }


    //double
    public static double get(String key, double defaultValue){
        checkCondition();
        SQLiteWrapper.TLookup lookup = sqLiteWrapper.findFirstWithCriteria(null, SQLiteWrapper.TLookup.class,
                "key=?", new String[]{key});
        return lookup==null?defaultValue:lookup.getDouble();
    }

    public static void set(String key, double value){
        checkCondition();
        SQLiteWrapper.TLookup lookup = sqLiteWrapper.findFirstWithCriteria(null, SQLiteWrapper.TLookup.class,
                "key=?", new String[]{key});
        if(lookup != null){
            lookup.setDouble(value);
            lookup.update();
        } else {
            lookup = new SQLiteWrapper.TLookup();
            lookup.setKey(key);
            lookup.setDouble(value);
            lookup.save();
        }
    }
}
