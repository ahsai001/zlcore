package com.zaitunlabs.zlcore.utils

import android.content.ContentValues
import android.content.Context
import android.content.res.AssetManager
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.text.TextUtils
import android.util.Log

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.HashMap


class SQLiteWrapper private constructor(context: Context, databaseName: String, databaseVersion: Int, private var tableMap: MutableMap<String, Table>?) : SQLiteOpenHelper(context, databaseName, null, databaseVersion) {
    private val assetManager: AssetManager
    private var migrationPlan: MigrationPlan? = null

    abstract class Database {

        abstract val appContext: Context
        abstract val databaseName: String
        abstract val databaseVersion: Int
        abstract val isWrapperCached: Boolean
        private val sqLiteWrapper: SQLiteWrapper?
            get() {
                if (sqLiteWrapperMap.containsKey(databaseName)) {
                    return sqLiteWrapperMap[databaseName]
                } else {
                    val sqLiteWrapper = SQLiteWrapper.Builder()
                            .setDatabaseName(databaseName)
                            .setDatabaseVersion(databaseVersion)
                            .create(appContext)
                    configure(sqLiteWrapper)
                    sqLiteWrapper.init()
                    if (isWrapperCached) {
                        sqLiteWrapperMap[databaseName] = sqLiteWrapper
                    }
                    return sqLiteWrapper
                }
            }

        abstract fun configure(sqLiteWrapper: SQLiteWrapper)
    }

    fun addTable(table: Table): SQLiteWrapper {
        tableMap!![table.name!!] = table
        return this
    }


    fun addTablesFromSQLAsset(filename: String): SQLiteWrapper {
        val tableList = readAndParseCreateTableScript(filename)
        if (tableList != null) {
            for (table in tableList) {
                tableMap!![table.name!!] = table
            }
        }
        return this
    }

    private fun init() {}


    private fun deInit() {}

    init {
        this.assetManager = context.assets
    }

