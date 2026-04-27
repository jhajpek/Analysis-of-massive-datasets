package hr.fer.zemris.avsp.pcy.search;

import hr.fer.zemris.avsp.pcy.model.ItemSet;
import hr.fer.zemris.avsp.pcy.util.PCYDefinitionException;
import hr.fer.zemris.avsp.simhash.util.SearchEngineDefinitionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PCYSearchEngine {
    private List<List<Integer>> baskets = new ArrayList<>();
    private int supportThreshold;
    private int[] boxes;
    private int[] itemCount;
    private final Map<ItemSet, Integer> repetitionMap = new HashMap<>();

    public PCYSearchEngine(List<List<Integer>> baskets,
                           int supportThreshold,
                           int numberOfBoxes,
                           int numberOfDifferentItems) {
        this.baskets = baskets;
        this.supportThreshold = supportThreshold;
        this.boxes = new int[numberOfBoxes];
        this.itemCount = new int[numberOfDifferentItems];
    }

    public PCYSearchEngine() {
        try {
            setEngineDefinitionFromSystemIn();
        } catch (NumberFormatException e) {
            String message = "Number of baskets, number of buckets and items should be integers. Support threshold should be a float";
            throw new SearchEngineDefinitionException(message);
        } catch (IOException e) {
            String message = String.format("There was an issue while reading from System.in: %s", e.getMessage());
            throw new SearchEngineDefinitionException(message);
        }
    }

    private void setEngineDefinitionFromSystemIn() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        int n = Integer.parseInt(reader.readLine());
        this.supportThreshold = (int) (Double.parseDouble(reader.readLine()) * n);
        if (this.supportThreshold < 0 || this.supportThreshold > n) {
            throw new PCYDefinitionException("Support threshold should be a float with value between 0 and 1.");
        }
        this.boxes = new int[Integer.parseInt(reader.readLine())];

        Set<Integer> allItems = new HashSet<>();
        for (int i = 0; i < n; i++) {
            String line = reader.readLine();
            List<Integer> basket = Arrays.stream(line.split(" +"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            this.baskets.add(basket);
            allItems.addAll(basket);
        }
        this.itemCount = new int[allItems.size()];

        reader.close();
    }

    public void runPCYAlgorithm() {
        passFirstTime();
        passSecondTime();

        System.out.println(getAprioriCandidatesSize());
        System.out.println(getPCYCandidatesSize());
        getDescendingFrequentPairsFrequencies().forEach(System.out::println);
    }

    private void passFirstTime() {
        for (List<Integer> basket : baskets) {
            for (Integer item : basket) {
                itemCount[item - 1]++;
            }
        }
        for (List<Integer> basket : baskets) {
            int size = basket.size();
            for (int i = 0; i < size; i++) {
                int first = basket.get(i) - 1;
                if (itemCount[first] < supportThreshold) {
                    continue;
                }
                for (int j = i + 1; j < size; j++) {
                    int second = basket.get(j) - 1;
                    if (itemCount[second] < supportThreshold) {
                        continue;
                    }
                    int boxIdx = getBoxIndex(first, second, itemCount.length, boxes.length);
                    boxes[boxIdx]++;
                }
            }
        }
    }

    private void passSecondTime() {
        for (List<Integer> basket : baskets) {
            int size = basket.size();
            for (int i = 0; i < size; i++) {
                int first = basket.get(i) - 1;
                if (itemCount[first] < supportThreshold) {
                    continue;
                }
                for (int j = i + 1; j < size; j++) {
                    int second = basket.get(j) - 1;
                    if (itemCount[second] < supportThreshold) {
                        continue;
                    }
                    int boxIdx = getBoxIndex(first, second, itemCount.length, boxes.length);
                    if (boxes[boxIdx] >= supportThreshold) {
                        ItemSet itemSet = new ItemSet(new ArrayList<>(List.of(first + 1, second + 1)));
                        repetitionMap.merge(itemSet, 1, Integer::sum);
                    }
                }
            }
        }
    }

    private int getBoxIndex(int i, int j, int k, int b) {
        return (Math.min(i, j) * k + Math.max(i, j)) % b;
    }

    private int getAprioriCandidatesSize() {
        int m = (int) Arrays.stream(itemCount).filter(i -> i >= supportThreshold).count();
        return m * (m - 1) / 2;
    }

    private int getPCYCandidatesSize() {
        return repetitionMap.size();
    }

    private List<Integer> getDescendingFrequentPairsFrequencies() {
        return repetitionMap.values().stream()
                .filter(i -> i >= supportThreshold)
                .sorted(Comparator.reverseOrder())
                .toList();
    }

}
