package ac.soton.eventb.prover.utils;

import java.util.List;

import org.eclipse.swt.graphics.Point;

public class ProverUtilities {

	/**
	 * An utility method to return the operator source location within the range
	 * (start, end).
	 * <p>
	 * 
	 * @param predStr
	 *            the actual predicate string.
	 * @param start
	 *            the starting index for searching.
	 * @param end
	 *            the last index for searching
	 * @return the location in the predicate string ignore the empty spaces or
	 *         brackets in the beginning and in the end.
	 */
	public static Point getOperatorPosition(String predStr, int start, int end) {
		int i = start;
		int x = start;
		int y;
		boolean letter = false;
		while (i < end) {
			char c = predStr.charAt(i);
			if (letter == false && !ProverUtilities.isSpaceOrBracket(c)) {
				x = i;
				letter = true;
			} else if (letter == true && ProverUtilities.isSpaceOrBracket(c)) {
				y = i;
				return new Point(x, y);
			}
			++i;
		}
		if (letter == true)
			return new Point(x, end);
		else
			return new Point(start, end);
	}

	/**
	 * A utility method to check if a character is either a space or a
	 * bracket.
	 * <p>
	 * 
	 * @param c
	 *            the character to check.
	 * @return <code>true</code> if the character is a space or bracket,
	 *         otherwise return <code>false</code>.
	 */
	public static boolean isSpaceOrBracket(char c) {
		return (c == '\t' || c == '\n' || c == ' ' || c == '(' || c == ')');
	}

	/**
	 * <p>Utility to print items in a list in a displayable fashion.</p>
	 * <p>The return of this method will be of the shape: {<}item0,...,itemn{>}</p>
	 * @param items
	 * @return the displayable string
	 */
	public static String printListedItems(List<String> items){
		if(items.size() == 0){
			return "";
		}
		String result = "";
		int i = 0;
		for(String str : items){
			if(i==0){
				
				result=str;
			}
			else {
				result+=","+str;
			}
			i++;
		}
		result = "<"+result+">";
		return result;
	}

}
