/**
 * P6Spy
 *
 * Copyright (C) 2002 - 2020 P6Spy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.p6spy.engine.common;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.p6spy.engine.logging.Category;

/**
 * @author Quinton McCombs
 * @since 09/2013
 */
public class ResultSetInformation implements Loggable {

  private final StatementInformation statementInformation;
  private ResultSet resultSet;
  private String query;
  private final Map<String, Value> resultMap = new LinkedHashMap<String, Value>();
  private int currRow = -1;
  private int lastRowLogged = -1;

  public ResultSetInformation(final StatementInformation statementInformation) {
    this.statementInformation = statementInformation;
    this.query = statementInformation.getStatementQuery();
  }

  /**
   * Generates log message with column values accessed if the row's column values have not already been logged.
   */
  public void generateLogMessage() {
    if (lastRowLogged != currRow) {
      P6LogQuery.log(Category.RESULTSET, this);
      lastRowLogged = currRow;
    }
  }

  public int getCurrRow() {
    return currRow;
  }

  public void incrementCurrRow() {
    this.currRow++;
    this.resultMap.clear();
  }

  public void setColumnValue(String columnName, Object value) {
    resultMap.put(columnName, new Value(value));
  }

  @Override
  public String getSql() {
    return query;
  }

  @Override
  public String getSqlWithValues() {
    final StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, Value> entry : resultMap.entrySet()) {
      if (sb.length() > 0) {
        sb.append(", ");
      }
      sb.append(entry.getKey());
      sb.append(" = ");
      sb.append(entry.getValue() != null ? entry.getValue().toString() : new Value().toString());
    }

    return sb.toString();
  }

  /**
   * Returns column/value map for the last row read.
   *
   * NOTE: Only values that were retrieved from {@link ResultSet}
   * ({@link ResultSet#getString(int)}, {@link ResultSet#getBoolean(int)}, etc.) will be returned.
   */
  public Map<String, Value> getResultMap() {
    return Collections.unmodifiableMap(resultMap);
  }

  public StatementInformation getStatementInformation() {
    return statementInformation;
  }

  /** {@inheritDoc} */
  @Override
  public ConnectionInformation getConnectionInformation() {
    return this.statementInformation.getConnectionInformation();
  }

  public ResultSet getResultSet() {
    return resultSet;
  }

  public void setResultSet(ResultSet resultSet) {
    this.resultSet = resultSet;
  }
}
