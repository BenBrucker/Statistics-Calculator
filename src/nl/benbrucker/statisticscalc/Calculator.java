package nl.benbrucker.statisticscalc;

import java.math.BigDecimal;
import java.util.Observable;

import android.util.Log;

public class Calculator extends Observable {
    public static final float MEAN = 8;
    public static final float STDEV = 2;
    public static final float MIN = 0f;
	public static final float MAX = 16f;
	public static final float NEGERROR = -1000;
	public static final float POSERROR = 1000;
	
	public static enum FUNCTION_TYPE {decielen, percentielen, zScores, tScores, cScores};
	private static float currentValue;
	private static boolean isRange = false;
	private static float firstValue;
	private static float secondValue;
	private FUNCTION_TYPE lastType;
	
	public Calculator ()	{
		currentValue = (MAX - MIN) / 2f;
		firstValue = currentValue;
		secondValue = currentValue;
	}
	
	public void setNewValue	(float value, FUNCTION_TYPE type)	{
		switch(type)	{
			case decielen:	{
				isRange = true;
				if (value <= 1)	{
					Calculator.firstValue = NEGERROR;
					Calculator.secondValue = ((decileToZScore((int)value) * STDEV) + MEAN);
					Calculator.currentValue = secondValue;
				}
				else if (value <= 9)	{
					Calculator.firstValue = ((decileToZScore((int)value-1) * STDEV) + MEAN);
					Calculator.secondValue = ((decileToZScore((int)(value)) * STDEV) + MEAN);
					Calculator.currentValue = firstValue + 0.001f;
				}
				else	{
					Calculator.firstValue = ((decileToZScore((int)value) * STDEV) + MEAN);
					Calculator.secondValue = POSERROR;
					Calculator.currentValue = firstValue + 0.001f;
				}
				lastType = FUNCTION_TYPE.decielen;
			} break;
			case percentielen: {
				isRange = true;
				if (value <= 1)	{
					Calculator.firstValue = NEGERROR;
					Calculator.secondValue = ((percentileToZScore((int)value) * STDEV) + MEAN);
					Calculator.currentValue = secondValue;
				}
				else if (value <= 98)	{
					Calculator.firstValue = ((percentileToZScore((int)value-1) * STDEV) + MEAN);
					Calculator.secondValue = ((percentileToZScore((int)(value)) * STDEV) + MEAN);
					Calculator.currentValue = firstValue + 0.001f;
				}
				else	{
					Calculator.firstValue = ((percentileToZScore((int)value) * STDEV) + MEAN);
					Calculator.secondValue = POSERROR;
					Calculator.currentValue = firstValue + 0.001f;
				}
				lastType = FUNCTION_TYPE.percentielen;
			} break;
			case zScores:	{
				isRange = false;
				Calculator.currentValue = value * STDEV + MEAN;
				lastType = FUNCTION_TYPE.zScores;
			} break;
			case tScores:	{
				isRange = false;
				Calculator.currentValue = (value - 50) * STDEV / 10 + MEAN;
				lastType = FUNCTION_TYPE.tScores;
			}  break;
			case cScores:	{
				isRange = true;
				if (value <= 0)	{
					Calculator.firstValue = NEGERROR;
					Calculator.secondValue = ((CScoreToZScore((int)value) * STDEV) + MEAN);
					Calculator.currentValue = secondValue;
				}
				else if (value <= 9)	{
					Calculator.firstValue = ((CScoreToZScore((int)value-1) * STDEV) + MEAN);
					Calculator.secondValue = ((CScoreToZScore((int)(value)) * STDEV) + MEAN);
					Calculator.currentValue = firstValue + 0.001f;
				}
				else	{
					Calculator.firstValue = ((CScoreToZScore((int)value) * STDEV) + MEAN);
					Calculator.secondValue = POSERROR;
					Calculator.currentValue = firstValue + 0.001f;
				}
				lastType = FUNCTION_TYPE.cScores;
			} break;
		}
		Log.d("Class: Calculator", "Set new Value");
		Log.d("Type", type.toString());
		Log.d("Input", Float.toString(value));
		Log.d("Current Value", Float.toString(Calculator.currentValue));
		notifyObservers();
	}
	
