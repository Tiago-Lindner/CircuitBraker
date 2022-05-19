package dev.pw2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;

@ApplicationScoped
public class CBRService {

    private Map<Integer, Item> itemList = new HashMap<>();

    public CBRService() {
        itemList.put(1, new Item(1, "Mouse"));
        itemList.put(2, new Item(2, "Teclado"));
        itemList.put(3, new Item(3, "Headset"));
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(itemList.values());
    }

    public Item getItemById(Integer id) {
        return itemList.get(id);
    }

    public List<Item> getRecommendations(Integer id) {
        if (id == null) {
            return Collections.emptyList();
        }
        return itemList.values().stream()
                .filter(item -> !id.equals(item.id))
                .limit(2)
                .collect(Collectors.toList());
    }

    private AtomicLong counter = new AtomicLong(0);

    @CircuitBreaker(requestVolumeThreshold = 4)
    public Integer getAvailability(Item item) {
        maybeFail();
        return new Random().nextInt(30);
    }

    private void maybeFail() {
        // introduce some artificial failures
        final Long invocationNumber = counter.getAndIncrement();
        if (invocationNumber % 4 > 1) { // alternate 2 successful and 2 failing invocations
            throw new RuntimeException("Service failed.");
        }
    }
}

