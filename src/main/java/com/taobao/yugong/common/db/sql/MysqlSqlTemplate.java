package com.taobao.yugong.common.db.sql;

import com.taobao.yugong.common.db.meta.ColumnMeta;

/**
 * mysql特定的sql构造
 *
 * @author agapple 2013-9-10 下午6:11:16
 * @since 3.0.0
 */
public class MysqlSqlTemplate extends SqlTemplate {

  public String getMergeSql(String schemaName, String tableName, String[] pkNames, String[] colNames,
      boolean mergeUpdatePk) {
    StringBuilder sql = new StringBuilder();
    sql.append("insert into ").append(makeFullName(schemaName, tableName)).append("(");
    String[] allColumns = buildAllColumns(pkNames, colNames);

    int size = allColumns.length;
    for (int i = 0; i < size; i++) {
      sql.append(getColumnName(allColumns[i])).append(splitCommea(size, i));
    }

    sql.append(") values (");
    for (int i = 0; i < size; i++) {
      sql.append("?").append(splitCommea(size, i));
    }
    sql.append(") on duplicate key update ");

    // mysql merge sql匹配了uniqe / primary key时都会执行update，所以需要更新pk信息
    if (mergeUpdatePk) {
      for (int i = 0; i < size; i++) {
        sql.append(getColumnName(allColumns[i]))
            .append("=values(")
            .append(getColumnName(allColumns[i]))
            .append(")");
        sql.append(splitCommea(size, i));
      }
    } else {
      // merge sql不更新主键信息, 规避drds情况下的分区键变更
      for (int i = 0; i < colNames.length; i++) {
        sql.append(getColumnName(colNames[i]))
            .append("=values(")
            .append(getColumnName(colNames[i]))
            .append(")");
        sql.append(splitCommea(colNames.length, i));
      }
    }

    // intern优化，避免出现大量相同的字符串
    return sql.toString().intern();
  }

  public String getInsertSql(String schemaName, String tableName, String[] pkNames, String[] columnNames) {
    StringBuilder sql = new StringBuilder();
    sql.append("insert ignore into ").append(makeFullName(schemaName, tableName)).append("(");
    String[] allColumns = buildAllColumns(pkNames, columnNames);

    int size = allColumns.length;
    for (int i = 0; i < size; i++) {
      sql.append(getColumnName(allColumns[i])).append(splitCommea(size, i));
    }

    sql.append(") values (");
    makeColumnQuestions(sql, allColumns);
    sql.append(")");
    return sql.toString().intern();// intern优化，避免出现大量相同的字符串
  }

  public String getInsertNomalSql(String schemaName, String tableName, String[] pkNames, String[] columnNames) {
    StringBuilder sql = new StringBuilder();
    sql.append("insert into ").append(makeFullName(schemaName, tableName)).append("(");
    String[] allColumns = buildAllColumns(pkNames, columnNames);

    int size = allColumns.length;
    for (int i = 0; i < size; i++) {
      sql.append(getColumnName(allColumns[i])).append(splitCommea(size, i));
    }

    sql.append(") values (");
    makeColumnQuestions(sql, allColumns);
    sql.append(")");
    return sql.toString().intern();// intern优化，避免出现大量相同的字符串
  }

  protected String getColumnName(String columName) {
    return "`" + columName + "`";
  }

  protected String getColumnName(ColumnMeta column) {
    return "`" + column.getName() + "`";
  }

}
