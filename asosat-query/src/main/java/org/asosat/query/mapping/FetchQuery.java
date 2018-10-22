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
package org.asosat.query.mapping;

import static org.asosat.kernel.util.MyStrUtils.defaultString;
import static org.asosat.kernel.util.MyStrUtils.isNotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * asosat-query
 *
 * @author bingo 上午10:26:45
 *
 */
public class FetchQuery implements Serializable {

  private static final long serialVersionUID = 449192431797295206L;
  private String referenceQuery;
  private String injectPropertyName;
  private Class<?> resultClass = Map.class;
  private int maxSize;
  private List<FetchQueryParameter> parameters = new ArrayList<>();
  private String referenceQueryversion = "";
  private boolean multiRecords = true;

  /**
   * @return the injectPropertyName
   */
  public String getInjectPropertyName() {
    return this.injectPropertyName;
  }

  /**
   * @return the maxSize
   */
  public int getMaxSize() {
    return this.maxSize;
  }

  /**
   * @return the parameters
   */
  public List<FetchQueryParameter> getParameters() {
    return this.parameters;
  }

  /**
   * @return the referenceQuery
   */
  public String getReferenceQuery() {
    return this.referenceQuery;
  }

  /**
   * @return the referenceQueryversion
   */
  public String getReferenceQueryversion() {
    return this.referenceQueryversion;
  }


  /**
   * @return the resultClass
   */
  public Class<?> getResultClass() {
    return this.resultClass;
  }

  public String getVersionedReferenceQueryName() {
    return defaultString(this.getReferenceQuery())
        + (isNotBlank(this.getReferenceQueryversion()) ? "_" + this.getReferenceQueryversion()
            : "");
  }

  public boolean isMultiRecords() {
    return this.multiRecords;
  }

  void setInjectPropertyName(String injectPropertyName) {
    this.injectPropertyName = injectPropertyName;
  }

  void setMaxSize(int maxSize) {
    this.maxSize = maxSize;
  }

  void setMultiRecords(boolean multiRecords) {
    this.multiRecords = multiRecords;
  }

  void setReferenceQuery(String referenceQuery) {
    this.referenceQuery = referenceQuery;
  }

  void setReferenceQueryversion(String referenceQueryversion) {
    this.referenceQueryversion = referenceQueryversion;
  }

  void setResultClass(Class<?> resultClass) {
    this.resultClass = resultClass;
  }

  public static class FetchQueryParameter implements Serializable {

    private static final long serialVersionUID = 5013658267151165784L;

    private String name;
    private String sourceName;
    private FetchQueryParameterSource source;

    /**
     * @return the name
     */
    public String getName() {
      return this.name;
    }

    /**
     * @return the source
     */
    public FetchQueryParameterSource getSource() {
      return this.source;
    }

    /**
     * @return the sourceName
     */
    public String getSourceName() {
      return this.sourceName;
    }

    void setName(String name) {
      this.name = name;
    }

    void setSource(FetchQueryParameterSource source) {
      this.source = source;
    }

    void setSourceName(String sourceName) {
      this.sourceName = sourceName;
    }

  }

  public enum FetchQueryParameterSource {
    P, R
  }
}
