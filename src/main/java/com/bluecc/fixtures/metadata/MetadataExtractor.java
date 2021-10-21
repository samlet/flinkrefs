package com.bluecc.fixtures.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MetadataExtractor {
    private final DatabaseMetaData databaseMetaData;

    public MetadataExtractor(Connection connection) throws SQLException {
        this.databaseMetaData = connection.getMetaData();
        DatabaseMetaData databaseMetaData = connection.getMetaData();
    }

    /**
     * 有时，我们想知道所有用户定义的表、系统表或视图的名称。此外，我们可能想知道表格上的一些解释性评论。
     * 所有这些都可以通过使用DatabaseMetaData对象的getTables()方法来完成。
     * 提取所有现有用户定义表的名称.
     * 在这里，前两个参数是catalog和schema。第三个参数采用表名模式。例如，如果我们提供“CUST%”，
     * 这将包括名称以“CUST”开头的所有表。最后一个参数采用包含表类型的字符串数组。将TABLE用于用户定义的表。
     * 如果我们要查找系统定义的表，我们要做的就是将表类型替换为“ SYSTEM TABLE ”.
     * <pre>
     *      ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[]{"SYSTEM TABLE"});
     *      while(resultSet.next()) {
     *          String systemTableName = resultSet.getString("TABLE_NAME");
     *      }
     * </pre>
     *
     * @throws SQLException
     */
    public void extractTableInfo() throws SQLException {
        ResultSet resultSet = databaseMetaData.getTables(null, null,
                "CUST%", new String[] { "TABLE" });
        while (resultSet.next()) {
            // Print the names of existing tables
            System.out.println(resultSet.getString("TABLE_NAME"));
            System.out.println(resultSet.getString("REMARKS"));
        }
    }

    public void extractSystemTables() throws SQLException {
        ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[] { "SYSTEM TABLE" });
        while (resultSet.next()) {
            // Print the names of system tables
            System.out.println(resultSet.getString("TABLE_NAME"));
        }
    }

    /**
     * 要找出所有现有的视图，我们只需将类型更改为“ VIEW ”。
     *
     * @throws SQLException
     */
    public void extractViews() throws SQLException {
        ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[] { "VIEW" });
        while (resultSet.next()) {
            // Print the names of existing views
            System.out.println(resultSet.getString("TABLE_NAME"));
        }
    }

    /**
     * 在这里，getColumns()调用返回一个ResultSet ，我们可以迭代它以找到每列的描述。
     * 每个描述都包含许多有用的列，例如COLUMN_NAME、COLUMN_SIZE和DATA_TYPE。
     *
     * @param tableName 表名
     * @throws SQLException
     */
    public void extractColumnInfo(String tableName) throws SQLException {
        ResultSet columns = databaseMetaData.getColumns(null, null, tableName, null);

        while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String columnSize = columns.getString("COLUMN_SIZE");
            String datatype = columns.getString("DATA_TYPE");
            String isNullable = columns.getString("IS_NULLABLE");
            String isAutoIncrement = columns.getString("IS_AUTOINCREMENT");
            System.out.println(String.format("ColumnName: %s, columnSize: %s, datatype: %s, isColumnNullable: %s, isAutoIncrementEnabled: %s", columnName, columnSize, datatype, isNullable, isAutoIncrement));
        }
    }

    /**
     * 除了常规列，我们还可以找出特定表的主键列
     *
     * @param tableName
     * @throws SQLException
     */
    public void extractPrimaryKeys(String tableName) throws SQLException {
        ResultSet primaryKeys = databaseMetaData.getPrimaryKeys(null, null, tableName);
        while (primaryKeys.next()) {
            String primaryKeyColumnName = primaryKeys.getString("COLUMN_NAME");
            String primaryKeyName = primaryKeys.getString("PK_NAME");
            System.out.println(String.format("columnName:%s, pkName:%s", primaryKeyColumnName, primaryKeyName));
        }
    }

    public void fun() throws SQLException {

    }

    /**
     * 我们可以检索外键列的描述以及给定表引用的主键列
     * @param tableName
     * @throws SQLException
     */
    public void extractForeignKeys(String tableName) throws SQLException {
        ResultSet foreignKeys = databaseMetaData.getImportedKeys(null, null, tableName);
        while (foreignKeys.next()) {
            String pkTableName = foreignKeys.getString("PKTABLE_NAME");
            String fkTableName = foreignKeys.getString("FKTABLE_NAME");
            String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
            String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
            System.out.println(String.format("pkTableName:%s, fkTableName:%s, pkColumnName:%s, fkColumnName:%s", pkTableName, fkTableName, pkColumnName, fkColumnName));
        }
    }

    /**
     * 使用相同的DatabaseMetaData 对象获取数据库级别的信息。
     * 例如，我们可以获取数据库产品的名称和版本、JDBC 驱动程序的名称、JDBC 驱动程序的版本号等。
     *
     * @throws SQLException
     */
    public void extractDatabaseInfo() throws SQLException {
        String productName = databaseMetaData.getDatabaseProductName();
        String productVersion = databaseMetaData.getDatabaseProductVersion();

        String driverName = databaseMetaData.getDriverName();
        String driverVersion = databaseMetaData.getDriverVersion();

        System.out.println(String.format("Product name:%s, Product version:%s", productName, productVersion));
        System.out.println(String.format("Driver name:%s, Driver Version:%s", driverName, driverVersion));
    }

    public void extractUserName() throws SQLException {
        String userName = databaseMetaData.getUserName();
        System.out.println(userName);
        ResultSet schemas = databaseMetaData.getSchemas();
        while (schemas.next()) {
            String table_schem = schemas.getString("TABLE_SCHEM");
            String table_catalog = schemas.getString("TABLE_CATALOG");
            System.out.println(String.format("Table_schema:%s, Table_catalog:%s", table_schem, table_catalog));
        }
    }

    /**
     * 不同的数据库支持不同的功能。例如，H2 不支持全外连接，而 MySQL 支持。
     *
     * @throws SQLException
     */
    public void extractSupportedFeatures() throws SQLException {
        System.out.println("Supports scrollable & Updatable Result Set: " + databaseMetaData.supportsResultSetConcurrency(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE));
        System.out.println("Supports Full Outer Joins: " + databaseMetaData.supportsFullOuterJoins());
        System.out.println("Supports Stored Procedures: " + databaseMetaData.supportsStoredProcedures());
        System.out.println("Supports Subqueries in 'EXISTS': " + databaseMetaData.supportsSubqueriesInExists());
        System.out.println("Supports Transactions: " + databaseMetaData.supportsTransactions());
        System.out.println("Supports Core SQL Grammar: " + databaseMetaData.supportsCoreSQLGrammar());
        System.out.println("Supports Batch Updates: " + databaseMetaData.supportsBatchUpdates());
        System.out.println("Supports Column Aliasing: " + databaseMetaData.supportsColumnAliasing());
        System.out.println("Supports Savepoints: " + databaseMetaData.supportsSavepoints());
        System.out.println("Supports Union All: " + databaseMetaData.supportsUnionAll());
        System.out.println("Supports Union: " + databaseMetaData.supportsUnion());
    }
}

