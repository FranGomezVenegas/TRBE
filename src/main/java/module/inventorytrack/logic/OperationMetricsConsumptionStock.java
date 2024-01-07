/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package module.inventorytrack.logic;

//works weka import weka.core.Attribute;
//works weka import weka.core.DenseInstance;
//works weka import weka.core.Instances;
//works weka import tech.tablesaw.api.DateColumn;
//works weka import tech.tablesaw.api.DoubleColumn;
//works weka import tech.tablesaw.api.Table;
//works weka import tech.tablesaw.columns.Column;

import java.time.LocalDate;
/**
 *
 * @author User
 */
public class OperationMetricsConsumptionStock {
    LocalDate date;
    double consumedGrams;
    
 public static String main() {
        // Create a Table with historical consumption data
        
/*        
        Table consumptionTable = Table.create("Consumption Data")
                .addColumns(
                        DateColumn.create("Date"),
                        DoubleColumn.create("Consumption")
                );

        // Add historical consumption data
        consumptionTable.dateColumn("Date").append(
                LocalDate.of(2022, 1, 1),
                LocalDate.of(2022, 5, 1),
                LocalDate.of(2022, 10, 1)
        );

       consumptionTable.doubleColumn("Consumption").append(100, 100, 100);
*/

//works weka Table consumptionTable = Table.create("consumption_Data").addColumns(
//works weka DateColumn.create("Date", LocalDate.of(2022, 1, 1), 
//works weka         LocalDate.of(2022, 1, 2), 
//works weka         LocalDate.of(2023, 1, 3)),
//works weka           DoubleColumn.create("Consumption", 2.0, 2.1, 2.2));
        // Define the target consumption
        double targetConsumption = 1000;

        // Perform time series analysis
        // Here, you would use statistical or time series analysis methods to predict when the target consumption will be reached.
        // This example assumes a simple linear projection.
//works weka         int numRows = consumptionTable.rowCount();
        double consumptionRate = 0;
//works weka         if (numRows > 1) {
//works weka     DateColumn dateColumn = consumptionTable.dateColumn("Date");
//works weka             Column<Double> consumptionColumn = consumptionTable.doubleColumn("Consumption");
//works weka             double initialConsumption = consumptionColumn.get(0);
//works weka             LocalDate initialDate = dateColumn.get(0);
            LocalDate targetDate = null;

//works weka             if (initialConsumption != 0) {
//works weka                 consumptionRate = (targetConsumption - initialConsumption) / numRows;
//works weka                 targetDate = initialDate.plusDays((long) ((targetConsumption - initialConsumption) / consumptionRate));
//works weka             }
            return "Target consumption of " + targetConsumption + " grams will be reached on: " + targetDate;
 //works weka        }
//works weka         return "no rows to calc";
    }    

//works weka     private static double daysBetween(LocalDate start, LocalDate end) {
//works weka         return ChronoUnit.DAYS.between(start, end);
//works weka     }
/* //works weka    
    private static Instances giveMeTheDataForRegression(List<OperationMetricsConsumptionEntry> historicalData){
                Attribute daysAttribute = new Attribute("days");
        Attribute consumedGramsAttribute = new Attribute("consumedGrams");
        
        // Create Instances object
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(daysAttribute);
        attributes.add(consumedGramsAttribute);
        Instances data = new Instances("ConsumptionData", attributes, 0);
        
        // Add instances (historical data)
        for (OperationMetricsConsumptionEntry entry : historicalData) {
            double[] values = new double[2];
            values[0] = daysBetween(entry.getDate(), LocalDate.now());
            values[1] = entry.getConsumedGrams();
            data.add(new DenseInstance(1.0, values));
        }            
            // Set class attribute (days to reach certain quantity)
            data.setClass(daysAttribute);
            return data;

    }
*/
/* //works weka 
  public static String[] mainWeka(double currentQuantity, double minStockAlert, List<OperationMetricsConsumptionEntry> historicalData) {
      String[] conclusion=new String[]{"empty","empty"};
        try {
            
            LocalDate currentDate = LocalDate.now();
//
//            // Create an instance for predicting days to reach minStockAlert grams (alert)
//            Instances dataA=giveMeTheDataForRegression(historicalData);
//            // Train a linear regression model
//            LinearRegression modelA = new LinearRegression();
//            modelA.buildClassifier(dataA);
//
//            
//            double[] alertInstanceValues = new double[]{minStockAlert, 0};
//            DenseInstance alertInstance = new DenseInstance(1.0, alertInstanceValues);
//            alertInstance.setDataset(dataA);
//            double daysToReach200Grams = modelA.classifyInstance(alertInstance);
//            LocalDate daysToReach200GramsDate = currentDate.plus((long) daysToReach200Grams, ChronoUnit.DAYS);
//            String alertConclusion = "Days to reach the min stock " + minStockAlert + " grams (alert): " + daysToReach200Grams + " Therefore the date is " + daysToReach200GramsDate;
//
            
            // Predict days to reach minStockAlert grams (alert)
            
//works weka            Instances data=giveMeTheDataForRegression(historicalData);
            // Train a linear regression model
//works weka            LinearRegression model = new LinearRegression();
//works weka            model.buildClassifier(data);

            // Create an instance for predicting days to reach 1000 grams
            double[] futureInstanceValues = new double[]{currentQuantity, 0}; // Predicting currentQuantity grams
//works weka            DenseInstance futureInstance = new DenseInstance(1.0, futureInstanceValues);
//works weka            futureInstance.setDataset(data);

            // Predict days to reach currentQuantity grams
//works weka            double daysToReach1000Grams = model.classifyInstance(futureInstance);
//works weka            LocalDate daysToReach1000GramsDate = currentDate.plus((long) daysToReach1000Grams, ChronoUnit.DAYS);
//works weka            String quantityConclusion = "Days to reach all the quantity of " + currentQuantity + " grams: " + daysToReach1000Grams + " Therefore the date is " + daysToReach1000GramsDate;

            //conclusion[0]=alertConclusion;
//works weka            conclusion[0]=quantityConclusion;

            return conclusion;
        } catch (Exception ex) {
            Logger.getLogger(OperationMetricsConsumptionStock.class.getName()).log(Level.SEVERE, null, ex);

        }
        return conclusion;
    }
*/
/* //works weka   
    public static String[] main(double currentQuantity, double minStockAlert, List<OperationMetricsConsumptionEntry> consumptionData) {
        String[] conclusion=new String[4];

        double totalConsumedGrams = 0;
        for (OperationMetricsConsumptionEntry entry : consumptionData) {
            totalConsumedGrams += entry.getConsumedGrams();
        }

        double averageConsumptionRate = totalConsumedGrams / consumptionData.size();
        double daysToReachMinStockAlert = averageConsumptionRate==0?0:(currentQuantity - minStockAlert) / averageConsumptionRate;

        int daysPerWeek = 7;
        int weeksToReachMinStockAlert = (int) Math.ceil(daysToReachMinStockAlert / daysPerWeek);

        LocalDate currentDate = LocalDate.now();
        LocalDate minStockAlertDate = currentDate.plus(weeksToReachMinStockAlert, ChronoUnit.WEEKS);
        conclusion[0]=(" Days to reach min stock alert: " + daysToReachMinStockAlert);
        conclusion[1]=(". Min stock alert will be reached on: " + minStockAlertDate);

        double daysToConsumeAll = averageConsumptionRate==0?0:currentQuantity / averageConsumptionRate;
        int weeksToConsumeAll = (int) Math.ceil(daysToConsumeAll / daysPerWeek);

        LocalDate fullyConsumedDate = currentDate.plus(weeksToConsumeAll, ChronoUnit.WEEKS);
        conclusion[2]=(". Days to consume all: " + daysToConsumeAll);
        conclusion[3]=(". Fully consumed on: " + fullyConsumedDate);
        return conclusion;
    }        
*/    
    
    
}
