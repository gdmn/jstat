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

/**
 *
 * @author Damian
 */
public class Funkcja extends ElementWyrazenia {

	/** referencja do wybranej funkcji w konstruktorze */
	private FunkcjaInterface myFunction;
	/** lista funkcji dodanych przez użytkownika, np. fcja X przy rysowaniu wykresu */
	private FunkcjaInterface[] userArrayOfFunctions;
	/** tablica wszystkich funkcji wbudowanych w program */
	private static final FunkcjaInterface[] arrayOfFunctions;
	/** stała potrzebna do liczenia rozkładu normalnego */
	private static Liczba JEDENPRZEZPIERWIASTEKZDWAPI;

	/** Creates a new instance of Funkcja */
	public Funkcja(String str, FunkcjaInterface... userArrayOfFunctions) throws IllegalArgumentException {
		super(str);
		//initializeStaticFields();
		setUserArrayOfFunctions(userArrayOfFunctions);
		try {
			Functions enumFunction = Enum.valueOf(Functions.class, str.toUpperCase());
			// myIndex = myFunction.ordinal();
			myFunction = arrayOfFunctions[enumFunction.ordinal()];
			stringRepresentation = enumFunction.toString();
		} catch (IllegalArgumentException e) {
			boolean found = false;
			if (userArrayOfFunctions != null) {
				for (FunkcjaInterface fcja : userArrayOfFunctions) {
					if (fcja.toString().equals(str.toUpperCase())) {
						found = true;
						myFunction = fcja;
						stringRepresentation = fcja.toString();
						break;
					}
				}
			}
			if (!found) {
				throw new IllegalArgumentException(str);
			}
		}
	}

	public Funkcja(Functions f, FunkcjaInterface... userArrayOfFunctions) {
		super(f.toString());
		//initializeStaticFields();
		setUserArrayOfFunctions(userArrayOfFunctions);
		myFunction = arrayOfFunctions[f.ordinal()];
	}

	private void initializeStaticFields() {
		if (JEDENPRZEZPIERWIASTEKZDWAPI == null) {
			JEDENPRZEZPIERWIASTEKZDWAPI = (Liczba) Znak.podzielElementy(Liczba.JEDEN, new Liczba(Math.sqrt(2 * Math.PI)));
		}

	}

	/**
	 * @return wartość userArrayOfFunctions
	 */
	private FunkcjaInterface[] getUserArrayOfFunctions() {
		return userArrayOfFunctions;
	}

	/**
	 * @param userArrayOfFunctions wartość userArrayOfFunctions do ustawienia
	 */
	private void setUserArrayOfFunctions(FunkcjaInterface[] userArrayOfFunctions) {
		this.userArrayOfFunctions = userArrayOfFunctions;
	}

	@Override
	public int getPriority() {
		return 5;
	}

	/**
	 * Liczba argumentów
	 *
	 * @return liczba
	 */
	public int getArgumentsCount() {
		return myFunction.getArgumentsCount();
	}

	/**
	 * Oblicza wynik funkcji z args.
	 *
	 * @return Wynik obliczeń.
	 * @param args Tablica z parametrami.
	 */
	public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
		return myFunction.getValue(args);
	}

	/**
	 * Oblicza wynik funkcji
	 * @param funkcja Funkcja nakładana na argumenty
	 * @param args argumenty funkcji
	 * @return wynik
	 * @throws kalkulator.InvalidArgumentException
	 */
	public static ElementWyrazenia getValue(Functions funkcja, ElementWyrazenia... args) throws InvalidArgumentException {
		Funkcja f = new Funkcja(funkcja);
		return f.getValue(args);
	}

	public boolean isMetaFunction() {
		return false;
	}

	/**
	 * @return Interfejs funkcji, z której aktualnie korzysta obiekt
	 */
	public FunkcjaInterface getFunkcjaInterface() {
		return myFunction;
	}

	/* statyczna inicjalizacja, czyli przed pierwszym użyciem jakiejkolwiek statycznej metody lub konstruktora: */
	static {
		arrayOfFunctions = new FunkcjaInterface[Functions.values().length];
		// System.out.println("Liczba funkcji wbudowanych: "+arrayOfFunctions.length);
		for (Functions i : Functions.values()) {
			switch (i) {
				case ABS:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(((Liczba) args[0]).getValueAsBigDecimal().abs());
							}
							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ARCCOS:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(Math.acos(((Liczba) args[0]).getValueAsDouble()));
							}
							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ARCCTG:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								Liczba l1 = new Liczba(Math.PI * 0.5);
								return (Liczba) Znak.odejmijElementy(l1, Funkcja.getValue(Functions.ARCTG, args));
							}
							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ARCSIN:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(Math.asin(((Liczba) args[0]).getValueAsDouble()));
							}
							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ARCTG:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(Math.atan(((Liczba) args[0]).getValueAsDouble()));
							}
							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

//				case ARCTG2:
//					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {
//
//						public int getArgumentsCount() {
//							return 2;
//						}
//
//						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
//							if ((args[0] instanceof Liczba) && (args[1] instanceof Liczba)) {
//								return new Liczba(Math.atan2(((Liczba) args[0]).getValueAsDouble(), ((Liczba) args[1]).getValueAsDouble()));
//							}
//							throw new InvalidArgumentException(getFunctionInfo(this, args));
//						}
//					};
//					break;

				case BETA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								try {
									return new Liczba(Funkcja.funkcjabeta(0, 1, ((Liczba) args[0]).getValueAsDouble(), ((Liczba) args[1]).getValueAsDouble()));
								} catch (InvalidArgumentException eex) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - " + eex.getMessage());
								}
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};

					break;

				case BETAINCMPL:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; //x, a,b
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba && args[2] instanceof Liczba) {
								try {
									return new Liczba(Funkcja.funkcjabeta(0, ((Liczba) args[0]).getValueAsDouble(), ((Liczba) args[1]).getValueAsDouble(), ((Liczba) args[2]).getValueAsDouble()));
								} catch (InvalidArgumentException eex) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - " + eex.getMessage());
								}
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case BETAREG:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba && args[2] instanceof Liczba) {
								try {
									Funkcja beta = new Funkcja(Functions.BETA);
									Funkcja betaincmpl = new Funkcja(Functions.BETAINCMPL);
									Liczba l2 = (Liczba) beta.getValue(args[1], args[2]);
									Liczba l1 = (Liczba) betaincmpl.getValue(args);
									return (Liczba) Znak.podzielElementy(l1, l2);
								} catch (InvalidArgumentException eex) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - " + eex.getMessage());
								}
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case CBRT:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(Math.cbrt(((Liczba) args[0]).getValueAsDouble()));
							}
							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case CEIL:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(Math.ceil(((Liczba) args[0]).getValueAsDouble()));
							}
							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case COS:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(Math.cos(((Liczba) args[0]).getValueAsDouble()));
							}
							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case COSH:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(Math.cosh(((Liczba) args[0]).getValueAsDouble()));
							}
							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case CSC:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(1 / Math.sin(((Liczba) args[0]).getValueAsDouble()));
							}
							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case CTGH:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(1 / Math.tanh(((Liczba) args[0]).getValueAsDouble()));
							}

							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case CTG:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(1 / Math.tan(((Liczba) args[0]).getValueAsDouble()));
							}

							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;


				case DET:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz) {
								try {
									// return new Liczba(Macierz.wyznacznikLaplaceBig((Macierz)args[0]).doubleValue());
									return (Macierz.wyznacznikGauss((Macierz) args[0]));
								} catch (InvalidArgumentException eex) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - " + eex.getMessage());
								}
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case E:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 0;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							return new Liczba(Math.E);
						}
					};
					break;

				case EXP:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(Math.exp(((Liczba) args[0]).getValueAsDouble()));
							}
							/* TODO: UWAGA: poniższy kod nie podnosi do niecałkowitej potęgi: return new
							 * Liczba(Liczba.bd(Math.E).pow(((Liczba)args[0]).getValueAsInt()));
							 */
							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case FACT:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								BigInteger result = BigInteger.ONE;//((Liczba)args[0]).getValue().toBigInteger();
								int value = ((Liczba) args[0]).getValueAsBigDecimal().intValueExact();
								if (value < 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - argument musi być nieujemny");
								}
								for (int j = 1; j <= value; j++) {
									result = result.multiply(new BigInteger(Integer.toString(j)));
								}
								//System.out.println("" + result);
								return new Liczba(new BigDecimal(result));
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				/*
				 * case FLOOR: arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() { public int getArgumentsCount() {return 1;} public
				 * ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException { if (args[0] instanceof Liczba) return new
				 * Liczba(((Liczba)args[0]).getFloor()); if (args[0] instanceof Macierz) return Macierz.getValueApplyFunction1((Macierz)args[0],
				 * this); throw new InvalidArgumentException(getFunctionInfo(this, args)); } }; break;
				 */

				case GAMMA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								try {
									//return new Liczba(Funkcja.simpgamma(((Liczba) args[0]).getValueAsDouble()));
									return new Liczba(Funkcja.funkcjagamma_lanczos(((Liczba) args[0]).getValueAsDouble()));
								} catch (InvalidArgumentException eex) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - " + eex.getMessage());
								}
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};

					break;

				case GAMMALOWER:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								try {
									//return new Liczba(Funkcja.simpgammalower(((Liczba) args[0]).getValueAsDouble(), ((Liczba) args[1]).getValueAsDouble()));
									return new Liczba(Funkcja.funkcjagamma(0, ((Liczba) args[0]).getValueAsDouble(), ((Liczba) args[1]).getValueAsDouble()));
								} catch (InvalidArgumentException eex) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - " + eex.getMessage());
								}
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};

					break;

				case GAMMAUPPER:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								try {
									//return new Liczba(Funkcja.simpgammaupper(((Liczba) args[0]).getValueAsDouble(), ((Liczba) args[1]).getValueAsDouble()));
									return new Liczba(Funkcja.funkcjagamma(((Liczba) args[0]).getValueAsDouble(), 1000, ((Liczba) args[1]).getValueAsDouble()));
								} catch (InvalidArgumentException eex) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - " + eex.getMessage());
								}
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case GAMMAINCMPL:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								try {
									//return new Liczba(Funkcja.simpgammaupper(((Liczba) args[0]).getValueAsDouble(), ((Liczba) args[1]).getValueAsDouble()));
									return new Liczba(Funkcja.funkcjagamma_incompletelower(((Liczba) args[0]).getValueAsDouble(), ((Liczba) args[1]).getValueAsDouble()));
								} catch (InvalidArgumentException eex) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - " + eex.getMessage());
								}
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

