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

import static org.asosat.kernel.util.MyStrUtils.split;
import static org.asosat.kernel.util.VFSUtils.getPathFileName;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.PatternFileSelector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.asosat.kernel.resource.MultiClassPathFiles;
import org.asosat.kernel.util.VFSUtils;
import org.asosat.query.QueryRuntimeException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * asosat-query
 *
 * @author bingo 上午10:56:43
 *
 */
public class QueryParser {

  public static final String SCHEMA_URL = "org/asosat/query/mapping";

  static Logger logger = LogManager.getLogger(QueryParser.class);

  public static void main(String... strings) {
    new QueryParser().parse("", ".*Query.*\\.xml").forEach(m -> {
      m.selfValidate().forEach(System.out::println);
    });
  }

  public List<QueryMapping> parse(String classPath, String queryFilePathRegex) {
    List<QueryMapping> qmList = new ArrayList<>();
    final QueryParserErrorHandler errHdl = new QueryParserErrorHandler();
    final SAXParserFactory factory = this.createSAXParserFactory();
    this.getQueryMappingFiles(classPath, queryFilePathRegex).forEach((s, f) -> {
      logger.info(String.format("Parse query mapping file %s.", s));
      try (InputStream is = f.getContent().getInputStream()) {
        QueryParseHandler handler = new QueryParseHandler(s);
        XMLReader reader = factory.newSAXParser().getXMLReader();
        reader.setErrorHandler(errHdl);
        reader.setContentHandler(handler);
        reader.parse(new InputSource(is));
        qmList.add(handler.getMapping());
      } catch (Exception ex) {
        String errMsg = String.format("Parse query mapping file [%s] error!", s);
        throw new QueryRuntimeException(errMsg, ex);
      }
    });
    return qmList;
  }

  SAXParserFactory createSAXParserFactory() {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setSchema(this.getSchema());
    factory.setNamespaceAware(true);
    factory.setValidating(false);
    return factory;
  }

  Map<String, FileObject> getQueryMappingFiles(String classPath, String queryFilePathRegex) {
    Map<String, FileObject> map = new HashMap<>();
    Arrays.stream(split(classPath, ";")).forEach(pkg -> {
      Arrays.stream(queryFilePathRegex.split(";"))
          .forEach(regex -> MultiClassPathFiles.select(pkg, new PatternFileSelector(regex))
              .forEach(f -> map.put(getPathFileName(f), f)));
    });

    return map;
  }

  Schema getSchema() {
    try {
      return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
          .newSchema(MultiClassPathFiles
              .single(SCHEMA_URL,
                  VFSUtils.buildSelector(
                      (fs) -> fs.getFile().getName().getBaseName().equals("nqms_1_0.xsd")))
              .getURL());
    } catch (Exception e) {
      String errMsg = String.format("Build query mapping xml schema [%s] error!", SCHEMA_URL);
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
