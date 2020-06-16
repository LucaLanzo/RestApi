package database.dao;

import java.util.List;

public interface EventDAO<D> {
    List<D> getAll(int offset, int size);
    List<D> getByName(String name, int offset, int size);
    D getById(String id);
    void insertInto(D document);
    void update(D updatedDocument, String id);
    void delete(String id);

    boolean isNotInDatabase(String id);
    int getAmountOfResources(String name);
    int getAmountOfResources();
}