    override fun onCreate(db: SQLiteDatabase) {
        if (tableMap == null) {
            return
        }

        val createTableScriptList = ArrayList<String>()
        val keySet = tableMap!!.keys
        for (key in keySet) {
            val table = tableMap!![key]
            if (table != null) {
                val createTableScript = getCreateTableScript(table)
                createTableScriptList.add(createTableScript)
            }
        }

        db.beginTransaction()
        try {
            for (createTableScript in createTableScriptList) {
                db.execSQL(createTableScript)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }


    private fun getCreateTableScript(table: Table?): String? {
        if (table == null) return null
        val stringBuilder = StringBuilder()
        stringBuilder.append("CREATE TABLE")
        stringBuilder.append(" ").append(table.name).append(" (")
        stringBuilder.append(ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT")

        val fieldList = table.getFieldList()
        for (field in fieldList) {
            stringBuilder.append(",")
            stringBuilder.append(field.name).append(" ").append(field.type)
            if (field.trueType == Boolean::class.javaPrimitiveType) {
                stringBuilder.append(" DEFAULT 0")
            }
        }

        stringBuilder.append(")")

        return stringBuilder.toString()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            val upgradeScriptList = ArrayList<String>()
            for (i in oldVersion until newVersion) {
                if (migrationPlan != null) {
                    val migrationStepList = migrationPlan!!.getUpgradePlan(i, i + 1)
                    if (migrationStepList != null && migrationStepList.size > 0) {
                        for (migrationStep in migrationStepList) {
                            val sqlScriptList = migrationStep.getSQLScriptList(this)
                            if (sqlScriptList != null && sqlScriptList.size > 0) {
                                for (sqlScript in sqlScriptList) {
                                    if (!TextUtils.isEmpty(sqlScript)) {
                                        upgradeScriptList.add(sqlScript)
                                    }
                                }
                            }
                        }
                        continue
                    }
                }

                val migrationFileName = String.format("%d-%d-%s.sql", i, i + 1, databaseName)
                Log.d(TAG, "Looking for migration file: $migrationFileName")
                val resultList = readAndGetSQLScript(migrationFileName)
                upgradeScriptList.addAll(resultList!!)

            }

            db.beginTransaction()
            try {
                for (upgradeScript in upgradeScriptList) {
                    db.execSQL(upgradeScript)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }

        } catch (exception: Exception) {
            Log.e(TAG, "Exception running upgrade script:", exception)
        }

    }


    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            val downgradeScriptList = ArrayList<String>()
            for (i in oldVersion downTo newVersion + 1) {
                if (migrationPlan != null) {
                    val migrationStepList = migrationPlan!!.getDowngradePlan(i, i - 1)
                    if (migrationStepList != null && migrationStepList.size > 0) {
                        for (migrationStep in migrationStepList) {
                            val sqlScriptList = migrationStep.getSQLScriptList(this)
                            if (sqlScriptList != null && sqlScriptList.size > 0) {
                                for (sqlScript in sqlScriptList) {
                                    if (!TextUtils.isEmpty(sqlScript)) {
                                        downgradeScriptList.add(sqlScript)
                                    }
                                }
                            }
                        }
                        continue
                    }
                }

                val migrationFileName = String.format("%d-%d-%s.sql", i, i - 1, databaseName)
                Log.d(TAG, "Looking for migration file: $migrationFileName")
                val resultList = readAndGetSQLScript(migrationFileName)
                downgradeScriptList.addAll(resultList!!)

            }

            db.beginTransaction()
            try {
                for (downgradeScript in downgradeScriptList) {
                    db.execSQL(downgradeScript)
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }

        } catch (exception: Exception) {
            Log.e(TAG, "Exception running downgrade script:", exception)
        }

    }

    private fun release() {
        tableMap = null
    }


    private fun getContentValues(fieldList: List<Field>, dataList: List<Any>): ContentValues {
        val contentValues = ContentValues()

        for (i in fieldList.indices) {
            val field = fieldList[i]
            when (field.type) {
                Field.TEXT -> contentValues.put(field.name, dataList[i] as String)
                Field.INTEGER -> if (field.trueType == Int::class.javaPrimitiveType) {
                    contentValues.put(field.name, dataList[i] as Int)
                } else if (field.trueType == Long::class.javaPrimitiveType) {
                    contentValues.put(field.name, dataList[i] as Long)
                } else if (field.trueType == Boolean::class.javaPrimitiveType) {
                    contentValues.put(field.name, dataList[i] as Boolean)
                }
                Field.REAL -> if (field.trueType == Float::class.javaPrimitiveType) {
                    contentValues.put(field.name, dataList[i] as Float)
                } else if (field.trueType == Double::class.javaPrimitiveType) {
                    contentValues.put(field.name, dataList[i] as Double)
                }
                Field.BLOB -> {
                }
            }//contentValues.put(field.getName(), (String) objectList.of(i));
        }

        return contentValues
    }

    //create or insert
    private fun save(tableClass: TableClass): Boolean {
        var id: Long = -1
        try {
            val database = writableDatabase

            val version = database.version

            val table = tableMap!![tableClass.tableName]

            val dataList = ArrayList<Any>()
            tableClass.getData(dataList)

            val fieldList = table!!.getFieldList()

            val contentValues = getContentValues(fieldList, dataList)

            id = database.insert(tableClass.tableName, null, contentValues)
            close()

            if (id <= 0) {
                return false
            }
        } catch (e: SQLException) {
            return false
        }

        tableClass.id = id
        return true
    }


    //update
    private fun update(tableClass: TableClass): Boolean {
        var affectedRows: Long = -1
        try {
            val database = writableDatabase

            val table = tableMap!![tableClass.tableName]

            val dataList = ArrayList<Any>()
            tableClass.getData(dataList)

            val fieldList = table!!.getFieldList()

            val contentValues = getContentValues(fieldList, dataList)

            affectedRows = database.update(tableClass.tableName, contentValues, "$ID=?",
                    arrayOf(java.lang.Long.toString(tableClass.id))).toLong()
            close()

            if (affectedRows <= 0) {
                return false
            }
        } catch (e: SQLException) {
            return false
        }

        return true
    }


    //delete
    private fun delete(tableClass: TableClass): Boolean {
        if (tableClass.id > 0) {
            try {
                val database = writableDatabase
                val affectedRows = database.delete(tableClass.tableName, "$ID=?",
                        arrayOf(java.lang.Long.toString(tableClass.id)))
                close()
                if (affectedRows <= 0) {
                    return false
                }
            } catch (e: SQLException) {
                return false
            }

            return true
        }
        return false
    }


    private fun fetchRow(cursor: Cursor, tableName: String): List<Any> {
        val dataList = ArrayList<Any>()
        val fieldList = tableMap!![tableName]!!.getFieldList()
        for (i in fieldList.indices) {
            val field = fieldList[i]
            when (field.type) {
                Field.TEXT -> dataList.add(cursor.getString(cursor.getColumnIndex(field.name)))
                Field.INTEGER -> if (field.trueType == Int::class.javaPrimitiveType) {
                    dataList.add(cursor.getInt(cursor.getColumnIndex(field.name)))
                } else if (field.trueType == Long::class.javaPrimitiveType) {
                    dataList.add(cursor.getLong(cursor.getColumnIndex(field.name)))
                } else if (field.trueType == Boolean::class.javaPrimitiveType) {
                    dataList.add(cursor.getInt(cursor.getColumnIndex(field.name)) == 1)
                }
                Field.REAL -> if (field.trueType == Float::class.javaPrimitiveType) {
                    dataList.add(cursor.getFloat(cursor.getColumnIndex(field.name)))
                } else if (field.trueType == Double::class.javaPrimitiveType) {
                    dataList.add(cursor.getDouble(cursor.getColumnIndex(field.name)))
                }
                Field.BLOB -> {
                }
            }//

        }
        return dataList
    }

    private fun getWarningWhenNoConstructorWithNoArgument(className: String): RuntimeException {
        return RuntimeException(String.format(
                "Class %s must has constructor with no argument", className))
    }

    fun <T : TableClass> findById(id: Long, tableName: String, clazz: Class<T>): T? {
        var tableName = tableName
        try {
            if (TextUtils.isEmpty(tableName)) {
                tableName = clazz.simpleName
            }
            val database = writableDatabase
            val sql = "SELECT * FROM $tableName WHERE $ID=?"
            val cursor = database.rawQuery(sql, arrayOf(java.lang.Long.toString(id)))
            cursor.moveToFirst()

            val dataList = fetchRow(cursor, tableName)

            val tableClass = clazz.newInstance()
            tableClass.id = cursor.getLong(cursor.getColumnIndex(ID))
            tableClass.setData(dataList)

            cursor.close()
            close()

            return tableClass
        } catch (e: SQLException) {
            return null
        } catch (e: InstantiationException) {
            throw getWarningWhenNoConstructorWithNoArgument(clazz.simpleName)
        } catch (e: IllegalAccessException) {
            return null
        }

    }

    fun <T : TableClass> findAll(tableName: String, clazz: Class<T>): List<T>? {
        var tableName = tableName
        try {
            if (TextUtils.isEmpty(tableName)) {
                tableName = clazz.simpleName
            }

            val database = writableDatabase
            val cursor = database.rawQuery("SELECT * FROM $tableName", null)
            cursor.moveToFirst()

            val resultList = ArrayList<T>()

            while (!cursor.isAfterLast) {
                val dataList = fetchRow(cursor, tableName)

                val tableClass = clazz.newInstance()
                tableClass.id = cursor.getLong(cursor.getColumnIndex(ID))
                tableClass.setData(dataList)

                resultList.add(tableClass)

                cursor.moveToNext()
            }

            cursor.close()
            close()

            return resultList
        } catch (e: SQLException) {
            return null
        } catch (e: InstantiationException) {
            throw getWarningWhenNoConstructorWithNoArgument(clazz.simpleName)
        } catch (e: IllegalAccessException) {
            return null
        }

    }

    fun <T : TableClass> findFirst(tableName: String, clazz: Class<T>): T? {
        val resultList = findAll(tableName, clazz)
        return if (resultList != null && resultList.size > 0) {
            resultList[0]
        } else null
    }

    fun <T : TableClass> findLast(tableName: String, clazz: Class<T>): T? {
        val resultList = findAll(tableName, clazz)
        return if (resultList != null && resultList.size > 0) {
            resultList[resultList.size - 1]
        } else null
    }

    fun <T : TableClass> findAllWithCriteria(tableName: String, clazz: Class<T>, whereClause: String, whereClauseArgs: Array<String>): List<T>? {
        var tableName = tableName
        try {
            if (TextUtils.isEmpty(tableName)) {
                tableName = clazz.simpleName
            }

            val database = writableDatabase
            val cursor = database.rawQuery("SELECT * FROM $tableName WHERE $whereClause", whereClauseArgs)
            cursor.moveToFirst()

            val resultList = ArrayList<T>()

            while (!cursor.isAfterLast) {
                val dataList = fetchRow(cursor, tableName)

                val tableClass = clazz.newInstance()
                tableClass.id = cursor.getLong(cursor.getColumnIndex(ID))
                tableClass.setData(dataList)

                resultList.add(tableClass)

                cursor.moveToNext()
            }

            cursor.close()
            close()

            return resultList
        } catch (e: SQLException) {
            return null
        } catch (e: InstantiationException) {
            throw getWarningWhenNoConstructorWithNoArgument(clazz.simpleName)
        } catch (e: IllegalAccessException) {
            return null
        }

    }

    fun <T : TableClass> findFirstWithCriteria(tableName: String, clazz: Class<T>, whereClause: String, whereClauseArgs: Array<String>): T? {
        val resultList = findAllWithCriteria(tableName, clazz, whereClause, whereClauseArgs)
        return if (resultList != null && resultList.size > 0) {
            resultList[0]
        } else null
    }

    fun <T : TableClass> findLastWithCriteria(tableName: String, clazz: Class<T>, whereClause: String, whereClauseArgs: Array<String>): T? {
        val resultList = findAllWithCriteria(tableName, clazz, whereClause, whereClauseArgs)
        return if (resultList != null && resultList.size > 0) {
            resultList[resultList.size - 1]
        } else null
    }


    fun <T : TableClass> selectQuery(distinct: Boolean, tableName: String, clazz: Class<T>, columns: Array<String>,
                                     selection: String, selectionArgs: Array<String>, groupBy: String,
                                     having: String, orderBy: String, limit: String): List<T>? {
        var tableName = tableName
        try {
            if (TextUtils.isEmpty(tableName)) {
                tableName = clazz.simpleName
            }

            val database = writableDatabase
            val cursor = database.query(distinct, tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit)
            cursor.moveToFirst()

            val resultList = ArrayList<T>()

            while (!cursor.isAfterLast) {
                val dataList = fetchRow(cursor, tableName)

                val tableClass = clazz.newInstance()
                tableClass.id = cursor.getLong(cursor.getColumnIndex(ID))
                tableClass.setData(dataList)

                resultList.add(tableClass)

                cursor.moveToNext()
            }

            cursor.close()
            close()

            return resultList
        } catch (e: SQLException) {
            return null
        } catch (e: InstantiationException) {
            throw getWarningWhenNoConstructorWithNoArgument(clazz.simpleName)
        } catch (e: IllegalAccessException) {
            return null
        }

    }

    fun <T : TableClass> rawQuery(clazz: Class<T>, selectSql: String, sqlArgs: Array<String>): List<T>? {
        try {
            val tableName = substringBetween(" from ", " ", selectSql.replace("FROM", "from") + " ")

            val database = writableDatabase
            val cursor = database.rawQuery(selectSql, sqlArgs)
            cursor.moveToFirst()

            val resultList = ArrayList<T>()

            while (!cursor.isAfterLast) {
                val dataList = fetchRow(cursor, tableName)

                val tableClass = clazz.newInstance()
                tableClass.id = cursor.getLong(cursor.getColumnIndex(ID))
                tableClass.setData(dataList)

                resultList.add(tableClass)

                cursor.moveToNext()
            }

            cursor.close()
            close()

            return resultList
        } catch (e: SQLException) {
            return null
        } catch (e: InstantiationException) {
            throw getWarningWhenNoConstructorWithNoArgument(clazz.simpleName)
        } catch (e: IllegalAccessException) {
            return null
        }

    }


    fun execSQL(sql: String, sqlArgs: Array<String>) {
        try {
            val database = writableDatabase
            database.execSQL(sql, sqlArgs)
            close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }


    fun <T : TableClass> delete(tableName: String, clazz: Class<T>, whereClause: String?, whereClauseArgs: Array<String>?) {
        var tableName = tableName
        if (TextUtils.isEmpty(tableName)) {
            tableName = clazz.simpleName
        }
        try {
            val database = writableDatabase
            database.delete(tableName, whereClause, whereClauseArgs)
            close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }


    fun <T : TableClass> deleteAll(tableName: String, clazz: Class<T>) {
        var tableName = tableName
        if (TextUtils.isEmpty(tableName)) {
            tableName = clazz.simpleName
        }
        delete(tableName, clazz, null, null)
    }


    class Table {
        var name: String? = null
        private val fieldList = ArrayList<Field>()

        constructor(name: String) {
            this.name = name
        }

        constructor(clazz: Class<*>) {
            this.name = clazz.simpleName
        }

        fun getFieldList(): List<Field> {
            return fieldList
        }

        fun addField(field: Field): Table {
            fieldList.add(field)
            return this
        }

        fun addIntField(name: String): Table {
            val field = Field(name, Field.INTEGER, Int::class.javaPrimitiveType)
            fieldList.add(field)
            return this
        }

        fun addLongField(name: String): Table {
            val field = Field(name, Field.INTEGER, Long::class.javaPrimitiveType)
            fieldList.add(field)
            return this
        }

        fun addStringField(name: String): Table {
            val field = Field(name, Field.TEXT, String::class.java)
            fieldList.add(field)
            return this
        }

        fun addFloatField(name: String): Table {
            val field = Field(name, Field.REAL, Float::class.javaPrimitiveType)
            fieldList.add(field)
            return this
        }

        fun addDoubleField(name: String): Table {
            val field = Field(name, Field.REAL, Double::class.javaPrimitiveType)
            fieldList.add(field)
            return this
        }

        fun addBooleanField(name: String): Table {
            val field = Field(name, Field.INTEGER, Boolean::class.javaPrimitiveType)
            fieldList.add(field)
            return this
        }
    }


    class Field(var name: String?, var type: String?, var trueType: Class<*>?) {
        companion object {
            val TEXT = "TEXT"
            val INTEGER = "INTEGER"
            val REAL = "REAL"
            val BLOB = "BLOB"
        }
    }


    private class Builder {
        private var databaseName: String? = null
        private var databaseVersion: Int = 0
        private val tableMap = HashMap<String, Table>()

        fun setDatabaseName(databaseName: String): Builder {
            this.databaseName = databaseName
            return this
        }

        fun setDatabaseVersion(version: Int): Builder {
            this.databaseVersion = version
            return this
        }

        fun create(context: Context): SQLiteWrapper {
            return SQLiteWrapper(context, databaseName, databaseVersion, tableMap)
        }
    }


    open class TableClass {
        var id: Long = 0

        val tableName: String
            get() = this.javaClass.getSimpleName()

        protected open val databaseName: String?
            get() = null

        open fun setData(dataList: List<Any>) {}
        open fun getData(dataList: List<Any>) {}

        fun saveIn(databaseName: String): Boolean {
            return SQLiteWrapper.of(databaseName)!!.save(this)
        }

        private fun checkCondition() {
            if (TextUtils.isEmpty(databaseName)) {
                throw RuntimeException(String.format(
                        "Class %s must define database name in method getDatabaseName() or use method saveIn() instead of save()",
                        javaClass.getSimpleName()))
            }
        }

        fun save(): Boolean {
            checkCondition()

            return SQLiteWrapper.of(databaseName)!!.save(this)
        }

        fun updateIn(databaseName: String): Boolean {
            return SQLiteWrapper.of(databaseName)!!.update(this)
        }

        fun update(): Boolean {
            checkCondition()

            return SQLiteWrapper.of(databaseName)!!.update(this)
        }

        fun deleteIn(databaseName: String): Boolean {
            return SQLiteWrapper.of(databaseName)!!.delete(this)
        }

        fun delete(): Boolean {
            checkCondition()

            return SQLiteWrapper.of(databaseName)!!.delete(this)
        }

        companion object {

            fun findById(databaseName: String, tableName: String, clazz: Class<*>, id: Long): TableClass? {
                var tableName = tableName
                if (TextUtils.isEmpty(tableName)) {
                    tableName = clazz.simpleName
                }
                return SQLiteWrapper.of(databaseName)!!.findById(id, tableName, clazz)
            }

            fun <T : TableClass> findAll(databaseName: String, tableName: String, clazz: Class<T>): List<T>? {
                var tableName = tableName
                if (TextUtils.isEmpty(tableName)) {
                    tableName = clazz.simpleName
                }
                return SQLiteWrapper.of(databaseName)!!.findAll(tableName, clazz)
            }

            fun <T : TableClass> findWithCriteria(databaseName: String, tableName: String,
                                                  clazz: Class<T>, whereClause: String, whereClauseArgs: Array<String>): List<T>? {
                var tableName = tableName
                if (TextUtils.isEmpty(tableName)) {
                    tableName = clazz.simpleName
                }
                return SQLiteWrapper.of(databaseName)!!.findAllWithCriteria(tableName, clazz, whereClause, whereClauseArgs)
            }

            fun <T : TableClass> selectQuery(databaseName: String, distinct: Boolean, tableName: String,
                                             clazz: Class<T>,
                                             columns: Array<String>,
                                             selection: String, selectionArgs: Array<String>, groupBy: String,
                                             having: String, orderBy: String, limit: String): List<T>? {
                var tableName = tableName
                if (TextUtils.isEmpty(tableName)) {
                    tableName = clazz.simpleName
                }
                return SQLiteWrapper.of(databaseName)!!.selectQuery(distinct, tableName, clazz, columns,
                        selection, selectionArgs, groupBy, having, orderBy, limit)
            }

            fun <T : TableClass> rawQuery(databaseName: String,
                                          clazz: Class<T>, selectSql: String, sqlArgs: Array<String>): List<T>? {
                return SQLiteWrapper.of(databaseName)!!.rawQuery(clazz, selectSql, sqlArgs)
            }

            fun execSQL(databaseName: String, sql: String, sqlArgs: Array<String>) {
                SQLiteWrapper.of(databaseName)!!.execSQL(sql, sqlArgs)
            }

            fun <T : TableClass> deleteAll(databaseName: String, tableName: String, clazz: Class<T>) {
                SQLiteWrapper.of(databaseName)!!.deleteAll(tableName, clazz)
            }
        }

    }


    fun setMigrationPlan(migrationPlan: MigrationPlan) {
        this.migrationPlan = migrationPlan
    }

    interface MigrationStep {
        fun getSQLScriptList(sqLiteWrapper: SQLiteWrapper): List<String>?
    }

    class RenameTableMigrationStep(private val oldTableName: String, private val newTableName: String) : MigrationStep {

        override fun getSQLScriptList(sqLiteWrapper: SQLiteWrapper): List<String>? {
            val sqlScriptList = ArrayList<String>()
            sqlScriptList.add(String.format("ALTER TABLE %s RENAME TO %s", oldTableName, newTableName))
            return sqlScriptList
        }
    }


    class AddTableMigrationStep(private val tableName: String) : MigrationStep {

        override fun getSQLScriptList(sqLiteWrapper: SQLiteWrapper): List<String>? {
            val sqlScriptList = ArrayList<String>()
            val table = sqLiteWrapper.tableMap!![tableName]
            sqlScriptList.add(sqLiteWrapper.getCreateTableScript(table))
            return sqlScriptList
        }
    }


    class RemoveTableMigrationStep(private val tableName: String) : MigrationStep {

        override fun getSQLScriptList(sqLiteWrapper: SQLiteWrapper): List<String>? {
            val sqlScriptList = ArrayList<String>()
            sqlScriptList.add(String.format("DROP TABLE %s", tableName))
            return sqlScriptList
        }
    }


    interface MigrationPlan {
        fun getUpgradePlan(oldVersion: Int, newVersion: Int): List<MigrationStep>?
        fun getDowngradePlan(oldVersion: Int, newVersion: Int): List<MigrationStep>?
    }


    private fun readAndParseCreateTableScript(fileName: String): List<Table>? {
        val tableList = ArrayList<Table>()

        if (TextUtils.isEmpty(fileName)) {
            Log.d(TAG, "Create SQL script file name is empty")
            return null
        }

        Log.d(TAG, "Create Script found. Executing...")
        var reader: BufferedReader? = null

        try {
            val `is` = assetManager.open(SQLW_FOLDER + fileName)
            val isr = InputStreamReader(`is`)
            reader = BufferedReader(isr)

            var line: String
            var statement = StringBuilder()
            while ((line = reader.readLine()) != null) {
                statement.append(line)
                statement.append("\n")

                if (line.endsWith(";")) {
                    var singleScript = statement.toString()

                    singleScript = singleScript
                            .replace("CREATE TABLE", "create table")
                            .replace("PRIMARY KEY", "primary key")
                            .replace("DEFAULT", "default")

                    val tableName = substringBetween("create table", "(", singleScript)
                    val fieldsString = substringBetween("(", ")", singleScript)
                    val fieldArray = fieldsString.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

                    val table = Table(tableName)

                    for (fieldString in fieldArray) {
                        if (fieldString.contains("primary key")) continue

                        val fieldParts = fieldString.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

                        when (fieldParts[1]) {
                            Field.INTEGER -> if (fieldString.contains("default 1") || fieldString.contains("default 0")) {
                                //case boolean
                                table.addBooleanField(fieldParts[0])
                            } else {
                                table.addIntField(fieldParts[0])
                            }
                            Field.TEXT -> table.addStringField(fieldParts[0])
                            Field.REAL -> table.addDoubleField(fieldParts[0])
                            Field.BLOB -> {
                            }
                        }//
                    }



                    tableList.add(table)
                    statement = StringBuilder()
                }
            }

        } catch (e: IOException) {
            Log.e(TAG, "Create IOException:", e)
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Create IOException:", e)
                }

            }
        }

        return tableList
    }


    private fun readAndGetSQLScript(fileName: String): List<String>? {
        if (TextUtils.isEmpty(fileName)) {
            Log.d(TAG, "Upgrade SQL script file name is empty")
            return null
        }

        val scriptList = ArrayList<String>()

        Log.d(TAG, "Upgrade Script found. Executing...")
        var reader: BufferedReader? = null

        try {
            val `is` = assetManager.open(SQLW_FOLDER + fileName)
            val isr = InputStreamReader(`is`)
            reader = BufferedReader(isr)

            val resultList = getSQLScript(reader)

            if (resultList != null && resultList.size > 0) {
                scriptList.addAll(resultList)
            }

        } catch (e: IOException) {
            Log.e(TAG, "Upgrade IOException:", e)
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Upgrade IOException:", e)
                }

            }
        }

        return scriptList
    }

