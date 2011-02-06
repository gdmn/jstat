/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package kalkulator;

import java.math.*;
import java.text.DecimalFormat;

/**
 *
 * @author Damian
 */
public class Liczba extends ElementWyrazenia implements Comparable<Liczba> {
	private BigDecimal value;
	/** Liczba zero. */
	static public Liczba ZERO = new Liczba(0);
	/** Liczba jeden. */
	static public Liczba JEDEN = new Liczba(1);
	/** Liczba 0,5 */
	static public Liczba POL = new Liczba("0.5");
	/** 0.618033989 */
	static public Liczba ZLOTA = new Liczba((Math.sqrt(5) - 1) * 0.5);
	//static private final char KROPKA = '.';
	static public final char KROPKA = ((DecimalFormat) DecimalFormat.getInstance()).getDecimalFormatSymbols().getDecimalSeparator();
	static private final boolean KROPKATOKROPKA = KROPKA == '.';

	/** Creates a new instance of Liczba */
	public Liczba(String str) throws NumberFormatException {
		super(str.replace('.', KROPKA));
		try {
			//value = BigDecimal.valueOf( Double.valueOf(str));
			value = new BigDecimal(str.replace(KROPKA, '.'));
		} catch (NumberFormatException ex) {
			throw new NumberFormatException(str);
		}
	}

	public Liczba(BigDecimal liczba) {
		super(null);
		value = liczba;
	}

	public Liczba(BigInteger liczba) {
		this(new BigDecimal(liczba));
	}

	public Liczba(double liczba) throws NumberFormatException {
		this(bd(liczba));
	}
MathContext mmm = new MathContext(25, RoundingMode.HALF_UP);
	public String toString() {
		//Throwable th = new Throwable(); th.printStackTrace();
		if (stringRepresentation == null) {
			if (KROPKATOKROPKA) {
				// taki bajer, żeby potem pamiętać:
				//((ElementWyrazenia) this).
				stringRepresentation = value.round(Settings.printContext()).stripTrailingZeros().toPlainString();
			} else {
				//((ElementWyrazenia) this).
				stringRepresentation = value.round(Settings.printContext()).stripTrailingZeros().toPlainString().replace('.', KROPKA);
			}
		}
		return super.toString();
	}

	public static BigDecimal bd(double d) throws NumberFormatException {
		// konwertujemy d na string, aby jak najdokładniej przekonwertować do bigdecimal
		return new BigDecimal(Double.toString(d), Settings.divideContext());
	}

	public static BigDecimal bd(BigInteger d) {
		return new BigDecimal(d, Settings.divideContext());
	}

	public int getPriority() {
		return 0;
	}

	public int getArgumentsCount() {
		return 0;
	}

	public ElementWyrazenia getValue(ElementWyrazenia[] args) throws InvalidArgumentException {
		throw new InvalidArgumentException("Metoda ElementWyrazenia getValue(ElementWyrazenia[] args) jest niepoprawna dla klasy " + this.getClass().getName());
	}

	/**
	 * Zwraca przechowywaną liczbę jako BigDecimal
	 */
	public BigDecimal getValueAsBigDecimal() {
		return value;
	}

	/**
	 * Zwraca przechowywaną liczbę jako double. Uwaga! Utrata dokładności!!!
	 */
	public double getValueAsDouble() {
		return value.doubleValue();
	}

	/**
	 * Zwraca przechowywaną liczbę jako int. Uwaga! Utrata dokładności!!!
	 */
	public int getValueAsInt() {
		return value.intValue();
	}

	public BigDecimal pow(BigDecimal a) throws ArithmeticException {
		int intPart = -1;
		double doublePart = 0d;
		try {
			intPart = a.intValueExact();
		} catch (ArithmeticException e) {
			//return bd(Math.pow(value.doubleValue(), a.doubleValue()));
			intPart = a.intValue();
			doublePart = a.subtract(new BigDecimal(intPart)).doubleValue();
		} finally {
			if ((intPart >= 0) && (doublePart == 0d)) {
				return value.pow(intPart);
			} else if ((doublePart == 0d) && (intPart <= 0)) {
				return bd(1).divide(value.pow(-intPart), Settings.divideContext());
			} else {
				if (intPart >= 0) {
					BigDecimal l1 = bd(Math.pow(value.doubleValue(), doublePart));
					return l1.multiply(value.pow(intPart));
				} else {
					BigDecimal l1 = bd(Math.pow(value.doubleValue(), -doublePart));
					return BigDecimal.ONE.divide(l1.multiply(value.pow(-intPart)), Settings.divideContext());
				}
			}
		}
	}

	/**
	 * wyciąga pierwiastek kwadratowy <br>
	 * TODO: poprawić!!!
	 */
	public BigDecimal getSqrt() {
		return bd(Math.sqrt(value.doubleValue()));
	}

	public Liczba round(RoundingMode mode) {
		//najpierw konwertujemy do BigInteger, żeby ustawić precision (liczba cyfr znaczących) na tyle ile jest w cześci całkowitej przy zastosowaniu wybranego RoundingMode
		//return value.round(new MathContext(bd(value.toBigInteger()).precision(), mode));
		return new Liczba(value.setScale(0, mode));
	//return bd(value.toBigInteger());
	//return value.round(new MathContext(3, mode));
	}

	public Liczba round() {
		return new Liczba(value.setScale(0, RoundingMode.HALF_UP));
	}

	public int compareTo(Liczba o) {
		//throw new UnsupportedOperationException("Not supported yet.");
		return value.compareTo(o.getValueAsBigDecimal());
	}

	/**
	 * Czy liczba jest ułamkiem?
	 */
	public boolean isFraction() {
		try {
			BigInteger i = value.toBigIntegerExact();
		} catch (ArithmeticException a) {
			return false;
		}
		return true;
	}

	/**
	 * Przekształca String do Liczba.
	 * @param string
	 * @return
	 */
	public static Liczba valueOf(String string) {
		return new Liczba(string);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Liczba) {
			return this.getValueAsBigDecimal().compareTo(((Liczba) obj).getValueAsBigDecimal()) == 0;
		}
		return super.equals(obj);
	}
}
