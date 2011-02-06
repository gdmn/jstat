/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package beanutils;

import java.awt.*;
import java.beans.*;
import java.lang.reflect.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

/**
 * @author Damian
 */
public class BeanEditorTable extends JTable implements TableValueSetterInterface {
    private Object bean;
    /** właściwości JavaBean */
    private PropertyDescriptor[] propertyDescriptor;

    private class MyTableModel extends AbstractTableModel {
        public int getColumnCount() {
            return 4;
        }

        public String getColumnName(int column) {
            if (column == getColumnCount() - 1) {
                return "Wartość";
            } else {
                switch (column) {
                    case 0:
                        return "Właściwość";
                    case 1:
                        return "Typ";
                    case 2:
                        return "Opis";
                }
            }
            return null;
        }

        public int getRowCount() {
            if (propertyDescriptor == null) {
                //System.err.println("propertyDescriptor == null");
                //return 0;
            } else if (propertyDescriptor.length < 1) {
                //System.err.println("propertyDescriptor.length < 1");
                //return 0;
                } else {
                //return propertyDescriptor.length;
                //System.out.println(propertyDescriptor.length);
                return propertyDescriptor.length;
            //return 27;
            }
            return 0;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            //System.out.println("getValueAt "+rowIndex+" "+columnIndex);
            PropertyDescriptor d = propertyDescriptor[rowIndex];
            Class<?> p = d.getPropertyType();
            if (columnIndex == getColumnCount() - 1) {
                return BeanSetterGetter.getBeanPropertyEasy(bean, d);
            } else {
                switch (columnIndex) {
                    case 2:
                        return d.getShortDescription();
                    case 1:
                        return p.getSimpleName();
                    case 0:
                        return d.getName();
                    default:
                        break;
                }
            }
            return null;
        }

        public boolean isCellEditable(int row, int col) {
            boolean result = col == getColumnCount() - 1;
            if (result) {
                PropertyDescriptor d = propertyDescriptor[row];
                Class<?> p = d.getPropertyType();
                Method writeMethod = d.getWriteMethod();
                result = (writeMethod != null);
            }
            return result;
        }

        public boolean tryToSetValueAt(Object value, int row, int col) {
            PropertyDescriptor d = propertyDescriptor[row];
			if (BeanSetterGetter.setBeanPropertyEasy(bean, d, value)) {
                fireTableCellUpdated(row, col);
                return true;
            }
            return false;
        }

        public void setValueAt(Object value, int row, int col) {
            tryToSetValueAt(value, row, col);
        }
    } // end of class MyTableModel

    /** Tablica zawierająca przyporządkowane TableCellRenderer dla każdej z klas */
    transient protected Hashtable<Class<?>, TableCellRenderer> myRenderersByClass;
    /** Tablica zawierająca przyporządkowane TableCellEditor dla każdej z klas */
    transient protected Hashtable<Class<?>, TableCellEditor> myEditorsByClass;

    /** Ustawia TableCellRenderer dla podanej klasy */
    public void setMyClassRenderer(Class<?> aClass, TableCellRenderer renderer) {
        if (renderer != null) {
            myRenderersByClass.put(aClass, renderer);
        } else {
            myRenderersByClass.remove(aClass);
        }
    }

    /** @return TableCellRenderer przyporządkowany do podanej klasy */
    public TableCellRenderer getMyClassRenderer(Class<?> aClass) {
        if (aClass == null) {
            return null;
        } else {
            Object renderer = myRenderersByClass.get(aClass);
            if (renderer != null) {
                return (TableCellRenderer) renderer;
            } else {
                if (aClass.isArray()) {
                    /* Mamy tablicę. Chcemy się dowiedzieć, jakie klasy zawiera,
                     * potem sprawdzamy, czy jest jakiś zarejestrowany TableCellRenderer
                     * dla tablicy klas bazowych.
                     * Żeby pobrać klasę, z których jest tablica:
                     *   aClass.getCanonicalName() = "java.lang.Integer[]"
                     *   aClass.getComponentType().getCanonicalName() = "java.lang.Integer"
                     * Nie działa dla typów podstawowych!!
                     * Teoretycznie można byłoby zrobić:
                     * baseClass = ValueOfParser.bazowyDoOpakowujacego(baseClass);
                     * ale potem nie chce rzutować na Number[] tablicy np. int[]
                     */
                    Class<?> baseClass = aClass.getComponentType();
                    Class<?> superClass = baseClass.getSuperclass();
                    if (superClass != null) {
                        Class<?> arrOfSuper = Array.newInstance(superClass, 0).getClass();
                        return getMyClassRenderer(arrOfSuper);
                    }
                    return null;
                }
                return getMyClassRenderer(aClass.getSuperclass());
            }
        }
    }

    /** Ustawia TableCellEditor dla podanej klasy */
    public void setMyClassEditor(Class<?> aClass, TableCellEditor editor) {
        if (editor != null) {
            myEditorsByClass.put(aClass, editor);
        } else {
            myEditorsByClass.remove(aClass);
        }
    }

