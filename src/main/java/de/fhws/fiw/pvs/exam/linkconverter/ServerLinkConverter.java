package de.fhws.fiw.pvs.exam.linkconverter;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;

import javax.ws.rs.core.Link;

/***
 * By Luca Lanzo
 */


public class ServerLinkConverter implements Converter<Link> {
    @Override
    public void serialize(Link link, ObjectWriter objectWriter, Context context) {
        objectWriter.writeName(link.getRel());
        objectWriter.beginObject();
        objectWriter.writeString("href", link.getUri().toASCIIString());
        objectWriter.writeString("rel", link.getRel());

        if (link.getType() != null && !link.getType().isEmpty()) {
            objectWriter.writeString("type", link.getType());
        }

        objectWriter.endObject();
    }

    @Override
    public Link deserialize(ObjectReader objectReader, Context context) {
        String uri = "";
        String type = "";
        String rel = "";

        objectReader.beginObject();

        while (objectReader.hasNext()) {
            if ("href".equals(objectReader.name())) {
                uri = objectReader.valueAsString();
            }
            if ("rel".equals(objectReader.name())) {
                rel = objectReader.valueAsString();
            }
            if ("type".equals(objectReader.name())) {
                type = objectReader.valueAsString();
            }
        }

        objectReader.endObject();

        return Link.fromUri(uri)
                .rel(rel)
                .type(type)
                .build();
    }
}