	public ValueMessage getValue (FUNCTION_TYPE type)	{
		switch(type)	{
			case decielen:	{
				float result = zToDecile((Calculator.currentValue - MEAN)/STDEV);
				return new ValueMessage(result, FUNCTION_TYPE.decielen);
			} 
			case percentielen: {
				float result = zToPercentile((Calculator.currentValue - MEAN)/STDEV);
				float minResult = zToPercentile((Calculator.firstValue - MEAN + 0.01f)/STDEV);
				float maxResult = zToPercentile((Calculator.secondValue - MEAN - 0.01f)/STDEV);
				if (lastType == FUNCTION_TYPE.decielen || lastType == FUNCTION_TYPE.cScores)
					return new ValueMessage(minResult, maxResult, null, " <> ", null,FUNCTION_TYPE.percentielen);
				else
					return new ValueMessage(result, FUNCTION_TYPE.percentielen);
			} 
			case zScores: {
				float result = (currentValue - MEAN) / STDEV;
				float minResult = (firstValue - MEAN) / STDEV;
				float maxResult = (secondValue - MEAN) / STDEV;
				if (Calculator.isRange)
					if (minResult < -4)
						return new ValueMessage(NEGERROR, maxResult, null, " < ", null,FUNCTION_TYPE.zScores);
					else if (maxResult > 4)
						return new ValueMessage(minResult, NEGERROR, " > ", null, null,FUNCTION_TYPE.zScores);
					else
						return new ValueMessage(minResult, maxResult, null, " <> ", null,FUNCTION_TYPE.zScores);
				else
					return new ValueMessage(result, FUNCTION_TYPE.zScores);
			}
			case tScores:  {
				float result = 50 + (10 * (currentValue - MEAN) / STDEV);
				float minResult = 50 + (10 * (firstValue - MEAN) / STDEV);
				float maxResult = 50 + (10 * (secondValue - MEAN) / STDEV);
				if (lastType == FUNCTION_TYPE.decielen)
					if (minResult < 10)
						return new ValueMessage(NEGERROR, maxResult, null, " < ", null,FUNCTION_TYPE.tScores);
					else if (maxResult > 90)
						return new ValueMessage(minResult, NEGERROR, " > ", null, null,FUNCTION_TYPE.tScores);
					else
						return new ValueMessage(minResult, maxResult, null, " <> ", null,FUNCTION_TYPE.tScores);
				else
					return new ValueMessage(result, FUNCTION_TYPE.tScores);
			} 
			case cScores:	{
				float result = zToCScore((Calculator.currentValue - MEAN)/STDEV);
				float minResult = zToCScore((Calculator.firstValue - MEAN + 0.01f)/STDEV);
				float maxResult = zToCScore((Calculator.secondValue - MEAN - 0.01f)/STDEV);
				if (lastType == FUNCTION_TYPE.decielen && minResult != maxResult)
					return new ValueMessage(minResult, maxResult, null, " <> ", null,FUNCTION_TYPE.cScores);
				else
					return new ValueMessage(result, FUNCTION_TYPE.cScores);
			} 
			default: return new ValueMessage(0, FUNCTION_TYPE.tScores);
		}
	}
	
	public static float round(float unrounded, int precision, int roundingMode)
	{
	    BigDecimal bd = new BigDecimal(unrounded);
	    BigDecimal rounded = bd.setScale(precision, roundingMode);
	    return rounded.floatValue();
	    
	}
	
