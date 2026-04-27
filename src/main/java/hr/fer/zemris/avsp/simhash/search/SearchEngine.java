package hr.fer.zemris.avsp.simhash.search;

import hr.fer.zemris.avsp.simhash.hashing.HashFunction;
import hr.fer.zemris.avsp.simhash.model.SearchQuery;
import hr.fer.zemris.avsp.simhash.util.SearchEngineDefinitionException;
import hr.fer.zemris.avsp.simhash.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

public abstract class SearchEngine {
    protected BigInteger[] simHashDocumentArray;
    protected SearchQuery[] searchQueries;
    protected final int hashBitLength;

    public SearchEngine(BigInteger[] simHashDocumentArray,
                        SearchQuery[] searchQueries,
                        int hashBitLength) {
        this.simHashDocumentArray = simHashDocumentArray;
        this.searchQueries = searchQueries;
        this.hashBitLength = hashBitLength;
    }

    public SearchEngine(HashFunction hashFunction) {
        try {
            setSearchEngineDefinitionFromSystemIn(hashFunction);
        } catch (NumberFormatException e) {
            String message = "Number of documents, number of queries and query parameters should be integers.";
            throw new SearchEngineDefinitionException(message);
        } catch (IOException e) {
            String message = String.format("There was an issue while reading from System.in: %s", e.getMessage());
            throw new SearchEngineDefinitionException(message);
        }
        this.hashBitLength = hashFunction.getHashBitLength();
    }

    private void setSearchEngineDefinitionFromSystemIn(HashFunction hashFunction) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        int n = Integer.parseInt(reader.readLine());
        if (n <= 0) {
            String message = "Number of documents must be a positive integer.";
            throw new SearchEngineDefinitionException(message);
        }
        this.simHashDocumentArray = new BigInteger[n];
        for (int i = 0; i < n; i++) {
            this.simHashDocumentArray[i] = Util.generateSimHash(reader.readLine(), hashFunction);
        }

        int q = Integer.parseInt(reader.readLine());
        if (q <= 0) {
            String message = "Number of queries must be a positive integer.";
            throw new SearchEngineDefinitionException(message);
        }
        this.searchQueries = new SearchQuery[q];
        for (int i = 0; i < q; i++) {
            String[] splitLine = reader.readLine().split(" +");
            if (splitLine.length != 2) {
                String message = "Each query definition line should have exactly 2 integers separated by a whitespace.";
                throw new SearchEngineDefinitionException(message);
            }
            this.searchQueries[i] = new SearchQuery(n, Integer.parseInt(splitLine[0]), Integer.parseInt(splitLine[1]));
        }

        reader.close();
    }

    public void setSimHashDocumentArray(BigInteger[] simHashDocumentArray) {
        this.simHashDocumentArray = simHashDocumentArray;
    }

    public void setSearchQueries(SearchQuery[] searchQueries) {
        this.searchQueries = searchQueries;
    }

    public abstract void runSearchQueries();

}
