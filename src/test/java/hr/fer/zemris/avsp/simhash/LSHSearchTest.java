package hr.fer.zemris.avsp.simhash;

import hr.fer.zemris.avsp.simhash.hashing.impl.MD5;
import hr.fer.zemris.avsp.simhash.search.impl.LSHSearchEngine;
import hr.fer.zemris.avsp.simhash.search.SearchEngine;
import hr.fer.zemris.avsp.Util;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class LSHSearchTest {

    public static Stream<Arguments> testSource() throws URISyntaxException, IOException {
        return Util.getTestResources("simhash_lsh_search_tests");
    }

    @ParameterizedTest
    @MethodSource("testSource")
    public void testSequentialSearch(Path inputPath, Path outputPath) throws IOException {
        Runnable lshSearch = () -> {
            SearchEngine lshSearchEngine = new LSHSearchEngine(new MD5());
            lshSearchEngine.runSearchQueries();
        };
        Util.runTest(lshSearch, inputPath, outputPath);
    }

}