//				case GRAM:
//					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {
//
//						public int getArgumentsCount() {
//							return 1;
//						}
//
//						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
//							if (args[0] instanceof Macierz) {
//								try {
//									return Macierz.macierzGrama(((Macierz) args[0]).getValue());
//								} catch (InvalidArgumentException eex) {
//									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - " + eex.getMessage());
//								}
//							}
//							throw new InvalidArgumentException(getFunctionInfo(this, args));
//						}
//					};
//
//					break;

				case HILBERT:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								try {
									return Macierz.macierzHilberta(((Liczba) args[0]).getValueAsInt());
								} catch (InvalidArgumentException eex) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - " + eex.getMessage());
								}

							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case HYPOT:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if ((args[0] instanceof Liczba) && (args[1] instanceof Liczba)) {
								// return new Liczba(Math.hypot(((Liczba)args[0]).getValue(), ((Liczba)args[1]).getValue()));
								BigDecimal a1 = ((Liczba) args[0]).getValueAsBigDecimal(),
										a2 = ((Liczba) args[1]).getValueAsBigDecimal();
								return new Liczba(new Liczba(a1.multiply(a1).add(a2.multiply(a2))).getSqrt());
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case INTEGRAL:
					arrayOfFunctions[i.ordinal()] = new MetaFunkcjaInterface() {

						public int getArgumentsCount() {
							return 3;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							//if (args[0] instanceof Liczba)
							return new Liczba(Calki.simpsmpl(args));
						//throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;


				case INV:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								try {
									return new Liczba((new BigDecimal(1)).divide(((Liczba) args[0]).getValueAsBigDecimal(), Settings.divideContext()));
								} catch (InvalidArgumentException eex) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - " + eex.getMessage());
								}

							}
							if (args[0] instanceof Macierz) {
								try {
									// return new Liczba(Macierz.wyznacznikLaplaceBig((Macierz)args[0]).doubleValue());
									return (Macierz.odwrotna((Macierz) args[0]));
								} catch (InvalidArgumentException eex) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - " + eex.getMessage());
								}

							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case LN:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(Math.log(((Liczba) args[0]).getValueAsDouble()));
							}

							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case LOG:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								return Znak.podzielElementy(new Liczba(Math.log(((Liczba) args[1]).getValueAsDouble())),
										new Liczba(Math.log(((Liczba) args[0]).getValueAsDouble())));
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case LOG10:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(Math.log10(((Liczba) args[0]).getValueAsDouble()));
							}

							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case JEDNOSTKOWA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								try {
									return Macierz.macierzJednostkowa(((Liczba) args[0]).getValueAsInt());
								} catch (InvalidArgumentException eex) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - " + eex.getMessage());
								}

							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case MAX:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if ((args[0] instanceof Liczba) && (args[1] instanceof Liczba)) {
								return new Liczba(((Liczba) args[0]).getValueAsBigDecimal().max(((Liczba) args[1]).getValueAsBigDecimal()));
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case MIN:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if ((args[0] instanceof Liczba) && (args[1] instanceof Liczba)) {
								return new Liczba(((Liczba) args[0]).getValueAsBigDecimal().min(((Liczba) args[1]).getValueAsBigDecimal()));
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;
				case NEGATE:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							return Znak.przeciwnyElement(args[0]);
//							if (args[0] instanceof Liczba) {
//								return new Liczba(((Liczba) args[0]).getValue().negate());
//							}
//							if (args[0] instanceof Macierz) {
//								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
//							}
//							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case NEWTON:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { // m nad n
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								int n = ((Liczba) args[0]).getValueAsBigDecimal().intValueExact();
								int k = ((Liczba) args[1]).getValueAsBigDecimal().intValueExact();
								if (k < 0 || n < 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - argumenty muszą być nieujemne");
								}
								if (n < k) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - pierwszy argument nie może być mniejszy od drugiego");
								}
								Funkcja silnia = new Funkcja(Functions.FACT);
								Liczba nn = new Liczba(new BigDecimal(n));
								Liczba kk = new Liczba(new BigDecimal(k));
								Liczba silnian = (Liczba) silnia.getValue(nn);
								Liczba silniak = (Liczba) silnia.getValue(kk);
								Liczba silnianminusk = (Liczba) silnia.getValue((Liczba) Znak.odejmijElementy(nn, kk));
								Liczba result = (Liczba) Znak.podzielElementy(
										silnian,
										Znak.pomnozElementy(silniak, silnianminusk));
								return result;
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case PI:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 0;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							return new Liczba(Math.PI);
						}
					};
					break;

				case PODMACIERZ:
					// podmacierz( {{1;2;3;4}{10;20;30;40}{100;200;300;400}} ; 2; 2)
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if ((args[0] instanceof Macierz) && (args[1] instanceof Liczba) && (args[2] instanceof Liczba)) {
								try {
									return Macierz.podmacierz((Macierz) args[0], ((Liczba) args[1]).getValueAsInt(), ((Liczba) args[2]).getValueAsInt());
								} catch (InvalidArgumentException eex) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - " + eex.getMessage());
								}

							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						// throw new InvalidArgumentException(this.toString());
						}
					};
					break;

				case POW:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							/*if ((args[0] instanceof Liczba) && (args[1] instanceof Liczba))
							return new Liczba( ((Liczba)args[0]).pow(((Liczba)args[1]).getValue()) );
							throw new InvalidArgumentException(getFunctionInfo(this, args));
							// throw new InvalidArgumentException(this.toString());
							 */
							try {
								return Znak.potegujElementy(args[0], args[1]);
							} catch (InvalidArgumentException ex) {
								throw new InvalidArgumentException(this.toString() + " " + ex.getMessage());
							}

						}
					};
					break;

				case RANDOM:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 0;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							return new Liczba(Math.random());
						}
					};
					break;

				/*
				 * case RINT: arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() { public int getArgumentsCount() {return 1;} public
				 * ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException { if (args[0] instanceof Liczba) return new
				 * Liczba(Math.rint(((Liczba)args[0]).getValue())); if (args[0] instanceof Macierz) return
				 * Macierz.getValueApplyFunction1((Macierz)args[0], this); throw new InvalidArgumentException(getFunctionInfo(this, args)); } };
				 * break;
				 */

				case ROUND:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return ((Liczba) args[0]).round();
							}

							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case SEC:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(1 / Math.cos(((Liczba) args[0]).getValueAsDouble()));
							}
							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case SGN:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(((Liczba) args[0]).getValueAsBigDecimal().signum());
							}

							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case SIN:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(Math.sin(((Liczba) args[0]).getValueAsDouble()));
							}

							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case SINH:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(Math.sinh(((Liczba) args[0]).getValueAsDouble()));
							}

							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case SQRT:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(((Liczba) args[0]).getSqrt());
							}
//							if (args[0] instanceof Macierz) {
//								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
//							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case TEKST:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							return new Tekst(args[0].toString());
						}
					};
					break;

				case TG:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(Math.tan(((Liczba) args[0]).getValueAsDouble()));
							}

							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case TGH:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(Math.tanh(((Liczba) args[0]).getValueAsDouble()));
							}

							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case TODEGREES:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(Math.toDegrees(((Liczba) args[0]).getValueAsDouble()));
							}

							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case TOFLOAT:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if ((args[0] instanceof Macierz) && ((Macierz) args[0]).getLengthX() == 1 && ((Macierz) args[0]).getLengthY() == 1) {
								return ((Macierz) args[0]).getValueAt(0, 0);
							}

							if (args[0] instanceof Liczba) {
								return args[0];
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args) + " (macierz musi być wymiaru [1, 1])");
						}
					};
					break;

				case TOMATRIX:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								Macierz result = new Macierz(new WyrazenieInfix[1][1]);
								result.set(0, 0, (Liczba) args[0]);
								return result;
							}

							if (args[0] instanceof Macierz) {
								return args[0];
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case TORADIANS:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(Math.toRadians(((Liczba) args[0]).getValueAsDouble()));
							}

							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case TRANSP:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz) {
								try {
									return Macierz.transpozycja((Macierz) args[0]);
								} catch (InvalidArgumentException eex) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - " + eex.getMessage());
								}
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ULP:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								return new Liczba(((Liczba) args[0]).getValueAsBigDecimal().ulp());
							}

							if (args[0] instanceof Macierz) {
								return Macierz.getValueApplyFunction1((Macierz) args[0], this);
							}

							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

