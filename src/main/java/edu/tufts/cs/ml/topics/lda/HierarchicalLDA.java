package edu.tufts.cs.ml.topics.lda;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.util.Randoms;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

public class HierarchicalLDA extends LDA {
  /** The topic model. */
  protected cc.mallet.topics.HierarchicalLDA model;
  /** The number of levels in the hierarchy. */
  protected int numLevels;

  /**
   * Default constructor.
   *
   * @param stopWordsFile
   * @param numLevels
   */
  public HierarchicalLDA( File stopWordsFile, int numLevels ) {
    this( stopWordsFile, DEFAULT_ENCODING, numLevels );
  }

  /**
   * Constructor including a stop words encoding.
   *
   * @param stopWordsFile
   * @param encoding
   * @param numLevels
   */
  public HierarchicalLDA( File stopWordsFile, String encoding,
      int numLevels ) {
    super( stopWordsFile, encoding );
    this.numLevels = numLevels;
  }

  @Override
  public void printModel( PrintStream ps ) {
    this.model.printNodes( ps );
  }

  @Override
  public void printState( PrintStream ps ) {
    this.model.printState( ps );
  }

  /**
   * Print a token-by-token representation of the model state.
   * @param ps
   */
  public void printDetailedState( PrintStream ps ) {
    this.model.printDetailedState( ps );
  }

  @Override
  public void test( Instance testingData ) {
    // TODO Auto-generated method stub
  }

  @Override
  public void train( InstanceList trainingData, int iterations )
    throws IOException {
    this.trainingData = trainingData;

    // Create a model with L levels
    this.model = new cc.mallet.topics.HierarchicalLDA();
    this.model.initialize( this.trainingData, null, numLevels, new Randoms() );
    this.model.estimate( iterations );
  }

  @Override
  public Multimap<Double, Integer> getTopics( int docIdx ) {
    Multimap<Double, Integer> map = TreeMultimap.create( DESC_COMPAR_DBL,
        ASC_COMPAR_INT );
    for ( int topic : model.getTopics( docIdx ) ) {
      map.put( 1.0, topic ); // each doc is in only one topic
    }                        // but each topic has multiple levels

    return map;
  }

  @Override
  public String getTopTerms( int topic, int k ) {
    // TODO Auto-generated method stub
    return null;
  }
}
