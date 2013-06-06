package edu.tufts.cs.ml.writer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

import edu.tufts.cs.ml.Relation;
import edu.tufts.cs.ml.reader.ArffReader;
import edu.tufts.cs.ml.reader.Reader;

public class WriterTest extends TestCase {

  /**
   * Test the ArffReader with training data.
   */
  @Test
  public void testArffWriterTrainData() throws IOException {
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

    String output = testArffWriter( trainInput );
    // assert these are the same, minus capitalization and whitespace
    String cleanInput = trainInput.toLowerCase().replaceAll( "\\s", "" );
    String cleanOutput = output.toLowerCase().replaceAll( "\\s", "" );
    assertEquals( cleanInput, cleanOutput );
  }

  /**
   * Test the ArffReader.
   * @param content
   * @throws IOException
   */
  protected String testArffWriter( String content ) {
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

      File outfile = new File( "testout.arff" );
      Writer arffWriter = new ArffWriter();
      arffWriter.write( r, outfile );

      FileReader fr = new FileReader( outfile );
      BufferedReader input = new BufferedReader( fr );

      StringBuilder sb = new StringBuilder();
      String line = input.readLine();
      while ( line != null ) {
        sb.append( line );
        sb.append( "\n" );
        line = input.readLine();
      }

      input.close();
      fr.close();

      // delete the test files
      f.delete();
      outfile.delete();

      return sb.toString();
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
  public void testArffWriterTestData() {
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

    String output = testArffWriter( testInput );
    // assert these are the same, minus capitalization and whitespace
    String cleanInput = testInput.toLowerCase().replaceAll( "\\s", "" );
    String cleanOutput = output.toLowerCase().replaceAll( "\\s", "" );
    assertEquals( cleanInput, cleanOutput );
  }
}
