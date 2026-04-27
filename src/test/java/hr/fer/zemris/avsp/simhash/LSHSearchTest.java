package hr.fer.zemris.avsp.simhash;

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
    public void testLSHSearch(Path inputPath, Path outputPath) throws IOException {
        Runnable lshSearch = LSHSearch::main;
        Util.runTest(lshSearch, inputPath, outputPath);
    }

}
