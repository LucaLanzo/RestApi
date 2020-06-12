package database;

import java.util.List;

public interface CourseDAO<D> {
    List<D> getAll();
    List<D> getByName(String name);
    D getById(String id);
    void insertInto(D document);
    void update(D updatedDocument, String id);
    void delete(String id);

    boolean isNotInDatabase(String id);
}
