/*
 * File:    IRSIterator.java
 * Created: October 8, 2017
 * Author:  hhraulerson
 * Project: Irrigation Recommendation System (IRS)
 */

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import java.io.*;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * <p>
 * The IRSIterator class is a custom DataSetIterator used to extract DataSets
 * from a comma separated value (csv) file.  The csv file must be properly
 * formatted with 1-3 soil moisture sensors in order for it to work correctly.
 * </p>
 *
 * @author hhraulerson
 * @version 1.5
 */
public class IRSIterator implements DataSetIterator
{
    /* Variables */

    /**
     * Number of input columns in the data.
     */
    private int columns;

    /**
     * Size of each example set (number of days).
     */
    private int exampleSize;

    /**
     * Size of each minibatch (number of sets).
     */
    private int miniBatches;

    /**
     * The RecordReader is used to read the data from the csv.
     */
    private RecordReader reader;

    /**
     * The total records in the csv.
     */
    private int fileRecords;

    /**
     * The next data point to read from for the RecordReader.
     */
    private int nextStartOffset;

    /**
     * The preProcessor is used to normalize the DataSet before they're used.
     */
    private NormalizerMinMaxScaler preprocessor;

    /* Constructors */

    /**
     * Default constructor for the IRSIterator class.
     * @param csvFilePath path to csv file to use for generating samples
     * @param cols number of columns used in a DataSet
     * @param miniBatchSize number of records per minibatch
     * @param exampleLength number of records for the example set
     */
    public IRSIterator(String csvFilePath, int cols, int miniBatchSize, int exampleLength)
    {
        setColumns(cols);
        setMiniBatches(miniBatchSize);
        nextStartOffset = 0;
        setPreProcessor();
        getPreProcessor().fitLabel(false);

        //print to console - this isn't a message to be displayed to the user
        System.out.println("CSV file path is " + csvFilePath);

        File dataFile = new File(csvFilePath);

        //skip the CSV header (i.e., the column titles)
        int numLinesToSkip = 1;

        setRecords(numLinesToSkip, dataFile, csvFilePath);

        setExampleSize(exampleLength);

        //print to console - this isn't a message to be displayed to the user
        System.out.println("Number of fileRecords " + fileRecords);

        String delimiter = ",";

        reader = new CSVRecordReader(numLinesToSkip, delimiter);

        try
        {
            reader.initialize(new FileSplit(dataFile));

            //print to console - this isn't a message to be displayed to the user
            System.out.println("CSVRecordReader initialized.");
        }
        catch (Exception e)
        {
            //print to console - this isn't a message to be displayed to the user
            System.err.println("Caught Exception: Error initializing CSVRecordReader.");
        }
    }

    /**
     * Determines if another data set can be retrieved.  This is done by determining if there
     * are more records in the file than the number of examples times the number of minibatches.
     * @return if there's enough data to retrieve another DataSet
     */
    public boolean hasNext()
    {
        return fileRecords - nextStartOffset >= numExamples() * batch();
    }

    /**
     * Returns the next DataSet based upon the saved minibatch size.
     * Will automatically normalize the data based on the preprocessor.
     * @return the next DataSet
     */
    public DataSet next()
    {
        return next(batch());
    }

    /**
     * Returns the next DataSet with a specific number of minibatches.
     * Will automatically normalize the data based on the preprocessor.
     * @param num the number of minibatches in this DataSet
     * @return the next DataSet with num minibatches
     */
    public DataSet next(int num)
    {
        //if csv has enough records, get next DataSet
        if (hasNext())
        {
            //scan to next record
            scanTo(nextStartOffset);

            //get next DataSet
            DataSet ds = getNextDataSet(num, numExamples());

            //preprocessor normalizes DataSet that's retrieved
            preprocessor.fit(ds);
            preprocessor.transform(ds);

            //update nextStartOffset to reflect new DataSet that was retrieved
            nextStartOffset += num * numExamples();

            return ds;
        }
        else
        {
            //return null if csv doesn't contain enough records for another DataSet
            return null;
        }
    }

    /**
     * Returns the next DataSet with a specific number of minibatches and examples.
     * Does not automatically normalize.
     * @param numBatches the number of minibatches in the DataSet
     * @param numExamples the number of examples in each minibatch
     * @return the next DataSet with numBatches minibatches of numExamples examples each
     */
    public DataSet getNextDataSet(int numBatches, int numExamples)
    {

        // index 0 = numBatches
        // index 1 = inputColumns()
        // index 2 = numExamples
        //Why 'f' order here? See http://deeplearning4j.org/usingrnns.html#data section "Alternative: Implementing a custom DataSetIterator"
        INDArray input = Nd4j.create(new int[]{numBatches, inputColumns(), numExamples}, 'f');
        INDArray labels = Nd4j.create(new int[]{numBatches, totalOutcomes(), numExamples}, 'f');

        for (int i = 0; i < numBatches; i++)
        {
            for (int j = 0; j < numExamples; j++)
            {
                List nextValues = reader.next();

                //first column is date (skip it)
                for (int k = 1; k < inputColumns(); k++)
                {
                    //get the value in each column for a single record and put in input INDArray
                    input.putScalar(new int[]{i, k, j}, Float.parseFloat(nextValues.get(k).toString()));
                }

                double label = Double.parseDouble(nextValues.get(inputColumns()).toString());
                labels.putScalar(new int[]{i, 0, j}, label);
            }
        }

        return new DataSet(input, labels);
    }

