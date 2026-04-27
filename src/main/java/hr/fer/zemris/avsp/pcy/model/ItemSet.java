package hr.fer.zemris.avsp.pcy.model;

import java.util.List;

public record ItemSet(List<Integer> items) {

    public ItemSet {
        if (items.size() < 2) {
            throw new IllegalArgumentException("Size of an item set should be at least 2.");
        }
        items.sort(Integer::compareTo);
    }

    public int size() {
        return items.size();
    }

}
