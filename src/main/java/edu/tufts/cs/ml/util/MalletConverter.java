package edu.tufts.cs.ml.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.FeatureSequence2FeatureVector;
import cc.mallet.pipe.FeatureValueString2FeatureVector;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.Metadata;
import edu.tufts.cs.ml.Relation;
import edu.tufts.cs.ml.TrainRelation;

public class MalletConverter {
  /** File containing stop words. */
  public static final String DEFAULT_STOP_LIST =
      "src/main/resources/stoplists/en.txt";
  /** The default file encoding. */
  public static final String DEFAULT_ENCODING = "UTF-8";
  /** The file containing the list of stop words. */
  protected static File stopWords = new File( DEFAULT_STOP_LIST );
  /** The encoding for the stop words file. */
  protected static String stopWordsEncoding = DEFAULT_ENCODING;

  /**
   * Set the stop words.
   * @param stopWords
   * @param encoding
   */
  public static void setStopWords( File f, String encoding ) {
    if ( f != null ) {
      stopWords = f;
    }
    if ( encoding != null ) {
      stopWordsEncoding = encoding;
    }
  }

  /**
   * Load the documents into an InstanceList.
   *
   * @return
   * @throws IOException
   */
  public static InstanceList loadInstances( File input, String encoding )
    throws IOException {
    Reader fileReader = new InputStreamReader( new FileInputStream( input ),
        encoding );

    return loadInstances( fileReader );
  }

  /**
   * Prepare Instances for use with LDA.
   * @param r
   * @return
   */
  public static InstanceList loadInstancesLDA( Reader r ) {
    ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

    // Pipes: lowercase, tokenize, remove stopwords, map to features
    pipeList.add( new Target2Label() );
    pipeList.add( new CharSequenceLowercase() );
    pipeList.add( new CharSequence2TokenSequence( Pattern
        .compile( "\\p{L}[\\p{L}\\p{P}]+\\p{L}" ) ) );
    pipeList.add( new TokenSequenceRemoveStopwords( stopWords,
        stopWordsEncoding, false, false, false ) );
    pipeList.add( new TokenSequence2FeatureSequence() );
    SerialPipes pipes = new SerialPipes( pipeList );

    InstanceList instances = new InstanceList( pipes );

    // create instances with: 3: data; 2: label; 1: name fields
    instances.addThruPipe( new CsvIterator( r, Pattern
        .compile( "(.*)\t(.*)\t(.*)" ), 3, 2, 1 ) );

    return instances;
  }

  /**
   * Prepare Instances for use as Bag of Words.
   * @param r
   * @return
   */
  public static InstanceList loadInstancesBoW( Reader r ) {
    ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

    // Pipes: lowercase, tokenize, remove stopwords, map to features
    pipeList.add( new Target2Label() );
    pipeList.add( new CharSequenceLowercase() );
    pipeList.add( new CharSequence2TokenSequence( Pattern
        .compile( "\\p{L}[\\p{L}\\p{P}]+\\p{L}" ) ) );
    pipeList.add( new TokenSequenceRemoveStopwords( stopWords,
        stopWordsEncoding, false, false, false ) );
    pipeList.add( new TokenSequence2FeatureSequence() );
    pipeList.add( new FeatureSequence2FeatureVector() );
    SerialPipes pipes = new SerialPipes( pipeList );

    InstanceList instances = new InstanceList( pipes );

    // create instances with: 3: data; 2: label; 1: name fields
    instances.addThruPipe( new CsvIterator( r, Pattern
        .compile( "(.*)\t(.*)\t(.*)" ), 3, 2, 1 ) );

    return instances;
  }

