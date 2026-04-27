package hr.fer.zemris.avsp.simhash.search.impl;

import hr.fer.zemris.avsp.simhash.hashing.HashFunction;
import hr.fer.zemris.avsp.simhash.model.SearchQuery;
import hr.fer.zemris.avsp.simhash.search.SearchEngine;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LSHSearchEngine extends SearchEngine {

    private static final int NUMBER_OF_BANDS = 8;

    public LSHSearchEngine(BigInteger[] simHashDocumentArray,
                           SearchQuery[] searchQueries,
                           int hashBitLength) {
        super(simHashDocumentArray, searchQueries, hashBitLength);
    }

    public LSHSearchEngine(HashFunction hashFunction) {
        super(hashFunction);
    }

    @Override
    public void runSearchQueries() {
        HashMap<Integer, Set<Integer>> potentialSimilarSimHashesMap = new HashMap<>();
        for (int i = 0; i < simHashDocumentArray.length; i++) {
            potentialSimilarSimHashesMap.put(i, new HashSet<>());
        }

        int bitsPerBand = hashBitLength / NUMBER_OF_BANDS;
        BigInteger mask = BigInteger.ONE.shiftLeft(bitsPerBand).subtract(BigInteger.ONE);

        for (int band = 0; band < NUMBER_OF_BANDS; band++) {
            HashMap<Integer, Set<Integer>> buckets = new HashMap<>();

            for (int i = 0; i < simHashDocumentArray.length; i++) {
                BigInteger simHash = simHashDocumentArray[i];
                int bucketKey = simHash.shiftRight(band * bitsPerBand).and(mask).intValue();

                Set<Integer> bucketTextsIds = buckets.get(bucketKey);
                if (bucketTextsIds != null) {
                    for (Integer textId : bucketTextsIds) {
                        potentialSimilarSimHashesMap.get(i).add(textId);
                        potentialSimilarSimHashesMap.get(textId).add(i);
                    }
                } else {
                    bucketTextsIds = new HashSet<>();
                }

                bucketTextsIds.add(i);
                buckets.put(bucketKey, bucketTextsIds);
            }
        }

        for (SearchQuery query : searchQueries) {
            int numberOfSimilarItems = 0;
            BigInteger firstSimHash = simHashDocumentArray[query.documentIndex()];
            Set<Integer> ids = potentialSimilarSimHashesMap.get(query.documentIndex());
            for (Integer id : ids) {
                BigInteger secondSimHash = simHashDocumentArray[id];
                int hammingDistance = firstSimHash.xor(secondSimHash).bitCount();
                if (hammingDistance <= query.numberOfDifferentBits()) {
                    numberOfSimilarItems++;
                }
            }
            System.out.println(numberOfSimilarItems);
        }
    }

}
