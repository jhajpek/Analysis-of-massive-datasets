package hr.fer.zemris.avsp.simhash;

import hr.fer.zemris.avsp.simhash.hashing.HashFunction;
import hr.fer.zemris.avsp.simhash.hashing.impl.MD5;
import hr.fer.zemris.avsp.simhash.search.SearchEngine;
import hr.fer.zemris.avsp.simhash.search.impl.SequentialSearchEngine;

public class SequentialSearch {

    public static void main() {
        HashFunction hashFunction = new MD5();
        SearchEngine searchEngine = new SequentialSearchEngine(hashFunction);
        searchEngine.runSearchQueries();
    }

}
