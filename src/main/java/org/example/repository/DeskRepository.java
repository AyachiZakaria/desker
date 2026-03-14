package org.example.repository;

import org.example.model.Desk;
import org.springframework.stereotype.Repository;

@Repository
public class DeskRepository extends FirestoreRepository<Desk> {

    @Override
    protected String getCollectionName() {
        return "desks";
    }

    @Override
    protected Class<Desk> getEntityClass() {
        return Desk.class;
    }
}
