package edu.tufts.cs.ml.reader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

import edu.tufts.cs.ml.Relation;

public class ReaderTest extends TestCase {

  /**
   * Test the ArffReader with training data.
   */
  @Test
  public void testArffReaderTrainData() throws IOException {
    String trainInput = "@relation HW1_TRAIN\n" +
       "\n" +
      "@attribute sepal_length real\n" +
      "@attribute sepal_width real\n" +
      "@attribute petal_length real\n" +
      "@attribute petal_width real\n" +
      "@attribute CLASS_LABEL {versicolor,virginica,setosa}\n" +
      "\n" +
      "@data\n" +
      "6.1,2.9,4.7,1.4,versicolor,x4\n" +
      "6.0,2.7,5.1,1.6,versicolor,x5";

    String output = testArffReader( trainInput );
    // assert these are the same, minus capitalization and whitespace
    String cleanInput = trainInput.toLowerCase().replaceAll( "\\s", "" ).trim();
    String cleanOutput = output.toLowerCase().replaceAll( "\\s", "" ).trim();
    assertEquals( cleanInput, cleanOutput );
  }

  /**
   * Test the ArffReader.
   * @param content
   * @throws IOException
   */
  protected String testArffReader( String content ) {
    System.out.println( "-----\nInput:\n" + content );
    try {
      File f = new File( "test.arff" );
      FileWriter fw = new FileWriter( f );
      BufferedWriter writer = new BufferedWriter( fw );
      writer.write( content );
      writer.close();
      fw.close();

      Reader<String> reader = new ArffReader<String>();
      Relation<?> r = reader.read( f );

      System.out.println( "\n\nOutput:\n" + r.toString() );

      // delete the test file
      f.delete();

      return r.toString();
    } catch ( Exception e ) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Test the ArffReader with testing data.
   * @throws IOException
   */
  @Test
  public void testArffReaderTestData() {
    String testInput = "@relation HW1_TEST\n" +
      "\n" +
      "@attribute sepal_length real\n" +
      "@attribute sepal_width real\n" +
      "@attribute petal_length real\n" +
      "@attribute petal_width real\n" +
      "\n" +
      "@data\n" +
      "6.7,3.1,4.4,1.4,x1\n" +
      "4.4,3.2,1.3,0.2,x2\n" +
      "5.3,3.7,1.5,0.2,x3";

    String output = testArffReader( testInput );
    // assert these are the same, minus capitalization and whitespace
    String cleanInput = testInput.toLowerCase().replaceAll( "\\s", "" );
    String cleanOutput = output.toLowerCase().replaceAll( "\\s", "" );
    assertEquals( cleanInput, cleanOutput );
  }
}
