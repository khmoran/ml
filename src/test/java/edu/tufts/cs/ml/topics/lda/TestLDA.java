package edu.tufts.cs.ml.topics.lda;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import cc.mallet.types.InstanceList;

import com.google.common.collect.Multimap;

import edu.tufts.cs.ml.util.Util;

public abstract class TestLDA {
  /** The Logger. */
  protected static final Logger LOG =  Logger.getLogger(
      TestLDA.class.getName() );
  /** File containing stop words. */
  public static final String STOP_WORDS =
      "src/main/resources/stoplists/en.txt";

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
    LOG.info( "Printing results..." );
    lda.printModel( new PrintStream( new FileOutputStream(
        new File( modelOutput ) ) ) );
    //lda.printState( new PrintStream( new FileOutputStream(
    //    new File( stateOutput ) ) ) );
    //printOrdered( new PrintStream( new FileOutputStream(
    //    new File( stateOutput ) ) ), lda );

    if ( lda instanceof HierarchicalLDA ) {
      HierarchicalLDA h = (HierarchicalLDA) lda;
      h.printDetailedState( new PrintStream( new FileOutputStream(
        new File( stateOutput + ".detail" ) ) ) );
    }
    LOG.info( "Done!" );
  }

  /**
   * Print the ordered topics for each record (descending).
   * @param ps
   * @param lda
   */
  protected void printOrdered( PrintStream ps, LDA lda ) {
    InstanceList instances = lda.getTrainingData();
    for ( int i = 0; i < instances.size(); i++ ) {
      Multimap<Double, Integer> topics = lda.getTopics( i );
      ps.print( instances.get( i ).getName() );

      List<Double> sorted = new ArrayList<Double>( topics.keySet() );
      Collections.sort( sorted );
      Collections.reverse( sorted ); // descending order

      int j = 0;
    out:
      for ( Double key : sorted ) {
        for ( Integer value : topics.get( key ) ) {
          if ( j > 2 ) break out;
          ps.print( "," + value + ":" + Util.round( key, 6 ) );
          j++;
        }
      }
      ps.println();
    }
  }

  /**
   * Initialize the LDA model.
   * @throws IOException
   */
  public void testLDA( File inputFile, LDA lda, int numIterations,
      String modelOutput, String stateOutput )
    throws IOException {
    LOG.info( "Training LDA..." );
    lda.train( inputFile, numIterations );
    print( lda, modelOutput, stateOutput );
  }

  /**
   * Initialize the LDA model.
   * @throws IOException
   */
  public void testLDA( File inputFile, LDA lda, String modelOutput,
      String stateOutput ) throws IOException {
    LOG.info( "Training LDA..." );
    lda.train( inputFile );
    print( lda, modelOutput, stateOutput );
  }

  /**
   * Initialize the LDA model.
   * @throws IOException
   */
  public void testLDA( String inputStr, LDA lda, int numIterations,
      String modelOutput, String stateOutput )
    throws IOException {
    LOG.info( "Training LDA..." );
    lda.train( inputStr, numIterations );
    print( lda, modelOutput, stateOutput );
  }

  /**
   * Initialize the LDA model.
   * @throws IOException
   */
  public void testLDA( String inputStr, LDA lda, String modelOutput,
      String stateOutput ) throws IOException {
    LOG.info( "Training LDA..." );
    lda.train( inputStr );
    print( lda, modelOutput, stateOutput );
  }
}
