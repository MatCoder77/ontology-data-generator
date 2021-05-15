package pl.edu.pwr.ontologydatagenerator.domain.similarity;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.util.PorterStemmer;
import edu.cmu.lti.ws4j.util.StopWordRemover;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pwr.ontologydatagenerator.infrastructure.transform.TransformUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SemanticSimilarityService {

    private static final ILexicalDatabase lexicalDatabase = new NictWordNet();
    private static final LinSimilarity LIN_SIMILARITY_METRIC = new LinSimilarity(lexicalDatabase);
    private static final PorterStemmer porterStemmer = new PorterStemmer();
    private static final StopWordRemover stopWordRemover = StopWordRemover.getInstance();
    private static final Pattern PUNCTSPACE = Pattern.compile("[ \\p{Punct}]+");
    private static final Pattern TRANSITION = Pattern.compile("(?<=[^\\p{Lu}])(?=[\\p{Lu}])|(?=[\\p{Lu}][^\\p{Lu}])");

    public double calculateSemanticSimilarity(String word1, String word2) {
        return calculateSemanticSimilarity(Collections.singleton(word1), Collections.singleton(word2));
    }

    public double calculateSemanticSimilarity(Collection<String> words1, Collection<String> words2) {
        List<List<String>> tokenizedAndPreprocessedWords1 = getTokenizedAndPreprocessedWords(words1);
        List<List<String>> tokenizedAndPreprocessedWords2 = getTokenizedAndPreprocessedWords(words2);
        return Math.max(
                calculateSimilarityWithJoinedTokens(tokenizedAndPreprocessedWords1, tokenizedAndPreprocessedWords2),
                calculateSimilarityWithSeparateTokens(tokenizedAndPreprocessedWords1, tokenizedAndPreprocessedWords2));
    }

    private double calculateSimilarityWithJoinedTokens(List<List<String>> tokenizedAndPreprocessedWords1, List<List<String>> tokenizedAndPreprocessedWords2) {
        List<String> words1 = buildMultipartWordFromTokens(tokenizedAndPreprocessedWords1);
        List<String> words2 = buildMultipartWordFromTokens(tokenizedAndPreprocessedWords2);
        double[][] similarityMatrix = LIN_SIMILARITY_METRIC.getSimilarityMatrix(words1.toArray(new String[0]), words2.toArray(new String[0]));
        return HungarianMaximumMathingAlgorithm.calculateNormalizedSumForMaximumMatching(similarityMatrix);
    }

    private List<String> buildMultipartWordFromTokens(Collection<? extends Collection<String>> tokenizedAndPreprocessedWords) {
        return tokenizedAndPreprocessedWords.stream()
                .map(this::buildMultipartWord)
                .collect(Collectors.toList());
    }

    private String buildMultipartWord(Collection<String> tokens) {
        return String.join("_", tokens);
    }

    private List<List<String>> getTokenizedAndPreprocessedWords(Collection<String> words) {
        return words.stream()
                .map(this::getTokenizedAndPreprocessedWord)
                .collect(Collectors.toList());
    }

    private List<String> getTokenizedAndPreprocessedWord(String word) {
        List<String> tokenizedWords = splitToWords(word);
        List<String> stemmedWords = getStemmedWords(tokenizedWords);
        List<String> lowercasedWords = getLowercasedWords(stemmedWords);
        return getWithoutStopWords(lowercasedWords);
    }

    private List<String> splitToWords(String text) {
        return Arrays.stream(PUNCTSPACE.split(text))
                .filter(word -> !word.isEmpty())
                .flatMap(word -> Arrays.stream(TRANSITION.split(word)))
                .collect(Collectors.toList());
    }

    private List<String> getStemmedWords(Collection<String> words) {
        return new ArrayList<>(words);
        //return Arrays.asList(porterStemmer.stemWords(words.toArray(new String[0])));
    }

    private List<String> getWithoutStopWords(Collection<String> words) {
        List<String> wordsWithoutStopWords = Arrays.asList(stopWordRemover.removeStopWords(words.toArray(new String[0])));
        if (wordsWithoutStopWords.isEmpty()) {
            return new ArrayList<>(words);
        }
        return wordsWithoutStopWords;
    }

    private List<String> getLowercasedWords(Collection<String> words) {
        return words.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    private double calculateSimilarityWithSeparateTokens(List<List<String>> tokenizedAndPreprocessedWords1, List<List<String>> tokenizedAndPreprocessedWords2) {
        List<String> words1 = TransformUtils.flattenValues(tokenizedAndPreprocessedWords1, ArrayList::new);
        List<String> words2 = TransformUtils.flattenValues(tokenizedAndPreprocessedWords2, ArrayList::new);
        double[][] similarityMatrix = LIN_SIMILARITY_METRIC.getSimilarityMatrix(words1.toArray(new String[0]), words2.toArray(new String[0]));
        return HungarianMaximumMathingAlgorithm.calculateNormalizedSumForMaximumMatching(similarityMatrix);
    }

}