	private float percentileToZScore (int value)	{
		switch (value)	{
		case	1	:	return	-2.33f	;
		case	2	:	return	-2.05f	;
		case	3	:	return	-1.88f	;
		case	4	:	return	-1.75f	;
		case	5	:	return	-1.64f	;
		case	6	:	return	-1.55f	;
		case	7	:	return	-1.48f	;
		case	8	:	return	-1.41f	;
		case	9	:	return	-1.34f	;
		case	10	:	return	-1.28f	;
		case	11	:	return	-1.23f	;
		case	12	:	return	-1.17f	;
		case	13	:	return	-1.13f	;
		case	14	:	return	-1.08f	;
		case	15	:	return	-1.04f	;
		case	16	:	return	-0.99f	;
		case	17	:	return	-0.95f	;
		case	18	:	return	-0.92f	;
		case	19	:	return	-0.88f	;
		case	20	:	return	-0.84f	;
		case	21	:	return	-0.81f	;
		case	22	:	return	-0.77f	;
		case	23	:	return	-0.74f	;
		case	24	:	return	-0.71f	;
		case	25	:	return	-0.67f	;
		case	26	:	return	-0.64f	;
		case	27	:	return	-0.61f	;
		case	28	:	return	-0.58f	;
		case	29	:	return	-0.55f	;
		case	30	:	return	-0.52f	;
		case	31	:	return	-0.50f	;
		case	32	:	return	-0.47f	;
		case	33	:	return	-0.44f	;
		case	34	:	return	-0.41f	;
		case	35	:	return	-0.39f	;
		case	36	:	return	-0.36f	;
		case	37	:	return	-0.33f	;
		case	38	:	return	-0.31f	;
		case	39	:	return	-0.28f	;
		case	40	:	return	-0.25f	;
		case	41	:	return	-0.23f	;
		case	42	:	return	-0.20f	;
		case	43	:	return	-0.18f	;
		case	44	:	return	-0.15f	;
		case	45	:	return	-0.13f	;
		case	46	:	return	-0.10f	;
		case	47	:	return	-0.08f	;
		case	48	:	return	-0.05f	;
		case	49	:	return	-0.03f	;
		case	50	:	return	0.00f	;
		case	51	:	return	0.03f	;
		case	52	:	return	0.05f	;
		case	53	:	return	0.08f	;
		case	54	:	return	0.10f	;
		case	55	:	return	0.13f	;
		case	56	:	return	0.15f	;
		case	57	:	return	0.18f	;
		case	58	:	return	0.20f	;
		case	59	:	return	0.23f	;
		case	60	:	return	0.25f	;
		case	61	:	return	0.28f	;
		case	62	:	return	0.31f	;
		case	63	:	return	0.33f	;
		case	64	:	return	0.36f	;
		case	65	:	return	0.39f	;
		case	66	:	return	0.41f	;
		case	67	:	return	0.44f	;
		case	68	:	return	0.47f	;
		case	69	:	return	0.50f	;
		case	70	:	return	0.52f	;
		case	71	:	return	0.55f	;
		case	72	:	return	0.58f	;
		case	73	:	return	0.61f	;
		case	74	:	return	0.64f	;
		case	75	:	return	0.67f	;
		case	76	:	return	0.71f	;
		case	77	:	return	0.74f	;
		case	78	:	return	0.77f	;
		case	79	:	return	0.81f	;
		case	80	:	return	0.84f	;
		case	81	:	return	0.88f	;
		case	82	:	return	0.92f	;
		case	83	:	return	0.95f	;
		case	84	:	return	0.99f	;
		case	85	:	return	1.04f	;
		case	86	:	return	1.08f	;
		case	87	:	return	1.13f	;
		case	88	:	return	1.17f	;
		case	89	:	return	1.23f	;
		case	90	:	return	1.28f	;
		case	91	:	return	1.34f	;
		case	92	:	return	1.41f	;
		case	93	:	return	1.48f	;
		case	94	:	return	1.55f	;
		case	95	:	return	1.64f	;
		case	96	:	return	1.75f	;
		case	97	:	return	1.88f	;
		case	98	:	return	2.05f	;
		case	99	:	return	2.33f	;
			default: return 0;
		}	
	}
	
