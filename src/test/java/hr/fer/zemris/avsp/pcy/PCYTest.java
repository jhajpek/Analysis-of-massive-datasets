package hr.fer.zemris.avsp.pcy;

import hr.fer.zemris.avsp.Util;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class PCYTest {

    public static Stream<Arguments> testSource() throws URISyntaxException, IOException {
        return Util.getTestResources("pcy_tests");
    }

    @ParameterizedTest
    @MethodSource("testSource")
    public void testFrequentItemPairsSearch(Path inputPath, Path outputPath) throws IOException {
        Runnable pcyAlgorithm = FrequentPairsSearch::main;
        Util.runTest(pcyAlgorithm, inputPath, outputPath);
    }

}
