/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lbplanet.utilities;

/**
 *
 * @author User
 */
public class LPEnums {


public static class CellHelper<T> {
    final T[] mValues;
    final Class<T> cls;
    // Helper needs Class<T> to work around type erasure
    public CellHelper(T[] values, Class<T> c) {
        mValues = values;
        cls = c;
    }
    public T getCell(int index) {
        if (index < mValues.length ) {
            return mValues[index];              
        }
        throw new IllegalArgumentException("Invalid " + cls.getSimpleName() + " value: " + index);
    }
}

public interface Indexed<E extends Enum> {

    /**
     *
     * @param index
     * @return
     */
    public default  E getByIndexFran(int index) {
        if (Boolean.FALSE.equals(this.getClass().isEnum())) {
            //not implemented on enum, you can do as you like here
        }
        Enum<?>[] vals = (Enum<?>[]) this.getClass().getEnumConstants();
        if (index < 0 || index >= vals.length) {
            //illegal arg exception
        }
        return (E) vals[index];
    }

}
    
}
