package org.example.repository;

import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.example.model.Desk;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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

    public List<Desk> findByName(String name) throws ExecutionException, InterruptedException {
        return getCollection().whereEqualTo("name", name).get().get().getDocuments()
                .stream()
                .map(this::mapDocumentSnapshot)
                .collect(Collectors.toList());
    }

    private Desk mapDocumentSnapshot(QueryDocumentSnapshot doc) {
        Desk d = doc.toObject(Desk.class);
        setId(d, doc.getId());
        return d;
    }
}
