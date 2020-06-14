package paging;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriInfo;


public class Pagination {
    public static Link createPreviousPage(UriInfo uriInfo, String rel, String name, int offset, int size) {
        if (offset == 0 || size == 0) {
            return null;
        } else if ((offset - size) <= 0){
            return createLink(uriInfo, rel, name, 0, offset);
        } else {
            return createLink(uriInfo, rel, name, (offset - size), size);
        }
    }


    public static Link createThisPage(UriInfo uriInfo, String rel, String name, int offset, int size) {
        return createLink(uriInfo, rel, name, offset, size);
    }


    public static Link createNextPage(UriInfo uriInfo, String rel, String name, int offset, int size,
                                      int amountOfResources) {
        if ((offset + size) >= amountOfResources) {
            return null;
        } else if ((offset + size * 2) > amountOfResources) {
            return createLink(uriInfo, rel, name, (offset + size), (amountOfResources - (offset + size)));
        } else {
            return createLink(uriInfo, rel, name, (offset + size), size);
        }
    }


    private static Link createLink(UriInfo uriInfo, String rel, String name, int offset, int size) {
        if (name.equals("")) {
            return Link.fromUri(uriInfo.getAbsolutePath() + "?offset=" + offset + "&size=" + size)
                    .rel(rel)
                    .type("application/json")
                    .build();
        } else {
            return Link.fromUri(uriInfo.getAbsolutePath() + "?name=" + name + "&offset=" + offset + "&size=" + size)
                    .rel(rel)
                    .type("application/json")
                    .build();
        }
    }
}
