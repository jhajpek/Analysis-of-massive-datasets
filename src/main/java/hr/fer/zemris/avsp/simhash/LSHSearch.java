package hr.fer.zemris.avsp.simhash;

import hr.fer.zemris.avsp.simhash.hashing.HashFunction;
import hr.fer.zemris.avsp.simhash.hashing.impl.MD5;
import hr.fer.zemris.avsp.simhash.search.SearchEngine;
import hr.fer.zemris.avsp.simhash.search.impl.LSHSearchEngine;

public class LSHSearch {

    public static void main(String[] args) {
        HashFunction hashFunction = new MD5();
        SearchEngine searchEngine = new LSHSearchEngine(hashFunction);
        searchEngine.runSearchQueries();
    }

}
