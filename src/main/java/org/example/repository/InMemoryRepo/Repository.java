package org.example.repository.InMemoryRepo;

import java.util.List;

public interface Repository<US, E> {
    void save(E e);
    void delete(US us);
    List<E> findAll();
    E findOne(US us);
}
