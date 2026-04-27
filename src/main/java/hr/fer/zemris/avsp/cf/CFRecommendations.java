package hr.fer.zemris.avsp.cf;

import hr.fer.zemris.avsp.cf.recommender.CFRecommender;

public class CFRecommendations {

    public static void main() {
        CFRecommender cfRecommender = new CFRecommender();
        cfRecommender.runCFQueries();
    }

}
