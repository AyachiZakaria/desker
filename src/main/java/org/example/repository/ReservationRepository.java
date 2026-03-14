package org.example.repository;

import org.example.model.Reservation;
import org.springframework.stereotype.Repository;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class ReservationRepository extends FirestoreRepository<Reservation> {

    @Override
    protected String getCollectionName() {
        return "reservations";
    }

    @Override
    protected Class<Reservation> getEntityClass() {
        return Reservation.class;
    }

    public List<Reservation> findByUserId(String userId) throws ExecutionException, InterruptedException {
        return getCollection().whereEqualTo("userId", userId).get().get().getDocuments()
                .stream()
                .map(this::mapDocumentSnapshot)
                .collect(Collectors.toList());
    }
    
    public List<Reservation> findByUserIdAndMonth(String userId, String yearMonthPrefix) throws ExecutionException, InterruptedException {
        // yearMonthPrefix is e.g., "2026-03"
        return getCollection()
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("date", yearMonthPrefix + "-01")
                .whereLessThanOrEqualTo("date", yearMonthPrefix + "-31")
                .get().get().getDocuments()
                .stream()
                .map(this::mapDocumentSnapshot)
                .collect(Collectors.toList());
    }

    public List<Reservation> findByDate(String date) throws ExecutionException, InterruptedException {
        return getCollection().whereEqualTo("date", date).get().get().getDocuments()
                .stream()
                .map(this::mapDocumentSnapshot)
                .collect(Collectors.toList());
    }
    
    public List<Reservation> findByDeskIdAndDate(String deskId, String date) throws ExecutionException, InterruptedException {
        return getCollection()
                .whereEqualTo("deskId", deskId)
                .whereEqualTo("date", date)
                .get().get().getDocuments()
                .stream()
                .map(this::mapDocumentSnapshot)
                .collect(Collectors.toList());
    }

    public List<Reservation> findByMonth(String yearMonthPrefix) throws ExecutionException, InterruptedException {
        return getCollection()
                .whereGreaterThanOrEqualTo("date", yearMonthPrefix + "-01")
                .whereLessThanOrEqualTo("date", yearMonthPrefix + "-31")
                .get().get().getDocuments()
                .stream()
                .map(this::mapDocumentSnapshot)
                .collect(Collectors.toList());
    }

    private Reservation mapDocumentSnapshot(QueryDocumentSnapshot doc) {
        Reservation r = doc.toObject(Reservation.class);
        setId(r, doc.getId());
        return r;
    }
}
