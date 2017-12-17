/**
 * File:    TextPanel.java
 * Created: August 20, 2017
 * Author:  hhraulerson
 * Project: Irrigation Recommendation System (IRS)
 */

import org.deeplearning4j.eval.RegressionEvaluation;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * <p>
 * This class creates the GUI for the
 * Irrigation Recommendation System.  The
 * GUI is constructed using various Java
 * Swing components built in other classes.
 * </p>
 *
 * @author hhraulerson
 * @version 1.7
 */
public class GUI extends JFrame
{
    /* Variables */

    /**
     * Stores the width of the JFrame.
     */
    private final int WIDTH = 500;

    /**
     * Stores the height of the JFrame.
     */
    private final int HEIGHT = 300;

    /**
     * Creates the file upload panel.
     */
    private FilePanel fp;

    /**
     * Creates the text fields' panel.
     */
    private TextPanel tp;

    /**
     * Creates the button panel.
     */
    private ButtonPanel bp;

    /**
     * Creates the west format panel.
     */
    private JPanel westPanel;

    /**
     * Creates the NE format panel.
     */
    private JPanel northeastPanel;

    /**
     * Creates the SE format panel.
     */
    private JPanel southeastPanel;

    /**
     * Creates the east format panel.
     */
    private JPanel eastPanel;

    /**
     * Stores the variable that will be set to
     * true if the user selects to train the model.
     */
    private boolean train;

    /**
     * Stores the variable that will be set to
     * true if the user selects to load parameters
     * from a previous model run.
     */
    private boolean load;

    /**
     * Stores the IRSFunctions object that can call
     * methods on the underlying RNN.
     */
    private IRSFunctions model;

    /**
     * Stores the number of columns for the datafile
     * uploaded by the user.
     */
    private int numColumns;

    /* Constructors */

    /**
     * Default constructor for GUI class.
     */
    public GUI()
    {
        super("Irrigation Recommendation System");
        setSize(WIDTH, HEIGHT);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setVisible(true);

        createPanels();

        northeastPanel.add(fp, BorderLayout.NORTH);
        southeastPanel.add(bp, BorderLayout.NORTH);

        eastPanel.add(northeastPanel, BorderLayout.NORTH);
        eastPanel.add(southeastPanel, BorderLayout.EAST);
        westPanel.add(tp, BorderLayout.NORTH);

        add(westPanel, BorderLayout.WEST);
        add(eastPanel, BorderLayout.EAST);

        resetButtonSelected();
    }

    /* Methods */

    /**
     * Creates the JPanels, FilePanel, TextPanel,
     * and the ButtonPanel for the GUI.
     */
    public void createPanels()
    {
        westPanel = new JPanel();
        westPanel.setSize(125, 125);
        westPanel.setLayout(new BorderLayout());

        eastPanel = new JPanel();
        eastPanel.setSize(125, 125);
        eastPanel.setLayout(new BorderLayout());

        northeastPanel = new JPanel();
        northeastPanel.setSize(125, 10);
        northeastPanel.setLayout(new BorderLayout());
        southeastPanel = new JPanel();
        southeastPanel.setSize(125, 240);
        southeastPanel.setLayout(new BorderLayout());

        fp = new FilePanel();
        tp = new TextPanel();
        bp = new ButtonPanel();
    }

    /**
     * Resets the booleans monitoring which
     * button has been selected in the case
     * that there is a data input exception,
     * null pointer exception, etc and the user
     * is instructed to fix the issue.
     */
    public void resetButtonSelected()
    {
        train = false;
        load = false;
    }

    /**
     * Creates a new model or loads the rnn from a previous run.
     * @return whether the model was created/loaded or if an
     * error occurred
     */
    public boolean configureModel()
    {
        if (train)
        {
            //creates new RNN with 500 layers
            model = new IRSFunctions(500, 1, 250, 250, 1, fp.getPath(), getNumColumns());

            return true;
        }
        else if (load)
        {

             //User chose to load parameters from a previous run
            JOptionPane.showMessageDialog(null, "Loading the neural network using data from " + fp.getPath() + "...", "Loading Parameters", JOptionPane.INFORMATION_MESSAGE);

            File loadLocation = new File(fp.getPath());

            try
            {
                model = new IRSFunctions(loadLocation);

                if(model.getNetwork() == null)
                {
                    JOptionPane.showMessageDialog(null, "RNN could not be loaded.  " +
                            "Please check the uploaded file and try again.", "Loading Error", JOptionPane.ERROR_MESSAGE);

                    return false;
                }

                JOptionPane.showMessageDialog(null, "RNN successfully loaded from " + fp.getPath() + ".", "RNN Loaded", JOptionPane.INFORMATION_MESSAGE);

                return true;

            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(null, "RNN could not be loaded.  " +
                        "Please try again.", "Loading Error", JOptionPane.ERROR_MESSAGE);

                return false;
            }
        }

        return false;
    }

