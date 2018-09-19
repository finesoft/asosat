/*
 * Copyright (c) 2013-2018. BIN.CHEN
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.asosat.query.dynamic.asql.ast;

/**
 * asosat-query
 *
 * @author bingo 上午11:46:55
 *
 */
public interface Operator {

  String name();

  String symbol();

  public enum ComparisonOperator implements Operator {

    GT(">"), EQ("=="), LT("<"), GE(">="), LE("<="), NE("!=");

    private final String symbol;

    private ComparisonOperator(String symbol) {
      this.symbol = symbol;
    }

    @Override
    public String symbol() {
      return this.symbol;
    }
  }

  public enum LogicalOperator implements Operator {
    AND("&"), OR("|");
    private final String symbol;

    private LogicalOperator(String symbol) {
      this.symbol = symbol;
    }

    @Override
    public String symbol() {
      return this.symbol;
    }
  }

  public enum XComparisonOperator implements Operator {
    IN("^"), NN("!^");
    private final String symbol;

    private XComparisonOperator(String symbol) {
      this.symbol = symbol;
    }

    @Override
    public String symbol() {
      return this.symbol;
    }
  }


}
