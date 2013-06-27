package edu.tufts.cs.ml.topics.lda;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.TreeSet;

import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

public class BasicLDA extends LDA {
  /** The topic model. */
  protected ParallelTopicModel model;
  /** The prior on the number of topics. */
  protected final int numTopics;
  /** The alpha prior. */
  protected final double alpha;
  /** The beta prior. */
  protected final double beta;

  /**
   * Default constructor using default stop words file.
   *
   * @param stopWordsFile
   * @param numTopics
   * @param alpha
   * @param beta
   */
  public BasicLDA( int numTopics, double alpha, double beta ) {
    this( null, null, numTopics, alpha, beta );
  }

  /**
   * Default constructor.
   *
   * @param stopWordsFile
   * @param numTopics
   * @param alpha
   * @param beta
   */
  public BasicLDA( File stopWordsFile, int numTopics, double alpha,
      double beta ) {
    this( stopWordsFile, null, numTopics, alpha, beta );
  }

  /**
   * Constructor including a stop words encoding.
   *
   * @param stopWordsFile
   * @param encoding
   * @param numTopics
   * @param alpha
   * @param beta
   */
  public BasicLDA( File stopWordsFile, String encoding, int numTopics,
      double alpha, double beta ) {
    super( stopWordsFile, encoding );
    this.numTopics = numTopics;
    this.alpha = alpha;
    this.beta = beta;
  }

  @Override
  public void printModel( PrintStream ps ) {
    // The data alphabet maps word IDs to strings
    Alphabet dataAlphabet = trainingData.getDataAlphabet();

    /*
     * sStore each topic with a list of words and their weight:
     * (topic name) - (word): (weight), ...
     */
    int i = 0;
    StringBuilder sb;
    for ( TreeSet<IDSorter> set : model.getSortedWords() ) {
      sb = new StringBuilder().append( i );
      sb.append( " - " );
      for ( IDSorter s : set ) {
        sb.append( dataAlphabet.lookupObject( s.getID() ) ).append( ":" )
            .append( s.getWeight() ).append( ", " );
      }
      ps.append( sb.append( "\n" ).toString() );
      i++;
    }

    ps.close();
  }

  @Override
  public String getTopTerms( int topic, int k ) {
    // The data alphabet maps word IDs to strings
    Alphabet dataAlphabet = trainingData.getDataAlphabet();
    TreeSet<IDSorter> terms = model.getSortedWords().get( topic );
    if ( k > terms.size() ) k = terms.size(); // don't go outside bounds

    StringBuilder sb = new StringBuilder();
    for ( int i = 0; i < k; i++ ) {
      IDSorter s = (IDSorter) terms.toArray()[i];
      sb.append( dataAlphabet.lookupObject( s.getID() ) + ", " );
      //    .append( s.getWeight() ).append( ", " );
    }

    int idx = sb.lastIndexOf( ", " );
    if ( idx > 0 ) {
      sb.replace( idx, idx+2, "" );
    }

    return sb.toString();
  }

  @Override
  public void printState( PrintStream ps ) {
    /**
     * Get topic per document results
     */
    TopicInferencer inferencer = model.getInferencer();

    for ( int i = 0; i < this.trainingData.size(); i++ ) {
      StringBuilder sb1 = new StringBuilder();
      Instance inst = this.trainingData.get( i );
      double[] testProbabilities = inferencer.getSampledDistribution(
        inst, 10, 1, 5 );
      sb1.append( inst.getName() );

      for ( int j = 0; j < testProbabilities.length; j++ ) {
        sb1.append( '\t' ).append( testProbabilities[j] );
      }
      ps.append( sb1.append( '\n' ).toString() );
    }

    ps.close();
  }

  @Override
  public void test( Instance testingData ) {
    // TODO Auto-generated method stub
  }

  @Override
  public void train( InstanceList trainingData, int iterations )
    throws IOException {
    this.trainingData = trainingData;

    // Create a model with T topics, alpha_t = 0.01, beta_w = 0.01
    // Note that the first parameter is passed as the sum over topics, while
    // the second is the parameter for a single dimension of the Dirichlet
    // prior.
    this.model = new ParallelTopicModel( numTopics, alpha, beta );
    this.model.addInstances( trainingData );

    // Use two parallel samplers, which each look at one half the corpus and
    // combine
    // statistics after every iteration.
    this.model.setNumThreads( 2 );

    // Run the model for x iterations and stop
    this.model.setNumIterations( iterations );
    this.model.estimate();
  }

  @Override
  public Multimap<Double, Integer> getTopics( int docIdx ) {
    Multimap<Double, Integer> map = TreeMultimap.create( DESC_COMPAR_DBL,
        ASC_COMPAR_INT );
    TopicInferencer inferencer = model.getInferencer();

    double[] testProbabilities = inferencer.getSampledDistribution(
      trainingData.get( docIdx ), 10, 1, 5 );

    for ( int j = 0; j < testProbabilities.length; j++ ) {
      map.put( testProbabilities[j], j );
    }

    return map;
  }

}
