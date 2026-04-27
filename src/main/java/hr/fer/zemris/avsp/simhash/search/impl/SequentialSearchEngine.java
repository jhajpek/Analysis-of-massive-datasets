package hr.fer.zemris.avsp.simhash.search.impl;

import hr.fer.zemris.avsp.simhash.hashing.HashFunction;
import hr.fer.zemris.avsp.simhash.model.SearchQuery;
import hr.fer.zemris.avsp.simhash.search.SearchEngine;

import java.math.BigInteger;

public class SequentialSearchEngine extends SearchEngine {

    public SequentialSearchEngine(BigInteger[] simHashDocumentArray,
                                  SearchQuery[] searchQueries,
                                  int hashBitLength) {
        super(simHashDocumentArray, searchQueries, hashBitLength);
    }

    public SequentialSearchEngine(HashFunction hashFunction) {
        super(hashFunction);
    }

    @Override
    public void runSearchQueries() {
        for (SearchQuery query : searchQueries) {
            BigInteger firstSimHash = simHashDocumentArray[query.documentIndex()];
            int numberOfSimilarItems = 0;
            for (int i = 0; i < simHashDocumentArray.length; i++) {
                if (query.documentIndex() == i) {
                    continue;
                }
                BigInteger secondSimHash = simHashDocumentArray[i];
                int hammingDistance = firstSimHash.xor(secondSimHash).bitCount();
                if (hammingDistance <= query.numberOfDifferentBits()) {
                    numberOfSimilarItems++;
                }
            }
            System.out.println(numberOfSimilarItems);
        }
    }

}
