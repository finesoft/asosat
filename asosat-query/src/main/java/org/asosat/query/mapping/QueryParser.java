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

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.PatternFileSelector;
import org.asosat.kernel.resource.MultiClassPathFiles;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * asosat-query
 *
 * @author bingo 上午10:56:43
 *
 */
public class QueryParser {

  public static final String SHCEMA_PATH = "classpath:org/asosat/query/mapping/qm_1_0.xsd";
  public static final String DFLT_QUERY_FILES_REGEX = ".*dynamicQuery.*\\.xml;.*query.*\\.xml";

  public static void main(String... strings) {}

  public Map<String, FileObject> getQueryFiles() {
    Map<String, FileObject> map = new HashMap<>();
    Arrays.stream(DFLT_QUERY_FILES_REGEX.split(";")).forEach(regex -> {
      MultiClassPathFiles.select(new PatternFileSelector(regex)).forEach((s, fo) -> {
        map.put(s, fo);
      });
    });
    return map;
  }

  public Map<String, Query> parse() {
    Map<String, Query> map = new LinkedHashMap<>();
    this.getQueryFiles().forEach((s, f) -> {

    });
    return map;
  }

  public QueryMapping parse(Document doc) {
    this.validate(new DOMSource(doc));// validate
    NodeList queryMappings = doc.getElementsByTagName(Query.QUE_MAP_ELE);
    if (queryMappings == null || queryMappings.getLength() == 0) {
      throw new RuntimeException();
    }
    Node queryMappingNode = queryMappings.item(0);
    QueryMapping qm = new QueryMapping();
    return qm;
  }

  public QueryMapping parse(FileObject fo) {
    try {
      return this.parse(DocumentBuilderFactory.newInstance().newDocumentBuilder()
          .parse(fo.getContent().getInputStream()));
    } catch (SAXException | IOException | ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * validate query mapping file
   *
   * @param xml validate
   */
  public void validate(Source xml) {
    try {
      Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
          .newSchema(new URL(SHCEMA_PATH));
      Validator validator = schema.newValidator();
      validator.validate(xml);
    } catch (SAXException | IOException e) {
      throw new RuntimeException(e);
    }
  }
}
