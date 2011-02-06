/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package wykres;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

/**
 *
 * @author Damian
 */
public class Settings {

    public static Color   COLORBACKGROUND = new Color(0xFFFFFF), COLORBORDER = new Color(0xdddddd), COLORGRIDV = new Color(0xdddddd),
            COLORGRIDH = new Color(0xdddddd), COLORAXISOX = new Color(0x0000aa), COLORAXISOY = new Color(0x0000aa),
            COLORAXISOXINTERSECTION = new Color(0x0000bb), COLORAXISOYINTERSECTION = new Color(0x0000bb), COLORAXISOXTEXT = new Color(0x000000),
            COLORAXISOYTEXT = new Color(0x000000), COLORGRAPH = new Color(0xee0000);


    public static Boolean LIVESCROLLING = !true;

    public Boolean liveScrolling = !true;
    public Color   colorBackground = new Color(0xFFFFFF), colorBorder = new Color(0xdddddd), colorGridV = new Color(0xdddddd),
            colorGridH = new Color(0xdddddd), colorAxisOX = new Color(0x0000aa), colorAxisOY = new Color(0x0000aa),
            colorAxisOXIntersection = new Color(0x0000bb), colorAxisOYIntersection = new Color(0x0000bb), colorAxisOXText = new Color(0x000000),
            colorAxisOYText = new Color(0x000000), colorGraph = new Color(0xee0000);
    private String readOnly = "Read-only text";
    private String changeable = "Change that text!";
    private Integer mójInteger = new Integer(51);
    private Double jakisDouble = null;

