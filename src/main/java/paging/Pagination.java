package paging;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriInfo;


public class Pagination {
    private static Link createPreviousPage(UriInfo uriInfo, String rel, String name, int offset, int size) {
        if (offset == 0 || size == 0) {
            return null;
        } else if ((offset - size) <= 0){
            return createLink(uriInfo, rel, name, 0, offset);
        } else {
            return createLink(uriInfo, rel, name, (offset - size), size);
        }
    }


    private static Link createThisPage(UriInfo uriInfo, String rel, String name, int offset, int size) {
        return createLink(uriInfo, rel, name, offset, size);
    }


    private static Link createNextPage(UriInfo uriInfo, String rel, String name, int offset, int size,
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


    private static Link[] getLinkArray(Link linkForPost, Link previousPage, Link thisPage, Link nextPage) {
        if (previousPage == null && nextPage == null) {
            return new Link[] {linkForPost, thisPage};
        } else if (previousPage == null) {
            return new Link[] {linkForPost, thisPage, nextPage};
        } else if (nextPage == null) {
            return new Link[] {linkForPost, previousPage, thisPage};
        } else {
            return new Link[] {linkForPost, previousPage, thisPage, nextPage};
        }
    }


    private static int checkOffset(int offset, int amountOfResources) {
        if (offset > amountOfResources) offset = amountOfResources;
        if (offset < 0) offset = 0;
        return offset;
    }


    private static int checkSize(int size) {
        if (size <= 0) return 1;
        else return size;
    }


    public static Link[] createPagination(UriInfo uriInfo, int size, int offset, int amountOfResources, String name,
                                          Link linkForPost) {
        size = checkSize(size);
        offset = checkOffset(offset, amountOfResources);

        Link previousPage = createPreviousPage(uriInfo, "previousPage", name, offset, size);
        Link thisPage = createThisPage(uriInfo, "selfPage", name, offset, size);
        Link nextPage = createNextPage(uriInfo, "nextPage", name, offset, size, amountOfResources);

        return getLinkArray(linkForPost, previousPage, thisPage, nextPage);
    }
}
