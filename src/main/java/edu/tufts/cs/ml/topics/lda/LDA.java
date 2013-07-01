package edu.tufts.cs.ml.topics.lda;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Comparator;

import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

import com.google.common.collect.Multimap;

import edu.tufts.cs.ml.util.MalletConverter;

public abstract class LDA {
  /** The default number of iterations for estimating the model. */
  public static final int DEFAULT_NUM_ITERATIONS = 2000;
  /** The default file encoding. */
  public static final String DEFAULT_ENCODING = "UTF-8";
  /** Descending Double comparator. */
  public static final Comparator<Double> DESC_COMPAR_DBL =
    new Comparator<Double>() {
      public int compare( Double o1, Double o2 ) {
        return o2.compareTo( o1 );
      }
    };
    /** ascending Double comparator. */
  public static final Comparator<Integer> ASC_COMPAR_INT =
      new Comparator<Integer>() {
        public int compare( Integer o1, Integer o2 ) {
          return o1.compareTo( o2 );
        }
      };
  /** The training data. */
  protected InstanceList trainingData;

  /**
   * Constructor using default English stop words.
   */
  public LDA() {
  }

  /**
   * Constructor including a stop words encoding.
   *
   * @param stopWordsFile
   * @param encoding
   */
  public LDA( File stopWordsFile, String encoding ) {
    MalletConverter.setStopWords( stopWordsFile, encoding );
  }

  /**
   * Get the topic assignment(s) for this Instance.
   *
   * @param instance
   * @return
   */
  public abstract Multimap<Double, Integer> getTopics( int docIdx );

  /**
   * Get the top k terms for the provided topic.
   *
   * @param instance
   * @return
   */
  public abstract String getTopTerms( int topic, int k );

  /**
   * Get the topic assignment(s) for this Instance.
   *
   * @param instanceName
   * @return
   */
  public Multimap<Double, Integer> getTopics( String instanceName ) {
    for ( int i = 0; i < trainingData.size(); i++ ) {
      if ( trainingData.get( i ).getName().equals( instanceName ) ) {
        return getTopics( i );
      }
    }

    return null; // no such instance
  }

  /**
   * Get the training data.
   *
   * @return
   */
  public InstanceList getTrainingData() {
    return trainingData;
  }

  /**
   * Print the topics to the provided PrintStream.
   *
   * @param ps
   */
  public abstract void printModel( PrintStream ps );

  /**
   * Infer the topics for each training document and print the results to the
   * provided PrintStream.
   *
   * @param ps
   */
  public abstract void printState( PrintStream ps );

  /**
   * Test the provided data.
   *
   * @param testingFile
   */
  public abstract void test( Instance testingData );

  /**
   * Test the provided data.
   *
   * @param testingFile
   */
  public void test( InstanceList testingData ) {
    for ( Instance i : testingData ) {
      test( i );
    }
  }

  /**
   * Test the provided data.
   *
   * @param testingFile
   * @throws IOException
   */
  public void test( String testingFile ) throws IOException {
    test( testingFile );
  }

  /**
   * Test the provided data.
   *
   * @param testingFile
   * @throws IOException
   */
  public void test( String testingFile, String encoding ) throws IOException {
    File f = new File( testingFile, encoding );
    InstanceList testingData = MalletConverter.loadInstancesLDA( f, encoding );

    test( testingData );
  }

  /**
   * Train the model with the data provided in the [inputFile].
   *
   * @param inputFile
   * @throws IOException
   */
  public void train( File trainingFile ) throws IOException {
    train( trainingFile, DEFAULT_ENCODING, DEFAULT_NUM_ITERATIONS );
  }

  /**
   * Train the model with the data provided in the [inputFile].
   *
   * @param inputFile
   * @param iterations
   * @throws IOException
   */
  public void train( File trainingFile, int iterations ) throws IOException {
    train( trainingFile, DEFAULT_ENCODING, iterations );
  }

  /**
   * Train the model with the data provided in the [inputFile].
   *
   * @param inputFile
   * @param encoding
   * @throws IOException
   */
  public void train( File trainingFile, String encoding ) throws IOException {
    train( trainingFile, encoding, DEFAULT_NUM_ITERATIONS );
  }

  /**
   * Train the model with the data provided in the inputFile.
   *
   * @param inputFile
   * @throws IOException
   */
  public void train( File trainingFile, String encoding, int iterations )
    throws IOException {
    InstanceList t = MalletConverter.loadInstancesLDA( trainingFile, encoding );

    train( t, iterations );
  }

  /**
   * Train the model for the default number of iterations.
   *
   * @param trainingData
   * @throws IOException
   */
  public void train( InstanceList trainingData ) throws IOException {
    train( trainingData, DEFAULT_NUM_ITERATIONS );
  }

  /**
   * Train the model.
   *
   * @param trainingData
   * @throws IOException
   */
  public abstract void train( InstanceList trainingData, int iterations )
    throws IOException;

  /**
   * Train the model with the data provided.
   *
   * @param inputFile
   * @throws IOException
   */
  public void train( String trainingData ) throws IOException {
    train( trainingData, DEFAULT_NUM_ITERATIONS );
  }

  /**
   * Train the model with the data provided.
   *
   * @param inputFile
   * @param iterations
   * @throws IOException
   */
  public void train( String trainingData, int iterations ) throws IOException {
    InstanceList t = MalletConverter.loadInstancesLDA( trainingData );

    train( t, iterations );
  }
}
