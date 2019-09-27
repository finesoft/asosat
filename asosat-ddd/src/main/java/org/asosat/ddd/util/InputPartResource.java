package org.asosat.ddd.util;

import org.corant.shared.exception.NotSupportedException;
import org.corant.shared.util.Resources.Resource;
import org.corant.shared.util.Resources.SourceType;
import org.corant.suites.servlet.abstraction.ContentDispositions;
import org.corant.suites.servlet.abstraction.ContentDispositions.ContentDisposition;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import static org.corant.shared.util.Assertions.shouldNotNull;
import static org.corant.shared.util.MapUtils.immutableMapOf;
import static org.corant.shared.util.ObjectUtils.defaultObject;

/**
 * resteasy  InputPart resource
 * @author don
 * @date 2019-09-26
 */
public class InputPartResource implements Resource {

    private InputPart inputPart;

    private String filename;

    public InputPartResource(InputPart inputPart) {
        this.inputPart = shouldNotNull(inputPart);
        ContentDisposition cd = ContentDispositions.parse(inputPart.getHeaders().getFirst("Content-Disposition"));
        this.filename = defaultObject(cd.getFilename(), () -> "unnamed-" + UUID.randomUUID());
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public String getLocation() {
        return this.filename;
    }

    @Override
    public SourceType getSourceType() {
        return null;
    }

    @Override
    public URL getUrl() {
        throw new NotSupportedException();
    }

    @Override
    public InputStream openStream() throws IOException {
        return inputPart.getBody(InputStream.class, null);
    }

    @Override
    public Map<String, Object> getMetadatas() {
        return immutableMapOf("location", getLocation(), "sourceType", getSourceType());
    }
}