    @Throws(IOException::class)
    private fun getSQLScript(reader: BufferedReader): List<String> {
        var line: String
        var statement = StringBuilder()

        val scriptList = ArrayList<String>()

        while ((line = reader.readLine()) != null) {
            statement.append(line)
            statement.append(" ")
            if (line.endsWith(";")) {
                scriptList.add(statement.toString())
                statement = StringBuilder()
            }
        }

        return scriptList
    }

    @Throws(IOException::class)
    private fun executeSQLScript(db: SQLiteDatabase, reader: BufferedReader) {
        var line: String
        var statement = StringBuilder()
        while ((line = reader.readLine()) != null) {
            statement.append(line)
            statement.append("\n")
            if (line.endsWith(";")) {
                db.execSQL(statement.toString())
                statement = StringBuilder()
            }
        }
    }

    class TLookup : TableClass() {
        var key: String? = null
        var string: String? = null
        var boolean: Boolean = false
        var int: Int = 0
        var long: Long = 0
        var float: Float = 0.toFloat()
        var double: Double = 0.toDouble()

        override val databaseName: String?
            get() = LOOKUP_DATABASE_NAME

        protected override fun getData(dataList: MutableList<Any>) {
            dataList.add(key)
            dataList.add(string)
            dataList.add(boolean)
            dataList.add(int)
            dataList.add(long)
            dataList.add(float)
            dataList.add(double)
        }

