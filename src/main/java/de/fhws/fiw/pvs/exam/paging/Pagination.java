package de.fhws.fiw.pvs.exam.paging;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriInfo;

/***
 * By Luca Lanzo
 */


public class Pagination {
    // Create the pagination which creates all links and returns an array of all the links
    public static Link[] createPagination(UriInfo uriInfo, int size, int offset, int amountOfResources, String name,
                                          Link linkForPost) {
        size = checkSize(size);
        offset = checkOffset(offset, amountOfResources);


        Link previousPage = createPreviousPage(uriInfo, name, offset, size);
        Link thisPage = createThisPage(uriInfo, name, offset, size);
        Link nextPage = createNextPage(uriInfo, name, offset, size, amountOfResources);

        return getLinkArray(linkForPost, previousPage, thisPage, nextPage);
    }

    // This structures the linkArray and looks for missing links
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


    // Create the header link for the previous page
    private static Link createPreviousPage(UriInfo uriInfo, String name, int offset, int size) {
        if (offset == 0 || size == 0) {
            return null;
        } else if ((offset - size) <= 0){
            return createLink(uriInfo, "previousPage", name, 0, offset);
        } else {
            return createLink(uriInfo, "previousPage", name, (offset - size), size);
        }
    }


    // Create the header link for the self page
    private static Link createThisPage(UriInfo uriInfo, String name, int offset, int size) {
        return createLink(uriInfo, "selfPage", name, offset, size);
    }


    // Create the header link for the next page
    private static Link createNextPage(UriInfo uriInfo, String name, int offset, int size,
                                       int amountOfResources) {
        if ((offset + size) >= amountOfResources) {
            return null;
        } else if ((offset + size * 2) > amountOfResources) {
            return createLink(uriInfo, "nextPage", name, (offset + size), (amountOfResources - (offset + size)));
        } else {
            return createLink(uriInfo, "nextPage", name, (offset + size), size);
        }
    }


    // Template to create a link
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



    // Additional methods:

    // Check the offset

    private static int checkOffset(int offset, int amountOfResources) {
        if (offset > amountOfResources) offset = amountOfResources;
        if (offset < 0) offset = 0;
        return offset;
    }

    // Check the size

    private static int checkSize(int size) {
        if (size <= 0) return 1;
        else return size;
    }



}
