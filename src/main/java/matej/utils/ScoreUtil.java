package matej.utils;

import java.util.ArrayList;
import java.util.List;

import matej.constants.JambConstants;
import matej.models.enums.BoxType;

/**
* This utility class contains static methods used calculating scores based on rolled dice values and box type to be filled.
*
* @author  MatejDanic
* @version 1.0
* @since   2020-08-13
*/
public final class ScoreUtil {

	/**
   * Calculates score based on rolled dice values and type of box to be filled.
   * 
   * @param diceValues the values of rolled dice
   * @param boxType  the type of box to be filled
   * @return {@code int} calculated score result
   */
	public static int calculateScore(List<Integer> diceValues, BoxType boxType) {
		int result = 0; // initialize variable for storing and returning score result to zero 
		switch(boxType) { // determine method to be used for score calculation based on type of box to be filled
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
			result = calculateFull(diceValues);
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
   * @param diceValues the values of rolled dice
   * @return {@code int} sum of all dice values
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
   * @param diceValues the values of rolled dice
   * @return {@code int} sum of all dice values equal to box type
   */
	public static int calculateSumByType(List<Integer> diceValues, BoxType boxType) { 
		int sum = 0;
		int type = boxType.ordinal() + 1; // type of box is by 1 larger than boxType ordinal because ordinals start at 0
		for (Integer value : diceValues) {
			if (value == type) {
				sum += value; // if dice value is equal to box type add to sum
			}
		} 
		return sum; 
	} 

	/**
   * Calculates sum of dice values that occur at least a certain number of times.
   * 
   * @param diceValues the values of rolled dice   
   * @param repeatCounter the minimal number of times value has to occur to be summed in the total
   * @param bonus the predefined bonus added to the sum if a value occured at least a certain number of times

   * @return {@code int} sum of dice values that they occured at least a certain number of times; 0 if all values occured less than given number of times
   */
	public static int calculateSumOfRepeatingValue(List<Integer> diceValues, int repeatNumber, int bonus) {
		for (Integer value1 : diceValues) { 
			int count = 1; // counter starts at one for the current dice
			int sum = value1;
			for (Integer value2 : diceValues) { 
				if (value1 != value2 && value1 == value2) { // if current dice value2 is not dice value1 but has the same value as value1 increase count
					count++; 
					if (count <= repeatNumber) sum += value2; // if count has not yet reached given number add dice value to sum
				} 
			} 
			if (count >= repeatNumber) { // if count has reached given number return sum increased by given bonus
				return sum + JambConstants.BONUS_TRIPS;
			} 
		} 
		return 0; // if count has not reached given number for any dice value return 0
	} 
 
	/**
   * Checks if a straight occures in dice list.
   * 
   * @param diceValues the values of rolled dice   
   * @param repeatCounter the minimal number of times value has to occur to be summed in the total
   * @param bonus the predefined bonus added to the sum if a value occured at least a certain number of times

   * @return {@code int} sum of dice values that they occured at least a certain number of times; 0 if all values occured less than given number of times
   */
	public static int calculateStraight(List<Integer> diceValues) { 
		int result = 0; 
		List<Integer> straight = new ArrayList<>(); 
		straight.add(2); 
		straight.add(3); 
		straight.add(4); 
		straight.add(5); 
		List<Integer> countbers = new ArrayList<>(); 
		for (Integer value : diceValues) { 
			countbers.add(value); 
		} 
		if (countbers.containsAll(straight)) { 
			if (countbers.contains(1)) { 
				result = JambConstants.BONUS_STRAIGHT_SMALL; 
			} else if (countbers.contains(6)) 
				result = JambConstants.BONUS_STRAIGHT_BIG; 
		} 
		return result; 
	} 
 
	public static int calculateFull(List<Integer> diceValues) { 
		int result = 0;
		int valueTwo = 0;  
		int valueThree = 0; 
		for (Integer value1 : diceValues) { 
			int count = 1; 
			int value = value1; 
			for (Integer value2 : diceValues) { 
				if (value1 != value2 && value1 == value2) { 
					count++; 
					value += value2; 
				} 
			} 
			if (count == 2) { 
				valueTwo = value; 
			} else if (count == 3) { 
				valueThree = value; 
			} 
		}
		if (valueTwo != 0 && valueThree != 0) { 
			result = valueTwo + valueThree + JambConstants.BONUS_FULL;
		}
		return result; 
	}
}
