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

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.PatternFileSelector;
import org.asosat.kernel.resource.MultiClassPathFiles;
import org.asosat.query.QueryRuntimeException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * asosat-script
 *
 * @author bingo 上午10:56:43
 *
 */
public class QueryParser {

  public static final String SCHEMA_URL = "org/asosat/query/mapping/qm_1_0.xsd";
  public static final String DFLT_QUERY_FILES_REGEX = ".*Query.*\\.xml";

  public static void main(String... strings) {
    Map<String, QueryMapping> map = new QueryParser().parse();
    map.forEach((s, m) -> {
      System.out.println(s);
    });
  }

  public Map<String, QueryMapping> parse() {
    Map<String, QueryMapping> map = new LinkedHashMap<>();
    final QueryParserErrorHandler errHdl = new QueryParserErrorHandler();
    final SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setSchema(this.getSchema());
    factory.setNamespaceAware(true);
    factory.setValidating(false);
    this.getQueryMappingFiles().forEach((s, f) -> {
      try (InputStream is = f.getContent().getInputStream()) {
        QueryParseHandler handler = new QueryParseHandler(s);
        XMLReader reader = factory.newSAXParser().getXMLReader();
        reader.setErrorHandler(errHdl);
        reader.setContentHandler(handler);
        reader.parse(new InputSource(is));
        map.put(s, handler.getMapping());
      } catch (Exception ex) {
        String errMsg = String.format("Parse query mapping file [%s] error!", s);
        throw new QueryRuntimeException(errMsg, ex);
      }
    });
    return map;
  }

  Map<String, FileObject> getQueryMappingFiles() {
    Map<String, FileObject> map = new HashMap<>();
    Arrays.stream(DFLT_QUERY_FILES_REGEX.split(";")).forEach(
        regex -> MultiClassPathFiles.select(new PatternFileSelector(regex)).forEach(map::put));
    return map;
  }

  Schema getSchema() {
    try {
      return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
          .newSchema(MultiClassPathFiles.get(SCHEMA_URL).getURL());
    } catch (Exception e) {
      String errMsg = String.format("Build script schema [%S] validator error!", SCHEMA_URL);
      throw new QueryRuntimeException(errMsg, e);
    }
  }

  static class QueryParserErrorHandler implements ErrorHandler {
    @Override
    public void error(SAXParseException exception) throws SAXException {
      throw new QueryRuntimeException(exception);
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
      throw new QueryRuntimeException(exception);
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
      throw new QueryRuntimeException(exception);
    }
  }
}