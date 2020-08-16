package matej.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import matej.constants.JambConstants;
import matej.models.Dice;
import matej.models.enums.BoxType;

/**
 * This utility class contains static methods used calculating scores based on
 * rolled dice values and box type to be filled.
 *
 * @author MatejDanic
 * @version 1.0
 * @since 2020-08-13
 */
public final class ScoreUtil {

	/**
	 * Calculates score based on rolled dice values and type of box to be filled.
	 * 
	 * @param diceList the values of rolled dice
	 * @param boxType  the type of box to be filled
	 * @return {@code int} the calculated score result
	 */
	public static int calculateScore(List<Dice> diceList, BoxType boxType) {
		int result = 0; // initialize variable for storing and returning score result to zero
		List<Integer> diceValues = new ArrayList<>(diceList.size());
		diceList.forEach((dice) -> diceValues.add(dice.getValue()));
		switch (boxType) { // determine method to be used for score calculation based on type of box to be
							// filled
			case ONES:
			case TWOS:
			case THREES:
			case FOURS:
			case FIVES:
			case SIXES:
				result = calculateSumByType(diceValues, boxType);
				break;
			case MAX:
			case MIN:
				result = calculateSum(diceValues);
				break;
			case TRIPS:
				result = calculateSumOfRepeatingValue(diceValues, 3, JambConstants.BONUS_TRIPS);
				break;
			case STRAIGHT:
				result = calculateStraight(diceValues);
				break;
			case FULL:
				result = calculateFull(diceValues, JambConstants.BONUS_FULL);
				break;
			case POKER:
				result = calculateSumOfRepeatingValue(diceValues, 4, JambConstants.BONUS_POKER);
				break;
			case JAMB:
				result = calculateSumOfRepeatingValue(diceValues, 5, JambConstants.BONUS_JAMB);
				break;
		}
		return result;
	}

	/**
	 * Calculates sum of all dice values from received list.
	 * 
	 * @param diceList the values of rolled dice
	 * @return {@code int} the sum of all dice values
	 */
	public static int calculateSum(List<Integer> diceValues) {
		int sum = 0;
		for (int value : diceValues) {
			sum += value;
		}
		return sum;
	}

	/**
	 * Calculates sum of all dice values equal to box type.
	 * 
	 * @param diceList the values of rolled dice
	 * @return {@code int} the sum of all dice values equal to box type
	 */
	public static int calculateSumByType(List<Integer> diceValues, BoxType boxType) {
		int sum = 0;
		int type = boxType.ordinal() + 1; // type of box is by 1 larger than boxType ordinal because ordinals start at 0
		for (int value : diceValues) {
			if (value == type) {
				sum += value; // if dice value is equal to box type add to sum
			}
		}
		return sum;
	}

	/**
	 * Calculates sum of dice values that occur at least a certain number of times.
	 * 
	 * @param diceList      the values of rolled dice
	 * @param repeatCounter the minimal number of times value has to occur to be
	 *                      summed in the total
	 * @param bonus         the predefined bonus added to the sum if a value occured
	 *                      at least a certain number of times
	 * 
	 * @return {@code int} the sum of dice values that they occured at least a certain
	 *         number of times; 0 if all values occured less than given number of
	 *         times
	 */
	public static int calculateSumOfRepeatingValue(List<Integer> diceValues, int repeatNumber, int bonus) {
		for (int i = 1; i <= 6; i++) {
			int count = Collections.frequency(diceValues, i);
			if (count >= repeatNumber) { // if count has reached given number return sum increased by given bonus
				return i * count + bonus;
			}
		}
		return 0; // if count has not reached given number for any dice value return 0
	}

	/**
	 * Checks if a straight occures in dice list. Straight is defined as 5 consecutive numbers, and can be either a small (1, 2, 3, 4 , 5), or big (2, 3, 4, 5, 6) straight.
	 * 
	 * @param diceList      the values of rolled dice
	 * 
	 * @return {@code int} the predefined value of a small or big straight if it occured and 0 otherwise.
	 */
	public static int calculateStraight(List<Integer> diceValues) {
		List<Integer> straight = new ArrayList<>();
		straight.add(2);
		straight.add(3);
		straight.add(4);
		straight.add(5);
		if (diceValues.containsAll(straight)) {
			if (diceValues.contains(1)) {
				return JambConstants.BONUS_STRAIGHT_SMALL;
			} else if (diceValues.contains(6))
				return JambConstants.BONUS_STRAIGHT_BIG;
		}
		return 0;
	}

	/**
	 * Checks if a full occures in dice list.
	 * 
	 * @param diceList      the values of rolled dice
	 * @param bonus         the predefined bonus added to the sum if a full occured
	 * 
	 * @return {@code int} the sum of dice if both a pair and trips occured
	 */
	public static int calculateFull(List<Integer> diceValues, int bonus) {
		int valueOfPair = 0;
		int valueOfTrips = 0;
		for (int i = 1; i <= 6; i++) {
			int count = Collections.frequency(diceValues, i);
			if (count == 2) {
				valueOfPair = i * count;
			} else if (count == 3) {
				valueOfTrips = i * count;
			}
		}
		if (valueOfPair > 0 && valueOfTrips > 0) {
			return valueOfPair + valueOfTrips + bonus;
		}
		return 0;
	}
}
