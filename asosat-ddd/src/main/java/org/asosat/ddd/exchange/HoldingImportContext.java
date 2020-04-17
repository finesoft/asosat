package org.asosat.ddd.exchange;

import static org.corant.shared.util.Assertions.shouldBeNull;

import org.asosat.shared.exchange.DataImporter.ImportContext;

/**
 * @author don
 * @date 2020-02-26
 */
public class HoldingImportContext implements ImportContext {

  final protected Integer total;
  protected Integer right;
  protected Integer error;

  private Long referenceId;
  private String referenceNumber;
  private Object current;

  HoldingImportContext(Integer total) {
    this.total = total;
    this.right = 0;
    this.error = 0;
  }

  public <T> void setOnceCurrentVariable(T obj) {
    shouldBeNull(this.current);
    this.current = obj;
  }

  public void setOnceReference(Long refId, String refNumber) {
    shouldBeNull(this.referenceId);
    shouldBeNull(this.referenceNumber);
    this.referenceId = refId;
    this.referenceNumber = refNumber;
  }

  @SuppressWarnings("unchecked")
  public <T> T getCurrentVariable() {
    return (T) this.current;
  }

  @Override
  public Long getReferenceId() {
    return referenceId;
  }

  @Override
  public String getReferenceNumber() {
    return referenceNumber;
  }

  @Override
  public Integer getRight() {
    return right;
  }

  @Override
  public Integer getError() {
    return error;
  }

  @Override
  public Integer getTotal() {
    return total;
  }
}