//////////////////////////////////////////////////////////
				//KOMBINACJE, KOMBINACJE2, WARIANCJE, WARIANCJE2,
				case KOMBINACJE:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { // k, n
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							/*if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
							Liczba k = new Liczba(((Liczba) args[0]).getValue().toBigIntegerExact());
							Liczba n = new Liczba(((Liczba) args[1]).getValue().toBigIntegerExact());
							if (k.compareTo(Liczba.ZERO) < 0 || n.compareTo(Liczba.ZERO) < 0) {
							throw new InvalidArgumentException(getFunctionInfo(this, args) + " - argumenty muszą być nieujemne");
							}
							if (k.compareTo(n) <= 0) {
							throw new InvalidArgumentException(getFunctionInfo(this, args) + " - drugi argument nie może być większy od pierwszego");
							}
							Funkcja silnia = new Funkcja(Functions.FACT, null);
							Liczba silnian = (Liczba) silnia.getValue(n);
							Liczba silniak = (Liczba) silnia.getValue(k);
							Liczba silnianminusk = (Liczba) silnia.getValue((Liczba) Znak.odejmijElementy(n, k));
							Liczba result = (Liczba) Znak.podzielElementy(
							silnian,
							Znak.pomnozElementy(silniak, silnianminusk));
							return result;
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));*/
							return Funkcja.getValue(Functions.NEWTON, args[1], args[0]);
						}
					};
					break;
				case KOMBINACJE2:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								Liczba k = new Liczba(((Liczba) args[0]).getValueAsBigDecimal().toBigIntegerExact());
								Liczba n = new Liczba(((Liczba) args[1]).getValueAsBigDecimal().toBigIntegerExact());
								Funkcja kombinacje = new Funkcja(Functions.KOMBINACJE);
								Liczba result = (Liczba) kombinacje.getValue(k, Znak.odejmijElementy(Znak.dodajElementy(n, k), Liczba.JEDEN));
								return result;
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;
				case WARIANCJE:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								Liczba k = new Liczba(((Liczba) args[0]).getValueAsBigDecimal().toBigIntegerExact());
								Liczba n = new Liczba(((Liczba) args[1]).getValueAsBigDecimal().toBigIntegerExact());
								Funkcja silnia = new Funkcja(Functions.FACT);
								Liczba silnian = (Liczba) silnia.getValue(n);
								Liczba silnianminusk = (Liczba) silnia.getValue((Liczba) Znak.odejmijElementy(n, k));
								Liczba result = (Liczba) Znak.podzielElementy(
										silnian, silnianminusk);
								return result;
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;
				case WARIANCJE2:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								Liczba k = new Liczba(((Liczba) args[0]).getValueAsBigDecimal().toBigIntegerExact());
								Liczba n = new Liczba(((Liczba) args[1]).getValueAsBigDecimal().toBigIntegerExact());
								Funkcja pow = new Funkcja(Functions.POW);
								Liczba result = (Liczba) pow.getValue(n, k);
								return result;
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

//////////////////////////////////////////////////////////

				case ZEROJEDYNKOWY:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //p, x
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								BigDecimal p = ((Liczba) args[0]).getValueAsBigDecimal();
								if (p.compareTo(new BigDecimal("0")) < 0 || p.compareTo(new BigDecimal("1")) > 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - pierwszy argument musi zawierać się w przedziale <0; 1>");

								}
								int x = ((Liczba) args[1]).getValueAsBigDecimal().intValueExact();
								if (x != 0 && x != 1) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - drugi argument musi być 0 lub 1");
								}
								return new Liczba(x == 1 ? p : (BigDecimal.ONE.subtract(p)));
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case FZEROJEDYNKOWY:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //p,x
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								BigDecimal p = ((Liczba) args[0]).getValueAsBigDecimal();
								if (p.compareTo(BigDecimal.ZERO) < 0 || p.compareTo(BigDecimal.ONE) > 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - pierwszy argument musi zawierać się w przedziale <0; 1>");
								}
								BigDecimal x = ((Liczba) args[1]).getValueAsBigDecimal();
								if (x.compareTo(BigDecimal.ZERO) <= 0) {
									return new Liczba(BigDecimal.ZERO);
								} else if (x.compareTo(BigDecimal.ONE) <= 0) {
									return new Liczba(BigDecimal.ONE.subtract(p));
								} else {
									return new Liczba(BigDecimal.ONE);
								}
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