    /**
     * Passes the crop and soil types to the
     * IRSFunctions class so they can be used
     * in generating a recommendation report.
     */
    public void determineModelVariables()
    {
        model.setCropType(tp.getCropType());
        model.setSoilType(tp.getSoilType());
    }

    /**
     * Sets the number of columns for the uploaded
     * csv file based on the number of sensor depths
     * that were provided.
     */
    public void setNumColumns()
    {
        int sensorDepths = 0;

        for (int i = 1; i < 4; i++)
        {
            if (tp.getSensorDepth(i) != -1 && tp.getSensorDepth(i) != 0)
            {
                ++sensorDepths;
            }
        }

        /*
         *
         * If 1 sensor depth, csv should have
         * 7 columns.  2 sensor depths =
         * csv with 10 columns.  3 sensor
         * depths = csv with 13 columns.
         */
        if (sensorDepths == 2)
        {
            numColumns = 10;
        }
        else if (sensorDepths == 3)
        {
            numColumns = 13;
        }
        else
        {
            numColumns = 7;
        }

    }

    /**
     * Returns the number of columns in the uploaded csv.  This
     * value is determined in the setNumColumns function.
     * @return the number of columns in the uploaded csv
     */
    public int getNumColumns()
    {
        return numColumns;
    }

    /**
     * Main method for running the IRS program.
     *
     * @param args program arguments indicated at runtime
     */
    public static void main(String[] args)
    {
        GUI g = new GUI();
        g.setVisible(true);
    }

    /* Private Classes */

    /**
     * * <p>
     * Private class that creates a Button Panel class to be
     * added to a JFrame that displays the Irrigation
     * Recommendation System's GUI. This panel creates
     * the buttons for the user to select whether to
     * train the model, load parameters into the model,
     * or to generate an irrigation recommendation report
     * using the model.
     * </p>
     *
     * @author hhraulerson
     * @version 1.3
     */
    private class ButtonPanel extends JPanel
    {
    /* Variables */

        /**
         * Stores the width of the ButtonPanel.
         */
        private final int WIDTH = 100;

        /**
         * Stores the height of the ButtonPanel.
         */
        private final int HEIGHT = 100;

        /**
         * Creates the train JButton.
         */
        private JButton trainButton;

        /**
         * Creates the load JButton.
         */
        private JButton loadButton;

        /**
         * Creates the generate JButton.
         */
        private JButton generateButton;

        /**
         * Creates the user's manual JButton.
         */
        private JButton usersManualButton;

        /**
         * Creates an ActionListener for
         * the train JButton.
         */
        private ActionListener trainListener;

        /**
         * Creates an ActionListener for
         * the load JButton.
         */
        private ActionListener loadListener;

        /**
         * Creates an ActionListener for
         * the generate JButton.
         */
        private ActionListener generateListener;

        /**
         * Creates an ActionListener for
         * the user's manual JButton.
         */
        private ActionListener usersManualListener;

        /**
         * Default constructor for ButtonPanel class.
         */
        public ButtonPanel()
        {
            setSize(WIDTH, HEIGHT);
            trainButton = new JButton("Train the System Using Your Crop and Soil Data");
            loadButton = new JButton("Load Parameters from a Previous Model Run");
            generateButton = new JButton("Generate Irrigation Recommendation Report");
            usersManualButton = new JButton("Click to Open User's Manual for this System");

            setLayout(new GridLayout(4, 1));

            trainListener = new TrainListener();
            loadListener = new LoadListener();
            generateListener = new GenerateListener();
            usersManualListener = new UsersManualListener();

            trainButton.addActionListener(trainListener);
            loadButton.addActionListener(loadListener);
            generateButton.addActionListener(generateListener);
            usersManualButton.addActionListener(usersManualListener);

            add(trainButton);
            add(loadButton);
            add(generateButton);
            add(usersManualButton);
        }

    }

    /**
     * Private class that creates an ActionListener
     * for the train button in the GUI class.
     */
    private class TrainListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            //sets boolean variables so configureModel function knows what to do
            train = true;
            load = false;

            //captures data entered in text fields by the user when button is selected
            tp.setCropType();
            tp.setSoilType();
            tp.setSensorDepth(1);
            tp.setSensorDepth(2);
            tp.setSensorDepth(3);

