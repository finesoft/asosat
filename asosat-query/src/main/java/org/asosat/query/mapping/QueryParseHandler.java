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

import static org.asosat.kernel.util.MyClsUtils.tryToLoadClassForName;
import static org.asosat.kernel.util.MyStrUtils.defaultString;
import static org.asosat.kernel.util.MyStrUtils.isBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import org.asosat.kernel.util.ConvertUtils;
import org.asosat.query.QueryRuntimeException;
import org.asosat.query.mapping.FetchQuery.FetchQueryParameter;
import org.asosat.query.mapping.FetchQuery.FetchQueryParameterSource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * asosat-script
 *
 * @author bingo 下午4:24:51
 *
 */
public class QueryParseHandler extends DefaultHandler {

  private final List<Query> queries = new ArrayList<>();
  private final List<ParameterMapping> paraMappings = new ArrayList<>();
  private final Stack<Object> valueStack = new Stack<>();
  private final Stack<String> nameStack = new Stack<>();
  private String commonSegment;
  private QueryMapping mapping;
  private final StringBuilder charBuffer = new StringBuilder();
  private final String url;

  public QueryParseHandler(String url) {
    this.url = url;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    String cqn = this.currentQName();
    if (SchemaNames.COMMON_SGEMENT.equalsIgnoreCase(cqn)
        || SchemaNames.QUE_DESC_ELE.equalsIgnoreCase(cqn)
        || SchemaNames.QUE_SCPT_ELE.equalsIgnoreCase(cqn)) {
      this.charBuffer.append(ch, start, length);
    }
  }

  @Override
  public void endDocument() throws SAXException {
    this.mapping.commonSegment = this.commonSegment;
    this.mapping.paraMapping
        .putAll(this.paraMappings.stream().collect(Collectors.toMap(p -> p.name, p -> p)));
    this.mapping.queries.addAll(this.queries);
    this.valueStack.clear();
    this.nameStack.clear();
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (qName.equalsIgnoreCase(SchemaNames.PARAM_ENTRY_ELE)) {
      this.handleParamEntry(false, qName, null);
    } else if (qName.equalsIgnoreCase(SchemaNames.QUE_ELE)) {
      this.handleQuery(false, qName, null);
    } else if (qName.equalsIgnoreCase(SchemaNames.QUE_FQE_ELE)) {
      this.handleFetchQuery(false, qName, null);
    } else if (qName.equalsIgnoreCase(SchemaNames.FQE_ELE_PARAM)) {
      this.handleFetchQueryParameter(false, qName, null);
    } else if (qName.equalsIgnoreCase(SchemaNames.QUE_HIT_ELE)) {
      this.handleQueryHint(false, qName, null);
    } else if (qName.equalsIgnoreCase(SchemaNames.COMMON_SGEMENT)) {
      this.handleCommonSegment(false, qName, null);
    } else if (qName.equalsIgnoreCase(SchemaNames.QUE_DESC_ELE)) {
      this.handleQueryDesc(false, qName, null);
    } else if (qName.equalsIgnoreCase(SchemaNames.QUE_SCPT_ELE)) {
      this.handleQueryScript(false, qName, null);
    }
  }

  public QueryMapping getMapping() {
    return this.mapping;
  }