    public Settings() {
        this.addVetoableChangeListener(new VetoableChangeListener() {
            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                if (evt.getPropertyName().equals("vetoableOver100")) {
                    if ((Integer)evt.getNewValue() > 100) {
                        throw new PropertyVetoException("vetoableOver100 > 100", evt);
                    }
                } else
                if (evt.getPropertyName().equals("vetoableInteger")) {
                    if ((Integer)evt.getNewValue() > 200) {
                        throw new PropertyVetoException("vetoableInteger > 200", evt);
                    }
                } else
                if (evt.getPropertyName().equals("tableShortGreater10")) {
                    for (short i : (short[])evt.getNewValue()) {
                        if (i < 10) {
                            throw new PropertyVetoException("tableShortGreater10 < 10", evt);
                        }
                    }
                } else
                if (evt.getPropertyName().equals("vetoableString")) {
                    if (((String)evt.getNewValue()).length() < 3) {
                        throw new PropertyVetoException("vetoableString, minimalna długość to 3", evt);
                    }
                } else
                if (evt.getPropertyName().equals("fcja_vetoable")) {
                    String newname = ((kalkulator.Funkcja.Functions)evt.getNewValue()).name();
                    if (newname.length() != 3) {
                        throw new PropertyVetoException("fcja_vetoable, długość nazwy musi być równa 3", evt);
                    }
                }


            }
        });
        //
    }
    /**
     * @return wartość colorAxisOX
     */
    public Color getColorAxisOX() {
        return colorAxisOX;
    }
    /**
     * @param colorAxisOX wartość colorAxisOX do ustawienia
     */
    public void setColorAxisOX(Color colorAxisOX) {
        this.colorAxisOX = colorAxisOX;
    }
    /**
     * @return wartość colorAxisOXIntersection
     */
    public Color getColorAxisOXIntersection() {
        return colorAxisOXIntersection;
    }
    /**
     * @param colorAxisOXIntersection wartość colorAxisOXIntersection do ustawienia
     */
    public void setColorAxisOXIntersection(Color colorAxisOXIntersection) {
        this.colorAxisOXIntersection = colorAxisOXIntersection;
    }
    /**
     * @return wartość colorAxisOXText
     */
    public Color getColorAxisOXText() {
        return colorAxisOXText;
    }
    /**
     * @param colorAxisOXText wartość colorAxisOXText do ustawienia
     */
    public void setColorAxisOXText(Color colorAxisOXText) {
        this.colorAxisOXText = colorAxisOXText;
    }
    /**
     * @return wartość colorAxisOY
     */
    public Color getColorAxisOY() {
        return colorAxisOY;
    }
    /**
     * @param colorAxisOY wartość colorAxisOY do ustawienia
     */
    public void setColorAxisOY(Color colorAxisOY) {
        this.colorAxisOY = colorAxisOY;
    }
    /**
     * @return wartość colorAxisOYIntersection
     */
    public Color getColorAxisOYIntersection() {
        return colorAxisOYIntersection;
    }
    /**
     * @param colorAxisOYIntersection wartość colorAxisOYIntersection do ustawienia
     */
    public void setColorAxisOYIntersection(Color colorAxisOYIntersection) {
        this.colorAxisOYIntersection = colorAxisOYIntersection;
    }
    /**
     * @return wartość colorAxisOYText
     */
    public Color getColorAxisOYText() {
        return colorAxisOYText;
    }
    /**
     * @param colorAxisOYText wartość colorAxisOYText do ustawienia
     */
    public void setColorAxisOYText(Color colorAxisOYText) {
        this.colorAxisOYText = colorAxisOYText;
    }
    /**
     * @return wartość colorBackground
     */
    public Color getColorBackground() {
        return colorBackground;
    }
    /**
     * @param colorBackground wartość colorBackground do ustawienia
     */
    public void setColorBackground(Color colorBackground) {
        this.colorBackground = colorBackground;
    }
    /**
     * @return wartość colorBorder
     */
    public java.awt.Color getColorBorder() {
        return colorBorder;
    }
    /**
     * @param colorBorder wartość colorBorder do ustawienia
     */
    public void setColorBorder(java.awt.Color colorBorder) {
        this.colorBorder = colorBorder;
    }
    /**
     * @return wartość colorGraph
     */
    public Color getColorGraph() {
        return colorGraph;
    }
    /**
     * @param colorGraph wartość colorGraph do ustawienia
     */
    public void setColorGraph(Color colorGraph) {
        this.colorGraph = colorGraph;
    }
    /**
     * @return wartość colorGridH
     */
    public Color getColorGridH() {
        return colorGridH;
    }
    /**
     * @param colorGridH wartość colorGridH do ustawienia
     */
    public void setColorGridH(Color colorGridH) {
        this.colorGridH = colorGridH;
    }
    /**
     * @return wartość colorGridV
     */
    public Color getColorGridV() {
        return colorGridV;
    }
    /**
     * @param colorGridV wartość colorGridV do ustawienia
     */
    public void setColorGridV(Color colorGridV) {
        this.colorGridV = colorGridV;
    }
    /**
     * @return wartość liveScrolling
     */
    public Boolean getLiveScrolling() {
        return liveScrolling;
    }
    /**
     * @param liveScrolling wartość liveScrolling do ustawienia
     */
    public void setLiveScrolling(Boolean liveScrolling) {
        this.liveScrolling = liveScrolling;
    }

    public void scramble() {
        liveScrolling = !liveScrolling;
        colorGraph = new Color(100,100,100);
    }

    public String getReadOnly() {
        return readOnly;
    }

    public java.lang.String getChangeable() {
        return changeable;
    }

    public void setChangeable(java.lang.String changeable) {
        this.changeable = changeable;
    }

    public Integer getMójInteger() {
        return mójInteger;
    }

    public void setMójInteger(Integer mójInteger) {
        this.mójInteger = mójInteger;
    }

    public Double getJakisDouble() {
        return jakisDouble;
    }

    public void setJakisDouble(Double jakisDouble) {
        this.jakisDouble = jakisDouble;
    }






    /**
     * Holds value of property vetoableOver100.
     */
    private int vetoableOver100;

    /**
     * Utility field used by bound properties.
     */
    private java.beans.PropertyChangeSupport propertyChangeSupport =  new java.beans.PropertyChangeSupport(this);

    /**
     * Utility field used by constrained properties.
     */
    private java.beans.VetoableChangeSupport vetoableChangeSupport =  new java.beans.VetoableChangeSupport(this);

    /**
     * Adds a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    /**
     * Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }

    /**
     * Adds a VetoableChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addVetoableChangeListener(java.beans.VetoableChangeListener l) {
        vetoableChangeSupport.addVetoableChangeListener(l);
    }

    /**
     * Removes a VetoableChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removeVetoableChangeListener(java.beans.VetoableChangeListener l) {
        vetoableChangeSupport.removeVetoableChangeListener(l);
    }

    /**
     * Getter for property vetoableOver100.
     * @return Value of property vetoableOver100.
     */
    public int getVetoableOver100() {
        return this.vetoableOver100;
    }

    /**
     * Setter for property vetoableOver100.
     * @param vetoableOver100 New value of property vetoableOver100.
     *
     * @throws PropertyVetoException if some vetoable listeners reject the new value
     */
    public void setVetoableOver100(int vetoableOver100) throws java.beans.PropertyVetoException {
        int oldVetoableOver100 = this.vetoableOver100;
        vetoableChangeSupport.fireVetoableChange("vetoableOver100", new Integer(oldVetoableOver100), new Integer(vetoableOver100));
        this.vetoableOver100 = vetoableOver100;
        propertyChangeSupport.firePropertyChange("vetoableOver100", new Integer(oldVetoableOver100), new Integer(vetoableOver100));
    }

    /**
     * Holds value of property vetoableInteger.
     */
    private Integer vetoableInteger = 199;

    /**
     * Getter for property vetoableInteger.
     * @return Value of property vetoableInteger.
     */
    public Integer getVetoableInteger() {
        return this.vetoableInteger;
    }

    /**
     * Setter for property vetoableInteger.
     * @param vetoableInteger New value of property vetoableInteger.
     *
     * @throws PropertyVetoException if some vetoable listeners reject the new value
     */
    public void setVetoableInteger(Integer vetoableInteger) throws java.beans.PropertyVetoException {
        Integer oldVetoableInteger = this.vetoableInteger;
        vetoableChangeSupport.fireVetoableChange("vetoableInteger", oldVetoableInteger, vetoableInteger);
        this.vetoableInteger = vetoableInteger;
        propertyChangeSupport.firePropertyChange("vetoableInteger", oldVetoableInteger, vetoableInteger);
    }

    /**
     * Holds value of property tableShortGreater10.
     */
    private short[] tableShortGreater10 = new short[] { 11, 12, 13 };

    /**
     * Indexed getter for property tableShortGreater10.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public short getTableShortGreater10(int index) {
        return this.tableShortGreater10[index];
    }

    /**
     * Getter for property tableShortGreater10.
     * @return Value of property tableShortGreater10.
     */
    public short[] getTableShortGreater10() {
        return this.tableShortGreater10;
    }

    /**
     * Indexed setter for property tableShortGreater10.
     * @param index Index of the property.
     * @param tableShortGreater10 New value of the property at <CODE>index</CODE>.
     *
     * @throws PropertyVetoException if some vetoable listeners reject the new value
     */
    public void setTableShortGreater10(int index, short tableShortGreater10) throws java.beans.PropertyVetoException {
        short oldTableShortGreater10 = this.tableShortGreater10[index];
        this.tableShortGreater10[index] = tableShortGreater10;
        try {
            vetoableChangeSupport.fireVetoableChange("tableShortGreater10", null, null );
        } catch(java.beans.PropertyVetoException vetoException ) {
            this.tableShortGreater10[index] = oldTableShortGreater10;
            throw vetoException;
        }
        propertyChangeSupport.firePropertyChange("tableShortGreater10", null, null );
    }

    /**
     * Setter for property tableShortGreater10.
     * @param tableShortGreater10 New value of property tableShortGreater10.
     *
     * @throws PropertyVetoException if some vetoable listeners reject the new value
     */
    public void setTableShortGreater10(short[] tableShortGreater10) throws java.beans.PropertyVetoException {
        short[] oldTableShortGreater10 = this.tableShortGreater10;
        vetoableChangeSupport.fireVetoableChange("tableShortGreater10", oldTableShortGreater10, tableShortGreater10);
        this.tableShortGreater10 = tableShortGreater10;
        propertyChangeSupport.firePropertyChange("tableShortGreater10", oldTableShortGreater10, tableShortGreater10);
    }

    /**
     * Holds value of property tableInteger.
     */
    private Integer[] tableInteger = new Integer[] {3 , 241, 123123 , -12222};

    /**
     * Indexed getter for property tableInteger.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public Integer getTableInteger(int index) {
        return this.tableInteger[index];
    }

    /**
     * Getter for property tableInteger.
     * @return Value of property tableInteger.
     */
    public Integer[] getTableInteger() {
        return this.tableInteger;
    }

    /**
     * Indexed setter for property tableInteger.
     * @param index Index of the property.
     * @param tableInteger New value of the property at <CODE>index</CODE>.
     */
    public void setTableInteger(int index, Integer tableInteger) {
        this.tableInteger[index] = tableInteger;
    }

    /**
     * Setter for property tableInteger.
     * @param tableInteger New value of property tableInteger.
     */
    public void setTableInteger(Integer[] tableInteger) {
        this.tableInteger = tableInteger;
    }

    /**
     * Holds value of property tableboolean.
     */
    private boolean[] tableboolean = new boolean[] { false, true };

    /**
     * Indexed getter for property tableboolean.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public boolean getTableboolean(int index) {
        return this.tableboolean[index];
    }

    /**
     * Getter for property tableboolean.
     * @return Value of property tableboolean.
     */
    public boolean[] getTableboolean() {
        return this.tableboolean;
    }

    /**
     * Indexed setter for property tableboolean.
     * @param index Index of the property.
     * @param tableboolean New value of the property at <CODE>index</CODE>.
     */
    public void setTableboolean(int index, boolean tableboolean) {
        this.tableboolean[index] = tableboolean;
    }

    /**
     * Setter for property tableboolean.
     * @param tableboolean New value of property tableboolean.
     */
    public void setTableboolean(boolean[] tableboolean) {
        this.tableboolean = tableboolean;
    }

    /**
     * Holds value of property vetoableString.
     */
    private String vetoableString = "min. długość to 3";

    /**
     * Getter for property vetoableString.
     * @return Value of property vetoableString.
     */
    public String getVetoableString() {
        return this.vetoableString;
    }

    /**
     * Setter for property vetoableString.
     * @param vetoableString New value of property vetoableString.
     *
     * @throws PropertyVetoException if some vetoable listeners reject the new value
     */
    public void setVetoableString(String vetoableString) throws java.beans.PropertyVetoException {
        String oldVetoableString = this.vetoableString;
        vetoableChangeSupport.fireVetoableChange("vetoableString", oldVetoableString, vetoableString);
        this.vetoableString = vetoableString;
        propertyChangeSupport.firePropertyChange ("vetoableString", oldVetoableString, vetoableString);
    }

    /**
     * Holds value of property chooseOnlyGrey.
     */
    private java.awt.Color chooseOnlyGrey = new Color(100, 100, 100);

    /**
     * Getter for property chooseOnlyGrey.
     * @return Value of property chooseOnlyGrey.
     */
    public java.awt.Color getChooseOnlyGrey() {
        return this.chooseOnlyGrey;
    }

    /**
     * Setter for property chooseOnlyGrey.
     * @param chooseOnlyGrey New value of property chooseOnlyGrey.
     *
     * @throws PropertyVetoException if some vetoable listeners reject the new value
     */
    public void setChooseOnlyGrey(java.awt.Color chooseOnlyGrey) throws java.beans.PropertyVetoException {
        java.awt.Color oldChooseOnlyGrey = this.chooseOnlyGrey;
        if (chooseOnlyGrey.getRed() != chooseOnlyGrey.getBlue() || chooseOnlyGrey.getRed() != chooseOnlyGrey.getGreen()) {
            throw new PropertyVetoException("Wybieraj tylko spośród szarości!", null);
        }
        vetoableChangeSupport.fireVetoableChange("chooseOnlyGrey", oldChooseOnlyGrey, chooseOnlyGrey);
        this.chooseOnlyGrey = chooseOnlyGrey;
        propertyChangeSupport.firePropertyChange ("chooseOnlyGrey", oldChooseOnlyGrey, chooseOnlyGrey);
    }

    /**
     * Holds value of property tableString.
     */
    private String[] tableString = new String[] {"jeden", "", "dwa"};

    /**
     * Indexed getter for property tableString.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public String getTableString(int index) {
        return this.tableString[index];
    }

    /**
     * Getter for property tableString.
     * @return Value of property tableString.
     */
    public String[] getTableString() {
        return this.tableString;
    }

    /**
     * Indexed setter for property tableString.
     * @param index Index of the property.
     * @param tableString New value of the property at <CODE>index</CODE>.
     */
    public void setTableString(int index, String tableString) {
        this.tableString[index] = tableString;
    }

    /**
     * Setter for property tableString.
     * @param tableString New value of property tableString.
     */
    public void setTableString(String[] tableString) {
        this.tableString = tableString;
    }

    /**
     * Holds value of property tableOf_floats.
     */
    private float[] tableOf_floats = new float[] {1};

    /**
     * Indexed getter for property tableOf_floats.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public float getTableOf_floats(int index) {
        return this.tableOf_floats[index];
    }

    /**
     * Getter for property tableOf_floats.
     * @return Value of property tableOf_floats.
     */
    public float[] getTableOf_floats() {
        return this.tableOf_floats;
    }

    /**
     * Indexed setter for property tableOf_floats.
     * @param index Index of the property.
     * @param tableOf_floats New value of the property at <CODE>index</CODE>.
     *
     * @throws PropertyVetoException if some vetoable listeners reject the new value
     */
    public void setTableOf_floats(int index, float tableOf_floats) throws java.beans.PropertyVetoException {
        float oldTableOf_floats = this.tableOf_floats[index];
        this.tableOf_floats[index] = tableOf_floats;
        try {
            vetoableChangeSupport.fireVetoableChange ("tableOf_floats", null, null );
        }
        catch(java.beans.PropertyVetoException vetoException ) {
            this.tableOf_floats[index] = oldTableOf_floats;
            throw vetoException;
        }
        propertyChangeSupport.firePropertyChange ("tableOf_floats", null, null );
    }

    /**
     * Setter for property tableOf_floats.
     * @param tableOf_floats New value of property tableOf_floats.
     *
     * @throws PropertyVetoException if some vetoable listeners reject the new value
     */
    public void setTableOf_floats(float[] tableOf_floats) throws java.beans.PropertyVetoException {
        float[] oldTableOf_floats = this.tableOf_floats;
        vetoableChangeSupport.fireVetoableChange("tableOf_floats", oldTableOf_floats, tableOf_floats);
        this.tableOf_floats = tableOf_floats;
        propertyChangeSupport.firePropertyChange ("tableOf_floats", oldTableOf_floats, tableOf_floats);
    }

    /**
     * Holds value of property tableOf_Floats.
     */
    private Float[] tableOf_Floats = new Float[] {1.1f};

    /**
     * Indexed getter for property tableOf_Floats.
     * @param index Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public Float getTableOf_Floats(int index) {
        return this.tableOf_Floats[index];
    }

    /**
     * Getter for property tableOf_Floats.
     * @return Value of property tableOf_Floats.
     */
    public Float[] getTableOf_Floats() {
        return this.tableOf_Floats;
    }

    /**
     * Indexed setter for property tableOf_Floats.
     * @param index Index of the property.
     * @param tableOf_Floats New value of the property at <CODE>index</CODE>.
     */
    public void setTableOf_Floats(int index, Float tableOf_Floats) {
        this.tableOf_Floats[index] = tableOf_Floats;
        propertyChangeSupport.firePropertyChange ("tableOf_Floats", null, null );
    }

    /**
     * Setter for property tableOf_Floats.
     * @param tableOf_Floats New value of property tableOf_Floats.
     */
    public void setTableOf_Floats(Float[] tableOf_Floats) {
        Float[] oldTableOf_Floats = this.tableOf_Floats;
        this.tableOf_Floats = tableOf_Floats;
        propertyChangeSupport.firePropertyChange ("tableOf_Floats", oldTableOf_Floats, tableOf_Floats);
    }

    /**
     * Holds value of property fcja.
     */
    private kalkulator.Funkcja.Functions fcja = kalkulator.Funkcja.Functions.ARCCOS;

    /**
     * Getter for property fcja.
     * @return Value of property fcja.
     */
    public kalkulator.Funkcja.Functions getFcja() {
        return this.fcja;
    }

    /**
     * Setter for property fcja.
     * @param fcja New value of property fcja.
     */
    public void setFcja(kalkulator.Funkcja.Functions fcja) {
        this.fcja = fcja;
    }

    /**
     * Holds value of property fcja_vetoable.
     */
    private kalkulator.Funkcja.Functions fcja_vetoable = kalkulator.Funkcja.Functions.COS;

    /**
     * Getter for property fcja_vetoable.
     * @return Value of property fcja_vetoable.
     */
    public kalkulator.Funkcja.Functions getFcja_vetoable() {
        return this.fcja_vetoable;
    }

    /**
     * Setter for property fcja_vetoable.
     * @param fcja_vetoable New value of property fcja_vetoable.
     *
     * @throws PropertyVetoException if some vetoable listeners reject the new value
     */
    public void setFcja_vetoable(kalkulator.Funkcja.Functions fcja_vetoable) throws java.beans.PropertyVetoException {
        kalkulator.Funkcja.Functions oldFcja_vetoable = this.fcja_vetoable;
        vetoableChangeSupport.fireVetoableChange("fcja_vetoable", oldFcja_vetoable, fcja_vetoable);
        this.fcja_vetoable = fcja_vetoable;
    }

}
