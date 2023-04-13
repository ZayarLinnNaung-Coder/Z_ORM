package org.z_orm.repository;

import java.util.List;
import java.util.Optional;

public interface ZRepository<T, ID> {

    T save(T entity);

    List<T> findAll();

    Optional<T> findById(ID id);

    void delete(T entity);

}
