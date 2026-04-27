package hr.fer.zemris.avsp.cf.recommender;

import hr.fer.zemris.avsp.cf.model.CFQuery;
import hr.fer.zemris.avsp.cf.util.CFDefinitionException;
import hr.fer.zemris.avsp.cf.util.CFType;
import hr.fer.zemris.avsp.cf.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CFRecommender {
    private int[][] itemUserMatrix;
    private int[][] userItemMatrix;
    private CFQuery[] cfQueries;

    public CFRecommender() {
        try {
            setCFDefinitionFromSystemIn();
        } catch (IOException e) {
            throw new CFDefinitionException("There was an issue while reading from System.in: " + e.getMessage());
        }
    }

    public CFRecommender(int[][] itemUserMatrix, CFQuery[] cfQueries) {
        this.itemUserMatrix = itemUserMatrix;
        this.userItemMatrix = Util.transposeMatrix(itemUserMatrix);
        this.cfQueries = cfQueries;
    }

    private void setCFDefinitionFromSystemIn() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String[] firstLine = reader.readLine().split(" +");
        if (firstLine.length != 2) {
            throw new CFDefinitionException("First line in input should be number of items and number of users separated with a whitespace.");
        }

        int numberOfItems;
        int numberOfUsers;
        try {
            numberOfItems = Integer.parseInt(firstLine[0]);
            numberOfUsers = Integer.parseInt(firstLine[1]);
        } catch (NumberFormatException e) {
            throw new CFDefinitionException("First line in input should be number of items and number of users separated with a whitespace.");
        }
        if (numberOfItems < 1 || numberOfItems > 100) {
            throw new CFDefinitionException("Number of items should be an integer value between 1 and 100.");
        }
        if (numberOfUsers < 1 || numberOfUsers > 100) {
            throw new CFDefinitionException("Number of users should be an integer value between 1 and 100.");
        }

        int[][] itemUserMatrix = new int[numberOfItems][numberOfUsers];

        for (int i = 0; i < numberOfItems; i++) {
            String[] itemRatings = reader.readLine().split(" +");
            if (itemRatings.length != numberOfUsers) {
                throw new CFDefinitionException("Number of ratings for each item should be equal to the number of users.");
            }
            for (int j = 0; j < numberOfUsers; j++) {
                try {
                    int rating = Integer.parseInt(itemRatings[j]);
                    if (rating < 1 || rating > 5) {
                        throw new CFDefinitionException("Rating in User-Item matrix should only be '1', '2', '3', '4', '5' or 'X'.");
                    }
                    itemUserMatrix[i][j] = rating;
                } catch (NumberFormatException e) {
                    if (!itemRatings[j].equals("X")) {
                        throw new CFDefinitionException("Rating in User-Item matrix should only be '1', '2', '3', '4', '5' or 'X'.");
                    }
                    itemUserMatrix[i][j] = 0;
                }
            }
        }

        int q;
        try {
            q = Integer.parseInt(reader.readLine());
        } catch (NumberFormatException e) {
            throw new CFDefinitionException("Number of hr.fer.zemris.avsp.cf.algorithm.CollaborativeFilteringRecommender queries should be an integer between 1 and 100.");
        }

        if (q < 1 || q > 100) {
            throw new CFDefinitionException("Number of hr.fer.zemris.avsp.cf.algorithm.CollaborativeFilteringRecommender queries should be an integer between 1 and 100.");
        }

        String queryFormat = "Query format: '{i} {j} {t} {k}'.";
        CFQuery[] cfQueries = new CFQuery[q];
        for (int qIdx = 0; qIdx < q; qIdx++) {
            String[] queryDefinition = reader.readLine().split(" +");
            if (queryDefinition.length != 4) {
                throw new CFDefinitionException(queryFormat + " Each query should have exactly 4 integers in their definition.");
            }

            int i;
            try {
                i = Integer.parseInt(queryDefinition[0]);
                if (i < 1 || i > numberOfItems) {
                    throw new CFDefinitionException(queryFormat + " Each query should have an item index i(1 <= i <= number of items).");
                }
            } catch (NumberFormatException e) {
                throw new CFDefinitionException(queryFormat + " Each query should have exactly 4 integers in their definition.");
            }

            int j;
            try {
                j = Integer.parseInt(queryDefinition[1]);
                if (j < 1 || j > numberOfUsers) {
                    throw new CFDefinitionException(queryFormat + " Each query should have a user index j(1 <= j <= number of users).");
                }
            } catch (NumberFormatException e) {
                throw new CFDefinitionException(queryFormat + " Each query should have exactly 4 integers in their definition.");
            }

            CFType cfType;
            try {
                int t = Integer.parseInt(queryDefinition[2]);
                if (t == 0) {
                    cfType = CFType.ItemItem;
                } else if (t == 1) {
                    cfType = CFType.UserUser;
                } else {
                    throw new CFDefinitionException(queryFormat + " Each query should have a type of algorithm t(t is 0 for item-item or 1 for user-user algorithm).");
                }
            } catch (NumberFormatException e) {
                throw new CFDefinitionException(queryFormat + " Each query should have exactly 4 integers in their definition.");
            }

            int k;
            try {
                k = Integer.parseInt(queryDefinition[3]);
                if (k < 1) {
                    throw new CFDefinitionException(queryFormat + " Each query should have a maximum cardinal number of similar items k(k >= 1).");
                }
                if (cfType == CFType.ItemItem && k > numberOfItems) {
                    throw new CFDefinitionException(queryFormat + " Each query should have a maximum cardinal number of similar items k(1 <= k <= number of items).");
                }
                if (cfType == CFType.UserUser && k > numberOfUsers) {
                    throw new CFDefinitionException(queryFormat + " Each query should have a maximum cardinal number of similar items k(1 <= k <= number of users).");
                }
            } catch (NumberFormatException e) {
                throw new CFDefinitionException(queryFormat + " Each query should have exactly 4 integers in their definition.");
            }

            cfQueries[qIdx] = new CFQuery(i, j, k, cfType);
        }

        this.itemUserMatrix = itemUserMatrix;
        this.userItemMatrix = Util.transposeMatrix(itemUserMatrix);
        this.cfQueries = cfQueries;

        reader.close();
    }

    public void setCfQueries(CFQuery[] cfQueries) {
        this.cfQueries = cfQueries;
    }

    public void runCFQueries() {
        DecimalFormat decimalFormat = new DecimalFormat("#.000");
        for (CFQuery cfQuery : cfQueries) {
            BigDecimal recommendationValue = cfQuery.cfType() == CFType.ItemItem ?
                    getRecommendationValue(itemUserMatrix, cfQuery.i() - 1, cfQuery.j() - 1, cfQuery.k()) :
                    getRecommendationValue(userItemMatrix, cfQuery.j() - 1, cfQuery.i() - 1, cfQuery.k());
            System.out.println(decimalFormat.format(recommendationValue));
        }
    }

    private BigDecimal getRecommendationValue(int[][] matrix, int i, int j, int k) {
        Map<Integer, BigDecimal> pearsonSimilaritiesMap = new HashMap<>(matrix.length - 1);
        for (int row = 0; row < matrix.length; row++) {
            if (row == i) {
                continue;
            }
            pearsonSimilaritiesMap.put(row, getPearsonSimilarity(matrix[i], matrix[row]));
        }

        List<Map.Entry<Integer, BigDecimal>> topKPearsonSimilaritiesMapEntries = pearsonSimilaritiesMap.
                entrySet().stream().
                filter(e -> matrix[e.getKey()][j] != 0 && e.getValue().compareTo(BigDecimal.ZERO) > 0).
                sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).
                limit(k).
                toList();

        if (topKPearsonSimilaritiesMapEntries.isEmpty()) {
            throw new IllegalStateException("There are no similarities available.");
        }

        BigDecimal numerator = BigDecimal.ZERO;
        BigDecimal denominator = BigDecimal.ZERO;
        for (Map.Entry<Integer, BigDecimal> e : topKPearsonSimilaritiesMapEntries) {
            BigDecimal pearsonValue = e.getValue();
            BigDecimal userRating = BigDecimal.valueOf(matrix[e.getKey()][j]);
            numerator = numerator.add(pearsonValue.multiply(userRating));
            denominator = denominator.add(pearsonValue);
        }

        BigDecimal result = numerator.divide(denominator, 10, RoundingMode.HALF_UP);
        return result.setScale(3, RoundingMode.HALF_UP);
    }

    private BigDecimal getPearsonSimilarity(int[] arr1, int[] arr2) {
        if (arr1.length != arr2.length) {
            throw new IllegalArgumentException("Both arrays in getPearsonSimilarity method should have the same length.");
        }

        BigDecimal xMean = getArrayMean(arr1);
        BigDecimal yMean = getArrayMean(arr2);

        BigDecimal sumXY = BigDecimal.ZERO;
        BigDecimal sumXX = BigDecimal.ZERO;
        BigDecimal sumYY = BigDecimal.ZERO;
        for (int i = 0; i < arr1.length; i++) {
            BigDecimal diffX = arr1[i] == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(arr1[i]).subtract(xMean);
            BigDecimal diffY = arr2[i] == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(arr2[i]).subtract(yMean);
            sumXY = sumXY.add(diffX.multiply(diffY));
            sumXX = sumXX.add(diffX.multiply(diffX));
            sumYY = sumYY.add(diffY.multiply(diffY));
        }

        BigDecimal denominatorProduct = sumXX.multiply(sumYY);
        if (denominatorProduct.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal denominator = denominatorProduct.sqrt(new MathContext(10));
        return sumXY.divide(denominator, 10, RoundingMode.HALF_UP);
    }

    private BigDecimal getArrayMean(int[] arr) {
        BigDecimal sum = BigDecimal.valueOf(Arrays.stream(arr).sum());
        BigDecimal count = BigDecimal.valueOf(Arrays.stream(arr).filter(i -> i != 0).count());
        return sum.divide(count, 10, RoundingMode.HALF_UP);
    }

}
