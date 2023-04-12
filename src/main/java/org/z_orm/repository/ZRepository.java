package org.z_orm.repository;

import java.util.List;
import java.util.Optional;

public interface ZRepository<T, ID> {

    <S extends T> S save(S entity);

    <S extends T> List<S> findAll();

    Optional<T> findById(ID id);

}
