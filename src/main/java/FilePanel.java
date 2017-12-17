/**
 * File:    Recommendation.java
 * Created: August 12, 2017
 * Author:  hhraulerson
 * Project: Irrigation Recommendation System (IRS)
 */

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>
 * The FilePanel class to adds a JFrame that displays the
 * Irrigation Recommendation System's GUI. This panel allows
 * the user to browse for the SMS and weather data
 * that they want to upload to the RNN.
 * </p>
 *
 * @author hhraulerson
 * @version 1.4
 */
public class FilePanel extends JPanel implements ActionListener
{
    /* Variables */

    /**
     * Stores the width of the FilePanel
     */
    private final int WIDTH = 50;

    /**
     * Stores the height of the FilePanel
     */
    private final int HEIGHT = 10;

    /**
     * Creates the browse JButton
     */
    private JButton browse;

    /**
     * Creates the upload file JLabel
     */
    private JLabel uploadLabel;

    /**
     * Creates the JTextField that will display
     * the file path that is chosen
     */
    private JTextField filePath;

    /**
     * Creates the JFileChooser that will
     * allow the user to select a file
     * to upload
     */
    private JFileChooser file;

    /**
     * Stores the file path of the file to
     * be uploaded
     */
    private String path;

    /**
     * Default constructor for FilePanel class.
     */
    public FilePanel()
    {
        setSize(WIDTH, HEIGHT);
        uploadLabel = new JLabel("Upload File: ");
        browse = new JButton("Browse");
        filePath = new JTextField("File path");
        file = new JFileChooser();

        setLayout(new GridLayout(0, 3));

        browse.addActionListener(this);

        add(uploadLabel);
        add(filePath);
        add(browse);
    }

    /* Methods */

    /**
     * Method for performing action when the Browse
     * button is selected.
     */
    public void actionPerformed(ActionEvent event)
    {
        setPath();
    }

    /**
     * Sets the file path for the file that will be
     * uploaded by creating a JFileChooser.  This
     * method also calls a method that sets the filePath
     * string.
     * @return whether a file was selected
     */
    public boolean setPath()
    {
        FileNameExtensionFilter filter1 = new FileNameExtensionFilter("csv file", "csv");
        FileNameExtensionFilter filter2 = new FileNameExtensionFilter("zip file", "zip");

        file.addChoosableFileFilter(filter1);
        file.addChoosableFileFilter(filter2);

        file.setFileFilter(filter1);
        int a = file.showOpenDialog(this);

        if(a == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                path = file.getSelectedFile().getCanonicalPath();
            }
            catch(Exception e)
            {
                JOptionPane.showMessageDialog(null, "Error setting file path!", "Error", JOptionPane.ERROR_MESSAGE);
            }

            setFilePath(getPath());

            JOptionPane.showMessageDialog(null, "File chosen: " + getPath(), "File Selected", JOptionPane.INFORMATION_MESSAGE);

            return true;
        }

        return false;
    }

    /**
     * Returns the absolute path for the file that was
     * uploaded
     * @return the path for the file that was uploaded
     */
    public String getPath()
    {
        if (path == null || path.isEmpty())
        {
            return null;
        }
        else
        {
            return path;
        }
    }

    /**
     * Sets the absolute path for the file that was uploaded
     * by the user
     * @param fp the path for the uploaded file
     */
    public void setFilePath(String fp)
    {
        filePath.setText(fp);
    }
}
