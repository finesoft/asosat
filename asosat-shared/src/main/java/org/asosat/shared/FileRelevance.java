package org.asosat.shared;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;

/**
 * @author don
 * @date 2019-09-27
 */
@MappedSuperclass
@Embeddable
public class FileRelevance implements ValueObject {

  private static final long serialVersionUID = 3204979646451864469L;

  @Column
  private String uri;
  @Column
  private String name;

  public FileRelevance(String uri, String name) {
    this.uri = uri;
    this.name = name;
  }

  protected FileRelevance() {}

  public String getName() {
    return name;
  }

  public String getUri() {
    return uri;
  }
}
