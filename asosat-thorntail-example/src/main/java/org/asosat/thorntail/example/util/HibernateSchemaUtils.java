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
package org.asosat.thorntail.example.util;

import static org.asosat.kernel.util.MyClsUtils.getClassPathPackageClassNames;
import static org.asosat.kernel.util.Preconditions.requireNotBlank;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Properties;
import org.apache.commons.vfs2.FileExtensionSelector;
import org.asosat.domain.repository.JpaUtils;
import org.asosat.kernel.resource.GlobalMessageCodes;
import org.asosat.kernel.resource.MultiClassPathFiles;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaExport.Action;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.hbm2ddl.SchemaValidator;
import org.hibernate.tool.hbm2ddl.UniqueConstraintSchemaUpdateStrategy;
import org.hibernate.tool.schema.TargetType;


/**
 * @author bingo 上午11:23:45
 *
 */
public class HibernateSchemaUtils {

  public static void stdoutPersistClasses(String pkg) {
    new ArrayList<>(getClassPathPackageClassNames(pkg)).stream()
        .filter(c -> JpaUtils.isPersistenceClass(c)).sorted(String::compareTo)
        .map(x -> new StringBuilder("<class>").append(x).append("</class>").toString())
        .forEach(x -> System.out.println(x));
  }

  public static void stdoutPersistes(String pkg) {
    System.out.println("<!-- mapping files -->");
    stdoutPersistJpaOrmXml(pkg);
    System.out.println("<!-- mapping classes -->");
    stdoutPersistClasses(pkg);
  }

  public static void stdoutPersistJpaOrmXml(String pkg) {
    String packageNameToUse =
        requireNotBlank(pkg, GlobalMessageCodes.ERR_PARAM).replaceAll("\\.", "/");
    MultiClassPathFiles.select(new FileExtensionSelector("xml")).keySet().stream()
        .sorted(String::compareTo).forEach(s -> {
          if (s.contains(packageNameToUse) && s.endsWith("JpaOrm.xml")) {
            System.out.println(new StringBuilder().append("<mapping-file>")
                .append(s.substring(s.indexOf(packageNameToUse))).append("</mapping-file>"));
          }
        });
  }

  public static void stdoutRebuildSchema(String pu, String pkg, TypeContributor... contributors) {
    out(false);
    EntityManagerFactoryBuilderImpl builder =
        genEntityManagerFactoryBuilder(pu, null, contributors);
    builder.build();
    new SchemaExport().setFormat(true).execute(EnumSet.of(TargetType.STDOUT), Action.BOTH,
        builder.getMetadata());
    out(true);
  }

  public static void stdoutUpdateSchema(String pu, String pkg, TypeContributor... contributors) {
    out(false);
    EntityManagerFactoryBuilderImpl builder =
        genEntityManagerFactoryBuilder(pu, null, contributors);
    builder.build();
    new SchemaUpdate().setFormat(true).execute(EnumSet.of(TargetType.STDOUT),
        builder.getMetadata());
    out(true);
  }

  @SuppressWarnings("deprecation")
  public static void validateNamedQuery(String pu, String pkg, String mappingResource) {
    out(false);
    EntityManagerFactoryBuilderImpl builder = genEntityManagerFactoryBuilder(pu, mappingResource);
    SessionFactoryImplementor sf = ((SessionFactoryImplementor) builder.build());
    sf.getNamedQueryRepository().checkNamedQueries(sf.getQueryPlanCache());
    out(true);
  }

  public static void validateSchema(String pu, String pkg) {
    EntityManagerFactoryBuilderImpl builder = genEntityManagerFactoryBuilder(pu, null);
    builder.build();
    new SchemaValidator().validate(builder.getMetadata());
  }

  private static EntityManagerFactoryBuilderImpl genEntityManagerFactoryBuilder(String pu,
      String mappingResource, TypeContributor... contributors) {
    Properties props = new Properties();
    // props.put(AvailableSettings.DIALECT, SQLServer2012Dialect.class);
    props.put(AvailableSettings.UNIQUE_CONSTRAINT_SCHEMA_UPDATE_STRATEGY,
        UniqueConstraintSchemaUpdateStrategy.RECREATE_QUIETLY);
    EntityManagerFactoryBuilder builder = null;
    try {
      builder = Bootstrap.getEntityManagerFactoryBuilder(
          HibernateSchemaUtils.class.getClassLoader().getResource("META-INF/persistence.xml"), pu,
          props);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return (EntityManagerFactoryBuilderImpl) builder;
  }


  private static void out(boolean end) {
    if (!end) {
      System.out.println("\n-->>>>>>>> Schema output start");
    } else {
      System.out.println("\n--Version: V1_0_" + System.currentTimeMillis()
          + "\n\n--<<<<<<<< Schema output end. \n");
    }
  }

}
