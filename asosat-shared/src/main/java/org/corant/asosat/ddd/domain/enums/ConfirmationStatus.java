package org.corant.asosat.ddd.domain.enums;

/**
 * @author don
 * @create 2019-08-13
 */
public enum ConfirmationStatus {

    UNCONFIRM(0), APPROVED(1), DISAPPROVED(2);

    int sign;

    private ConfirmationStatus(int sign) {
        this.sign = sign;
    }
}
