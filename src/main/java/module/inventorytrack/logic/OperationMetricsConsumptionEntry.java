/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package module.inventorytrack.logic;
import java.time.LocalDate;

/**
 *
 * @author User
 */
public class OperationMetricsConsumptionEntry {
    
    

    private LocalDate date;
    private double consumedGrams;

    public OperationMetricsConsumptionEntry(LocalDate date, double consumedGrams) {
        this.date = date;
        this.consumedGrams = consumedGrams;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getConsumedGrams() {
        return consumedGrams;
    }

    public void setConsumedGrams(double consumedGrams) {
        this.consumedGrams = consumedGrams;
    }

    @Override
    public String toString() {
        return "ConsumptionEntry{" +
                "date=" + date +
                ", consumedGrams=" + consumedGrams +
                '}';
    }
    
    
    
}
