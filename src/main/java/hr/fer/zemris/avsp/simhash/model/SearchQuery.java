package hr.fer.zemris.avsp.simhash.model;

public record SearchQuery(int numberOfDocuments,
                          int documentIndex,
                          int numberOfDifferentBits) {

    private static final int MIN_NUMBER_OF_DOCUMENTS = 2;
    private static final int MAX_NUMBER_OF_DIFFERENT_BITS = 31;

    public SearchQuery {
        if (numberOfDocuments < MIN_NUMBER_OF_DOCUMENTS) {
            String message = String.format("Number of documents must be at least %d.", MIN_NUMBER_OF_DOCUMENTS);
            throw new IllegalArgumentException(message);
        }

        if (documentIndex < 0 || documentIndex >= numberOfDocuments) {
            String message = String.format("Document index must be between 0 and %d.", numberOfDocuments - 1);
            throw new IllegalArgumentException(message);
        }

        if (numberOfDifferentBits < 0 || numberOfDifferentBits > MAX_NUMBER_OF_DIFFERENT_BITS) {
            String message = "Number of different bits must be between 0 and 31.";
            throw new IllegalArgumentException(message);
        }
    }

}
