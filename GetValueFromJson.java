
/***************************************************************************************************************************/
/// <summary>
///
/// 2023/06/06
/// A little experiment to get values from a JSON without using an external library
/// Ported from my C# code
/// by Luis Felipe La Rotta
/// Includes code from user Bala R to implement Tuple and Triplets in Java

///Usage Triplet<Boolean, possibleResults, List<Duple<String, String>>> result = getKey("subscriberName", json);
///
/// </summary>

/***************************************************************************************************************************/

import java.util.ArrayList;
import java.util.List;


public class GetValueFromJson {

    public enum PossibleResults {
        SUCCESS,
        JSON_IS_EMPTY,
        KEY_NOT_FOUND_ON_JSON,
        INVALID_JSON_STRUCTURE
    }

    public class Duple<T, U> {

        private final T first;
        private final U second;


        public Duple(T first, U second) {
            this.first = first;
            this.second = second;
        }

        public T getFirst() { return first; }
        public U getSecond() { return second; }
    }


    public class Triplet<T, U, V> {

        private final T first;
        private final U second;
        private final V third;

        public Triplet(T first, U second, V third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public T getFirst() { return first; }
        public U getSecond() { return second; }
        public V getThird() { return third; }

    }

    public Triplet<Boolean, PossibleResults, List<Duple<String, String>>> getKey(String argRequiredkey, String argJsonAsPlanText, Boolean removeCarriageReturn, Boolean removeDuplicatedWhitespaces) {
        try {
            if (removeCarriageReturn) {
                argJsonAsPlanText = argJsonAsPlanText.replace("\n", "");
                argJsonAsPlanText = argJsonAsPlanText.replace("\r", "");
            }

            if (removeDuplicatedWhitespaces) {
                argJsonAsPlanText = removeRepeatedCharacter(argJsonAsPlanText, " ");
            }

            if (argJsonAsPlanText == null || argJsonAsPlanText.isEmpty()) {
                return new Triplet<>(false, PossibleResults.JSON_IS_EMPTY, new ArrayList<>());
            }

            if (!argJsonAsPlanText.toLowerCase().contains(argRequiredkey.toLowerCase())) {
                return new Triplet<Boolean, PossibleResults, List<Duple<String, String>>>(false, PossibleResults.KEY_NOT_FOUND_ON_JSON, new ArrayList<>());
            }

            List<Duple<String, String>> resultsList = new ArrayList<>();

            for (int pos = 0; pos < argJsonAsPlanText.length(); pos++) {
                String character = argJsonAsPlanText.substring(pos, pos + 1);

                if (character.equals(":")) {
                    int leftEndingQuote = argJsonAsPlanText.lastIndexOf('"', pos);

                    if (leftEndingQuote == -1) {
                        continue;
                    }

                    int leftStartingQuote = argJsonAsPlanText.lastIndexOf('"', leftEndingQuote - 1);

                    if (leftStartingQuote == -1) {
                        continue;
                    }

                    String key = argJsonAsPlanText.substring((leftStartingQuote + 1), leftEndingQuote);

                    if (!key.toLowerCase().equals(argRequiredkey.toLowerCase())) {
                        continue;
                    }

                    String resultBetweenSquareBraces = detectTextBetweenSquareBraces(argJsonAsPlanText, pos);

                    if (resultBetweenSquareBraces != null) {
                        resultsList.add(new Duple<>(argRequiredkey, resultBetweenSquareBraces));
                    }

                    String resultBetweenCurlyBraces = detectTextBetweenCurlyBraces(argJsonAsPlanText, pos);

                    if (resultBetweenCurlyBraces != null) {
                        resultsList.add(new Duple<String, String>(argRequiredkey, resultBetweenCurlyBraces));
                    }

                    String resultBetweenQuotes = detectTextBetweenQuotes(argJsonAsPlanText, pos);

                    if (resultBetweenQuotes != null) {
                        resultsList.add(new Duple<String, String>(argRequiredkey, resultBetweenQuotes));
                    }
                }
            }

            return new Triplet<Boolean, PossibleResults, List<Duple<String, String>>>(true, PossibleResults.SUCCESS, resultsList);
        } catch (Exception ex) {
            System.out.println(ex.toString());

            return new Triplet<>(false, PossibleResults.INVALID_JSON_STRUCTURE, new ArrayList<>());
        }
    }