  @Override
  public void startDocument() throws SAXException {
    this.mapping = new QueryMapping();
    this.mapping.url = this.url;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    if (qName.equalsIgnoreCase(SchemaNames.PARAM_ENTRY_ELE)) {
      this.handleParamEntry(true, qName, attributes);
    } else if (qName.equalsIgnoreCase(SchemaNames.QUE_ELE)) {
      this.handleQuery(true, qName, attributes);
    } else if (qName.equalsIgnoreCase(SchemaNames.QUE_FQE_ELE)) {
      this.handleFetchQuery(true, qName, attributes);
    } else if (qName.equalsIgnoreCase(SchemaNames.FQE_ELE_PARAM)) {
      this.handleFetchQueryParameter(true, qName, attributes);
    } else if (qName.equalsIgnoreCase(SchemaNames.QUE_HIT_ELE)) {
      this.handleQueryHint(true, qName, attributes);
    } else if (qName.equalsIgnoreCase(SchemaNames.COMMON_SGEMENT)) {
      this.handleCommonSegment(true, qName, attributes);
    } else if (qName.equalsIgnoreCase(SchemaNames.QUE_DESC_ELE)) {
      this.handleQueryDesc(true, qName, attributes);
    } else if (qName.equalsIgnoreCase(SchemaNames.QUE_SCPT_ELE)) {
      this.handleQueryScript(true, qName, attributes);
    }
  }

  void handleCommonSegment(boolean start, String qName, Attributes attributes) {
    if (start) {
      this.nameStack.push(qName);
    } else {
      String segment = this.charBuffer.toString();
      this.charBuffer.delete(0, this.charBuffer.length());
      this.commonSegment = segment.trim();
      this.nameStack.pop();
    }
  }

  void handleFetchQuery(boolean start, String qName, Attributes attributes) {
    if (start) {
      FetchQuery fq = new FetchQuery();
      for (int i = 0; i < attributes.getLength(); i++) {
        String aqn = attributes.getQName(i), atv = attributes.getValue(i);
        if (aqn.equals(SchemaNames.FQE_ATT_NAME)) {
          fq.referenceQuery = atv;
        } else if (aqn.equalsIgnoreCase(SchemaNames.FQE_ATT_MAX_SIZE)) {
          fq.maxSize = ConvertUtils.toInteger(atv);
        } else if (aqn.equalsIgnoreCase(SchemaNames.FQE_ATT_PRO_NAME)) {
          fq.propertyName = atv;
        } else if (aqn.equalsIgnoreCase(SchemaNames.FQE_ATT_VER)) {
          fq.version = defaultString(atv);
        }
      }
      this.valueStack.push(fq);
      this.nameStack.push(qName);
    } else {
      Object obj = this.valueStack.pop();
      Query q = this.currentObject();
      if (q == null) {
        throw new QueryRuntimeException("Parse error the fetch query must be in query element!");
      }
      q.fetchQueries.add((FetchQuery) obj);
      this.nameStack.pop();
    }
  }


  void handleFetchQueryParameter(boolean start, String qName, Attributes attributes) {
    if (start) {
      FetchQueryParameter fqp = new FetchQueryParameter();
      for (int i = 0; i < attributes.getLength(); i++) {
        String aqn = attributes.getQName(i), atv = attributes.getValue(i);
        if (aqn.equals(SchemaNames.PARAM_ENTRY_ATT_NME)) {
          fqp.name = atv;
        } else if (aqn.equalsIgnoreCase(SchemaNames.PARAM_ENTRY_ATT_TYP)) {
          fqp.source = ConvertUtils.toEnum(atv, FetchQueryParameterSource.class);
        }
      }
      this.valueStack.push(fqp);
      this.nameStack.push(qName);
    } else {
      Object obj = this.valueStack.pop();
      FetchQuery q = this.currentObject();
      if (q == null) {
        throw new QueryRuntimeException(
            "Parse error the fetch query parameter must be in fetch query element!");
      }
      q.parameters.add((FetchQueryParameter) obj);
      this.nameStack.pop();
    }
  }

  void handleParamEntry(boolean start, String qName, Attributes attributes) {
    if (start) {
      ParameterMapping pm = new ParameterMapping();
      for (int i = 0; i < attributes.getLength(); i++) {
        String aqn = attributes.getQName(i), atv = attributes.getValue(i);
        if (aqn.equals(SchemaNames.PARAM_ENTRY_ATT_NME)) {
          pm.name = atv;
        } else if (aqn.equalsIgnoreCase(SchemaNames.PARAM_ENTRY_ATT_TYP)) {
          pm.type = tryToLoadClassForName(atv);
        }
      }
      this.valueStack.push(pm);
      this.nameStack.push(qName);
    } else {
      Object obj = this.valueStack.pop();
      this.paraMappings.add((ParameterMapping) obj);
      this.nameStack.pop();
    }
  }

