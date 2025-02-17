package vegetables.util;

import java.util.List;
import java.util.Random;

/**
 * Generates random vegetable-related fun facts.
 * <p>
 * Collection of predefined fun facts and
 * selects one at random when requested.
 *
 * This class was created by ChatGPT.
 * </p>
 */
public class FunFactGenerator {
    private static final List<String> funFacts = List.of(
            "Carrots were originally purple, not orange!",
            "Tomatoes are technically a fruit, but legally a vegetable.",
            "Cucumbers are 95% water, making them super hydrating.",
            "Broccoli has more protein per calorie than steak!",
            "Garlic has been used as medicine for thousands of years.",
            "Zucchini can grow to be as long as a canoe!",
            "Cucumbers are technically a melon.",
            "Potatoes were the first vegetable grown in space.",
            "The world's largest pumpkin weighed over 2,600 pounds!",
            "Peas were once used as a currency.",
            "Asparagus can grow 7 inches in one day!",
            "Radishes can glow in the dark."
    );

    private static final Random random = new Random();

    /**
     * Returns a random vegetable fun fact.
     *
     * @return A randomly selected fun fact from the {@code FUN_FACTS} array.
     */
    public static String getRandomFunFact() {
        int index = random.nextInt(funFacts.size()); // Randomly pick an index
        return funFacts.get(index);
    }
}
