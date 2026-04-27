package hr.fer.zemris.avsp.cf;

import hr.fer.zemris.avsp.Util;
import hr.fer.zemris.avsp.cf.recommender.CFRecommender;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class CFTest {

    public static Stream<Arguments> testSource() throws URISyntaxException, IOException {
        return Util.getTestResources("cf_tests");
    }

    @ParameterizedTest
    @MethodSource("testSource")
    public void testCollaborativeFilteringRecommendations(Path inputPath, Path outputPath) throws IOException {
        Runnable cfAlgorithm = () -> {
            CFRecommender cfRecommender = new CFRecommender();
            cfRecommender.runCFQueries();
        };
        Util.runTest(cfAlgorithm, inputPath, outputPath);
    }

}
