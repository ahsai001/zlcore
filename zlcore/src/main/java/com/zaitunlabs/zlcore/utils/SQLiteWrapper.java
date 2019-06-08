package com.zaitunlabs.zlcore.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SQLiteWrapper extends SQLiteOpenHelper {
    private static final String ID = "_id";
    private Map<String, Table> tableMap;

    public static Map<String, SQLiteWrapper> sqLiteWrapperMap = new HashMap<>();


    public SQLiteWrapper addTable(Table table){
        tableMap.put(table.getName(), table);
        return this;
    }

    public static SQLiteWrapper getInstance(String databaseName){
        if(sqLiteWrapperMap.containsKey(databaseName)){
            return sqLiteWrapperMap.get(databaseName);
        }
        return null;
    }

    public void initInstance(){
        if(!sqLiteWrapperMap.containsKey(getDatabaseName())) {
            sqLiteWrapperMap.put(getDatabaseName(), this);
        }
    }


    public void deInitInstance(){
        if(sqLiteWrapperMap.containsKey(getDatabaseName())) {
            SQLiteWrapper sqLiteWrapper = sqLiteWrapperMap.get(getDatabaseName());
            sqLiteWrapperMap.remove(getDatabaseName());
            sqLiteWrapper.release();
        }
    }

    private SQLiteWrapper(Context context, String databaseName, int version, Map<String, Table> tableMap){
        super(context, databaseName, null, version);
        this.tableMap = tableMap;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(tableMap == null){
            return;
        }

        List<String> createTableList = new ArrayList<>();
        Set<String> keySet = tableMap.keySet();
        Iterator iterator = keySet.iterator();
        while (iterator.hasNext()){
            String key = (String) iterator.next();
            Table table = tableMap.get(key);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("CREATE TABLE");
            stringBuilder.append(" ").append(table.getName()).append(" (");
            stringBuilder.append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT");

            List<Field> fieldList = table.getFieldList();
            for (Field field : fieldList){
                stringBuilder.append(",");
                stringBuilder.append(field.getName()).append(" ").append(field.getType());
                if(field.getTrueType() == boolean.class){
                    stringBuilder.append(" DEFAULT 0");
                }
            }

            stringBuilder.append(")");

            createTableList.add(stringBuilder.toString());
        }

        db.beginTransaction();
        try{
            for (String createTable : createTableList){
                db.execSQL(createTable);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    private void release(){
        tableMap = null;
    }


    private ContentValues getContentValues(List<Field> fieldList, List<Object> dataList){
        ContentValues contentValues = new ContentValues();

        for (int i = 0; i < fieldList.size(); i++) {
            Field field = fieldList.get(i);
            if(field.getType().equals(Field.TEXT)){
                contentValues.put(field.getName(), (String) dataList.get(i));
            } else if(field.getType().equals(Field.INTEGER)){
                if(field.getTrueType() == int.class) {
                    contentValues.put(field.getName(), (int) dataList.get(i));
                } else if(field.getTrueType() == long.class){
                    contentValues.put(field.getName(), (long) dataList.get(i));
                } else if(field.getTrueType() == boolean.class){
                    contentValues.put(field.getName(), (boolean) dataList.get(i));
                }
            } else if(field.getType().equals(Field.REAL)){
                if(field.getTrueType() == float.class) {
                    contentValues.put(field.getName(), (float) dataList.get(i));
                } else if(field.getTrueType() == double.class){
                    contentValues.put(field.getName(), (double) dataList.get(i));
                }
            } else if(field.getType().equals(Field.BLOB)){
                //contentValues.put(field.getName(), (String) objectList.get(i));
            }
        }

        return contentValues;
    }

    //create or insert
    public boolean save(TableClass tableClass) {
        long id = -1;
        try {
            SQLiteDatabase database = getWritableDatabase();

            Table table = tableMap.get(tableClass.getTableName());

            List<Object> dataList = new ArrayList<>();
            tableClass.getData(dataList);

            List<Field> fieldList = table.getFieldList();

            ContentValues contentValues = getContentValues(fieldList, dataList);

            id = database.insert(tableClass.getTableName(), null, contentValues);
            close();

            if(id <= 0){
                return false;
            }
        } catch (SQLException e){
            return false;
        } catch (Exception e){
            return false;
        }

        tableClass.id = id;
        return true;
    }



    //update
    public boolean update(TableClass tableClass) {
        long affectedRows = -1;
        try {
            SQLiteDatabase database = getWritableDatabase();

            Table table = tableMap.get(tableClass.getTableName());

            List<Object>  dataList = new ArrayList<>();
            tableClass.getData(dataList);

            List<Field> fieldList = table.getFieldList();

            ContentValues contentValues = getContentValues(fieldList, dataList);

            affectedRows = database.update(tableClass.getTableName(), contentValues, ID+"=?",
                    new String[]{Long.toString(tableClass.id)});
            close();

            if(affectedRows <= 0){
                return false;
            }
        } catch (SQLException e){
            return false;
        } catch (Exception e){
            return false;
        }
        return true;
    }


    //delete
    public boolean deleteById(TableClass tableClass) {
        if(tableClass.id > 0) {
            try {
                SQLiteDatabase database = getWritableDatabase();
                int affectedRows = database.delete(tableClass.getTableName(), ID + "=?",
                        new String[]{Long.toString(tableClass.id)});
                close();
                if(affectedRows <= 0){
                    return false;
                }
            } catch (SQLException e){
                return false;
            } catch (Exception e){
                return false;
            }

            return true;
        }
        return true;
    }


    private List<Object> fetchRow(Cursor cursor, String tableName){
        List<Object> dataList = new ArrayList<>();
        List<Field> fieldList =  tableMap.get(tableName).getFieldList();
        for (int i = 0; i < fieldList.size(); i++) {
            Field field = fieldList.get(i);
            if(field.getType().equals(Field.TEXT)){
                dataList.add(cursor.getString(cursor.getColumnIndex(field.getName())));
            } else if(field.getType().equals(Field.INTEGER)){
                if(field.getTrueType() == int.class) {
                    dataList.add(cursor.getInt(cursor.getColumnIndex(field.getName())));
                } else if(field.getTrueType() == long.class){
                    dataList.add(cursor.getLong(cursor.getColumnIndex(field.getName())));
                } else if(field.getTrueType() == boolean.class){
                    dataList.add(cursor.getInt(cursor.getColumnIndex(field.getName())) == 1);
                }
            } else if(field.getType().equals(Field.REAL)){
                if(field.getTrueType() == float.class) {
                    dataList.add(cursor.getFloat(cursor.getColumnIndex(field.getName())));
                } else if(field.getTrueType() == double.class){
                    dataList.add(cursor.getDouble(cursor.getColumnIndex(field.getName())));
                }
            } else if(field.getType().equals(Field.BLOB)){
                //
            }

        }
        return dataList;
    }


    public TableClass findById(long id, String tableName, Class clazz){
        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "SELECT * FROM "+tableName+" WHERE "+ID+"=?";
            Cursor cursor = database.rawQuery(sql, new String[]{Long.toString(id)});
            cursor.moveToFirst();

            List<Object> dataList = fetchRow(cursor, tableName);

            TableClass tableClass = (TableClass) clazz.newInstance();
            tableClass.id = cursor.getLong(cursor.getColumnIndex(ID));
            tableClass.setData(dataList);

            cursor.close();
            close();

            return tableClass;
        } catch (SQLException e){
            return null;
        } catch (Exception e){
            return null;
        }
    }

    public List<Object> findAll(String tableName, Class clazz) {
        try {
            SQLiteDatabase database = getWritableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM " + tableName, null);
            cursor.moveToFirst();

            List<Object> resultList = new ArrayList<>();

            while (!cursor.isAfterLast()) {
                List<Object> dataList = fetchRow(cursor, tableName);

                TableClass tableClass = (TableClass) clazz.newInstance();
                tableClass.id = cursor.getLong(cursor.getColumnIndex(ID));
                tableClass.setData(dataList);

                resultList.add(clazz.cast(tableClass));

                cursor.moveToNext();
            }

            cursor.close();
            close();

            return resultList;
        } catch (SQLException e){
            return null;
        } catch (Exception e){
            return null;
        }
    }



    public static class Table{
        private String name;
        private List<Field> fieldList = new ArrayList<>();

        public Table(String name) {
            this.name = name;
        }

        public Table(Class clazz) {
            this.name = clazz.getSimpleName();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Field> getFieldList() {
            return fieldList;
        }

        public Table addIntField(String name){
            Field field = new Field(name, Field.INTEGER, int.class);
            fieldList.add(field);
            return this;
        }

        public Table addLongField(String name){
            Field field = new Field(name, Field.INTEGER, long.class);
            fieldList.add(field);
            return this;
        }

        public Table addStringField(String name){
            Field field = new Field(name, Field.TEXT, String.class);
            fieldList.add(field);
            return this;
        }

        public Table addFloatField(String name){
            Field field = new Field(name, Field.REAL, float.class);
            fieldList.add(field);
            return this;
        }

        public Table addDoubleField(String name){
            Field field = new Field(name, Field.REAL, double.class);
            fieldList.add(field);
            return this;
        }

        public Table addBooleanField(String name){
            Field field = new Field(name, Field.INTEGER, boolean.class);
            fieldList.add(field);
            return this;
        }
    }


    public static class Field{
        public static final String TEXT = "TEXT";
        public static final String INTEGER = "INTEGER";
        public static final String REAL = "REAL";
        public static final String BLOB = "BLOB";

        private String name;
        private String type;
        private Class trueType;

        public Field(String name, String type, Class trueType) {
            this.name = name;
            this.type = type;
            this.trueType = trueType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Class getTrueType() {
            return trueType;
        }

        public void setTrueType(Class trueType) {
            this.trueType = trueType;
        }
    }


    public static class Builder {
        private Context context;
        private String databaseName;
        private int databaseVersion;
        private Map<String, Table> tableMap = new HashMap<>();

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setDatabaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        public Builder setDatabaseVersion(int version) {
            this.databaseVersion = version;
            return this;
        }

        public SQLiteWrapper create() {
            return new SQLiteWrapper(context, databaseName, databaseVersion, tableMap);
        }
    }


    public static class TableClass {
        public long id;
        protected String getTableName(){
            return this.getClass().getSimpleName();
        }
        protected void setData(List<Object> dataList){}
        protected void getData(List<Object> dataList){}

        public boolean save(String databaseName){
             return SQLiteWrapper.getInstance(databaseName).save(this);
        }

        public boolean update(String databaseName){
            return SQLiteWrapper.getInstance(databaseName).update(this);
        }

        public boolean deleteById(String databaseName){
            return SQLiteWrapper.getInstance(databaseName).deleteById(this);
        }
    }

}
