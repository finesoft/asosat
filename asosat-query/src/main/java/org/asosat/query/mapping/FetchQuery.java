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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * asosat-query
 *
 * @author bingo 上午10:26:45
 *
 */
public class FetchQuery implements Serializable {

  private static final long serialVersionUID = 449192431797295206L;


  String referenceQuery;
  String propertyName;
  int maxSize;
  List<FetchQueryParameter> parameters = new ArrayList<>();
  String version = "";

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
   * @return the propertyName
   */
  public String getPropertyName() {
    return this.propertyName;
  }

  /**
   * @return the referenceQuery
   */
  public String getReferenceQuery() {
    return this.referenceQuery;
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return this.version;
  }

  public static class FetchQueryParameter implements Serializable {

    private static final long serialVersionUID = 5013658267151165784L;

    String name;
    FetchQueryParameterSource source;

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

  }

  public enum FetchQueryParameterSource {
    P, R
  }
}
