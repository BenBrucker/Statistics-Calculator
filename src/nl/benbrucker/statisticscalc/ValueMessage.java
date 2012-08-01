package nl.benbrucker.statisticscalc;

import java.math.BigDecimal;

public class ValueMessage {
	private float valueOne;
	private float valueTwo;
	private String prefix;
	private String middle;
	private String suffix;
	private final float ERROR = -1000;
	private Calculator.FUNCTION_TYPE type;
	
	
	public ValueMessage (float valueOne, float valueTwo, String prefix, String middle, String suffix, Calculator.FUNCTION_TYPE type)	{
		this.valueOne = valueOne;
		this.valueTwo = valueTwo;
		this.prefix = prefix;
		this.middle = middle;
		this.suffix = suffix;
		this.type = type;
	}
	
	public ValueMessage(float value, Calculator.FUNCTION_TYPE type)	{
		this.valueOne = value;
		this.valueTwo = ERROR;
		this.prefix = null;
		this.suffix = null;
		this.middle = null;
		this.type = type;
	}
	
	public float getValueTwo() {
		return valueTwo;
	}

	public void setY(float valueTwo) {
		this.valueTwo = valueTwo;
	}

	public float getValueOne() {
		return valueOne;
	}

	public void setValueOne(float valueOne) {
		this.valueOne = valueOne;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getMiddle() {
		return middle;
	}

	public void setMiddle(String middle) {
		this.middle = middle;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	@Override
	public String toString()	{

		StringBuilder builder = new StringBuilder("");
		if (prefix != null)
			builder.append(prefix);
		if (valueOne != ERROR && !(type == Calculator.FUNCTION_TYPE.decielen || type == Calculator.FUNCTION_TYPE.percentielen || type == Calculator.FUNCTION_TYPE.cScores))
			builder.append(round(valueOne, 2, BigDecimal.ROUND_FLOOR));
		else if (valueOne != ERROR && valueOne != -ERROR)
			builder.append((int)valueOne);
		if (middle != null)
			builder.append(middle);
		if (valueTwo != ERROR && !(type == Calculator.FUNCTION_TYPE.decielen || type == Calculator.FUNCTION_TYPE.percentielen || type == Calculator.FUNCTION_TYPE.cScores))
			builder.append(round(valueTwo, 2, BigDecimal.ROUND_FLOOR));
		else if (valueTwo != ERROR && valueTwo != -ERROR)
			builder.append((int)valueTwo);
		if (suffix != null)
			builder.append(suffix);
		return builder.toString();
	}
	
	public static float round(float unrounded, int precision, int roundingMode)
	{
	    BigDecimal bd = new BigDecimal(unrounded);
	    BigDecimal rounded = bd.setScale(precision, roundingMode);
	    return rounded.floatValue();
	    
	}
}