  void handleQuery(boolean start, String qName, Attributes attributes) {
    if (start) {
      Query q = new Query();
      for (int i = 0; i < attributes.getLength(); i++) {
        String aqn = attributes.getQName(i), atv = attributes.getValue(i);
        if (aqn.equals(SchemaNames.QUE_ATT_NAME)) {
          q.name = atv;
        } else if (aqn.equalsIgnoreCase(SchemaNames.QUE_ATT_CACHE)) {
          q.cache = ConvertUtils.toBoolean(atv);
        } else if (aqn.equalsIgnoreCase(SchemaNames.QUE_ATT_CACHE_RS_MD)) {
          q.cacheResultSetMetadata = ConvertUtils.toBoolean(atv);
        } else if (aqn.equalsIgnoreCase(SchemaNames.QUE_ATT_RST_CLS)) {
          q.resultClass = isBlank(atv) ? java.util.Map.class : tryToLoadClassForName(atv);
        } else if (aqn.equalsIgnoreCase(SchemaNames.QUE_ATT_RST_SET_CLS)) {
          q.resultSetMapping = isBlank(atv) ? null : tryToLoadClassForName(atv);
        } else if (aqn.equalsIgnoreCase(SchemaNames.QUE_ATT_VER)) {
          q.version = defaultString(atv);
        }
      }
      this.valueStack.push(q);
      this.nameStack.push(qName);
    } else {
      Object obj = this.valueStack.pop();
      this.queries.add((Query) obj);
      this.nameStack.pop();
    }
  }

  void handleQueryDesc(boolean start, String qName, Attributes attributes) {
    if (start) {
      this.nameStack.push(qName);
    } else {
      String desc = this.charBuffer.toString();
      this.charBuffer.delete(0, this.charBuffer.length());
      Query q = this.currentObject();
      if (q == null) {
        throw new QueryRuntimeException(
            "Parse error the query description must be in query element!");
      }
      q.description = desc.trim();
      this.nameStack.pop();
    }
  }

  void handleQueryHint(boolean start, String qName, Attributes attributes) {
    if (start) {
      QueryHint hit = new QueryHint();
      for (int i = 0; i < attributes.getLength(); i++) {
        String aqn = attributes.getQName(i), atv = attributes.getValue(i);
        if (aqn.equals(SchemaNames.QUE_HIT_ATT_KEY)) {
          hit.key = atv;
        } else if (aqn.equalsIgnoreCase(SchemaNames.QUE_HIT_ATT_KEY)) {
          hit.value = atv;
        }
      }
      this.valueStack.push(hit);
      this.nameStack.push(qName);
    } else {
      Object obj = this.valueStack.pop();
      Query q = this.currentObject();
      if (q == null) {
        throw new QueryRuntimeException("Parse error the query hit must be in query element!");
      }
      q.hints.add((QueryHint) obj);
      this.nameStack.pop();
    }
  }

  void handleQueryScript(boolean start, String qName, Attributes attributes) {
    if (start) {
      this.nameStack.push(qName);
    } else {
      String script = this.charBuffer.toString();
      this.charBuffer.delete(0, this.charBuffer.length());
      Query q = this.currentObject();
      if (q == null || isBlank(script)) {
        throw new QueryRuntimeException(
            "Parse error the query script must be in query element and script can't null!");
      }
      q.script = script.trim();
      this.nameStack.pop();
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T currentObject() {
    return this.valueStack.isEmpty() ? null : (T) this.valueStack.peek();
  }

  private String currentQName() {
    return this.nameStack.isEmpty() ? null : this.nameStack.peek();
  }
}
