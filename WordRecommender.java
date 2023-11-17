import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;

public class WordRecommender {

    private HashSet<String> dictionary;

    public WordRecommender(String dictionaryFile) {
        dictionary = new HashSet<>();
        try (Scanner scanner = new Scanner(new File(dictionaryFile))) {
            while (scanner.hasNextLine()) {
                dictionary.add(scanner.nextLine().trim());
            }
        } catch (FileNotFoundException e) {
            System.out.printf(Util.FILE_OPENING_ERROR);
        }
    }

    public double getSimilarity(String word1, String word2) {
        int leftSimilarity = 0;
        int rightSimilarity = 0;
        int minLength = Math.min(word1.length(), word2.length());
        // Calculate left similarity
        for (int i = 0; i < minLength; i++) {
            if (word1.charAt(i) == word2.charAt(i)) {
                leftSimilarity++;
            }
        }

        // Calculate right similarity
        for (int i = 0; i < minLength; i++) {
            if (word1.charAt(word1.length() - 1 - i) == word2.charAt(word2.length() - 1 - i)) {
                rightSimilarity++;
            }
        }

        // Calculate the average of left and right similarity scores
        return (double) (leftSimilarity + rightSimilarity) / 2;
    }


    public ArrayList<String> getWordSuggestions(String word, int tolerance, double commonPercent, int topN) {
        ArrayList<String> suggestions = new ArrayList<>();
        ArrayList<Double> similarities = new ArrayList<>();

        // Iterate over each word in the dictionary
        for (String candidate : dictionary) {
            if (Math.abs(candidate.length() - word.length()) <= tolerance &&
                    commonPercent(word, candidate) >= commonPercent) {

                double candidateSimilarity = getSimilarity(word, candidate);

                // Find the correct position to insert this candidate
                int position = 0;
                while (position < similarities.size()) {
                    if (candidateSimilarity > similarities.get(position)) {
                        break; // Found a lower similarity, insert here
                    } else if (candidateSimilarity == similarities.get(position) &&
                            candidate.compareTo(suggestions.get(position)) < 0) {
                        break; // Same similarity but lexicographically smaller, insert here
                    }
                    position++;
                }

                // Insert the candidate at the found position
                if (position < topN) {
                    suggestions.add(position, candidate);
                    similarities.add(position, candidateSimilarity);

                    // If we have more than topN suggestions, remove the last one
                    if (suggestions.size() > topN) {
                        suggestions.remove(suggestions.size() - 1);
                        similarities.remove(similarities.size() - 1);
                    }
                }
            }
        }

        return suggestions;
    }



    public double commonPercent(String word1, String word2) {
        Set<Character> aSet = new HashSet<>();
        for (char c : word1.toCharArray()) {
            aSet.add(c);
        }

        Set<Character> bSet = new HashSet<>();
        for (char c : word2.toCharArray()) {
            bSet.add(c);
        }

        // Intersection of the sets
        Set<Character> intersection = new HashSet<>(aSet);
        intersection.retainAll(bSet);

        // Union of the sets
        Set<Character> union = new HashSet<>(aSet);
        union.addAll(bSet);

        return (double) intersection.size() / union.size();
    }


    public boolean isWordInDictionary(String word) {
        // List of words to be explicitly accepted as correct
        List<String> exceptions = Arrays.asList("a", "i");

        // Convert the word to lower case and check if it's in the list of exceptions
        if (exceptions.contains(word.toLowerCase())) {
            return true;
        }

        // Continue to check if the word is in the main dictionary
        return dictionary.contains(word.toLowerCase());
    }

}