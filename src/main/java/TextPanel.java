/**
 * File:    TextPanel.java
 * Created: August 12, 2017
 * Author:  hhraulerson
 * Project: Irrigation Recommendation System (IRS)
 */

import javax.swing.*;
import java.awt.*;

/**
 * <p>
 * The TextPanel class will be added to a JFrame that
 * displays the Irrigation Recommendation System's GUI.
 * This panel allows the user to enter the crop type,
 * soil type, and soil moisture sensor depths for their
 * operation.
 * </p>
 *
 * @author hhraulerson
 * @version 1.4
 */
public class TextPanel extends JPanel
{
    /* Variables */

    /**
     * Stores the width of the TextPanel
     */
    private final int WIDTH = 50;

    /**
     * Stores the height of the TextPanel
     */
    private final int HEIGHT = 100;

    /**
     * Creates the crop type JLabel
     */
    private JLabel cropLabel;

    /**
     * Creates the soil type JLabel
     */
    private JLabel soilLabel;

    /**
     * Creates the JLabel for soil sensor #1's depth.
     */
    private JLabel depthLabel1;

    /**
     * Creates the JLabel for soil sensor #2's depth.
     */
    private JLabel depthLabel2;

    /**
     * Creates the JLabel for soil sensor #3's depth.
     */
    private JLabel depthLabel3;

    /**
     * Creates the JTextField that will allow the user to
     * enter their crop type.
     */
    private JTextField cropType;

    /**
     * Creates the JTextField that will allow the user to
     * enter their soil type.
     */
    private JTextField soilType;

    /**
     * Creates the JTextField that will allow the user to enter
     * the depth for soil moisture sensor #1.
     */
    private JTextField depth1;

    /**
     * Creates the JTextField that will allow the user to enter
     * the depth for soil moisture sensor #2.
     */
    private JTextField depth2;

    /**
     * Creates the JTextField that will allow the user to enter
     * the depth for soil moisture sensor #3.
     */
    private JTextField depth3;

    /**
     * Stores the user's crop type.
     */
    private String crop;

    /**
     * Stores the user's soil type.
     */
    private String soil;

    /**
     * Stores the depth for soil moisture sensor #1.
     */
    private double dep1;

    /**
     * Stores the depth for soil moisture sensor #2.
     */
    private double dep2;

    /**
     * Stores the depth for soil moisture sensor #3.
     */
    private double dep3;

    /**
     * The variable that contains the number of sensors provided
     * by the user.
     */
    private int numSensorDepths;

    /**
     * Stores whether there's an issue with the crop entered.
     */
    private boolean cropException;

    /**
     * Stores whether there's an issue with the soil entered.
     */
    private boolean soilException;

    /**
     * Stores whether there are no sensor depths provided.
     */
    private boolean sensorException;

    /**
     * Default constructor for TextPanel class.
     */
    public TextPanel()
    {
        setSize(WIDTH, HEIGHT);
        cropLabel = new JLabel("Enter Crop Type");
        soilLabel = new JLabel("Enter Soil Type");
        depthLabel1 = new JLabel("Enter Sensor Depth (in.)");
        depthLabel2 = new JLabel("Enter Sensor Depth (in.)");
        depthLabel3 = new JLabel("Enter Sensor Depth (in.)");

        cropType = new JTextField("");
        soilType = new JTextField("");
        depth1 = new JTextField("");
        depth2 = new JTextField("");
        depth3 = new JTextField("");

        setLayout(new GridLayout(10, 1));

        add(cropLabel);
        add(cropType);
        add(soilLabel);
        add(soilType);
        add(depthLabel1);
        add(depth1);
        add(depthLabel2);
        add(depth2);
        add(depthLabel3);
        add(depth3);

        numSensorDepths = 0;

        dep1 = -1;
        dep2 = -1;
        dep3 = -1;
    }

    /**
     * Sets the crop type input by the user.
     */
    public void setCropType()
    {
        cropException = false;

        if(cropType.getText().equals(""))
        {
            cropException = true;
        }
        else
        {
            crop = cropType.getText();
        }
    }

    /**
     * Returns the crop type entered by the user
     * @return the crop type the user entered
     */
    public String getCropType()
    {
        return crop;
    }

    /**
     * Sets the soil type input by the user.
     */
    public void setSoilType()
    {
        soilException = false;

        if(soilType.getText().equals(""))
        {
            soilException = true;
        }
        else
        {
            soil = soilType.getText();
        }
    }

