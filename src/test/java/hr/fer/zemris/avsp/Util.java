package hr.fer.zemris.avsp;

import org.junit.jupiter.params.provider.Arguments;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Util {

    public static Stream<Arguments> getTestResources(String path) throws URISyntaxException, IOException {
        Objects.requireNonNull(path, "Argument of getTestResources method can't be null.");

        URL resource = Util.class.getClassLoader().getResource(path);
        if (resource == null) {
            String message = String.format("Couldn't find parent path: %s for test resources.", path);
            throw new IllegalArgumentException(message);
        }

        Path root = Paths.get(resource.toURI());
        Stream<Path> walk = Files.list(root);
        return walk.filter(Files::isDirectory)
                .sorted()
                .map(p -> Arguments.of(p.resolve("R.in"), p.resolve("R.out")))
                .onClose(walk::close);
    }

    public static void runTest(Runnable algorithm, Path inputPath, Path outputPath) throws IOException {
        try (InputStream testInputStream = Files.newInputStream(inputPath);
             ByteArrayOutputStream testOutputByteStream = new ByteArrayOutputStream();
             PrintStream testOutputStream = new PrintStream(testOutputByteStream)) {

            InputStream originalIn = System.in;
            PrintStream originalOut = System.out;

            try {
                System.setIn(testInputStream);
                System.setOut(testOutputStream);

                algorithm.run();

                String expectedOutput = Files.readString(outputPath).trim().replace("\r\n", "\n");
                String receivedOutput = testOutputByteStream.toString().trim().replace("\r\n", "\n");

                String message = String.format("Test failed: %s", inputPath.getParent().toString());
                assertEquals(expectedOutput, receivedOutput, message);
            } finally {
                System.setIn(originalIn);
                System.setOut(originalOut);
            }
        }
    }

}
