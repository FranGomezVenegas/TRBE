/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package trazit.queries;

import databases.SqlWhere;

/**
 *
 * @author User
 */
public interface FilterCriteria {
    SqlWhere buildSqlWhere(Object[] args, String[] fieldNames, Object[] fieldValues);
}


