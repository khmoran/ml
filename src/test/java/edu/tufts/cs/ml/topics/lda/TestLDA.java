package edu.tufts.cs.ml.topics.lda;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import edu.tufts.cs.ml.topics.lda.HierarchicalLDA;
import edu.tufts.cs.ml.topics.lda.LDA;

public abstract class TestLDA {
  /** File containing stop words. */
  public static final String STOP_WORDS =
      "src/main/resources/stoplists/en.txt";
  /** Input file Format: (document Id) \t X \t (document text). */
  public static final String INPUT_FILE =
      "src/test/resources/docs_en_clopidogrel.csv";

  /**
   * Private constructor for utility class.
   */
  protected TestLDA() {
    // purposely not intantiable
  }

  /**
   * Print the model.
   * @param lda
   * @param modelOutput
   * @param stateOutput
   * @throws FileNotFoundException
   */
  protected void print( LDA lda, String modelOutput, String stateOutput )
    throws FileNotFoundException {
    lda.printModel( new PrintStream( new FileOutputStream(
        new File( modelOutput ) ) ) );
    lda.printState( new PrintStream( new FileOutputStream(
        new File( stateOutput ) ) ) );

    if ( lda instanceof HierarchicalLDA ) {
      HierarchicalLDA h = (HierarchicalLDA) lda;
      h.printDetailedState( new PrintStream( new FileOutputStream(
        new File( stateOutput + ".detail" ) ) ) );
    }
  }

  /**
   * Initialize the LDA model.
   * @throws IOException
   */
  public void testLDA( LDA lda, int numIterations, String modelOutput,
      String stateOutput )
    throws IOException {
    lda.train( INPUT_FILE, numIterations );
    print( lda, modelOutput, stateOutput );
  }

  /**
   * Initialize the LDA model.
   * @throws IOException
   */
  public void testLDA( LDA lda, String modelOutput, String stateOutput )
    throws IOException {
    lda.train( INPUT_FILE );
    print( lda, modelOutput, stateOutput );
  }
}
