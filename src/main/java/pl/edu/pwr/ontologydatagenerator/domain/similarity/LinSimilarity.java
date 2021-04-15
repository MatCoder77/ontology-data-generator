package pl.edu.pwr.ontologydatagenerator.domain.similarity;

import edu.cmu.lti.lexical_db.ILexicalDatabase;

public class LinSimilarity extends edu.cmu.lti.ws4j.impl.Lin {

    public LinSimilarity(ILexicalDatabase db) {
        super(db);
    }

    @Override
    public double calcRelatednessOfWords(String word1, String word2) {
        double relatedness = super.calcRelatednessOfWords(word1, word2);
        if (relatedness == Double.MAX_VALUE) {
            return 1;
        }
        return relatedness;
    }

}
