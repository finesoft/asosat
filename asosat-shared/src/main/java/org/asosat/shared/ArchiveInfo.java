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
package org.asosat.shared;

import static org.corant.shared.util.Maps.getMapBoolean;
import static org.corant.shared.util.Maps.getMapInstant;
import static org.corant.shared.util.Maps.getMapLong;
import static org.corant.shared.util.Maps.getMapString;
import java.time.Instant;
import java.util.Map;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;

/**
 * @author bingo 上午11:14:48
 */
@MappedSuperclass
@Embeddable
public class ArchiveInfo implements MaintainInfo {

  public static final String KEY_LOG = "archivedLog";
  public static final String KEY_ARVSTID = "archivistId";
  public static final String KEY_ARVSTNME = "archivistName";
  public static final String KEY_ARC = "archived";
  public static final String KEY_ARC_TIME = "archivedTime";
  static final ArchiveInfo EMPTY_INST = new ArchiveInfo();

  private static final long serialVersionUID = 5130212947036396844L;

  public static ArchiveInfo empty() {
    return EMPTY_INST;
  }

  @Embedded
  @AttributeOverrides(value = {
      @AttributeOverride(column = @Column(name = "archivistId"), name = "id"),
      @AttributeOverride(column = @Column(name = "archivistName", length = 320), name = "name")})
  private Participator archivist;

  @Column
  private boolean archived = false;

  @Column
  private Instant archivedTime;

  @Column(length = 2048)
  @Basic(fetch = FetchType.LAZY)
  private String archivedLog;

  protected ArchiveInfo() {}

  public ArchiveInfo(Map<Object, Object> param) {
    if (param != null) {
      init(new Participator(getMapLong(param, KEY_ARVSTID), getMapString(param, KEY_ARVSTNME)),
          getMapBoolean(param, KEY_ARC), getMapString(param, KEY_LOG),
          getMapInstant(param, KEY_ARC_TIME));
    }
  }

  public ArchiveInfo(Param param, boolean archived) {
    Param paramToUse = param == null ? Param.EMPTY_INST : param;
    init(paramToUse.getOperator(), archived, paramToUse.getAttributes().getString(KEY_LOG),
        paramToUse.getAttributes().getInstant(KEY_ARC_TIME, Instant.now()));
  }

  public ArchiveInfo(Participator archivist) {
    this(archivist, true, null);
  }

  public ArchiveInfo(Participator archivist, boolean archived, String archivedLog) {
    this(archivist, archived, archivedLog, Instant.now());
  }

  public ArchiveInfo(Participator archivist, boolean archived, String archivedLog,
      Instant archivedTime) {
    super();
    init(archivist, archived, archivedLog, archivedTime);
  }

  public String getArchivedLog() {
    return archivedLog;
  }

  public Instant getArchivedTime() {
    return archivedTime;
  }

  public Participator getArchivist() {
    return archivist == null ? null : archivist;
  }

  protected void init(Participator archivist, boolean archived, String archivedLog,
      Instant archivedTime) {
    this.archived = archived;
    if (archived) {
      this.archivist = archivist;
      this.archivedTime = archivedTime;
      this.archivedLog = archivedLog;
    } else {
      this.archivist = null;
      this.archivedTime = null;
      this.archivedLog = null;
    }
  }

  public boolean isArchived() {
    return archived;
  }

  @Override
  public Instant obtainOperatedTime() {
    return getArchivedTime();
  }

  @Override
  public Long obtainOperatorId() {
    return getArchivist() != null ? getArchivist().getId() : null;
  }

}