    /**
     * Total number of times you can call next() based upon default
     * miniBatchSize and exampleLength.
     * @return total number of DataSets that can be retrieved from next()
     */
    public int totalExamples()
    {
        return fileRecords / (batch() * numExamples());
    }

    /**
     * Sets the number of columns for the csv file.
     * @param cols the number of columns in the uploaded csv file
     */
    public void setColumns(int cols)
    {
        if (cols <=0)
        {
            System.out.println("\nNumber of columns provided is <= 0; number of columns is " +
                    "being set to default size of 7.\n");

            columns = 7;
        }
        else
        {
            columns = cols;

            //print to console - this isn't a message to be displayed to the user
            System.out.println("Number of columns was set to " + inputColumns());
        }
    }

    /**
     * How many input columns there are.
     * @return the number of input columns
     */
    public int inputColumns()
    {
        return columns;
    }

    /**
     * Total number of output columns.
     * @return number of output columns
     */
    public int totalOutcomes()
    {
        return 1;
    }

    /**
     * Resets the RecordReader and next line to read.
     */
    public void reset()
    {
        nextStartOffset = 1;

        reader.reset();
    }

    /**
     * Sets the RecordReader to read a specific line next.
     * @param line the next line that should be read from the RecordReader
     */
    public void scanTo(int line)
    {
        reader.reset();

        int curLine = 1;

        while (curLine < line && reader.hasNext())
        {
            reader.next();
            ++curLine;
        }
    }

    /**
     * Returns true since the IRSIterator can be reset.
     * @return true since the IRSIterator is able to be reset
     */
    public boolean resetSupported()
    {
        return true;
    }

    /**
     * Returns true if this can be ran async.
     * @return that async is supported
     */
    @Override
    public boolean asyncSupported()
    {
        return true;
    }

    /**
     * Sets the miniBatches variable.
     * @param mb the number of minibatchs for the RNN to be trained on
     */
    public void setMiniBatches(int mb)
    {
        if (mb <= 0)
        {
            System.out.println("\nMini batch size provided is <= 0; mini batch size is " +
                    "being set to default size of 1.\n");

            miniBatches = 1;
        }
        else
        {
            miniBatches = mb;

            //print to console - this isn't a message to be displayed to the user
            System.out.println("Number of minibatches was set to " + batch());
        }
    }

    /**
     * Returns how many sets are in a minibatch.
     * @return how many sets are in a minibatch
     */
    public int batch()
    {
        return miniBatches;
    }

    /**
     * Returns which DataSet the iterator is currently on.
     * @return the current DataSet
     */
    public int cursor()
    {
        return nextStartOffset / (batch() * numExamples());
    }

    /**
     * Sets the exampleSize variable.
     * @param ex the number of examples for the RNN to be trained on
     */
    public void setExampleSize(int ex)
    {
        //set exampleSize
        if (ex >= fileRecords || ex <= 0)
        {
            exampleSize = fileRecords - 1;
        }
        else
        {
            exampleSize = ex;
        }

        //print to console - this isn't a message to be displayed to the user
        System.out.println("Number of examples was set to " + numExamples());
    }

    /**
     * How many examples the exampleSize variable was set to.
     * @return the number of examples
     */
    public int numExamples()
    {
        return exampleSize;
    }

    /**
     * Sets the preProcessor to a NormalizerMinMaxScaler preProcessor.
     */
    public void setPreProcessor()
    {
        preprocessor = new NormalizerMinMaxScaler(-10, 10);
    }

    /**
     * Returns the preProcessor currently in use.
     * @return the preProcessor currently in use
     */
    @Override
    public NormalizerMinMaxScaler getPreProcessor()
    {
        return preprocessor;
    }

    /**
     * Sets the preProcessor to a DataSetPreProcessor;
     * not currently supported.
     * @param preProcessor the preprocessor to use
     */
    @Override
    public void setPreProcessor(DataSetPreProcessor preProcessor)
    {
        throw new UnsupportedOperationException("Functionality not implemented");
    }

    /**
     * Sets the fileRecords variable.
     * @param numLinesToSkip number of lines to skip (i.e., skip file header)
     * @param file the file that the number of records is being calculated for
     * @param path the path for the file passed in - used if Exceptions occur
     */
    private void setRecords(int numLinesToSkip, File file, String path)
    {
        //get file length
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(file));

            fileRecords = -numLinesToSkip;

            while (br.readLine() != null)
            {
                fileRecords++;
            }

            br.close();
        }
        catch (FileNotFoundException e)
        {
            //print to console - this isn't a message to be displayed to the user
            System.out.println("Caught FileNotFoundException: could not open the data file at " + path);
        }
        catch (IOException e)
        {
            //print to console - this isn't a message to be displayed to the user
            System.out.println("Caught IOException: could not read file at " + path);
        }
    }

    /**
     * Returns labels list; not currently supported.
     * @return a list of labels
     */
    @Override
    public List<String> getLabels()
    {
        throw new UnsupportedOperationException("Functionality not implemented");
    }

    /**
     * Removes the iterator; not currently supported.
     */
    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Functionality not implemented");
    }

}