////

				case DWUMIANOWY:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //n, p, k
							return 3;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba && args[2] instanceof Liczba) {
								Liczba n = new Liczba(((Liczba) args[0]).getValueAsBigDecimal().toBigIntegerExact()); // sprawdzamy, czy całkowita
								Liczba p = (Liczba) args[1];
								Liczba k = new Liczba(((Liczba) args[2]).getValueAsBigDecimal().toBigIntegerExact());
								if (p.compareTo(Liczba.ZERO) < 0 || p.compareTo(Liczba.JEDEN) > 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - drugi argument musi zawierać się w przedziale <0; 1>");
								}
								Liczba l1 = (Liczba) Funkcja.getValue(Functions.KOMBINACJE, k, n); // kombinacje k po n
								Liczba l2 = (Liczba) Funkcja.getValue(Functions.POW, p, k); // p^k
								Liczba l3 = (Liczba) Funkcja.getValue(Functions.POW, Znak.odejmijElementy(Liczba.JEDEN, p), Znak.odejmijElementy(n, k)); // q^(n-k)
								return Znak.pomnozElementy(l1, Znak.pomnozElementy(l2, l3));
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case FDWUMIANOWY:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //n, p, x
							return 3;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba && args[2] instanceof Liczba) {
								Liczba n = new Liczba(((Liczba) args[0]).getValueAsBigDecimal().toBigIntegerExact()); // sprawdzamy, czy całkowita
								Liczba p = (Liczba) args[1];
								int x = ((Liczba) args[2]).getValueAsBigDecimal().intValue();
								if (((Liczba) args[2]).getValueAsBigDecimal().compareTo(new BigDecimal(x)) > 0) {
									x++;
								}
								//zaokrąglanie w górę
								Funkcja dwumianowy = new Funkcja(Functions.DWUMIANOWY);
								Liczba result = Liczba.ZERO;
								for (int j = 0; j < x; j++) {
									result = (Liczba) Znak.dodajElementy(result,
											dwumianowy.getValue(n, p, new Liczba(new BigDecimal(j))));
								}
								return result;
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

///

				//POISSONA, FPOISSONA,
				case POISSONA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //m, k
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								Liczba m = (Liczba) args[0];
								Liczba k = (Liczba) args[1];
								Liczba l1 = (Liczba) Funkcja.getValue(Functions.POW, m, k); // m^k
								Liczba l2 = (Liczba) Funkcja.getValue(Functions.FACT, k); // k!
								Liczba l3 = (Liczba) Funkcja.getValue(Functions.POW, Funkcja.getValue(Functions.E), Znak.przeciwnyElement(m)); // e^(-m)
								return Znak.pomnozElementy(l3, Znak.podzielElementy(l1, l2));
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case FPOISSONA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //m, x
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								Liczba m = (Liczba) args[0];
								//Liczba x = (Liczba) args[1];
								int x = ((Liczba) args[1]).getValueAsBigDecimal().intValue();
								if (((Liczba) args[1]).getValueAsBigDecimal().compareTo(new BigDecimal(x)) > 0) {
									x++;
								}
								//zaokrąglanie w górę
								Funkcja poissona = new Funkcja(Functions.POISSONA);
								Liczba result = Liczba.ZERO;
								for (int j = 0; j <= x; j++) {
									result = (Liczba) Znak.dodajElementy(result,
											poissona.getValue(m, new Liczba(new BigDecimal(j))));
								}
								return result;
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;
///
				//GEOMETRYCZNY, FGEOMETRYCZNY
				case GEOMETRYCZNY:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //p, k
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								Liczba p = (Liczba) args[0];
								Liczba k = (Liczba) args[1];
								if (p.compareTo(Liczba.ZERO) < 0 || p.compareTo(Liczba.JEDEN) > 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - pierwszy argument musi zawierać się w przedziale <0; 1>");
								}
								Liczba q = (Liczba) Znak.odejmijElementy(Liczba.JEDEN, p);
								Liczba l2 = (Liczba) Funkcja.getValue(Functions.POW, q, Znak.odejmijElementy(k, Liczba.JEDEN)); // q^(k-1)
								return Znak.pomnozElementy(p, l2);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;
				case FGEOMETRYCZNY:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //p, x
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								Liczba p = (Liczba) args[0];
								//Liczba x = (Liczba) args[1];
								int x = ((Liczba) args[1]).getValueAsBigDecimal().intValue();
								if (((Liczba) args[1]).getValueAsBigDecimal().compareTo(new BigDecimal(x)) > 0) {
									x++;
								}
								//zaokrąglanie w górę
								Funkcja geometryczny = new Funkcja(Functions.GEOMETRYCZNY);
								Liczba result = Liczba.ZERO;
								for (int j = 1; j < x; j++) {
									result = (Liczba) Znak.dodajElementy(result,
											geometryczny.getValue(p, new Liczba(new BigDecimal(j))));
								}
								return result;
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;
///
				case JEDNOSTAJNY:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //a,b,x
							return 3;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba && args[2] instanceof Liczba) {
								Liczba a = (Liczba) args[0];
								Liczba b = (Liczba) args[1];
								Liczba x = (Liczba) args[2];
								if (a.compareTo(b) > 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - pierwszy argument nie może być większy od drugiego");
								}
								if (x.compareTo(a) < 0) {
									return Liczba.ZERO;
								} else if (x.compareTo(b) > 0) {
									return Liczba.ZERO;
								} else {
									return (Liczba) Znak.podzielElementy(Liczba.JEDEN, Znak.odejmijElementy(b, a));
								}
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case FJEDNOSTAJNY:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //a,b,x
							return 3;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba && args[2] instanceof Liczba) {
								Liczba a = (Liczba) args[0];
								Liczba b = (Liczba) args[1];
								Liczba x = (Liczba) args[2];
								if (a.compareTo(b) > 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - pierwszy argument nie może być większy od drugiego");
								}
								if (x.compareTo(a) < 0) {
									return Liczba.ZERO;
								} else if (x.compareTo(b) > 0) {
									return Liczba.JEDEN;
								} else {
									return (Liczba) Znak.podzielElementy(Znak.odejmijElementy(x, a), Znak.odejmijElementy(b, a));
								}
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;
///
				case WYKLADNICZY:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //gamma,x
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								Liczba g = (Liczba) args[0];
								Liczba x = (Liczba) args[1];
								if (x.compareTo(Liczba.ZERO) < 0) {
									return Liczba.ZERO;
								} else {
									Liczba l2 = (Liczba) Funkcja.getValue(Functions.POW, Funkcja.getValue(Functions.E), Znak.przeciwnyElement(Znak.pomnozElementy(g, x)));
									return (Liczba) Znak.pomnozElementy(g, l2);
								}
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case FWYKLADNICZY:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //gamma,x
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								Liczba g = (Liczba) args[0];
								Liczba x = (Liczba) args[1];
								if (x.compareTo(Liczba.ZERO) < 0) {
									return Liczba.ZERO;
								} else {
									Liczba l2 = (Liczba) Funkcja.getValue(Functions.POW, Funkcja.getValue(Functions.E), Znak.przeciwnyElement(Znak.pomnozElementy(g, x)));
									return (Liczba) Znak.odejmijElementy(Liczba.JEDEN, l2);
								}
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;
///
				case NORMALNYSTD:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //t
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								Liczba t = (Liczba) args[0];
								Liczba l1 = (Liczba) Znak.podzielElementy(Liczba.JEDEN, new Liczba(Math.sqrt(2 * Math.PI)));
								Funkcja pow = new Funkcja(Functions.POW);
								Liczba l2 = (Liczba) Znak.przeciwnyElement(Znak.pomnozElementy(
										new Liczba("0.5"),
										Znak.pomnozElementy(t, t))); // e^(-t^2 / 2)
								return (Liczba) Znak.pomnozElementy(l1, pow.getValue(new Liczba(Math.E), l2));

							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case NORMALNYFI:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //t
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								Liczba t = (Liczba) args[0];
								if (t.compareTo(Liczba.ZERO) < 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - argument musi być nieujemny");
								}
								Liczba l1 = (Liczba) Znak.podzielElementy(Liczba.JEDEN, new Liczba(Math.sqrt(2 * Math.PI)));
								WyrazeniePostfix podcalkowa = WyrazeniePostfix.valueOf("e^(-x*x*0,5)", Funkcja.funkcjaX);
								double tt = t.getValueAsDouble();
								Liczba l2 = null;
								if (tt == 0) {
									return new Liczba("0");
								} else {
									double w = (new Calki()).simpsonsimple(0d, tt, podcalkowa, 0.0000000001, 7, 0);
									l2 = new Liczba(w);
									return (Liczba) Znak.pomnozElementy(l1, l2);
								}

							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case FNORMALNYSTD:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //t
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								Liczba t = (Liczba) args[0];
								Funkcja fi = new Funkcja(Functions.NORMALNYFI);
								if (t.compareTo(Liczba.ZERO) < 0) {
									Liczba fv = (Liczba) fi.getValue(Znak.przeciwnyElement(t));
									return Znak.odejmijElementy(new Liczba("0.5"), fv);
								} else {
									Liczba fv = (Liczba) fi.getValue(t);
									return Znak.dodajElementy(fv, new Liczba("0.5"));
								}
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case NORMALNY:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //m,s,x
							return 3;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba && args[2] instanceof Liczba) {
								Liczba m = (Liczba) args[0];
								Liczba s = (Liczba) args[1];
								Liczba x = (Liczba) args[2];
								if (s.compareTo(Liczba.ZERO) < 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - drugi argument nie może mniejszy od zera");
								}
								/*Funkcja pow = new Funkcja(Functions.POW);
								Liczba dwa = new Liczba("2");
								Liczba l1 = (Liczba) Znak.podzielElementy(Liczba.JEDEN, Znak.pomnozElementy(s, new Liczba(Math.sqrt(2 * Math.PI)))); // 1/(s*sqrt(2*pi))
								Liczba l2 = (Liczba) Znak.przeciwnyElement(Znak.podzielElementy(
								pow.getValue(Znak.odejmijElementy(x, m), dwa), //(x-m)^2
								Znak.pomnozElementy(dwa, Znak.pomnozElementy(s, s)) // 2*s^2
								));
								return (Liczba) Znak.pomnozElementy(l1, pow.getValue(new Liczba(Math.E), l2));
								 */
								Liczba t = (Liczba) Znak.podzielElementy(Znak.odejmijElementy(x, m), s);
								return Funkcja.getValue(Functions.NORMALNYSTD, t);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case FNORMALNY:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //m,s,x
							return 3;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba && args[2] instanceof Liczba) {
								Liczba m = (Liczba) args[0];
								Liczba s = (Liczba) args[1];
								Liczba x = (Liczba) args[2];
								if (s.compareTo(Liczba.ZERO) < 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - drugi argument nie może mniejszy od zera");
								}
								Liczba t = (Liczba) Znak.podzielElementy(Znak.odejmijElementy(x, m), s);
								return Funkcja.getValue(Functions.FNORMALNYSTD, t);

							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case NORMALNYSTDTAIL:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //p
							return 1;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba) {
								Liczba p = (Liczba) args[0];
								if (p.compareTo(Liczba.ZERO) < 0 || p.compareTo(Liczba.JEDEN) > 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - argument musi zawierać się w przedziale <0; 1>");
								}
								Funkcja f = new Funkcja(Functions.FNORMALNYSTD);
								//dzieli na pół, bo dwustronny!
								//Liczba p2 = (Liczba) Znak.odejmijElementy(Liczba.JEDEN, Znak.pomnozElementy(Liczba.POL, p));
								Liczba p2 = p;

								Liczba low = new Liczba("-3"), high = new Liczba("3");
								Liczba eps = new Liczba("0,0000000000001");
								Liczba result;
								result = ZlotyPodzial.znajdzX(p2, low, high, eps, new Liczba(3), f, 0, args);
								return result;
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;
///
				case CHIKWADRAT:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //k,x
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								Liczba k = (Liczba) args[0];
								Liczba x = (Liczba) args[1];
								if (k.compareTo(Liczba.ZERO) < 0 || x.compareTo(Liczba.ZERO) < 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - argumenty nie mogą być ujemne");
								}
								//xx = (x + 1) / 10;
								if (x.compareTo(Liczba.ZERO) <= 0) {
									return Liczba.ZERO;
								} else {
									//return (1 / (Math.pow(2, m / 2) * gamma)) * Math.pow(x, (m / 2) - 1) * Math.exp(-x / 2);\
									/*
									 * http://mathworld.wolfram.com/Chi-SquaredDistribution.html:
									 * x^(k/2-1)*e^(-x/2) / ( gamma(k/2)*2^(k/2) )
									 */

									Liczba kpolowa = (Liczba) Znak.pomnozElementy(k, Liczba.POL);
									Liczba l1 = (Liczba) Funkcja.getValue(Functions.POW, x, Znak.odejmijElementy(kpolowa, Liczba.JEDEN));
									Liczba l2 = (Liczba) Funkcja.getValue(Functions.EXP, Znak.pomnozElementy(x, new Liczba("-0.5")));
									//Liczba gamma = new Liczba(simpgamma(k.getValueAsDouble() * 0.5));
									Liczba gamma = (Liczba) Funkcja.getValue(Functions.GAMMA, kpolowa);
									//Liczba l3 = (Liczba) Znak.pomnozElementy(gamma, Funkcja.getValue(Functions.POW, new Liczba("2"), kpolowa));
									Liczba l3 = (Liczba) Znak.pomnozElementy(gamma, Funkcja.getValue(Functions.POW, new Liczba("2"), kpolowa));
									Liczba result = (Liczba) Znak.podzielElementy(Znak.pomnozElementy(l2, l1), l3);
									return result;
								}
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case FCHIKWADRAT:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //k,x
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								Liczba k = (Liczba) args[0];
								Liczba x = (Liczba) args[1];
								if (k.compareTo(Liczba.ZERO) < 0 || x.compareTo(Liczba.ZERO) < 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - argumenty nie mogą być ujemne");
								}
								Funkcja gamma = new Funkcja(Functions.GAMMA);
								Funkcja gammalower = new Funkcja(Functions.GAMMALOWER);
								Liczba polk = (Liczba) Znak.pomnozElementy(k, Liczba.POL);
								/*Liczba polx = (Liczba) Znak.pomnozElementy(x, Liczba.POL);
								//return new Liczba(simpgammalower(x.getValueAsDouble() * 0.5, k.getValueAsDouble() * 0.5) / simpgamma(k.getValueAsDouble() * 0.5));
								return (Liczba) Znak.podzielElementy(gammalower.getValue(polx, polk), gamma.getValue(polk));
								 */

								Liczba polx = (Liczba) Znak.pomnozElementy(x, Liczba.POL);
								return (Liczba) Znak.podzielElementy(gammalower.getValue(polk, polx), gamma.getValue(polk));
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case CHIKWADRATTAIL:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //k,x
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								//return (Liczba) Znak.odejmijElementy(Liczba.JEDEN, Funkcja.getValue(Functions.FCHIKWADRAT, args[0], args[1]));
								Liczba k = (Liczba) args[0];
								Liczba x = (Liczba) args[1];
								if (k.compareTo(Liczba.ZERO) < 0 || x.compareTo(Liczba.ZERO) < 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - argumenty nie mogą być ujemne");
								}
								Funkcja fchi = new Funkcja(Functions.FCHIKWADRAT);
								return (Liczba) Znak.odejmijElementy(Liczba.JEDEN, fchi.getValue(args));
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case STUDENTA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //k,x
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								Liczba k = (Liczba) args[0];
								Liczba x = (Liczba) args[1];
								if (k.compareTo(Liczba.ZERO) <= 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - pierwszy argument musi być większy od zera");
								}
								/* wg wzoru http://mathworld.wolfram.com/Studentst-Distribution.html */
								Funkcja gamma = new Funkcja(Functions.GAMMA);
								Liczba l1 = (Liczba) gamma.getValue(Znak.pomnozElementy(Liczba.POL, Znak.dodajElementy(Liczba.JEDEN, k))); // gamma((k+1)/2)
								Liczba l2 = (Liczba) Funkcja.getValue(Functions.SQRT, Znak.pomnozElementy(Funkcja.getValue(Functions.PI), k)); // sqrt(pi*k)
								Liczba l3 = (Liczba) gamma.getValue(Znak.pomnozElementy(Liczba.POL, k)); // gamma(k/2)
								Liczba l4 = (Liczba) Znak.dodajElementy(Liczba.JEDEN, Znak.podzielElementy(Znak.pomnozElementy(x, x), k)); // 1+x^2/k
								Liczba l5 = (Liczba) Znak.pomnozElementy(Znak.przeciwnyElement(Liczba.POL), Znak.dodajElementy(Liczba.JEDEN, k)); // -((k+1)/2)
								Liczba result = (Liczba) Znak.podzielElementy(
										Znak.pomnozElementy(
										l1,
										Funkcja.getValue(Functions.POW,
										l4,
										l5)),
										Znak.pomnozElementy(l2, l3));
								return result;
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case FSTUDENTA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //k,x
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								Liczba k = (Liczba) args[0];
								Liczba x = (Liczba) args[1];
								if (k.compareTo(Liczba.ZERO) <= 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - pierwszy argument musi być większy od zera");
								}
								/* wg wzoru http://mathworld.wolfram.com/Studentst-Distribution.html */
								Funkcja betareg = new Funkcja(Functions.BETAREG);
								Liczba polk = (Liczba) Znak.pomnozElementy(Liczba.POL, k);
								Liczba l1 = (Liczba) betareg.getValue(Liczba.JEDEN, polk, Liczba.POL);
								Liczba l2 = (Liczba) betareg.getValue(Znak.podzielElementy(k, Znak.dodajElementy(k, Znak.pomnozElementy(x, x))), polk, Liczba.POL);
								Liczba l3 = (Liczba) Znak.odejmijElementy(l1, l2);
								Liczba result = (Liczba) Znak.dodajElementy(Liczba.POL, Znak.pomnozElementy(Liczba.POL, Znak.pomnozElementy(Funkcja.getValue(Functions.SGN, x), l3)));
								return result;


							/* wg JSci:
							Funkcja betaincmpl = new Funkcja(Functions.BETAINCMPL);
							Liczba l1 = (Liczba) Znak.pomnozElementy(Liczba.POL,
							betaincmpl.getValue(Liczba.ZERO, Znak.podzielElementy(k, Znak.dodajElementy(k, Znak.pomnozElementy(x, x))), Znak.pomnozElementy(Liczba.POL, k), Liczba.POL));
							//double A=0.5*SpecialMath.incompleteBeta((dgrFreedom)/(dgrFreedom+X*X),0.5*dgrFreedom,0.5);return X>0 ? 1-A : A;
							return l1;
							 */
							/* // z pliku studenttdistr.cpp:
							 * niedokończone!
							Funkcja gamma = new Funkcja(Functions.GAMMA);
							Liczba l1 = (Liczba) gamma.getValue(Znak.pomnozElementy(Liczba.POL, Znak.dodajElementy(Liczba.JEDEN, k)));
							Liczba pi = (Liczba) Funkcja.getValue(Functions.PI);
							Liczba l2 = (Liczba) Funkcja.getValue(Functions.SQRT, Znak.pomnozElementy(k, pi));
							Liczba l3 = (Liczba) gamma.getValue(Znak.pomnozElementy(Liczba.POL, k));
							Liczba result = (Liczba) Znak.podzielElementy(l1, Znak.pomnozElementy(l2, l3));
							return result;
							 */
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case STUDENTATAIL:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //k,p
							return 2;
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								Liczba k = (Liczba) args[0];
								Liczba p = (Liczba) args[1];
								if (k.compareTo(Liczba.ZERO) <= 0 || p.compareTo(Liczba.ZERO) < 0 || p.compareTo(Liczba.JEDEN) > 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - pierwszy argument musi być większy od zera, drugi musi zawierać się w przedziale <0; 1>");
								}
								Funkcja fstudenta = new Funkcja(Functions.FSTUDENTA);
								//(1-fstudenta(26;2.056))*2 = 0.05
								//fstudenta(26;2.056) = 1-0.05/2
								//dzieli na pół, bo dwustronny!
								Liczba p2 = (Liczba) Znak.odejmijElementy(Liczba.JEDEN, Znak.pomnozElementy(Liczba.POL, p));
								Liczba low = new Liczba("-10"), high = new Liczba("10"), mid = new Liczba("0");
								Liczba eps = new Liczba("0,0000000000001");
								Liczba result;
								result = ZlotyPodzial.znajdzX(p2, low, high, eps, new Liczba(10), fstudenta, 1, args);
								return result;
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case SNEDECORA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; //r1, r2, x
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba && args[2] instanceof Liczba) {
								try {
									Liczba r1 = (Liczba) args[0];
									Liczba r2 = (Liczba) args[1];
									Liczba x = (Liczba) args[2];
									if (r1.compareTo(Liczba.ZERO) < 0 || r2.compareTo(Liczba.ZERO) < 0) {
										throw new InvalidArgumentException(getFunctionInfo(this, args) + " - stopnie swobody muszą być dodatnie");
									}
									if (x.compareTo(Liczba.ZERO) <= 0) {
										return Liczba.ZERO;
									}
									if (r1.compareTo(r2) > 0) {
									//Liczba t = r1; r1 = r2; r2 = t;
									}
									// http://pl.wikipedia.org/wiki/Rozk%C5%82ad_F_Snedecora - po prawej: gęstość prawdopodobieństwa
									Liczba r1x = (Liczba) Znak.pomnozElementy(r1, x);
									Liczba r1pol = (Liczba) Znak.pomnozElementy(r1, Liczba.POL);
									Liczba r2pol = (Liczba) Znak.pomnozElementy(r2, Liczba.POL);
									Funkcja beta = new Funkcja(Functions.BETA);
									Funkcja pow = new Funkcja(Functions.POW);
									Funkcja sqrt = new Funkcja(Functions.SQRT);
									Liczba l1 = (Liczba) pow.getValue(r1x, r1);
									Liczba l2 = (Liczba) pow.getValue(r2, r2);
									Liczba l3 = (Liczba) pow.getValue(Znak.dodajElementy(r1x, r2), Znak.dodajElementy(r1, r2));
									Liczba licznik = (Liczba) sqrt.getValue(Znak.podzielElementy(Znak.pomnozElementy(l1, l2), l3));
									Liczba mianownik = (Liczba) Znak.pomnozElementy(x, beta.getValue(r1pol, r2pol));
									return (Liczba) Znak.podzielElementy(licznik, mianownik);
								} catch (InvalidArgumentException eex) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - " + eex.getMessage());
								}
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case FSNEDECORA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; //r1, r2, x
						}

						public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba && args[2] instanceof Liczba) {
								try {
									Funkcja betareg = new Funkcja(Functions.BETAREG);
									Liczba r1 = (Liczba) args[0];
									Liczba r2 = (Liczba) args[1];
									Liczba x = (Liczba) args[2];
									if (r1.compareTo(Liczba.ZERO) < 0 || r2.compareTo(Liczba.ZERO) < 0) {
										throw new InvalidArgumentException(getFunctionInfo(this, args) + " - stopnie swobody muszą być dodatnie");
									}
									if (x.compareTo(Liczba.ZERO) <= 0) {
										return Liczba.ZERO;
									}
									// http://en.wikipedia.org/wiki/Snedecor%27s_F-distribution
									if (r1.compareTo(r2) > 0) {
									//Liczba t = r1; r1 = r2; r2 = t;
									}
									Liczba r1x = (Liczba) Znak.pomnozElementy(r1, x);
									Liczba lx = (Liczba) Znak.podzielElementy(r1x, Znak.dodajElementy(r1x, r2));

									return (Liczba) betareg.getValue(lx, Znak.pomnozElementy(Liczba.POL, r1), Znak.pomnozElementy(Liczba.POL, r2));
								} catch (InvalidArgumentException eex) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - " + eex.getMessage());
								}
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case SNEDECORATAIL:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() { //k,p
							return 3; //r1, r2, p
						}

						public ElementWyrazenia getValue(
								ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								Liczba r1 = (Liczba) args[0];
								Liczba r2 = (Liczba) args[1];
								Liczba p = (Liczba) args[2];
								if (r1.compareTo(Liczba.ZERO) < 0 || r2.compareTo(Liczba.ZERO) < 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - stopnie swobody muszą być dodatnie");
								}
								if (p.compareTo(Liczba.ZERO) < 0 || p.compareTo(Liczba.JEDEN) > 0) {
									throw new InvalidArgumentException(getFunctionInfo(this, args) + " - trzeci argument musi zawierać się w przedziale <0; 1>");
								}
								Funkcja fsnedecora = new Funkcja(Functions.FSNEDECORA);
								//(1-fstudenta(26;2.056))*2 = 0.05
								//fstudenta(26;2.056) = 1-0.05/2
								Liczba p2 = (Liczba) Znak.odejmijElementy(Liczba.JEDEN, p);
								Liczba low = new Liczba("0"), mid = new Liczba("0"),
										high = new Liczba("10"); // jak wypada poza przedział, korygowane jest o 100 w górę
								//high = new Liczba(Math.pow(10, 18));

								Liczba eps = new Liczba("0,0000000000001");
								Liczba result;
								//WyrazeniePostfix f = new WyrazeniePostfix(new ElementWyrazenia[] {new Funkcja("X", funkcjaX), r2, r1, new Funkcja(Functions.FSNEDECORA)}, funkcjaX);
								//result = ZlotyPodzial.znajdzX(f, p2, low, high, eps, new Liczba(100));
								Funkcja f = new Funkcja(Functions.FSNEDECORA);
								result = ZlotyPodzial.znajdzX(p2, low, high, eps, new Liczba(100), f, 2, args);
								return result;
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////


				case WSPOLCZYNNIKKORELACJILINIOWEJ:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //macierz_y, macierz_x
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Macierz) {
								return Regresja.wspolczynnikKorelacjiLiniowej((Macierz) args[0], (Macierz) args[1]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case KOWARIANCJA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //macierz_y, macierz_x
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Macierz) {
								return Regresja.kowariancja((Macierz) args[0], (Macierz) args[1]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case MACIERZKORELACJI:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1; //macierz
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Macierz) {
								return Regresja.macierzKorelacji((Macierz) args[0]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case WEKTORKORELACJI:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //macierz_y, macierz_x
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Macierz) {
								return Regresja.wektorKorelacji((Macierz) args[0], (Macierz) args[1]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ROZSZERZONAMACIERZKORELACJI:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //macierz_y, macierz_x
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Macierz) {
								return Regresja.rozszerzonaMacierzKorelacji((Macierz) args[0], (Macierz) args[1]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case WSPOLCZYNNIKKORELACJIWIELORAKIEJ:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //macierzKorelacji, rozszerzonaMacierzKorelacji
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Macierz) {
								return Regresja.wspolczynnikKorelacjiWielorakiej((Macierz) args[0], (Macierz) args[1]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case WSPOLCZYNNIKA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //macierz_y, macierz_x
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Macierz) {
								return Regresja.wspolczynnikA((Macierz) args[0], (Macierz) args[1]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case DODAJWYRAZWOLNY:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1; //macierz_x
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz) {
								return Regresja.dodajWyrazWolny((Macierz) args[0]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case PARAMETRYALFA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; //macierz_y, macierz_x, wyraz_wolny
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Macierz && args[2] instanceof Liczba) {
								return Regresja.parametryAlfa((Macierz) args[0], (Macierz) args[1], ((Liczba) args[2]).equals(Liczba.JEDEN));
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case WSPOLCZYNNIKB:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //macierz_y, macierz_x
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Macierz) {
								return Regresja.wspolczynnikB((Macierz) args[0], (Macierz) args[1]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case OBLICZTEORETYCZNE:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //macierz_x, parametry_alfa
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Macierz) {
								return Regresja.obliczTeoretyczne((Macierz) args[0], (Macierz) args[1]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case SUMAKWADRATOW:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1; //macierz
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz) {
								return Regresja.sumaKwadratow((Macierz) args[0]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case WSPOLCZYNNIKZBIEZNOSCIFI2:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //macierz_y, macierz_y_teoretyczne
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Macierz) {
								return Regresja.wspolczynnikZbieznosciFi2((Macierz) args[0], (Macierz) args[1]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case WSPOLCZYNNIKDETERMINACJIR2:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //macierz_y, macierz_y_teoretyczne
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Macierz) {
								return Regresja.wspolczynnikDeterminacjiR2((Macierz) args[0], (Macierz) args[1]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case WARIANCJASKLADNIKARESZTOWEGO:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; //macierz_y, macierz_y_teoretyczne, k
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Macierz && args[2] instanceof Liczba) {
								return Regresja.wariancjaSkladnikaResztowego((Macierz) args[0], (Macierz) args[1], ((Liczba) args[2]));
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ODCHYLENIESTANDARDOWERESZT:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; //macierz_y, macierz_y_teoretyczne, k
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Macierz && args[2] instanceof Liczba) {
								return Regresja.odchylenieStandardoweReszt((Macierz) args[0], (Macierz) args[1], ((Liczba) args[2]));
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case WARIANCJASKLADNIKALOSOWEGO:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; //macierz_y, macierz_y_teoretyczne, m
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Macierz && args[2] instanceof Liczba) {
								return Regresja.wariancjaSkladnikaLosowego((Macierz) args[0], (Macierz) args[1], ((Liczba) args[2]).getValueAsBigDecimal().intValueExact());
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case MACIERZWARIANCJIKOWARIANCJI:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //se2, macierz_x
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Macierz) {
								return Regresja.macierzWariancjiKowariancji((Liczba) args[0], (Macierz) args[1]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case STANDARDOWEBLEDYOCEN:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1; //macierz_wariancji_kowariancji
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz) {
								return Regresja.standardoweBledyOcen((Macierz) args[0]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case TESTWSPOLCZYNNIKAKORELACJI_STATYSTYKA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //macierz_r, n
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Liczba) {
								return Regresja.testWspolczynnikaKorelacji_sprawdzianHipotezy((Macierz) args[0], ((Liczba) args[1]).getValueAsBigDecimal().intValueExact());
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case TESTWSPOLCZYNNIKAKORELACJI_KRYTYCZNA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; // n, alfa
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								return Regresja.testWspolczynnikaKorelacji_wartoscKrytyczna(((Liczba) args[0]).getValueAsBigDecimal().intValueExact(), ((Liczba) args[1]).getValueAsDouble());
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case TESTWSPOLCZYNNIKAKORELACJI_TESTUJ:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //SPRAWDZIAN, WARTOSCKRYTYCZNA
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Liczba) {
								return Regresja.testWspolczynnikaKorelacji_testuj((Macierz) args[0], (Liczba) args[1]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case DOBORZMIENNYCHMETODAANALIZYWSPOLCZYNNIKOWKORELACJI_KRYTYCZNA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; // n, alfa
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								return Regresja.doborZmiennychMetodaAnalizyWspolczynnikowKorelacji_wartoscKrytyczna(((Liczba) args[0]).getValueAsBigDecimal().intValueExact(), ((Liczba) args[1]).getValueAsDouble());
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case DOBORZMIENNYCHMETODAANALIZYWSPOLCZYNNIKOWKORELACJI_DOBIERZ:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //MACIERZ_R, LICZBA WARTOSCKRYTYCZNA
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Liczba) {
								return Regresja.doborZmiennychMetodaAnalizyWspolczynnikowKorelacji_dobierz((Macierz) args[0], (Liczba) args[1]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case DOBORZMIENNYCHMETODAWSKAZNIKOWPOJEMNOSCIINFORMACJI_DOBIERZ:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1; //MACIERZ_R
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz) {
								return Regresja.doborZmiennychMetodaWskaznikowPojemnosciInformacji_dobierz((Macierz) args[0]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ISTOTNOSCPARAMETROWSTRUKTURALNYCH_STATYSTKA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; // INT N, INT M, LICZBA WSPR2
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba && args[2] instanceof Liczba) {
								return Regresja.istotnoscParametrowStrukturalnych_statystka(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										((Liczba) args[1]).getValueAsBigDecimal().intValueExact(),
										(Liczba) args[2]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ISTOTNOSCPARAMETROWSTRUKTURALNYCH_KRYTYCZNA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; // INT N, INT M, DOUBLE ALFA
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba && args[2] instanceof Liczba) {
								return Regresja.istotnoscParametrowStrukturalnych_wartoscKrytyczna(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										((Liczba) args[1]).getValueAsBigDecimal().intValueExact(),
										((Liczba) args[2]).getValueAsDouble());
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ISTOTNOSCPARAMETROWSTRUKTURALNYCH_TESTUJ:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //LICZBA STATYSTYKA, LICZBA KRYTYCZNA
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								if (Regresja.istotnoscParametrowStrukturalnych_testuj((Liczba) args[0], (Liczba) args[1]))
									return Liczba.JEDEN;
								else
									return Liczba.ZERO;
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ISTOTNOSCPOSZCZEGOLNYCHPARAMETROWSTRUKTURALNYCH_STATYSTYKA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //MACIERZ PARAMETRY_ALFA, MACIERZ BLEDY
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Macierz) {
								return Regresja.istotnoscPoszczegolnychParametrowStrukturalnych_statystyka((Macierz) args[0], (Macierz) args[1]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ISTOTNOSCPOSZCZEGOLNYCHPARAMETROWSTRUKTURALNYCH_KRYTYCZNA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; // INT N, INT M, DOUBLE ALFA
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba && args[2] instanceof Liczba) {
								return Regresja.istotnoscPoszczegolnychParametrowStrukturalnych_wartoscKrytyczna(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										((Liczba) args[1]).getValueAsBigDecimal().intValueExact(),
										((Liczba) args[2]).getValueAsDouble());
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ISTOTNOSCPOSZCZEGOLNYCHPARAMETROWSTRUKTURALNYCH_TESTUJ:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //MACIERZ sprawdzian, LICZBA WARTOSCKRYTYCZNA
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Liczba) {
								return Regresja.istotnoscPoszczegolnychParametrowStrukturalnych_testuj((Macierz) args[0], (Liczba) args[1]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ISTOTNOSCWEKTORAPARAMETROW_STATYSTYKA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 4; // w, w0, macierz_kowariancji, parametry_alfa
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz && args[1] instanceof Liczba &&
									args[2] instanceof Macierz && args[3] instanceof Macierz) {
								return Regresja.istotnoscWektoraParametrow_statystyka(
										(Macierz) args[0],
										(Liczba) args[1],
										(Macierz) args[2],
										(Macierz) args[3]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ISTOTNOSCWEKTORAPARAMETROW_KRYTYCZNA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; // INT N, INT M, DOUBLE ALFA
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba && args[2] instanceof Liczba) {
								return Regresja.istotnoscWektoraParametrow_wartoscKrytyczna(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										((Liczba) args[1]).getValueAsBigDecimal().intValueExact(),
										((Liczba) args[2]).getValueAsDouble());
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ISTOTNOSCWEKTORAPARAMETROW_TESTUJ:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 2; //Liczba statystyka, LICZBA WARTOSCKRYTYCZNA
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba) {
								if (Regresja.istotnoscWektoraParametrow_testuj((Liczba) args[0], (Liczba) args[1]))
									return Liczba.JEDEN;
								else
									return Liczba.ZERO;
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case SREDNIA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1; //macierz
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz) {
								return PodstawoweStatystyki.srednia((Macierz) args[0]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case SUMA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1; //macierz
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz) {
								return PodstawoweStatystyki.suma((Macierz) args[0]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ILOCZYN:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1; //macierz
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz) {
								return PodstawoweStatystyki.iloczyn((Macierz) args[0]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case MMAX:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1; //macierz
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz) {
								return PodstawoweStatystyki.max((Macierz) args[0]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case MMIN:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1; //macierz
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz) {
								return PodstawoweStatystyki.min((Macierz) args[0]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case KUMULUJ:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1; //macierz
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz) {
								return PodstawoweStatystyki.kumuluj((Macierz) args[0]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case SREDNIAARYTMETYCZNA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; //int typszeregu, Macierz macierz_xi, Macierz macierz_ni
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Macierz && args[2] instanceof Macierz) {
								return PodstawoweStatystyki.sredniaArytmetyczna(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										(Macierz) args[1],
										(Macierz) args[2]
										);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case SREDNIAGEOMETRYCZNA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; //int typszeregu, Macierz macierz_xi, Macierz macierz_ni
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Macierz && args[2] instanceof Macierz) {
								return PodstawoweStatystyki.sredniaGeometryczna(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										(Macierz) args[1],
										(Macierz) args[2]
										);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case CZESTOSCI:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; //int typszeregu, Macierz macierz_xi, Macierz macierz_ni
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Macierz && args[2] instanceof Macierz) {
								return PodstawoweStatystyki.czestosci(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										(Macierz) args[1],
										(Macierz) args[2]
										);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ODCHYLENIESTATNDARDOWE:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; //int typszeregu, Macierz macierz_xi, Macierz macierz_ni
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Macierz && args[2] instanceof Macierz) {
								return PodstawoweStatystyki.odchylenieStandardowe(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										(Macierz) args[1],
										(Macierz) args[2]
										);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case WARIANCJA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; //int typszeregu, Macierz macierz_xi, Macierz macierz_ni
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Macierz && args[2] instanceof Macierz) {
								return PodstawoweStatystyki.wariancja(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										(Macierz) args[1],
										(Macierz) args[2]
										);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case ODCHYLENIEPRZECIETNE:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; //int typszeregu, Macierz macierz_xi, Macierz macierz_ni
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Macierz && args[2] instanceof Macierz) {
								return PodstawoweStatystyki.odchyleniePrzecietne(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										(Macierz) args[1],
										(Macierz) args[2]
										);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case WSPOLCZYNNIKZMIENNOSCIVS:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; //int typszeregu, Macierz macierz_xi, Macierz macierz_ni
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Macierz && args[2] instanceof Macierz) {
								return PodstawoweStatystyki.wspolczynnikZmiennosciVs(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										(Macierz) args[1],
										(Macierz) args[2]
										);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case WSPOLCZYNNIKZMIENNOSCIVD:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; //int typszeregu, Macierz macierz_xi, Macierz macierz_ni
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Macierz && args[2] instanceof Macierz) {
								return PodstawoweStatystyki.wspolczynnikZmiennosciVd(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										(Macierz) args[1],
										(Macierz) args[2]
										);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case MODALNA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 5; //int typszeregu, Macierz macierz_xi, Macierz macierz_xi_poczatek, Macierz macierz_xi_koniec, Macierz macierz_ni
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Macierz && args[2] instanceof Macierz) {
								return PodstawoweStatystyki.modalna(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										(Macierz) args[1],
										(Macierz) args[2],
										(Macierz) args[3],
										(Macierz) args[4]
										);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case MEDIANA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 5; //int typszeregu, Macierz macierz_xi, Macierz macierz_xi_poczatek, Macierz macierz_xi_koniec, Macierz macierz_ni
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Macierz && args[2] instanceof Macierz) {
								return PodstawoweStatystyki.mediana(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										(Macierz) args[1],
										(Macierz) args[2],
										(Macierz) args[3],
										(Macierz) args[4]
										);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case KWARTYL:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 6; //int kwartyl, int typszeregu, Macierz macierz_xi, Macierz macierz_xi_poczatek, Macierz macierz_xi_koniec, Macierz macierz_ni
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Liczba &&
									args[3] instanceof Macierz && args[3] instanceof Macierz && args[4] instanceof Macierz && args[5] instanceof Macierz) {
								return PodstawoweStatystyki.kwartyl(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										((Liczba) args[1]).getValueAsBigDecimal().intValueExact(),
										(Macierz) args[2],
										(Macierz) args[3],
										(Macierz) args[4],
										(Macierz) args[5]
										);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case WSPOLCZYNNIKSKOSNOSCIAD:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 5; //int typszeregu, Macierz macierz_xi, Macierz macierz_xi_poczatek, Macierz macierz_xi_koniec, Macierz macierz_ni
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Macierz && args[2] instanceof Macierz) {
								return PodstawoweStatystyki.wspolczynnikSkosnosciAd(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										(Macierz) args[1],
										(Macierz) args[2],
										(Macierz) args[3],
										(Macierz) args[4]
										);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case WSPOLCZYNNIKSKOSNOSCIAS:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 5; //int typszeregu, Macierz macierz_xi, Macierz macierz_xi_poczatek, Macierz macierz_xi_koniec, Macierz macierz_ni
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Macierz && args[2] instanceof Macierz) {
								return PodstawoweStatystyki.wspolczynnikSkosnosciAs(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										(Macierz) args[1],
										(Macierz) args[2],
										(Macierz) args[3],
										(Macierz) args[4]
										);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case WSPOLCZYNNIKASYMETRIIA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; //int typszeregu, Macierz macierz_xi, Macierz macierz_ni
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Macierz && args[2] instanceof Macierz) {
								return PodstawoweStatystyki.wspolczynnikAsymetriiA(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										(Macierz) args[1],
										(Macierz) args[2]
										);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case WSPOLCZYNNIKSKUPIENIAK:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 3; //int typszeregu, Macierz macierz_xi, Macierz macierz_ni
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Liczba && args[1] instanceof Macierz && args[2] instanceof Macierz) {
								return PodstawoweStatystyki.wspolczynnikSkupieniaK(
										((Liczba) args[0]).getValueAsBigDecimal().intValueExact(),
										(Macierz) args[1],
										(Macierz) args[2]
										);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;

				case TESTDURBINAWATSONA_STATYSTYKA:
					arrayOfFunctions[i.ordinal()] = new FunkcjaInterface() {

						public int getArgumentsCount() {
							return 1; //  Macierz RESZTY
						}

						public ElementWyrazenia getValue( ElementWyrazenia... args) throws InvalidArgumentException {
							if (args[0] instanceof Macierz) {
								return kalkulator.Regresja.testDurbinaWatsona_statystyka(
										(Macierz) args[0]);
							}
							throw new InvalidArgumentException(getFunctionInfo(this, args));
						}
					};
					break;




			} // switch

		} // for
	} // static
	//public static znajdzX()
	/**
	 * Stała mająca za zadanie symulować zmienną X w obliczeniach, gdzie potem i tak jest zmieniana.
	 * TODO: poprawić opis, wykorzystywane w liczeniu całek (zmieniamy potem X).
	 */
	public static final FunkcjaInterface funkcjaX = new FunkcjaInterface() {

		public int getArgumentsCount() {
			return 0;
		}

		public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
			return Liczba.ZERO;
		}

		public String toString() {
			return "X";
		}
	};

	public static String getFunctionInfo(
			FunkcjaInterface f, ElementWyrazenia... args) {
		final String argumentsInfo = ElementWyrazenia.getArgumentsInfo(args);
		for (int i = 0; i <
				arrayOfFunctions.length; i++) {
			if (arrayOfFunctions[i] == f) {
				for (Functions fi : Functions.values()) {
					if (fi.ordinal() == i) {
						return fi.toString() +
								((argumentsInfo != null) ? "" + argumentsInfo + "" : "");
					}

				}
			}
		}
		return f.toString() +
				((argumentsInfo != null) ? "" + argumentsInfo + "" : "");
	}

	/**
	 * Funkcja gamma liczona jako całka od <code>low</code> do <code>up</code>.
	 */
	private static double funkcjagamma(double low, double up, double m) {
		if (up < 1000) { // gammalower
			return funkcjagamma_incompletelower(up, m);
		}
		if (low > 0) {
			return funkcjagamma_lanczos(low) - funkcjagamma_incompletelower(low, m);
		}
		return funkcjagamma_lanczos(m);
	}

	/**
	 * Lanczos approximation for computing the Gamma function
	 * for any positive argument (indeed, for any complex argument
	 * with a nonnegative real part) to a high level of accuracy.
	 * http://www.rskey.org/gamma.htm
	 * @param low
	 * @param up
	 * @param m
	 * @return
	 */
	public static double funkcjagamma_lanczos(double z) {

		double[] p = new double[] {
			1.000000000190015,
			76.18009172947146,
			-86.50532032941677,
			24.01409824083091,
			-1.231739572450155,
			1.208650973866179 * 0.001,
			-5.395239384953 * 0.000001
		};

		double l1 = p[0];
		for (int n = 1; n < p.length; n++) {
			double t = p[n] / (z + n);
			l1 = t + l1;
		}
		double l2 = Math.sqrt(Math.PI * 2) / z;
		double l3 = Math.pow(z + 5.5, z + 0.5);
		double l4 = Math.exp(-(z + 5.5));
		return l3 * l4 * l2 * l1;
	}

	/**
	 * Całka od 0 do x
	 * @param a
	 * @param x
	 * @return
	 */
	public static double funkcjagamma_incompletelower(double a, double x) {
		double r = 0;
		double m = 1;
		for (int n = 0; n < 100; n++) {
			m *= (a + n);
			r += Math.pow(x, n) / m;
		}
		r *= Math.pow(x, a) * Math.exp(-x);
		return r;
	}

	private static double funkcjabeta(double low, double up, double z, double w) {

		if (low == 0 && up == 1) {
			return funkcjagamma_lanczos(z) / funkcjagamma_lanczos(w + z) * funkcjagamma_lanczos(w);
		}
		if (low < 0 || up > 1 || low >= up) {
			return Double.NaN;
		}
		if (low > 0) { // The upper incomplete beta function
			return funkcjabeta(0, 1, z, w) - //funkcjagamma_lanczos(z) / funkcjagamma_lanczos(w + z) * funkcjagamma_lanczos(w) -
					funkcjabeta(0, low, z, w);
		}
		if (up < 1) { // The lower incomplete beta function
			double r1 = Beta.incompletebeta(z, w, up);
			r1 *= funkcjabeta(0, 1, z, w);
			return r1;
		}
		return Double.NaN;
	}

	public static enum Functions {
		ABS, ARCCOS, ARCCTG, ARCSIN, ARCTG,
		BETA, BETAINCMPL, BETAREG,
		CBRT, CEIL, CHIKWADRAT, CHIKWADRATTAIL, COS, COSH, CSC, CTG, CTGH,
			CZESTOSCI,
		DET,
			DOBORZMIENNYCHMETODAANALIZYWSPOLCZYNNIKOWKORELACJI_DOBIERZ,
			DOBORZMIENNYCHMETODAANALIZYWSPOLCZYNNIKOWKORELACJI_KRYTYCZNA,
			DOBORZMIENNYCHMETODAWSKAZNIKOWPOJEMNOSCIINFORMACJI_DOBIERZ,
			DODAJWYRAZWOLNY, DWUMIANOWY,
		E, EXP,
		FACT, FDWUMIANOWY,FCHIKWADRAT,FGEOMETRYCZNY,FJEDNOSTAJNY,FNORMALNY,
			FNORMALNYSTD,FPOISSONA,FSNEDECORA,FSTUDENTA,FWYKLADNICZY,FZEROJEDYNKOWY, // FLOOR,
		GAMMA, GAMMAINCMPL, GAMMALOWER, GAMMAUPPER, GEOMETRYCZNY,//GRAM,
		HILBERT,
		HYPOT,
		ILOCZYN, INTEGRAL, INV,
			ISTOTNOSCPARAMETROWSTRUKTURALNYCH_KRYTYCZNA,
			ISTOTNOSCPARAMETROWSTRUKTURALNYCH_STATYSTKA,
			ISTOTNOSCPARAMETROWSTRUKTURALNYCH_TESTUJ,
			ISTOTNOSCPOSZCZEGOLNYCHPARAMETROWSTRUKTURALNYCH_KRYTYCZNA,
			ISTOTNOSCPOSZCZEGOLNYCHPARAMETROWSTRUKTURALNYCH_STATYSTYKA,
			ISTOTNOSCPOSZCZEGOLNYCHPARAMETROWSTRUKTURALNYCH_TESTUJ,
			ISTOTNOSCWEKTORAPARAMETROW_KRYTYCZNA,
			ISTOTNOSCWEKTORAPARAMETROW_STATYSTYKA,
			ISTOTNOSCWEKTORAPARAMETROW_TESTUJ,
		JEDNOSTAJNY, JEDNOSTKOWA,
		KOMBINACJE, KOMBINACJE2, KOWARIANCJA, KUMULUJ, KWARTYL,
		LN, LOG, LOG10,
		MACIERZKORELACJI, MACIERZWARIANCJIKOWARIANCJI, MAX, MEDIANA, MIN, MMAX, MMIN,
			MODALNA,
		NEGATE, NEWTON, NORMALNY,NORMALNYFI, NORMALNYSTD, NORMALNYSTDTAIL,
		OBLICZTEORETYCZNE, ODCHYLENIEPRZECIETNE, ODCHYLENIESTATNDARDOWE, ODCHYLENIESTANDARDOWERESZT,
		PARAMETRYALFA, PI,  PODMACIERZ,POISSONA,  POW, RANDOM, // RINT,
		ROUND, ROZSZERZONAMACIERZKORELACJI,
		SEC, SGN, SIN, SINH, SNEDECORA, SNEDECORATAIL, SQRT, SREDNIA,
			SREDNIAARYTMETYCZNA, SREDNIAGEOMETRYCZNA,
			STANDARDOWEBLEDYOCEN, STUDENTA,
			STUDENTATAIL, SUMA, SUMAKWADRATOW,
		TEKST, TESTDURBINAWATSONA_STATYSTYKA,
			TESTWSPOLCZYNNIKAKORELACJI_STATYSTYKA, TESTWSPOLCZYNNIKAKORELACJI_TESTUJ,
			TESTWSPOLCZYNNIKAKORELACJI_KRYTYCZNA,
			TG, TGH, TODEGREES,
			TOFLOAT, TOMATRIX,
			TORADIANS,
			TRANSP,
		ULP,
		WARIANCJA, WARIANCJASKLADNIKALOSOWEGO, WARIANCJASKLADNIKARESZTOWEGO,
			WARIANCJE, WARIANCJE2, WEKTORKORELACJI, WSPOLCZYNNIKA,
			WSPOLCZYNNIKASYMETRIIA, WSPOLCZYNNIKB,
			WSPOLCZYNNIKDETERMINACJIR2,
			WSPOLCZYNNIKKORELACJILINIOWEJ, WSPOLCZYNNIKKORELACJIWIELORAKIEJ,
			WSPOLCZYNNIKSKOSNOSCIAD, WSPOLCZYNNIKSKOSNOSCIAS,
			WSPOLCZYNNIKSKUPIENIAK,
			WSPOLCZYNNIKZBIEZNOSCIFI2, WSPOLCZYNNIKZMIENNOSCIVD,
			WSPOLCZYNNIKZMIENNOSCIVS, WYKLADNICZY,
		ZEROJEDYNKOWY,

	}
} // class


