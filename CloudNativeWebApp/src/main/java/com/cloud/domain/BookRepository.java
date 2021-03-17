package com.cloud.domain;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete
@Repository
public interface BookRepository extends CrudRepository<Book, String> {
    @Query
    Optional<Book> findById(String id);

    Iterable<Book> findAllByUserId(String id);

}