    /**
     * Returns the soil type entered by the user.
     * @return the soil type the user entered
     */
    public String getSoilType()
    {
        return soil;
    }

    /**
     * Sets sensor depth input by the user as indicated
     * by the parameter sensorNum.
     * @param sensorNum the sensor number that will be set
     */
    public void setSensorDepth(int sensorNum)
    {
        if(sensorNum == 1)
        {
            try
            {
                dep1 = Double.parseDouble(depth1.getText());
            }
            catch (NullPointerException e)
            {
                //print to console - this isn't a message to be displayed to the user
                System.err.println("Caught NullPointerException: sensor depth field 1 is null.");

                dep1 = -1;
            }
            catch (NumberFormatException e)
            {
                //print to console - this isn't a message to be displayed to the user
                System.err.println("Caught NumberFormatException: value entered in sensor depth field 1 isn't a number.");

                dep1 = -1;
            }
        }
        else if(sensorNum == 2)
        {
            try
            {
                dep2 = Double.parseDouble(depth2.getText());
            }
            catch(NullPointerException e)
            {
                //print to console - this isn't a message to be displayed to the user
                System.err.println("Caught NullPointerException: sensor depth field 2 is null.");

                dep2 = -1;
            }
            catch(NumberFormatException e)
            {
                //print to console - this isn't a message to be displayed to the user
                System.err.println("Caught NumberFormatException: value entered in sensor depth field 2 isn't a number.");

                dep2 = -1;
            }
        }
        else if(sensorNum == 3)
        {
            try
            {
                dep3 = Double.parseDouble(depth3.getText());
            }
            catch(NullPointerException e)
            {
                //print to console - this isn't a message to be displayed to the user
                System.err.println("Caught NullPointerException: sensor depth field 3 is null.");

                dep3 = -1;
            }
            catch(NumberFormatException e)
            {
                //print to console - this isn't a message to be displayed to the user
                System.err.println("Caught NumberFormatException: value entered in sensor depth field 3 isn't a number.");

                dep3 = -1;
            }
        }

        setNumSensorDepths();

    }

    /**
     * Sets the number of sensor depths input by the user.
     */
    private void setNumSensorDepths()
    {
        if(dep1 != -1 && dep2 != -1 && dep3 != -1)
        {
            numSensorDepths = 3;

            sensorException = false;
        }
        else if((dep1 != -1 && dep2 != -1) || (dep1 != -1 && dep3 != -1) || (dep2 != -1 && dep3 != -1))
        {
            numSensorDepths = 2;

            sensorException = false;
        }
        else if(dep1 != -1 || dep2 != -1 || dep3 != -1)
        {
            numSensorDepths = 1;

            sensorException = false;
        }
        else
        {
            numSensorDepths = 0;

            sensorException = true;
        }

        //print to console - this isn't a message to be displayed to the user
        System.out.println("SensorException = " + sensorException);
    }

    /**
     * Returns the number of sensor depths entered by the
     * user.
     * @return the number of sensor depths entered by the user
     */
    public int getNumSensorDepths()
    {
        return numSensorDepths;
    }

    /**
     * Returns the sensor depth for a particular sensor number,
     * which is passed to the function.
     * @param sensorNum the sensor number that the user
     * requests to return the value of
     * @return the depth of the sensor number indicated
     * by the parameter sensorNum
     */
    public double getSensorDepth(int sensorNum)
    {
        if(sensorNum == 1)
        {
            return dep1;
        }
        else if(sensorNum == 2)
        {
            return dep2;
        }
        else if(sensorNum == 3)
        {
            return dep3;
        }
        else
        {
            return 0;
        }
    }

    /**
     * Returns whether any exceptions exist for the required
     * GUI fields.
     * @return if an exception exists
     */
    public boolean displayExceptions()
    {
        boolean exceptions = false;

        if(cropException)
        {
            JOptionPane.showMessageDialog(null, "Crop Type Field - No crop type was entered.",
                    "Error - Crop Type Not Entered", JOptionPane.ERROR_MESSAGE);

            exceptions = true;
        }

        if(soilException)
        {
            JOptionPane.showMessageDialog(null, "Soil Type Field - No soil type was entered.",
                    "Error - Soil Type Not Entered", JOptionPane.ERROR_MESSAGE);

            exceptions = true;
        }

        if(sensorException)
        {
            JOptionPane.showMessageDialog(null, "A minimum of 1 sensor depth must be entered.",
                    "Error - Sensor Depth Not Entered", JOptionPane.ERROR_MESSAGE);

            exceptions = true;
        }

        return exceptions;
    }

}
