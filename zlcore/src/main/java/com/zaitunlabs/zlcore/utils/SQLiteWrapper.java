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


public final class SQLiteWrapper extends SQLiteOpenHelper {
    private static final String ID = "_id";
    private static final String TAG = SQLiteWrapper.class.getName();
    private static final String SQLW_FOLDER = "SQLW/";
    private Map<String, Table> tableMap;
    private AssetManager assetManager;

    private static Map<String, Database> sqLiteDatabaseMap = new HashMap<>();
    private static Map<String, SQLiteWrapper> sqLiteWrapperMap = new HashMap<>();

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

        public Database(){
        }

        public abstract Context getAppContext();
        public abstract String getDatabaseName();
        public abstract int getDatabaseVersion();
        public abstract boolean isWrapperCached();
        public abstract void configure(SQLiteWrapper sqLiteWrapper);
        private SQLiteWrapper getSQLiteWrapper(){
            if(sqLiteWrapperMap.containsKey(getDatabaseName())){
                return sqLiteWrapperMap.get(getDatabaseName());
            } else {
                SQLiteWrapper sqLiteWrapper = new SQLiteWrapper.Builder()
                        .setDatabaseName(getDatabaseName())
                        .setDatabaseVersion(getDatabaseVersion())
                        .create(getAppContext());
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
        List<Table> tableList = readAndParseCreateTableScript(filename);
        if(tableList != null) {
            for (Table table : tableList) {
                tableMap.put(table.getName(), table);
            }
        }
        return this;
    }

    public static SQLiteWrapper of(String databaseName){
        if(sqLiteDatabaseMap.containsKey(databaseName)){
            return sqLiteDatabaseMap.get(databaseName).getSQLiteWrapper();
        }
        return null;
    }

    private void init(){
    }


    private void deInit(){
    }

    private SQLiteWrapper(Context context, String databaseName, int databaseVersion, Map<String, Table> tableMap){
        super(context, databaseName, null, databaseVersion);
        this.assetManager = context.getAssets();
        this.tableMap = tableMap;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(tableMap == null){
            return;
        }

        List<String> createTableScriptList = new ArrayList<>();
        Set<String> keySet = tableMap.keySet();
        for (String key : keySet) {
            Table table = tableMap.get(key);
            if(table != null) {
                String createTableScript = getCreateTableScript(table);
                createTableScriptList.add(createTableScript);
            }
        }

        db.beginTransaction();
        try{
            for (String createTableScript : createTableScriptList){
                db.execSQL(createTableScript);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }


    private String getCreateTableScript(Table table){
        if(table == null) return null;
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

        return stringBuilder.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            List<String> upgradeScriptList = new ArrayList<>();
            for (int i = oldVersion; i < newVersion; ++i) {
                if(migrationPlan != null){
                    List<MigrationStep> migrationStepList = migrationPlan.getUpgradePlan(i,(i+1));
                    if(migrationStepList != null && migrationStepList.size() > 0){
                        for (MigrationStep migrationStep : migrationStepList){
                            List<String> sqlScriptList = migrationStep.getSQLScriptList(this);
                            if(sqlScriptList != null && sqlScriptList.size() > 0) {
                                for (String sqlScript : sqlScriptList) {
                                    if(!TextUtils.isEmpty(sqlScript)) {
                                        upgradeScriptList.add(sqlScript);
                                    }
                                }
                            }
                        }
                        continue;
                    }
                }

                String migrationFileName = String.format("%d-%d-%s.sql", i, (i + 1), getDatabaseName());
                Log.d(TAG, "Looking for migration file: " + migrationFileName);
                List<String> resultList = readAndGetSQLScript(migrationFileName);
                upgradeScriptList.addAll(resultList);

            }

            db.beginTransaction();
            try{
                for (String upgradeScript : upgradeScriptList){
                    db.execSQL(upgradeScript);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

        } catch (Exception exception) {
            Log.e(TAG, "Exception running upgrade script:", exception);
        }
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            List<String> downgradeScriptList = new ArrayList<>();
            for (int i = oldVersion; i > newVersion; --i) {
                if(migrationPlan != null){
                    List<MigrationStep> migrationStepList = migrationPlan.getDowngradePlan(i,(i-1));
                    if(migrationStepList != null && migrationStepList.size() > 0){
                        for (MigrationStep migrationStep : migrationStepList){
                            List<String> sqlScriptList = migrationStep.getSQLScriptList(this);
                            if(sqlScriptList != null && sqlScriptList.size() > 0) {
                                for (String sqlScript : sqlScriptList) {
                                    if(!TextUtils.isEmpty(sqlScript)) {
                                        downgradeScriptList.add(sqlScript);
                                    }
                                }
                            }
                        }
                        continue;
                    }
                }

                String migrationFileName = String.format("%d-%d-%s.sql", i, (i - 1), getDatabaseName());
                Log.d(TAG, "Looking for migration file: " + migrationFileName);
                List<String> resultList = readAndGetSQLScript(migrationFileName);
                downgradeScriptList.addAll(resultList);

            }

            db.beginTransaction();
            try{
                for (String downgradeScript : downgradeScriptList){
                    db.execSQL(downgradeScript);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

        } catch (Exception exception) {
            Log.e(TAG, "Exception running downgrade script:", exception);
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
                    //contentValues.put(field.getName(), (String) objectList.of(i));
                    break;
            }
        }

        return contentValues;
    }

    //create or insert
    private boolean save(TableClass tableClass) {
        long id = -1;
        try {
            SQLiteDatabase database = getWritableDatabase();

            int version = database.getVersion();

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
        }

        tableClass.id = id;
        return true;
    }



    //update
    private boolean update(TableClass tableClass) {
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
        }
        return true;
    }


    //delete
    private boolean delete(TableClass tableClass) {
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

    private RuntimeException getWarningWhenNoConstructorWithNoArgument(String className){
        return new RuntimeException(String.format(
                "Class %s must has constructor with no argument",className));
    }

    public <T extends TableClass> T findById(long id, String tableName, Class<T> clazz){
        try {
            if(TextUtils.isEmpty(tableName)){
                tableName = clazz.getSimpleName();
            }
            SQLiteDatabase database = getWritableDatabase();
            String sql = "SELECT * FROM "+tableName+" WHERE "+ID+"=?";
            Cursor cursor = database.rawQuery(sql, new String[]{Long.toString(id)});
            cursor.moveToFirst();

            List<Object> dataList = fetchRow(cursor, tableName);

            T tableClass = clazz.newInstance();
            tableClass.id = cursor.getLong(cursor.getColumnIndex(ID));
            tableClass.setData(dataList);

            cursor.close();
            close();

            return tableClass;
        } catch (SQLException e){
            return null;
        } catch (InstantiationException e){
            throw getWarningWhenNoConstructorWithNoArgument(clazz.getSimpleName());
        } catch (IllegalAccessException e){
            return null;
        }
    }

    public <T extends TableClass> List<T> findAll(String tableName, Class<T> clazz) {
        try {
            if(TextUtils.isEmpty(tableName)){
                tableName = clazz.getSimpleName();
            }

            SQLiteDatabase database = getWritableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM " + tableName, null);
            cursor.moveToFirst();

            List<T> resultList = new ArrayList<>();

            while (!cursor.isAfterLast()) {
                List<Object> dataList = fetchRow(cursor, tableName);

                T tableClass = clazz.newInstance();
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
        } catch (InstantiationException e){
            throw getWarningWhenNoConstructorWithNoArgument(clazz.getSimpleName());
        } catch (IllegalAccessException e){
            return null;
        }
    }

    public <T extends TableClass> T findFirst(String tableName, Class<T> clazz) {
        List<T> resultList = findAll(tableName, clazz);
        if(resultList != null && resultList.size() > 0){
            return resultList.get(0);
        }
        return  null;
    }

    public <T extends TableClass> T findLast(String tableName, Class<T> clazz) {
        List<T> resultList = findAll(tableName, clazz);
        if(resultList != null && resultList.size() > 0){
            return resultList.get(resultList.size()-1);
        }
        return  null;
    }

    public <T extends TableClass> List<T> findAllWithCriteria(String tableName, Class<T> clazz, String whereClause, String[] whereClauseArgs) {
        try {
            if(TextUtils.isEmpty(tableName)){
                tableName = clazz.getSimpleName();
            }

            SQLiteDatabase database = getWritableDatabase();
            Cursor cursor = database.rawQuery("SELECT * FROM " + tableName + " WHERE "+whereClause, whereClauseArgs);
            cursor.moveToFirst();

            List<T> resultList = new ArrayList<>();

            while (!cursor.isAfterLast()) {
                List<Object> dataList = fetchRow(cursor, tableName);

                T tableClass = clazz.newInstance();
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
        } catch (InstantiationException e){
            throw getWarningWhenNoConstructorWithNoArgument(clazz.getSimpleName());
        } catch (IllegalAccessException e){
            return null;
        }
    }

    public <T extends TableClass> T findFirstWithCriteria(String tableName, Class<T> clazz, String whereClause, String[] whereClauseArgs) {
        List<T> resultList = findAllWithCriteria(tableName, clazz, whereClause, whereClauseArgs);
        if(resultList != null && resultList.size() > 0){
            return resultList.get(0);
        }
        return  null;
    }

    public <T extends TableClass> T findLastWithCriteria(String tableName, Class<T> clazz, String whereClause, String[] whereClauseArgs) {
        List<T> resultList = findAllWithCriteria(tableName, clazz, whereClause, whereClauseArgs);
        if(resultList != null && resultList.size() > 0){
            return resultList.get(resultList.size()-1);
        }
        return  null;
    }


    public <T extends TableClass> List<T> selectQuery(boolean distinct, String tableName, Class<T> clazz, String[] columns,
                                            String selection, String[] selectionArgs, String groupBy,
                                            String having, String orderBy, String limit) {
        try {
            if(TextUtils.isEmpty(tableName)){
                tableName = clazz.getSimpleName();
            }

            SQLiteDatabase database = getWritableDatabase();
            Cursor cursor = database.query(distinct, tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            cursor.moveToFirst();

            List<T> resultList = new ArrayList<>();

            while (!cursor.isAfterLast()) {
                List<Object> dataList = fetchRow(cursor, tableName);

                T tableClass = clazz.newInstance();
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
        } catch (InstantiationException e){
            throw getWarningWhenNoConstructorWithNoArgument(clazz.getSimpleName());
        } catch (IllegalAccessException e){
            return null;
        }
    }

    public <T extends TableClass> List<T> rawQuery(Class<T> clazz, String selectSql, String[] sqlArgs) {
        try {
            String tableName = substringBetween(" from "," ", selectSql.replace("FROM","from")+" ");

            SQLiteDatabase database = getWritableDatabase();
            Cursor cursor = database.rawQuery(selectSql, sqlArgs);
            cursor.moveToFirst();

            List<T> resultList = new ArrayList<>();

            while (!cursor.isAfterLast()) {
                List<Object> dataList = fetchRow(cursor, tableName);

                T tableClass = clazz.newInstance();
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
        } catch (InstantiationException e){
            throw getWarningWhenNoConstructorWithNoArgument(clazz.getSimpleName());
        } catch (IllegalAccessException e){
            return null;
        }
    }


    public void execSQL(String sql, String[] sqlArgs) {
        try {
            SQLiteDatabase database = getWritableDatabase();
            database.execSQL(sql, sqlArgs);
            close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public <T extends TableClass> void delete(String tableName, Class<T> clazz,  String whereClause, String[] whereClauseArgs){
        if(TextUtils.isEmpty(tableName)){
            tableName = clazz.getSimpleName();
        }
        try {
            SQLiteDatabase database = getWritableDatabase();
            database.delete(tableName, whereClause, whereClauseArgs);
            close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public <T extends TableClass> void deleteAll(String tableName, Class<T> clazz) {
        if(TextUtils.isEmpty(tableName)){
            tableName = clazz.getSimpleName();
        }
        delete(tableName, clazz, null, null);
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



    private static class Builder {
        private String databaseName;
        private int databaseVersion;
        private Map<String, Table> tableMap = new HashMap<>();

        public Builder setDatabaseName(String databaseName) {
            this.databaseName = databaseName;
            return this;
        }

        public Builder setDatabaseVersion(int version) {
            this.databaseVersion = version;
            return this;
        }

        public SQLiteWrapper create(Context context) {
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

        public boolean saveIn(String databaseName){
             return SQLiteWrapper.of(databaseName).save(this);
        }

        private void checkCondition(){
            if(TextUtils.isEmpty(getDatabaseName())) {
                throw new RuntimeException(String.format(
                        "Class %s must define database name in method getDatabaseName() or use method saveIn() instead of save()",
                        getClass().getSimpleName()));
            }
        }

        public boolean save(){
            checkCondition();

            return SQLiteWrapper.of(getDatabaseName()).save(this);
        }

        public boolean updateIn(String databaseName){
            return SQLiteWrapper.of(databaseName).update(this);
        }

        public boolean update(){
            checkCondition();

            return SQLiteWrapper.of(getDatabaseName()).update(this);
        }

        public boolean deleteIn(String databaseName){
            return SQLiteWrapper.of(databaseName).delete(this);
        }

        public boolean delete(){
            checkCondition();

            return SQLiteWrapper.of(getDatabaseName()).delete(this);
        }

        public static TableClass findById(String databaseName, String tableName, Class clazz, long id){
            if(TextUtils.isEmpty(tableName)){
                tableName = clazz.getSimpleName();
            }
            return SQLiteWrapper.of(databaseName).findById(id, tableName, clazz);
        }

        public static <T extends TableClass> List<T> findAll(String databaseName, String tableName, Class<T> clazz){
            if(TextUtils.isEmpty(tableName)){
                tableName = clazz.getSimpleName();
            }
            return SQLiteWrapper.of(databaseName).findAll(tableName, clazz);
        }

        public static <T extends TableClass> List<T> findWithCriteria(String databaseName, String tableName,
                                                                  Class<T> clazz, String whereClause, String[] whereClauseArgs){
            if(TextUtils.isEmpty(tableName)){
                tableName = clazz.getSimpleName();
            }
            return SQLiteWrapper.of(databaseName).findAllWithCriteria(tableName, clazz, whereClause, whereClauseArgs);
        }

        public static <T extends TableClass> List<T> selectQuery(String databaseName, boolean distinct, String tableName,
                                                       Class<T> clazz,
                                                       String[] columns,
                                                       String selection, String[] selectionArgs, String groupBy,
                                                       String having, String orderBy, String limit){
            if(TextUtils.isEmpty(tableName)){
                tableName = clazz.getSimpleName();
            }
            return SQLiteWrapper.of(databaseName).selectQuery(distinct, tableName, clazz, columns,
                    selection, selectionArgs, groupBy, having, orderBy, limit);
        }

        public static <T extends TableClass> List<T> rawQuery(String databaseName,
                                                                    Class<T> clazz, String selectSql, String[] sqlArgs){
            return SQLiteWrapper.of(databaseName).rawQuery(clazz, selectSql, sqlArgs);
        }

        public static void execSQL(String databaseName, String sql, String[] sqlArgs){
            SQLiteWrapper.of(databaseName).execSQL(sql, sqlArgs);
        }

        public static <T extends TableClass> void deleteAll(String databaseName, String tableName, Class<T> clazz){
            SQLiteWrapper.of(databaseName).deleteAll(tableName, clazz);
        }

    }


    public void setMigrationPlan(MigrationPlan migrationPlan){
        this.migrationPlan = migrationPlan;
    }
    private MigrationPlan migrationPlan;

    public interface MigrationStep {
        List<String> getSQLScriptList(SQLiteWrapper sqLiteWrapper);
    }

    public static class RenameTableMigrationStep implements MigrationStep{
        private String oldTableName;
        private String newTableName;

        public RenameTableMigrationStep(String oldTableName, String newTableName){
            this.oldTableName = oldTableName;
            this.newTableName = newTableName;
        }

        @Override
        public List<String> getSQLScriptList(SQLiteWrapper sqLiteWrapper) {
            List<String> sqlScriptList = new ArrayList<>();
            sqlScriptList.add(String.format("ALTER TABLE %s RENAME TO %s", oldTableName, newTableName));
            return sqlScriptList;
        }
    }


    public static class AddTableMigrationStep implements MigrationStep{
        private String tableName;
        public AddTableMigrationStep(String tableName){
            this.tableName = tableName;
        }

        @Override
        public List<String> getSQLScriptList(SQLiteWrapper sqLiteWrapper) {
            List<String> sqlScriptList = new ArrayList<>();
            Table table = sqLiteWrapper.tableMap.get(tableName);
            sqlScriptList.add(sqLiteWrapper.getCreateTableScript(table));
            return sqlScriptList;
        }
    }


    public static class RemoveTableMigrationStep implements MigrationStep{
        private String tableName;
        public RemoveTableMigrationStep(String tableName){
            this.tableName = tableName;
        }

        @Override
        public List<String> getSQLScriptList(SQLiteWrapper sqLiteWrapper) {
            List<String> sqlScriptList = new ArrayList<>();
            sqlScriptList.add(String.format("DROP TABLE %s", tableName));
            return sqlScriptList;
        }
    }


    public interface MigrationPlan {
        List<MigrationStep> getUpgradePlan(int oldVersion, int newVersion);
        List<MigrationStep> getDowngradePlan(int oldVersion, int newVersion);
    }



    private List<Table> readAndParseCreateTableScript(String fileName) {
        List<Table> tableList = new ArrayList<>();

        if (TextUtils.isEmpty(fileName)) {
            Log.d(TAG, "Create SQL script file name is empty");
            return null;
        }

        Log.d(TAG, "Create Script found. Executing...");
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

                    singleScript = singleScript
                            .replace("CREATE TABLE", "create table")
                            .replace("PRIMARY KEY", "primary key")
                            .replace("DEFAULT", "default");

                    String tableName = substringBetween("create table","(", singleScript);
                    String fieldsString = substringBetween("(", ")", singleScript);
                    String[] fieldArray = fieldsString.split(",");

                    Table table = new Table(tableName);

                    for (String fieldString : fieldArray){
                        if(fieldString.contains("primary key"))continue;

                        String[] fieldParts = fieldString.split(" ");

                        switch (fieldParts[1]) {
                            case Field.INTEGER:
                                if(fieldString.contains("default 1") || fieldString.contains("default 0")){
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
        int endIndex = input.indexOf(end, startIndex + start.length());
        if(startIndex == -1 || endIndex == -1) return input;
        else return input.substring(startIndex + start.length(), endIndex).trim();
    }


    private List<String> readAndGetSQLScript(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Log.d(TAG, "Upgrade SQL script file name is empty");
            return null;
        }

        List<String> scriptList = new ArrayList<>();

        Log.d(TAG, "Upgrade Script found. Executing...");
        BufferedReader reader = null;

        try {
            InputStream is = assetManager.open(SQLW_FOLDER+fileName);
            InputStreamReader isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);

            List<String> resultList = getSQLScript(reader);

            if(resultList != null && resultList.size() > 0) {
                scriptList.addAll(resultList);
            }

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

        return scriptList;
    }

    private List<String> getSQLScript(BufferedReader reader) throws IOException {
        String line;
        StringBuilder statement = new StringBuilder();

        List<String> scriptList = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            statement.append(line);
            statement.append(" ");
            if (line.endsWith(";")) {
                scriptList.add(statement.toString());
                statement = new StringBuilder();
            }
        }

        return scriptList;
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


    private static String LOOKUP_DATABASE_NAME = "sqlwlookup.db";
    private static void enableLookupDatabase(final Context context){
        addDatabase(new Database() {
            @Override
            public Context getAppContext() {
                return context.getApplicationContext();
            }

            @Override
            public String getDatabaseName() {
                return LOOKUP_DATABASE_NAME;
            }

            @Override
            public int getDatabaseVersion() {
                return 1;
            }

            @Override
            public boolean isWrapperCached() {
                return true;
            }

            @Override
            public void configure(SQLiteWrapper sqLiteWrapper) {
                sqLiteWrapper.addTable(new Table(TLookup.class)
                        .addStringField("key")
                        .addStringField("string")
                        .addBooleanField("boolean")
                        .addIntField("integer")
                        .addLongField("long")
                        .addFloatField("float")
                        .addDoubleField("double"));
            }
        });
    }


    public static SQLiteWrapper getLookupDatabase(Context context){
        SQLiteWrapper sqLiteWrapper = SQLiteWrapper.of(LOOKUP_DATABASE_NAME);
        if(sqLiteWrapper == null){
            SQLiteWrapper.enableLookupDatabase(context);
            sqLiteWrapper = SQLiteWrapper.of(LOOKUP_DATABASE_NAME);
        }
        return sqLiteWrapper;
    }

    public static class TLookup extends TableClass{
        private String key;
        private String string;
        private boolean aBoolean;
        private int anInt;
        private long aLong;
        private float aFloat;
        private double aDouble;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public boolean getBoolean() {
            return aBoolean;
        }

        public void setBoolean(boolean aBoolean) {
            this.aBoolean = aBoolean;
        }

        public int getInt() {
            return anInt;
        }

        public void setInt(int anInt) {
            this.anInt = anInt;
        }

        public long getLong() {
            return aLong;
        }

        public void setLong(long aLong) {
            this.aLong = aLong;
        }

        public float getFloat() {
            return aFloat;
        }

        public void setFloat(float aFloat) {
            this.aFloat = aFloat;
        }

        public double getDouble() {
            return aDouble;
        }

        public void setDouble(double aDouble) {
            this.aDouble = aDouble;
        }

        @Override
        protected String getDatabaseName() {
            return LOOKUP_DATABASE_NAME;
        }

        @Override
        protected void getData(List<Object> dataList) {
            dataList.add(key);
            dataList.add(string);
            dataList.add(aBoolean);
            dataList.add(anInt);
            dataList.add(aLong);
            dataList.add(aFloat);
            dataList.add(aDouble);
        }

        @Override
        protected void setData(List<Object> dataList) {
            key = (String) dataList.get(0);
            string = (String) dataList.get(1);
            aBoolean = (boolean) dataList.get(2);
            anInt = (int) dataList.get(3);
            aLong = (long) dataList.get(4);
            aFloat = (float) dataList.get(5);
            aDouble = (double) dataList.get(6);
        }
    }
}
