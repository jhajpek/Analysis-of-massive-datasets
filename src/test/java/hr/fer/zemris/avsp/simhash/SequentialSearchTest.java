package hr.fer.zemris.avsp.simhash;

import hr.fer.zemris.avsp.simhash.hashing.impl.MD5;
import hr.fer.zemris.avsp.simhash.search.impl.SequentialSearchEngine;
import hr.fer.zemris.avsp.simhash.search.SearchEngine;
import hr.fer.zemris.avsp.Util;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class SequentialSearchTest {

    public static Stream<Arguments> testSource() throws URISyntaxException, IOException {
        return Util.getTestResources("simhash_sequential_search_tests");
    }

    @ParameterizedTest
    @MethodSource("testSource")
    public void testSequentialSearch(Path inputPath, Path outputPath) throws IOException {
        Runnable sequentialSearch = () -> {
            SearchEngine sequentialSearchEngine = new SequentialSearchEngine(new MD5());
            sequentialSearchEngine.runSearchQueries();
        };
        Util.runTest(sequentialSearch, inputPath, outputPath);
    }

}
