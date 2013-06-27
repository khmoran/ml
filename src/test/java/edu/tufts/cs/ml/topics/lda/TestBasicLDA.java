package edu.tufts.cs.ml.topics.lda;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.junit.Test;

import cc.mallet.types.InstanceList;

import com.google.common.collect.Multimap;

public class TestBasicLDA extends TestLDA {
  /** File to store topics. */
  public static final String MODEL_OUTPUT =
      "output/lda.model";
  /** File to store data on which document belongs to which topic. */
  public static final String STATE_OUTPUT =
      "output/lda.state";
  /** The expected number of topics. */
  public static final int NUM_TOPICS = 20;
  /** The alpha sum prior. */
  public static final double ALPHA_SUM_PRIOR = 10;
  /** The beta prior. */
  public static final double BETA_PRIOR = 0.01;

  /**
   * Initialize the LDA model.
   * @throws IOException
   */
  @Test
  public void testBasicLDA() throws IOException {
    BasicLDA lda = new BasicLDA(
        new File( STOP_WORDS ), NUM_TOPICS, ALPHA_SUM_PRIOR, BETA_PRIOR );
    testLDA( lda, MODEL_OUTPUT, STATE_OUTPUT );

    assertTrue( lda != null );

    InstanceList instances = lda.getTrainingData();
    for ( int i = 0; i < instances.size(); i++ ) {
      Multimap<Double, Integer> topics = lda.getTopics( i );
      System.out.println( instances.get( i ).getName() + ": " );
      for ( Entry<Double, Integer> e : topics.entries() ) {
        System.out.println( "\t" + e.getValue() + ":\t " +
          e.getKey()*100 + "%" );
      }
    }
  }

  /**
   * Private constructor for utility class.
   */
  protected TestBasicLDA() {
    super();
  }
}
