package org.example.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public abstract class FirestoreRepository<T> {

    protected abstract String getCollectionName();
    protected abstract Class<T> getEntityClass();

    protected Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }

    protected CollectionReference getCollection() {
        return getFirestore().collection(getCollectionName());
    }

    public List<T> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = getCollection().get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        return documents.stream()
                .map(doc -> {
                    T entity = doc.toObject(getEntityClass());
                    setId(entity, doc.getId());
                    return entity;
                })
                .collect(Collectors.toList());
    }

    public Optional<T> findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getCollection().document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            T entity = document.toObject(getEntityClass());
            setId(entity, document.getId());
            return Optional.ofNullable(entity);
        } else {
            return Optional.empty();
        }
    }

    public T save(T entity) throws ExecutionException, InterruptedException {
        String id = getId(entity);
        DocumentReference docRef;
        if (id == null || id.isEmpty()) {
            docRef = getCollection().document();
            setId(entity, docRef.getId());
        } else {
            docRef = getCollection().document(id);
        }
        
        ApiFuture<com.google.cloud.firestore.WriteResult> result = docRef.set(entity);
        result.get(); // Wait for completion
        return entity;
    }

    public void delete(String id) throws ExecutionException, InterruptedException {
        ApiFuture<com.google.cloud.firestore.WriteResult> result = getCollection().document(id).delete();
        result.get();
    }

    // Helper methods to get/set ID using reflection or similar mechanism
    // In a simpler way for this project, let's assume objects have 'id' field
    protected void setId(T entity, String id) {
        try {
            java.lang.reflect.Field idField = getEntityClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            // Ignore if no ID field exists
        }
    }

    protected String getId(T entity) {
        try {
            java.lang.reflect.Field idField = getEntityClass().getDeclaredField("id");
            idField.setAccessible(true);
            return (String) idField.get(entity);
        } catch (Exception e) {
            return null;
        }
    }
}
