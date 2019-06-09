package com.zaitunlabs.zlcore.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SQLiteWrapper extends SQLiteOpenHelper {
    private static final String ID = "_id";
    private static final String TAG = SQLiteWrapper.class.getName();
    private static final String SQLW_FOLDER = "SQLW/";
    private Map<String, Table> tableMap;
    private Context context;

    public static Map<String, Database> sqLiteDatabaseMap = new HashMap<>();
    public static Map<String, SQLiteWrapper> sqLiteWrapperMap = new HashMap<>();

    public static void addDatabase(Database database){
        if(!sqLiteDatabaseMap.containsKey(database.getDatabaseName())) {
            sqLiteDatabaseMap.put(database.getDatabaseName(), database);
        }
    }

    public static void removeDatabase(String databaseName){
        sqLiteWrapperMap.remove(databaseName);
        sqLiteDatabaseMap.remove(databaseName);
    }

    public static abstract class Database {
        public abstract String getDatabaseName();
        public abstract int getDatabaseVersion();
        public abstract boolean isWrapperCached();
        public abstract void configure(SQLiteWrapper sqLiteWrapper);
        private SQLiteWrapper getSQLiteWrapper(Context context){
            if(sqLiteWrapperMap.containsKey(getDatabaseName())){
                return sqLiteWrapperMap.get(getDatabaseName());
            } else {
                SQLiteWrapper sqLiteWrapper = new SQLiteWrapper.Builder()
                        .setContext(context)
                        .setDatabaseName(getDatabaseName())
                        .setDatabaseVersion(getDatabaseVersion())
                        .create();
                configure(sqLiteWrapper);
                sqLiteWrapper.init();
                if(isWrapperCached()){
                    sqLiteWrapperMap.put(getDatabaseName(),sqLiteWrapper);
                }
                return sqLiteWrapper;
            }
        }
    }

    public SQLiteWrapper addTable(Table table){
        tableMap.put(table.getName(), table);
        return this;
    }


    public SQLiteWrapper addTablesFromSQLAsset(String filename){
        List<Table> tableList = readAndParseCreateTableScript(context, filename);
        if(tableList != null) {
            for (Table table : tableList) {
                tableMap.put(table.getName(), table);
            }
        }
        return this;
    }

    public static SQLiteWrapper getInstance(Context context, String databaseName){
        if(sqLiteDatabaseMap.containsKey(databaseName)){
            return sqLiteDatabaseMap.get(databaseName).getSQLiteWrapper(context);
        }
        return null;
    }

    private void init(){
    }


    private void deInit(){
    }

    private SQLiteWrapper(Context context, String databaseName, int version, Map<String, Table> tableMap){
        super(context, databaseName, null, version);
        this.context = context;
        this.tableMap = tableMap;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(tableMap == null){
            return;
        }

        List<String> createTableList = new ArrayList<>();
        Set<String> keySet = tableMap.keySet();
        for (String key : keySet) {
            Table table = tableMap.get(key);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("CREATE TABLE");
            stringBuilder.append(" ").append(table.getName()).append(" (");
            stringBuilder.append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT");

            List<Field> fieldList = table.getFieldList();
            for (Field field : fieldList) {
                stringBuilder.append(",");
                stringBuilder.append(field.getName()).append(" ").append(field.getType());
                if (field.getTrueType() == boolean.class) {
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
        try {
            for (int i = oldVersion; i < newVersion; ++i) {
                String migrationFileName = String.format("%d-%d.sql", i, (i + 1));
                Log.d(TAG, "Looking for migration file: " + migrationFileName);
                readAndExecuteSQLScript(db, context, migrationFileName);
            }
        } catch (Exception exception) {
            Log.e(TAG, "Exception running upgrade script:", exception);
        }
    }


    private void release(){
        tableMap = null;
    }


    private ContentValues getContentValues(List<Field> fieldList, List<Object> dataList){
        ContentValues contentValues = new ContentValues();

        for (int i = 0; i < fieldList.size(); i++) {
            Field field = fieldList.get(i);
            switch (field.getType()) {
                case Field.TEXT:
                    contentValues.put(field.getName(), (String) dataList.get(i));
                    break;
                case Field.INTEGER:
                    if (field.getTrueType() == int.class) {
                        contentValues.put(field.getName(), (int) dataList.get(i));
                    } else if (field.getTrueType() == long.class) {
                        contentValues.put(field.getName(), (long) dataList.get(i));
                    } else if (field.getTrueType() == boolean.class) {
                        contentValues.put(field.getName(), (boolean) dataList.get(i));
                    }
                    break;
                case Field.REAL:
                    if (field.getTrueType() == float.class) {
                        contentValues.put(field.getName(), (float) dataList.get(i));
                    } else if (field.getTrueType() == double.class) {
                        contentValues.put(field.getName(), (double) dataList.get(i));
                    }
                    break;
                case Field.BLOB:
                    //contentValues.put(field.getName(), (String) objectList.get(i));
                    break;
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
    public boolean delete(TableClass tableClass) {
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
        return false;
    }


    private List<Object> fetchRow(Cursor cursor, String tableName){
        List<Object> dataList = new ArrayList<>();
        List<Field> fieldList =  tableMap.get(tableName).getFieldList();
        for (int i = 0; i < fieldList.size(); i++) {
            Field field = fieldList.get(i);
            switch (field.getType()) {
                case Field.TEXT:
                    dataList.add(cursor.getString(cursor.getColumnIndex(field.getName())));
                    break;
                case Field.INTEGER:
                    if (field.getTrueType() == int.class) {
                        dataList.add(cursor.getInt(cursor.getColumnIndex(field.getName())));
                    } else if (field.getTrueType() == long.class) {
                        dataList.add(cursor.getLong(cursor.getColumnIndex(field.getName())));
                    } else if (field.getTrueType() == boolean.class) {
                        dataList.add(cursor.getInt(cursor.getColumnIndex(field.getName())) == 1);
                    }
                    break;
                case Field.REAL:
                    if (field.getTrueType() == float.class) {
                        dataList.add(cursor.getFloat(cursor.getColumnIndex(field.getName())));
                    } else if (field.getTrueType() == double.class) {
                        dataList.add(cursor.getDouble(cursor.getColumnIndex(field.getName())));
                    }
                    break;
                case Field.BLOB:
                    //
                    break;
            }

        }
        return dataList;
    }


    public TableClass findById(long id, String tableName, Class clazz){
        try {
            if(TextUtils.isEmpty(tableName)){
                tableName = clazz.getSimpleName();
            }
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

    public List<? extends TableClass> findAll(String tableName, Class clazz) {
        try {
            if(TextUtils.isEmpty(tableName)){
                tableName = clazz.getSimpleName();
            }

            SQLiteDatabase database = getWritableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM " + tableName, null);
            cursor.moveToFirst();

            List<TableClass> resultList = new ArrayList<>();

            while (!cursor.isAfterLast()) {
                List<Object> dataList = fetchRow(cursor, tableName);

                TableClass tableClass = (TableClass) clazz.newInstance();
                tableClass.id = cursor.getLong(cursor.getColumnIndex(ID));
                tableClass.setData(dataList);

                resultList.add(tableClass);

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

        public Table addField(Field field){
            fieldList.add(field);
            return this;
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
            this.context = context.getApplicationContext();
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
        protected String getDatabaseName(){
            return null;
        }
        protected void setData(List<Object> dataList){}
        protected void getData(List<Object> dataList){}

        public boolean save(Context context, String databaseName){
             return SQLiteWrapper.getInstance(context, databaseName).save(this);
        }

        public boolean save(Context context){
            return SQLiteWrapper.getInstance(context, getDatabaseName()).save(this);
        }

        public boolean update(Context context, String databaseName){
            return SQLiteWrapper.getInstance(context, databaseName).update(this);
        }

        public boolean update(Context context){
            return SQLiteWrapper.getInstance(context, getDatabaseName()).update(this);
        }

        public boolean delete(Context context, String databaseName){
            return SQLiteWrapper.getInstance(context, databaseName).delete(this);
        }

        public boolean delete(Context context){
            return SQLiteWrapper.getInstance(context, getDatabaseName()).delete(this);
        }

        public static TableClass findById(Context context, String databaseName, String tableName, Class clazz, long id){
            if(TextUtils.isEmpty(tableName)){
                tableName = clazz.getSimpleName();
            }
            return SQLiteWrapper.getInstance(context, databaseName).findById(id, tableName, clazz);
        }

        public static List<? extends TableClass> findAll(Context context, String databaseName, String tableName, Class clazz){
            if(TextUtils.isEmpty(tableName)){
                tableName = clazz.getSimpleName();
            }
            return SQLiteWrapper.getInstance(context, databaseName).findAll(tableName, clazz);
        }
    }




    private List<Table> readAndParseCreateTableScript(Context context, String fileName) {
        List<Table> tableList = new ArrayList<>();

        if (TextUtils.isEmpty(fileName)) {
            Log.d(TAG, "Create SQL script file name is empty");
            return null;
        }

        Log.d(TAG, "Create Script found. Executing...");
        AssetManager assetManager = context.getAssets();
        BufferedReader reader = null;

        try {
            InputStream is = assetManager.open(SQLW_FOLDER+fileName);
            InputStreamReader isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);

            String line;
            StringBuilder statement = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                statement.append(line);
                statement.append("\n");

                if (line.endsWith(";")) {
                    String singleScript = statement.toString();
                    String tableName = substringBetween("CREATE TABLE","(", singleScript);
                    String fieldsString = substringBetween("(", ")", singleScript);
                    String[] fieldArray = fieldsString.split(",");

                    Table table = new Table(tableName);

                    for (String fieldString : fieldArray){
                        if(fieldString.contains("PRIMARY KEY"))continue;

                        String[] fieldParts = fieldString.split(" ");

                        switch (fieldParts[1]) {
                            case Field.INTEGER:
                                if(fieldString.contains("DEFAULT 1") || fieldString.contains("DEFAULT 0")){
                                    //case boolean
                                    table.addBooleanField(fieldParts[0]);
                                } else {
                                    table.addIntField(fieldParts[0]);
                                }
                                break;
                            case Field.TEXT:
                                table.addStringField(fieldParts[0]);
                                break;
                            case Field.REAL:
                                table.addDoubleField(fieldParts[0]);
                                break;
                            case Field.BLOB:
                                //
                                break;
                        }
                    }



                    tableList.add(table);
                    statement = new StringBuilder();
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "Create IOException:", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Create IOException:", e);
                }
            }
        }

        return tableList;
    }

    private static String substringBetween(String start, String end, String input) {
        int startIndex = input.indexOf(start);
        int endIndex = input.lastIndexOf(end);
        if(startIndex == -1 || endIndex == -1) return input;
        else return input.substring(startIndex + start.length(), endIndex + end.length()).trim();
    }


    private void readAndExecuteSQLScript(SQLiteDatabase db, Context context, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Log.d(TAG, "Upgrade SQL script file name is empty");
            return;
        }

        Log.d(TAG, "Upgrade Script found. Executing...");
        AssetManager assetManager = context.getAssets();
        BufferedReader reader = null;

        try {
            InputStream is = assetManager.open(SQLW_FOLDER+fileName);
            InputStreamReader isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);
            executeSQLScript(db, reader);
        } catch (IOException e) {
            Log.e(TAG, "Upgrade IOException:", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Upgrade IOException:", e);
                }
            }
        }

    }

    private void executeSQLScript(SQLiteDatabase db, BufferedReader reader) throws IOException {
        String line;
        StringBuilder statement = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            statement.append(line);
            statement.append("\n");
            if (line.endsWith(";")) {
                db.execSQL(statement.toString());
                statement = new StringBuilder();
            }
        }
    }
}