            //if there are no exceptions generated, train RNN; otherwise reset GUI buttons
            if (tp.displayExceptions())
            {
                //effectively resets GUI to act like no buttons have been selected
                resetButtonSelected();
            }
            else
            {
                if (fp.getPath() != null)
                {

                    setNumColumns();

                    int save = JOptionPane.showConfirmDialog(null, "Do you want to save the parameters " +
                            "from the trained model?", "Save Parameters", JOptionPane.YES_NO_OPTION);

                    configureModel();

                    //have to configure model prior to calling this method
                    determineModelVariables();

                    RegressionEvaluation eval = model.runModel(save);

                    //if user opts to save file and eval is not null (i.e., save was successful)
                    if (save == JOptionPane.YES_OPTION && eval != null)
                    {
                        JOptionPane.showMessageDialog(null, "File saved: " + model.getSavedParametersFile());
                    }

                    //displays RNN stats and train success message, if RNN is trained (regardless of whether save occurred)
                    if (eval != null)
                    {
                        JOptionPane.showMessageDialog(null, "RNN successfully trained!\n\nBelow are the stats for the RNN that was created." +
                                "\n\nEvalution Statistics: " + eval.stats(), "RNN Stats", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {
                        //display message if eval is null
                        JOptionPane.showMessageDialog(null, "An error occurred while trying to save model parameters.  " +
                                "Please try again.", "Save Error", JOptionPane.ERROR_MESSAGE);
                    }

                }
                else
                {
                    JOptionPane.showMessageDialog(null, "A file must be uploaded in order to train the model." +
                            "  Please upload a file and try again.", "Training Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Private class that creates an ActionListener
     * for the load button in the GUI class.
     */
    private class LoadListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            //sets boolean variables so configureModel function knows what to do
            train = false;
            load = true;

            //captures data entered in text fields by the user when button is selected
            tp.setCropType();
            tp.setSoilType();
            tp.setSensorDepth(1);
            tp.setSensorDepth(2);
            tp.setSensorDepth(3);

            //if there are no exceptions generated, load RNN; otherwise reset GUI buttons
            if(tp.displayExceptions())
            {
                //effectively resets GUI to act like no buttons have been selected
                resetButtonSelected();
            }
            else
            {
                if (fp.getPath() != null)
                {
                    if(configureModel())
                    {
                        //have to configure model prior to calling this method
                        determineModelVariables();

                        setNumColumns();
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "A file must be uploaded in order to load the model " +
                            "from a previous run.  Please upload a file and try again.", "Loading Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        }
    }

    /**
     * Private class that creates an ActionListener
     * for the generate button in the GUI class.
     */
    private class GenerateListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            //captures data entered in text fields by the user when button is selected
            tp.setCropType();
            tp.setSoilType();
            tp.setSensorDepth(1);
            tp.setSensorDepth(2);
            tp.setSensorDepth(3);

            //if there are no exceptions generated, generate recommendation; otherwise reset GUI buttons
            if(tp.displayExceptions())
            {
                //effectively resets GUI to act like no buttons have been selected
                resetButtonSelected();
            }
            else
            {
                if (fp.setPath())
                {
                    setNumColumns();

                    model.setDataFilePath(fp.getPath());

                    String report = model.generateRecommendation(getNumColumns(), tp.getSensorDepth(1),
                            tp.getSensorDepth(2), tp.getSensorDepth(3));

                    JOptionPane.showMessageDialog(null, "A recommendation report was generated and can be found at " +
                            report, "Report Generated", JOptionPane.INFORMATION_MESSAGE);

                    //open recommendation report and display on screen
                    if (Desktop.isDesktopSupported())
                    {
                        try
                        {
                            File reportFile = new File(report);

                            Desktop.getDesktop().open(reportFile);
                        }
                        catch (IOException e)
                        {
                            JOptionPane.showMessageDialog(null, "There was an error opening the recommendation report. " +
                                    "Please try again.", "Error Opening File", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                }
                else
                {
                    JOptionPane.showMessageDialog(null, "A file must be uploaded in order to generate a recommendation." +
                            "  Please upload a file and try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        }
    }

    /**
     * Private class that creates an ActionListener
     * for the user's manual button in the GUI class.
     */
    private class UsersManualListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            if (Desktop.isDesktopSupported())
            {
                try
                {
                    File usersManual = new File("/Users/raulie/IRS/IRS_Users_Manual.pdf");

                    Desktop.getDesktop().open(usersManual);
                }
                catch (IOException e)
                {
                    JOptionPane.showMessageDialog(null, "There was an error opening the User's Manual. " +
                            "Please try again.", "Error Opening File", JOptionPane.ERROR_MESSAGE);
                }

            }

        }
    }
}