	private float decileToZScore (int value)	{
		switch (value)	{
			case 1 : return	-1.281f;
			case 2 : return	-0.841f;
			case 3 : return	-0.524f;
			case 4 : return	-0.253f;
			case 5 : return	0f;
			case 6 : return	0.253f;
			case 7 : return	0.524f;
			case 8 : return	0.841f;
			case 9 : return	1.281f;
			case 10 : return 1.281f;
			default: return 0;
		}
	}
	
	private float zToDecile	(float value)	{
		if (value <= -1.281f) return 1f;
		else if (value <= -0.841f) return 2f;
		else if (value <= -0.524f) return 3f;
		else if (value <= -0.253f) return 4f;
		else if (value <= 0) return 5f;
		else if (value <= 0.253f) return 6f;
		else if (value <= 0.524f) return 7f;
		else if (value <= 0.841f) return 8f;
		else if (value <= 1.281f) return 9f;
		else return 10f;
	}

	private float zToCScore	(float value)	{
		if (value <= 	-2.33f	) return 	0f;
		else if (value <= 	-1.75f) return 	1f;
		else if (value <= 	-1.23f) return 	2f;
		else if (value <= 	-0.74f) return 	3f;
		else if (value <= 	-0.25f) return 	4f;
		else if (value <= 	0.25f) return 	5f;
		else if (value <= 	0.74f) return 	6f;
		else if (value <= 	1.23f) return 	7f;
		else if (value <= 	1.75f) return 	8f;
		else if (value <= 	2.05f) return 	9f;
		else return 10f;
	}
	