        protected override fun setData(dataList: List<Any>) {
            key = dataList[0] as String
            string = dataList[1] as String
            boolean = dataList[2] as Boolean
            int = dataList[3] as Int
            long = dataList[4] as Long
            float = dataList[5] as Float
            double = dataList[6] as Double
        }
    }

    companion object {
        private val ID = "_id"
        private val TAG = SQLiteWrapper::class.java!!.getName()
        private val SQLW_FOLDER = "SQLW/"

        private val sqLiteDatabaseMap = HashMap<String, Database>()
        private val sqLiteWrapperMap = HashMap<String, SQLiteWrapper>()

        fun addDatabase(database: Database) {
            if (!sqLiteDatabaseMap.containsKey(database.databaseName)) {
                sqLiteDatabaseMap[database.databaseName] = database
            }
        }

        fun removeDatabase(databaseName: String) {
            sqLiteWrapperMap.remove(databaseName)
            sqLiteDatabaseMap.remove(databaseName)
        }

        fun of(databaseName: String?): SQLiteWrapper? {
            return if (sqLiteDatabaseMap.containsKey(databaseName)) {
                sqLiteDatabaseMap.get(databaseName)!!.sqLiteWrapper
            } else null
        }

        private fun substringBetween(start: String, end: String, input: String): String {
            val startIndex = input.indexOf(start)
            val endIndex = input.indexOf(end, startIndex + start.length)
            return if (startIndex == -1 || endIndex == -1)
                input
            else
                input.substring(startIndex + start.length, endIndex).trim({ it <= ' ' })
        }


        private val LOOKUP_DATABASE_NAME = "sqlwlookup.db"
        private fun enableLookupDatabase(context: Context) {
            addDatabase(object : Database() {
                override val appContext: Context
                    get() = context.applicationContext

                override val databaseName: String
                    get() = LOOKUP_DATABASE_NAME

                override val databaseVersion: Int
                    get() = 1

                override val isWrapperCached: Boolean
                    get() = true

                override fun configure(sqLiteWrapper: SQLiteWrapper) {
                    sqLiteWrapper.addTable(Table(TLookup::class.java!!)
                            .addStringField("key")
                            .addStringField("string")
                            .addBooleanField("boolean")
                            .addIntField("integer")
                            .addLongField("long")
                            .addFloatField("float")
                            .addDoubleField("double"))
                }
            })
        }


        fun getLookupDatabase(context: Context): SQLiteWrapper {
            var sqLiteWrapper = SQLiteWrapper.of(LOOKUP_DATABASE_NAME)
            if (sqLiteWrapper == null) {
                SQLiteWrapper.enableLookupDatabase(context)
                sqLiteWrapper = SQLiteWrapper.of(LOOKUP_DATABASE_NAME)
            }
            return sqLiteWrapper
        }
    }
}
