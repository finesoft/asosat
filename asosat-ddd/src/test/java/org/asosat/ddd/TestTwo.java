/*
 * Copyright (c) 2013-2018, Bingo.Chen (finesoft@gmail.com).
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
package org.asosat.ddd;

import static org.corant.shared.util.Maps.mapOf;
import java.time.LocalDate;

import org.asosat.shared.Param;
import org.corant.devops.test.unit.CorantJunit4Suite;
import org.corant.devops.test.unit.RunConfig;
import org.corant.suites.json.JsonUtils;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * corant-asosat-ddd
 *
 * @author bingo 上午11:18:23
 *
 */
@RunWith(CorantJunit4Suite.class)
@RunConfig(profile = "bingo", randomWebPort = true, configClass = TestTwo.class)
@SuiteClasses({CorantJunit4ClassRunnerTest1.class, CorantJunit4ClassRunnerTest2.class})
public class TestTwo {

  public static void main(String... strings) throws Exception {
    Param p = Param.empty().withAttribute(mapOf("a", true, "b", "1234"));
    System.out.println(p.getAttributes().getString("b"));
    // System.out.println(JsonUtils.toString(ZonedDateTime.now(), true));
    System.out.println(JsonUtils.toString(LocalDate.now(), true));
  }
}
