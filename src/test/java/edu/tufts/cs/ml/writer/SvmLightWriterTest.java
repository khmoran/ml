package edu.tufts.cs.ml.writer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.testng.annotations.Test;

import edu.tufts.cs.ml.Relation;
import edu.tufts.cs.ml.reader.Reader;
import edu.tufts.cs.ml.reader.SvmLightReader;

public class SvmLightWriterTest {

  /**
   * Test the SvmLightWriter with training data.
   */
  @Test
  public void testSvmLightWriterTrainData() throws IOException {
    String trainInput = "# query 1\n" +
        "3 qid:1 1:1 2:1 3:0 4:0.2 5:0\n" +
        "2 qid:1 1:0 2:0 3:1 4:0.1 5:1\n" +
        "1 qid:1 1:0 2:1 3:0 4:0.4 5:0\n" +
        "1 qid:1 1:0 2:0 3:1 4:0.3 5:0\n" +
        "# query 2\n" +
        "1 qid:2 1:0 2:0 3:1 4:0.2 5:0\n" +
        "2 qid:2 1:1 2:0 3:1 4:0.4 5:0\n" +
        "1 qid:2 1:0 2:0 3:1 4:0.1 5:0\n" +
        "1 qid:2 1:0 2:0 3:1 4:0.2 5:0\n" +
        "# query 3\n" +
        "2 qid:3 1:0 2:0 3:1 4:0.1 5:1\n" +
        "3 qid:3 1:1 2:1 3:0 4:0.3 5:0\n" +
        "4 qid:3 1:1 2:0 3:0 4:0.4 5:1\n" +
        "1 qid:3 1:0 2:1 3:1 4:0.5 5:0";

    String output = testSvmLightWriter( trainInput );
    // assert these are the same, minus capitalization and whitespace
    String cleanInput = trainInput.toLowerCase().replaceAll( "\\s", "" );
    String cleanOutput = output.toLowerCase().replaceAll( "\\s", "" );
    assert cleanInput.equals( cleanOutput );
  }

  /**
   * Test the SvmLightReader.
   * @param content
   * @throws IOException
   */
  protected String testSvmLightWriter( String content ) {
    System.out.println( "-----\nInput:\n" + content );
    try {
      File f = new File( "test.dat" );
      FileWriter fw = new FileWriter( f );
      BufferedWriter writer = new BufferedWriter( fw );
      writer.write( content );
      writer.close();
      fw.close();

      Reader<String> reader = new SvmLightReader<String>();
      Relation<?> r = reader.read( f );

      File outfile = new File( "testout.dat" );
      Writer svmWriter = new SvmLightWriter();
      svmWriter.write( r, outfile );

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

      System.out.println( "\n\nOutput:\n" + sb.toString() );

      return sb.toString();
    } catch ( Exception e ) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Test the SvmLightReader with testing data.
   * @throws IOException
   */
  @Test
  public void testSvmLightWriterTestData() {
    String testInput = "4 qid:4 1:1 2:0 3:0 4:0.2 5:1\n" +
        "3 qid:4 1:1 2:1 3:0 4:0.3 5:0\n" +
        "2 qid:4 1:0 2:0 3:0 4:0.2 5:1\n" +
        "1 qid:4 1:0 2:0 3:1 4:0.2 5:0";

    String output = testSvmLightWriter( testInput );
    // assert these are the same, minus capitalization and whitespace
    String cleanInput = testInput.toLowerCase().replaceAll( "\\s", "" );
    String cleanOutput = output.toLowerCase().replaceAll( "\\s", "" );
    assert cleanInput.equals( cleanOutput );
  }
}