    /** @return TableCellEditor przyporządkowany do podanej klasy */
    public TableCellEditor getMyClassEditor(Class<?> aClass) {
        if (aClass == null) {
            return null;
        } else {
            Object editor = myEditorsByClass.get(aClass);
            if (editor != null) {
                return (TableCellEditor) editor;
            } else {
                if (aClass.isArray()) {
                    Class<?> baseClass = aClass.getComponentType();
                    Class<?> superClass = baseClass.getSuperclass();
                    if (superClass != null) {
                        Class<?> arrOfSuper = Array.newInstance(superClass, 0).getClass();
                        return getMyClassEditor(arrOfSuper);
                    }
                    return null;
                }
                return getMyClassEditor(aClass.getSuperclass());
            }
        }
    }

    /** @return TableCellRenderer przyporządkowany do podanej komórki w tabeli */
    public TableCellRenderer getCellRenderer(int row, int column) {
        if (convertColumnIndexToModel(column) == getModel().getColumnCount() - 1) {
            // konieczny convertColumnIndexToModel, bo zwraca poprawny index nawet w przypadku
            // przestawienia kolumn przez użytkownika
            PropertyDescriptor d = propertyDescriptor[convertRowIndexToModel(row)];
            TableCellRenderer result = getMyClassRenderer(d.getPropertyType());
            if (result != null) {
                return result;
            }
        }
        // else...
        return super.getCellRenderer(row, column);
    }

    /** @return TableCellEditor przyporządkowany do podanej komórki w tabeli */
    public TableCellEditor getCellEditor(int row, int column) {
        if (convertColumnIndexToModel(column) == getModel().getColumnCount() - 1) {
            // konieczny convertColumnIndexToModel, bo zwraca poprawny index nawet w przypadku
            // przestawienia kolumn przez użytkownika
            PropertyDescriptor d = propertyDescriptor[convertRowIndexToModel(row)];
            TableCellEditor result = getMyClassEditor(d.getPropertyType());
            if (result != null) {
                return result;
            }
        }
        // else...
        return super.getCellEditor(row, column);
    }

    public Class<?> getColumnClass(int column) {
        return super.getColumnClass(column);
    }

    public Class<?> getMyCellClass(int row, int column) {
        if (convertColumnIndexToModel(column) == getModel().getColumnCount() - 1) {
            // konieczny convertColumnIndexToModel, bo zwraca poprawny index nawet w przypadku
            // przestawienia kolumn przez użytkownika
            PropertyDescriptor d = propertyDescriptor[convertRowIndexToModel(row)];
            return d.getPropertyType();
        }
        // else...
        return String.class;
    }

    private <T extends Number> void addEditorAndRendererForNumber(Class<?> type) {
        @SuppressWarnings("unchecked")
        /** type2 to typ opakowujący typ type */
        Class<T> type2 = (Class<T>) ValueOfParser.bazowyDoOpakowujacego(type);

        setMyClassRenderer(type, new NumberRenderer());
        //type2 automatycznie jest rzutowane w górę na Number i renderowane przez NumberRender
        setMyClassEditor(type, new NumberEditor<T>(type2));
        setMyClassEditor(type2, new NumberEditor<T>(type2));

        Class<?> arr = Array.newInstance(type, 0).getClass();
        Class<?> arr2 = Array.newInstance(type2, 0).getClass();

        setMyClassRenderer(arr, new ArrayRenderer<T>(type));
        setMyClassEditor(arr, new ArrayEditor<T>(type));
        //type2 automatycznie jest rzutowane w górę na Number[] i renderowane przez ArrayRenderer<Number>(Number.class)
        setMyClassEditor(arr2, new ArrayEditor<T>(type2));

    }

    public BeanEditorTable(Object bean) throws IntrospectionException {
        this(bean, Object.class);
    }