    private String detectTextBetweenSquareBraces(String argJsonAsPlanText, int pos) {
        int rightStartingSquare = argJsonAsPlanText.indexOf('[', pos);
        int rightStartingCurly = argJsonAsPlanText.indexOf('{', pos);
        int rightStartingQuote = argJsonAsPlanText.indexOf('"', pos);

        if (rightStartingSquare == -1) {
            return null;
        }

        if (rightStartingCurly > -1 && rightStartingCurly < rightStartingSquare) {
            return null;
        }

        if (rightStartingQuote > -1 && rightStartingQuote < rightStartingSquare) {
            return null;
        }

        String character = null;
        int remainingOpenSquareBrackets = 1;
        int rightEndingSquare = -1;

        for (int i = rightStartingSquare + 1; i < argJsonAsPlanText.length(); i++) {
            character = argJsonAsPlanText.substring(i, i + 1);

            if (character.equals("[")) {
                remainingOpenSquareBrackets++;
            }

            if (character.equals("]")) {
                remainingOpenSquareBrackets--;

                if (remainingOpenSquareBrackets == 0) {
                    rightEndingSquare = i;

                    String value = argJsonAsPlanText.substring(rightStartingSquare, rightEndingSquare + 1);

                    return value;
                }
            }
        }

        return null;
    }

    private String detectTextBetweenCurlyBraces(String argJsonAsPlanText, int pos) {
        int rightStartingSquare = argJsonAsPlanText.indexOf('[', pos);
        int rightStartingCurly = argJsonAsPlanText.indexOf('{', pos);
        int rightStartingQuote = argJsonAsPlanText.indexOf('"', pos);

        if (rightStartingCurly == -1) {
            return null;
        }

        if (rightStartingSquare > -1 && rightStartingSquare < rightStartingCurly) {
            return null;
        }

        if (rightStartingQuote > -1 && rightStartingQuote < rightStartingCurly) {
            return null;
        }

        String character = null;
        int remainingOpenCurlyBraces = 1;
        int rightEndingSquare = -1;

        for (int i = rightStartingCurly + 1; i < argJsonAsPlanText.length(); i++) {
            character = argJsonAsPlanText.substring(i, i + 1);

            if (character.equals("{")) {
                remainingOpenCurlyBraces++;
            }

            if (character.equals("}")) {
                remainingOpenCurlyBraces--;

                if (remainingOpenCurlyBraces == 0) {
                    rightEndingSquare = i;

                    String value = argJsonAsPlanText.substring(rightStartingCurly, rightEndingSquare + 1);

                    return value;
                }
            }
        }

        return null;
    }

    private String detectTextBetweenQuotes(String argJsonAsPlanText, int pos) {
        int rightStartingSquare = argJsonAsPlanText.indexOf('[', pos);
        int rightStartingCurly = argJsonAsPlanText.indexOf('{', pos);
        int rightStartingQuote = argJsonAsPlanText.indexOf('"', pos);

        if (rightStartingQuote == -1) {
            return null;
        }

        if (rightStartingSquare > -1 && rightStartingSquare < rightStartingQuote) {
            return null;
        }

        if (rightStartingCurly > -1 && rightStartingCurly < rightStartingQuote) {
            return null;
        }

        int rightEndingQuote = argJsonAsPlanText.indexOf('"', rightStartingQuote + 1);

        String value = argJsonAsPlanText.substring(rightStartingQuote, rightEndingQuote + 1);

        return value;
    }

    public String removeRepeatedCharacter(String originalText, String characterToReplace) {
        String regex = "[" + characterToReplace + "]{2,}";
        originalText = originalText.replaceAll(regex, characterToReplace);
        return originalText;
    }
}
