package edu.tufts.cs.ml.topics.lda;

import java.io.File;
import java.io.IOException;

import org.testng.annotations.Test;



public class TestHierarchicalLDA extends TestLDA {
  /** Input file Format: (document Id) \t X \t (document text). */
  public static final String INPUT_FILE =
      "src/test/resources/docs_en_clopidogrel.csv";
  /** File to store topics. */
  public static final String MODEL_OUTPUT =
      "output/hlda.model";
  /** File to store data on which document belongs to which topic. */
  public static final String STATE_OUTPUT =
      "output/hlda.state";
  /** The number of iterations for estimating the model. */
  public static final int NUM_ITERATIONS = 1000; // recommended: 1000 - 2000
  /** The alpha sum prior. */
  public static final int NUM_LEVELS = 3;

  /**
   * Initialize the LDA model.
   * @throws IOException
   */
  @Test
  public void testHLDA() throws IOException {
    HierarchicalLDA lda = new HierarchicalLDA(
        new File( STOP_WORDS ), NUM_LEVELS );
    testLDA( new File( INPUT_FILE ), lda, NUM_ITERATIONS, MODEL_OUTPUT,
        STATE_OUTPUT );

    assert lda != null;
  }

  /**
   * Private constructor for utility class.
   */
  public TestHierarchicalLDA() {
    super();
  }
}