    public BeanEditorTable(Object bean, Class<?> stopClass) throws IntrospectionException {
        super();
        //TODO: char

        this.bean = bean;
        //this.setRowHeight(30);

        /* wzorowane na createDefaultEditors() z JTable.java */
        /* wzorowane na createDefaultRenderers() z JTable.java */

        myEditorsByClass = new Hashtable<Class<?>, TableCellEditor>();
        myRenderersByClass = new Hashtable<Class<?>, TableCellRenderer>();


        // Objects
        setMyClassRenderer(Object[].class, new ArrayRenderer<Object>(Object.class));
        setMyClassEditor(Object.class, new GenericEditor()); //String jest rzutowany na Object

        setMyClassRenderer(String[].class, new ArrayRenderer<String>(String.class));
        setMyClassEditor(String[].class, new ArrayEditor<String>(String.class));

        // Numbers, Int, ...
        setMyClassRenderer(Number.class, new NumberRenderer());
        //setMyClassEditor(Number.class, new NumberEditor<Number>(Number.class)); //bo Number nie ma metody valueOf()

        setMyClassRenderer(Number[].class, new ArrayRenderer<Number>(Number.class));
        //setMyClassEditor(Number[].class, new ArrayEditor<Number>(Number.class)); //bo Number nie ma metody valueOf()

        this.<Byte>addEditorAndRendererForNumber(byte.class);
        this.<Short>addEditorAndRendererForNumber(short.class);
        this.<Integer>addEditorAndRendererForNumber(int.class);
        this.<Long>addEditorAndRendererForNumber(long.class);
        this.<Float>addEditorAndRendererForNumber(float.class);
        this.<Double>addEditorAndRendererForNumber(double.class);
        //setMyClassEditor(Number.class, new NumberEditor());

        // Booleans
        setMyClassRenderer(boolean.class, new BooleanRenderer());
        setMyClassEditor(boolean.class, new BooleanEditor());
        setMyClassRenderer(Boolean.class, new BooleanRenderer());
        setMyClassEditor(Boolean.class, new BooleanEditor());
        setMyClassRenderer(Boolean[].class, new ArrayRenderer<Boolean>(Boolean.class));
        setMyClassEditor(Boolean[].class, new ArrayEditor<Boolean>(Boolean.class));
        setMyClassRenderer(boolean[].class, new ArrayRenderer<Boolean>(boolean.class));
        setMyClassEditor(boolean[].class, new ArrayEditor<Boolean>(boolean.class));

        //
        //

        // Numbers

        // Doubles and Floats

        // Dates
        setMyClassRenderer(Date.class, new DateRenderer());

        // Icons and ImageIcons
        setMyClassRenderer(Icon.class, new IconRenderer());
        setMyClassRenderer(ImageIcon.class, new IconRenderer());

        //
        //

        // Moje:
        setMyClassEditor(Color.class, new ColorEditor());
        setMyClassRenderer(Color.class, new ColorRenderer(true));

        setMyClassRenderer(Enum.class, new EnumRenderer());
        setMyClassEditor(Enum.class, new EnumEditor());

        //--
        MyTableModel model = new MyTableModel();
        TableRowSorter<MyTableModel> sorter = new TableRowSorter<MyTableModel>(model);

        //----------------------
        setModel(model);
        setRowSorter(sorter);
//        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        {
            int count = getColumnCount();
            for (int i = 0; i < count; i++) {
                TableColumn c = getColumnModel().getColumn(i);
                    switch (i) {
                        case 0:
                            c.setPreferredWidth(80);
                            break;
                        case 1:
                            c.setPreferredWidth(60);
                            break;
                        case 2:
                            c.setPreferredWidth(120);
                            break;
                        case 3:
                            c.setPreferredWidth(60);
                            break;
                        default:
                            c.setPreferredWidth(50);
                    }
            }
        }
        setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        //--
        BeanInfo bi = null;
        bi = Introspector.getBeanInfo(bean.getClass(), stopClass);
        propertyDescriptor = bi.getPropertyDescriptors();

        //musi być, bo inaczej wyjątek przy setValueAt
        getRowSorter().modelStructureChanged();
    }

    /**
     * Update the row filter regular expression from the expression in
     * the text box.
     */
    public void newFilter(String filter) {
        RowFilter<MyTableModel, Object> rf = null;
        //If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter(filter);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        ((TableRowSorter<MyTableModel>) getRowSorter()).setRowFilter(rf);
    }

    public boolean tryToSetValueAt(Object value, int row, int col) {
        //@SuppressWarnings("warning")
        MyTableModel m = (MyTableModel) getModel();
        return m.tryToSetValueAt(value, convertRowIndexToModel(row), convertColumnIndexToModel(col));

    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        // TODO
        throw new UnsupportedOperationException("BeanEditorTable.setBean()");
    //this.bean = bean;
    }

    class ColumnHider {
        boolean prev = true;
        TableColumn[] removed;

        public void set(boolean allColumnVisible) {
            if (allColumnVisible == prev) {
                return;
            }
            TableColumnModel colModel = BeanEditorTable.this.getColumnModel();
            if (allColumnVisible) {
                colModel.addColumn(removed[0]);
                colModel.moveColumn(colModel.getColumnCount() - 1, 0);
                colModel.addColumn(removed[1]);
                colModel.moveColumn(colModel.getColumnCount() - 1, 1);
                removed = null;
            } else {
                removed = new TableColumn[]{colModel.getColumn(0), colModel.getColumn(1)};
                colModel.removeColumn(removed[0]);
                colModel.removeColumn(removed[1]);
            }
            prev = allColumnVisible;
        }
    }
    ColumnHider columnHider = new ColumnHider();

    /**
     * Zmienia ilość widocznych kolumn
     * @param allColumnVisible czy wszystkie powinny być widoczne
     */
    public void setAllColumnVisible(boolean allColumnVisible) {
        columnHider.set(allColumnVisible);
    }
}
