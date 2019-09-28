package org.asosat.ddd.storage;

import org.corant.suites.ddd.event.AbstractEvent;

/**
 * asosat-ddd
 * @author bingo 下午12:03:06
 */
public class FileDeprecatedEvent extends AbstractEvent {

    private static final long serialVersionUID = 3061637650767783676L;

    private final String uri;

    public FileDeprecatedEvent(Object source, String uri) {
        super(source);
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}