	private float CScoreToZScore	(int value)	{
		switch (value)	{
		case 0 : return	-2.33f;
		case 1 : return	-1.75f;
		case 2 : return	-1.23f;
		case 3 : return	-0.74f;
		case 4 : return	-0.25f;
		case 5 : return	0.25f;
		case 6 : return	0.74f;
		case 7 : return	1.23f;
		case 8 : return	0.841f;
		case 9 : return	1.75f;
		case 10 : return 2.33f;
		default: return 0;
	}
	}
	
	
	private float zToPercentile	(float value)	{
		if (value <= 	-2.33f	) return 	1f;
		else if (value <= 	-2.05f) return 	2f;
		else if (value <= 	-1.88f) return 	3f;
		else if (value <= 	-1.75f) return 	4f;
		else if (value <= 	-1.64f) return 	5f;
		else if (value <= 	-1.55f) return 	6f;
		else if (value <= 	-1.48f) return 	7f;
		else if (value <= 	-1.41f) return 	8f;
		else if (value <= 	-1.34f) return 	9f;
		else if (value <= 	-1.28f) return 	10f;
		else if (value <= 	-1.23f) return 	11f;
		else if (value <= 	-1.17f) return 	12f;
		else if (value <= 	-1.13f) return 	13f;
		else if (value <= 	-1.08f) return 	14f;
		else if (value <= 	-1.04f) return 	15f;
		else if (value <= 	-0.99f) return 	16f;
		else if (value <= 	-0.95f) return 	17f;
		else if (value <= 	-0.92f) return 	18f;
		else if (value <= 	-0.88f) return 	19f;
		else if (value <= 	-0.84f) return 	20f;
		else if (value <= 	-0.81f) return 	21f;
		else if (value <= 	-0.77f) return 	22f;
		else if (value <= 	-0.74f) return 	23f;
		else if (value <= 	-0.71f) return 	24f;
		else if (value <= 	-0.67f) return 	25f;
		else if (value <= 	-0.64f) return 	26f;
		else if (value <= 	-0.61f) return 	27f;
		else if (value <= 	-0.58f) return 	28f;
		else if (value <= 	-0.55f) return 	29f;
		else if (value <= 	-0.52f) return 	30f;
		else if (value <= 	-0.50f) return 	31f;
		else if (value <= 	-0.47f) return 	32f;
		else if (value <= 	-0.44f) return 	33f;
		else if (value <= 	-0.41f) return 	34f;
		else if (value <= 	-0.39f) return 	35f;
		else if (value <= 	-0.36f) return 	36f;
		else if (value <= 	-0.33f) return 	37f;
		else if (value <= 	-0.31f) return 	38f;
		else if (value <= 	-0.28f) return 	39f;
		else if (value <= 	-0.25f) return 	40f;
		else if (value <= 	-0.23f) return 	41f;
		else if (value <= 	-0.20f) return 	42f;
		else if (value <= 	-0.18f) return 	43f;
		else if (value <= 	-0.15f) return 	44f;
		else if (value <= 	-0.13f) return 	45f;
		else if (value <= 	-0.10f) return 	46f;
		else if (value <= 	-0.08f) return 	47f;
		else if (value <= 	-0.05f) return 	48f;
		else if (value <= 	-0.03f) return 	49f;
		else if (value <= 	0.00f) return 	50f;
		else if (value <= 	0.03f) return 	51f;
		else if (value <= 	0.05f) return 	52f;
		else if (value <= 	0.08f) return 	53f;
		else if (value <= 	0.10f) return 	54f;
		else if (value <= 	0.13f) return 	55f;
		else if (value <= 	0.15f) return 	56f;
		else if (value <= 	0.18f) return 	57f;
		else if (value <= 	0.20f) return 	58f;
		else if (value <= 	0.23f) return 	59f;
		else if (value <= 	0.25f) return 	60f;
		else if (value <= 	0.28f) return 	61f;
		else if (value <= 	0.31f) return 	62f;
		else if (value <= 	0.33f) return 	63f;
		else if (value <= 	0.36f) return 	64f;
		else if (value <= 	0.39f) return 	65f;
		else if (value <= 	0.41f) return 	66f;
		else if (value <= 	0.44f) return 	67f;
		else if (value <= 	0.47f) return 	68f;
		else if (value <= 	0.50f) return 	69f;
		else if (value <= 	0.52f) return 	70f;
		else if (value <= 	0.55f) return 	71f;
		else if (value <= 	0.58f) return 	72f;
		else if (value <= 	0.61f) return 	73f;
		else if (value <= 	0.64f) return 	74f;
		else if (value <= 	0.67f) return 	75f;
		else if (value <= 	0.71f) return 	76f;
		else if (value <= 	0.74f) return 	77f;
		else if (value <= 	0.77f) return 	78f;
		else if (value <= 	0.81f) return 	79f;
		else if (value <= 	0.84f) return 	80f;
		else if (value <= 	0.88f) return 	81f;
		else if (value <= 	0.92f) return 	82f;
		else if (value <= 	0.95f) return 	83f;
		else if (value <= 	0.99f) return 	84f;
		else if (value <= 	1.04f) return 	85f;
		else if (value <= 	1.08f) return 	86f;
		else if (value <= 	1.13f) return 	87f;
		else if (value <= 	1.17f) return 	88f;
		else if (value <= 	1.23f) return 	89f;
		else if (value <= 	1.28f) return 	90f;
		else if (value <= 	1.34f) return 	91f;
		else if (value <= 	1.41f) return 	92f;
		else if (value <= 	1.48f) return 	93f;
		else if (value <= 	1.55f) return 	94f;
		else if (value <= 	1.64f) return 	95f;
		else if (value <= 	1.75f) return 	96f;
		else if (value <= 	1.88f) return 	97f;
		else if (value <= 	2.05f) return 	98f;
		else		 return 	99f;

	}
	
	public static float getCurrentValue()	{
		return currentValue;
	}
	
	public static boolean isRange()	{
		return isRange;
	}
	
	public static float getFirstValue()	{
		return firstValue;
	}
	
	public static float getSecondValue()	{
		return secondValue;
	}
}
