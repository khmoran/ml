package edu.tufts.cs.ml.topics.lda;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;


public class TestHierarchicalLDA extends TestLDA {
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
    HierarchicalLDA lda = new HierarchicalLDA( new File( STOP_WORDS ), NUM_LEVELS );
    testLDA( lda, NUM_ITERATIONS, MODEL_OUTPUT, STATE_OUTPUT );

    assertTrue( lda != null );
  }

  /**
   * Private constructor for utility class.
   */
  protected TestHierarchicalLDA() {
    super();
  }
}
