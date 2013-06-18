package edu.tufts.cs.ml.topics.lda;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveNonAlpha;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

import com.google.common.collect.Multimap;

public abstract class LDA {
  /** File containing stop words. */
  public static final String DEFAULT_STOP_LIST =
      "src/main/resources/stoplists/en.txt";
  /** The default file encoding. */
  public static final String DEFAULT_ENCODING = "UTF-8";
  /** The default number of iterations for estimating the model. */
  public static final int DEFAULT_NUM_ITERATIONS = 2000;
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
  /** The file containing the list of stop words. */
  protected final File stopWords;
  /** The encoding for the stop words file. */
  protected final String stopWordsEncoding;
  /** The training data. */
  protected InstanceList trainingData;

  /**
   * Constructor using default English stop words.
   */
  public LDA() {
    this.stopWords = new File( DEFAULT_STOP_LIST );
    this.stopWordsEncoding = DEFAULT_ENCODING;
  }

  /**
   * Default constructor.
   *
   * @param stopWordsFile
   */
  public LDA( String stopWordsFile ) {
    this.stopWords = new File( stopWordsFile );
    this.stopWordsEncoding = DEFAULT_ENCODING;
  }

  /**
   * Constructor including a stop words encoding.
   *
   * @param stopWordsFile
   * @param encoding
   */
  public LDA( String stopWordsFile, String encoding ) {
    this.stopWords = new File( stopWordsFile );
    this.stopWordsEncoding = encoding;
  }

  /**
   * Get the topic assignment(s) for this Instance.
   *
   * @param instance
   * @return
   */
  public abstract Multimap<Double, Integer> getTopics( int docIdx );

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
   * Load the documents into an InstanceList.
   *
   * @return
   * @throws IOException
   */
  public InstanceList loadInstances( File input, String encoding )
    throws IOException {
    Reader fileReader = new InputStreamReader( new FileInputStream( input ),
        encoding );

    return loadInstances( fileReader );
  }

  /**
   * Load the documents into an InstanceList.
   *
   * @return
   * @throws IOException
   */
  protected InstanceList loadInstances( Reader r ) {
    ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

    // Pipes: lowercase, tokenize, remove stopwords, map to features
    pipeList.add( new CharSequenceLowercase() );
    pipeList.add( new CharSequence2TokenSequence( Pattern
        .compile( "\\p{L}[\\p{L}\\p{P}]+\\p{L}" ) ) );
    pipeList.add( new TokenSequenceRemoveStopwords( stopWords,
        stopWordsEncoding, false, false, false ) );
    pipeList.add( new TokenSequenceRemoveNonAlpha() );
    pipeList.add( new TokenSequence2FeatureSequence() );

    InstanceList instances = new InstanceList( new SerialPipes( pipeList ) );

    // create instances with: 3: data; 2: label; 1: name fields
    instances.addThruPipe( new CsvIterator( r, Pattern
        .compile( "^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)" ), 3, 2, 1 ) );

    return instances;
  }

  /**
   * Load the documents into an InstanceList.
   *
   * @return
   * @throws IOException
   */
  public InstanceList loadInstances( String csvInput )
    throws IOException {
    Reader strReader = new StringReader( csvInput );

    return loadInstances( strReader );
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
    test( testingFile, DEFAULT_ENCODING );
  }

  /**
   * Test the provided data.
   *
   * @param testingFile
   * @throws IOException
   */
  public void test( String testingFile, String encoding ) throws IOException {
    File f = new File( testingFile, encoding );
    InstanceList testingData = loadInstances( f, encoding );

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
    InstanceList t = loadInstances( trainingFile, encoding );

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
    InstanceList t = loadInstances( trainingData );

    train( t, iterations );
  }
}
