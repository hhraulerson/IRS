/*
 * File:    Recommendation.java
 * Created: August 10, 2017
 * Author:  hhraulerson
 * Project: Irrigation Recommendation System (IRS)
 */

import java.io.*;
import java.nio.file.Paths;
import java.util.Calendar;
import java.nio.file.Path;

/**
 * <p>
 * The Recommendation class contains methods for creating a report
 * based on the results output by the RNN.
 * </p>
 *
 * @author hhraulerson
 * @version 1.3
 */
public class Recommendation
{
    /* Variables */

    /**
     * The variable that contains the crop type specified by the user.
     */
    private String crop;

    /**
     * The variable that contains the soil type specified by the user.
     */
    private String soil;

    /**
     * The variable that stores the depth for soil moisture sensor #1.
     */
    private double dep1;

    /**
     * The variable that stores the depth for soil moisture sensor #2.
     */
    private double dep2;

    /**
     * The variable that stores the depth for soil moisture sensor #3.
     */
    private double dep3;

    /**
     * The variable that contains the number of sensor depths provided
     * by the user.
     */
    private int numSensorDepths;

    /**
     * The variable that contains the report output by this class.
     */
    private File report;

    /**
     * The variable that contains file path for the report.
     */
    private String filePath;

    /* Constructors */

    /**
     * Default constructor for the Recommendation class.
     * @param cropType the crop type entered by the user
     * @param soilType the soil type entered by the user
     */
    public Recommendation(String cropType, String soilType)
    {
        setCropType(cropType);
        setSoilType(soilType);
        setFilePath();
        createFile();

        numSensorDepths = 0;
    }

    /**
     * Sets the crop type variable.
     * @param cropType the crop type
     */
    public void setCropType(String cropType)
    {
        crop = cropType;
    }

    /**
     * Returns the crop type variable.
     * @return the crop type
     */
    public String getCropType()
    {
        return crop;
    }

    /**
     * Sets the soil type variable.
     * @param soilType the soil type
     */
    public void setSoilType(String soilType)
    {
        soil = soilType;
    }

    /**
     * Returns the soil type variable.
     * @return the soil type
     */
    public String getSoilType()
    {
        return soil;
    }

    /**
     * Sets sensor depth input by the user as indicated
     * by the parameter sensorNum.
     * @param sensorNum the sensor number that will be set
     * @param sensorDepth the sensor depth that will be set
     */
    public void setSensorDepth(int sensorNum, double sensorDepth)
    {
        if(sensorNum == 1)
        {
            dep1 = sensorDepth;
            ++numSensorDepths;

            //print to console - this isn't a message to be displayed to the user
            System.out.println("Sensor depth 1 set to " + dep1 + ".");
        }
        else if(sensorNum == 2 && sensorDepth != -1)
        {
            dep2 = sensorDepth;
            ++numSensorDepths;

            //print to console - this isn't a message to be displayed to the user
            System.out.println("Sensor depth 2 set to " + dep2 + ".");
        }
        else if(sensorNum == 3 && sensorDepth != -1)
        {
            dep3 = sensorDepth;
            ++numSensorDepths;

            //print to console - this isn't a message to be displayed to the user
            System.out.println("Sensor depth 3 set to " + dep3 + ".");
        }
    }

    /**
     * Returns the numSensors variable.
     * @return the number of sensor depths provided by the user
     */
    public int getNumDepths()
    {
        return numSensorDepths;
    }

    /**
     * Creates the recommendation file.
     */
    public void createFile()
    {
        report = new File(getFilePath());
    }

    /**
     * Creates the recommendation report using the results provided by the RNN.
     * @param results the recommendation output by the RNN (# inches to irrigate)
     * @return the file path for the recommendation report
     */
    public String createReport(String results)
    {
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        String month = Integer.toString(Calendar.getInstance().get(Calendar.MONTH) + 1);
        String day = Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        try
        {
            PrintWriter pw = new PrintWriter(report);
            pw.println("Irrigation Recommendation Report for " + month + "/" + day + "/" + year);
            pw.println("Crop Type: " + getCropType());
            pw.println("Soil Type: " + getSoilType());
            System.out.println("Number of Sensors: " + getNumDepths());

            if(getNumDepths() == 3)
            {
                pw.println("Sensor Depth 1: " + dep1 + " inches");
                pw.println("Sensor Depth 2: " + dep2 + " inches");
                pw.println("Sensor Depth 3: " + dep3 + " inches");
            }
            else if(getNumDepths() == 2)
            {
                pw.println("Sensor Depth 1: " + dep1 + " inches");
                pw.println("Sensor Depth 2: " + dep2 + " inches");
                pw.println("Sensor Depth 3: N/A");
            }
            else if(getNumDepths() == 1)
            {
                pw.println("Sensor Depth 1: " + dep1 + " inches");
                pw.println("Sensor Depth 2: N/A");
                pw.println("Sensor Depth 3: N/A");
            }

            pw.println();

            if(results.isEmpty())
            {
                pw.println("Recommendation: Based on the SMS and weather data input into the model, no irrigation is " +
                        "recommended tomorrow.");
            }
            else
            {
                pw.println("Recommendation: Based on the SMS and weather data input into the model, tomorrow's " +
                        "recommended irrigation amount is " + results + " inches.");
            }

            pw.close();
        }
        catch(FileNotFoundException e)
        {
            //print to console - this isn't a message to be displayed to the user
            System.err.println("Caught FileNotFoundException: File was not found.");

            return null;
        }

        return getFilePath();

    }

    /**
     * Returns the file path for the report.
     * @return the report file path
     */
    public String getFilePath()
    {
        return filePath;
    }

    /**
     * Sets the filePath variable.
     */
    public void setFilePath()
    {
        Path fp = Paths.get("");
        String s = fp.toAbsolutePath().toString();
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        String month = Integer.toString(Calendar.getInstance().get(Calendar.MONTH) + 1);
        String day = Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        filePath = s + "/" + "Report" + getCropType().toUpperCase() + getSoilType().toLowerCase() + month + day + year + ".txt";
    }
}
