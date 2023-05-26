package ru.netology.netologySpringBootCourseProject.repository;

import org.springframework.stereotype.Repository;
import ru.netology.netologySpringBootCourseProject.model.Card;
import ru.netology.netologySpringBootCourseProject.model.Operation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class TransferRepository {
    private final AtomicInteger id = new AtomicInteger(0);
    private Map<String, Card> mapStorage;
    private ConcurrentHashMap<String, Operation> cardTransactionsWaitConfirmOperation;

    public Map<String, Card> getMapStorage() {
        return mapStorage;
    }

    public void setMapStorage(String cardNumber, Card card) {
        mapStorage.put(cardNumber, card);
    }

    public void setCardTransactionsWaitConfirmOperation(String id, Operation operation) {
        cardTransactionsWaitConfirmOperation.put(id, operation);
    }

    public void deleteWaitOperation(String operationId) {
        cardTransactionsWaitConfirmOperation.remove(operationId);
    }
}