  /**
   * Prepare Instances for use as general purpose feature vectors.
   * @param r
   * @return
   */
  public static InstanceList loadInstances( Reader r ) {
    ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

    // Pipes: lowercase, tokenize, remove stopwords, map to features
    pipeList.add( new Target2Label() );
    pipeList.add( new CharSequenceLowercase() );
    pipeList.add( new CharSequence2TokenSequence( Pattern.compile(
        "(.*)=(.*)" ) ) ); //"\\p{L}[\\p{L}\\p{P}]+\\p{L}" ) ) );
    pipeList.add( new TokenSequenceRemoveStopwords( stopWords,
        stopWordsEncoding, false, false, false ) );
    pipeList.add( new TokenSequence2FeatureSequence() );
    pipeList.add( new FeatureValueString2FeatureVector() );
    SerialPipes pipes = new SerialPipes( pipeList );

    InstanceList instances = new InstanceList( pipes );

    // create instances with: 3: data; 2: label; 1: name fields
    instances.addThruPipe( new CsvIterator( r, Pattern
        .compile( "(.*)\t(.*)\t(.*)" ), 3, 2, 1 ) );

    return instances;
  }

  /**
   * Load the documents into an InstanceList.
   *
   * @return
   * @throws IOException
   */
  public static InstanceList loadInstances( String tsvInput ) {
    Reader strReader = new StringReader( tsvInput );

    return loadInstances( strReader );
  }

  /**
   * Load the documents into an InstanceList.
   *
   * @return
   * @throws IOException
   */
  public static InstanceList loadInstancesBoW( String tsvInput ) {
    Reader strReader = new StringReader( tsvInput );

    return loadInstancesBoW( strReader );
  }


  /**
   * Load the documents into an InstanceList.
   *
   * @return
   * @throws IOException
   */
  public static InstanceList loadInstancesBoW( File input, String encoding )
    throws IOException {
    Reader fileReader = new InputStreamReader( new FileInputStream( input ),
        encoding );

    return loadInstancesBoW( fileReader );
  }

  /**
   * Load the documents into an InstanceList.
   *
   * @return
   * @throws IOException
   */
  public static InstanceList loadInstancesLDA( String tsvInput ) {
    Reader strReader = new StringReader( tsvInput );

    return loadInstancesLDA( strReader );
  }


  /**
   * Load the documents into an InstanceList.
   *
   * @return
   * @throws IOException
   */
  public static InstanceList loadInstancesLDA( File input, String encoding )
    throws IOException {
    Reader fileReader = new InputStreamReader( new FileInputStream( input ),
        encoding );

    return loadInstancesLDA( fileReader );
  }

  /**
   * Convert to the Mallet format.
   * @param train
   * @return
   * @throws IOException
   */
  public static InstanceList convert( Relation<?> train ) {
    boolean isTrain = ( train instanceof TrainRelation ) ? true : false;

    StringBuffer sb = new StringBuffer();
    for ( FeatureVector<?> fv : train ) {
      sb.append( fv.getId() + "\t" );

      if ( isTrain ) {
        sb.append( (
            (LabeledFeatureVector<?>) fv ).getLabel().toString() + "\t" );
      } else {
        sb.append( "X\t" );
      }

      for ( String feature : train.getMetadata().keySet() ) {
        if ( fv.containsKey( feature ) ) {
          sb.append( feature + "=" + fv.get( feature ).getValue() + " " );
        }
      }
      sb.append( "\n" );
    }

    InstanceList il = loadInstances( sb.toString() );

    return il;
  }

  /**
   * Convert to the Mallet format.
   * @param fv
   * @param m
   * @return
   */
  public static Instance convert( FeatureVector<?> fv, Metadata m ) {
    boolean isTrain = ( fv instanceof LabeledFeatureVector ) ? true : false;

    StringBuffer sb = new StringBuffer( fv.getId() + "\t" );

    if ( isTrain ) {
      sb.append( (
          (LabeledFeatureVector<?>) fv ).getLabel().toString() + "\t" );
    } else {
      sb.append( "1\t" );
    }

    for ( String feature : m.keySet() ) {
      if ( fv.containsKey( feature ) ) {
        sb.append( feature + "=" + fv.get( feature ).getValue() + " " );
      }
    }

    InstanceList il = loadInstances( sb.toString() );

    return il.get( 0 );
  }

  /**
   * Private constructor for utility class.
   */
  private MalletConverter() {
    // purposely not instantiable
  }
}
