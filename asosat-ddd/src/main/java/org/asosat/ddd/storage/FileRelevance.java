package org.asosat.ddd.storage;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.asosat.shared.ValueObject;

/**
 * @author don
 * @date 2019-09-27
 */
@Embeddable
@AttributeOverride(column = @Column(name = "fileUri"), name = "uri")
@AttributeOverride(column = @Column(name = "fileName"), name = "name")
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

    protected FileRelevance() {
    }

    public String getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }
}