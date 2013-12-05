package edu.tufts.cs.ml.text;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import edu.tufts.cs.ml.DoubleFeature;
import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.Metadata;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.UnlabeledFeatureVector;
import edu.tufts.cs.ml.util.Util;

public class BagOfWords<E> {
  /** The default minimum times a word must occur in the corpus to be included. */
  protected static final int DEFAULT_MIN_OCCURS = 4;
  /** The default minimum length a word must be to be included in the bag of words. */
  protected static final int DEFAULT_MIN_LENGTH = 2;
  /** Set of Citations already used to train the Classifier. */
  protected Set<String> trained = new HashSet<String>();
  /** The training data. */
  protected TrainRelation<E> train;
  /** The tokenizer to use. */
  protected Tokenizer tokenizer;
  /** Whether to use integer feature names instead of the words themselves. */
  protected boolean integerFeatureNames;
  /** Whether to use bigrams. */
  protected boolean bigrams;
  /** Mapping from the String feature name to Integer value. */
  Map<String, Integer> wordIndexMap = new HashMap<String, Integer>();
  /** The minimum times a word must occur in the corpus to be included. */
  protected int minOccurs = DEFAULT_MIN_OCCURS;
  /** The minimum length a word must be to be included in the bag of words. */
  protected int minLength = DEFAULT_MIN_LENGTH;

  /**
   * Default constructor.
   * @param classifier
   */
  public BagOfWords( File stopWordsFile ) {
    this.train = new TrainRelation<E>( "BoW", new Metadata() );
    this.tokenizer = new Tokenizer( stopWordsFile );
  }

  /**
   * Constructor that takes an array of options.
   * @param classifier
   */
  public BagOfWords( File stopWordsFile, boolean integerFeatureNames,
      boolean bigrams ) {
    this( stopWordsFile );
    this.integerFeatureNames = integerFeatureNames;
    this.bigrams = bigrams;
  }

  /**
   * Constructor that takes an array of options.
   * @param classifier
   */
  public BagOfWords( File stopWordsFile, boolean integerFeatureNames,
      boolean bigrams, int minOccurs, int minLength ) {
    this( stopWordsFile, integerFeatureNames, bigrams );
    this.minOccurs = minOccurs;
    this.minLength = minLength;
  }

  /**
   * Create the Bag of Words features.
   * @param citations
   */
  public void createFeatures( Collection<String> documents ) {
    Multiset<String> terms = HashMultiset.create();

    for ( String s : documents ) {
      List<String> unnormalized = tokenizer.tokenize( s );

      // normalize them
      for ( int i = 0; i < unnormalized.size(); i++ ) {
        String u = unnormalized.get( i );
        String norm = Util.normalize( u );
        if ( !norm.isEmpty() ) {
          terms.add( norm );
        }
        
        if ( bigrams && (i < unnormalized.size()-1) ) {
          String second = unnormalized.get( i+1 );
          String normSecond = Util.normalize( second );
          if ( !normSecond.isEmpty() ) {
            terms.add( norm + "_" + normSecond );
          }
        }
      }
    }

    int i = 0;
    for ( String term : terms.elementSet() ) {
      if ( terms.count( term ) >= minOccurs // don't count infreq. words
          && term.length() >= minLength ) { // or super short words
        if ( !integerFeatureNames ) {
          train.getMetadata().put( term, "boolean" );
        } else {
          wordIndexMap.put( term, i++ );
          train.getMetadata().put( String.valueOf( i ), "boolean" );
        }
      }
    }
  }

  /**
   * Clear the training set.
   */
  public void clearTrainingSet() {
    train.clear();
    trained.clear();
  }

  /**
   * Train the classifier with this instance.
   * @param fv
   * @param clazz
   */
  public void train( FeatureVector<E> fv, E clazz ) {
    if ( !trained.contains( fv.getId() ) ) {
      LabeledFeatureVector<E> lfv = new LabeledFeatureVector<E>(
          clazz, fv.getId() );
      lfv.putAll( fv );
      lfv.setQid( fv.getQid() );
      lfv.setRank( fv.getRank() );
      train.add( lfv );
      trained.add( fv.getId() );
    }
  }

  /**
   * Train the classifier with this instance.
   * @param c
   * @param clazz
   */
  public void train( String id, String text, E clazz ) {
    if ( !trained.contains( id  ) ) {
      LabeledFeatureVector<E> lfv = createLabeledFV( id, text, clazz );
      train.add( lfv );
      trained.add( id );
    }
  }

  /**
   * Get the training data.
   * @return
   */
  public TrainRelation<E> getTrainingData() {
    TrainRelation<E> copy = new TrainRelation<E>( train.getName(),
        (Metadata) train.getMetadata().clone() );
    copy.addAll( train );
    
    return copy;
  }

  /**
   * Get the training data.
   * @return
   */
  public TrainRelation<E> getTrainingData( E clazz ) {
    TrainRelation<E> subRelation = new TrainRelation<E>( "sub-relation",
        (Metadata) train.getMetadata().clone() );
    for ( LabeledFeatureVector<E> lfv : train ) {
      if ( lfv.getLabel().equals( clazz ) ) {
        subRelation.add( lfv );
      }
    }

    return subRelation;
  }

  /**
   * Create a Labeled Feature Vector using Bag of Words.
   * @param c
   * @param clazz
   * @return
   */
  protected LabeledFeatureVector<E> createLabeledFV( String id, String text,
      E clazz ) {
    LabeledFeatureVector<E> lfv = new LabeledFeatureVector<E>(
        clazz, id );

    populateFV( text, lfv );
    
    return lfv;
  }

  /**
   * Create an Unlabeled Feature Vector using Bag of Words.
   * @param c
   * @return
   */
  public UnlabeledFeatureVector<E> createUnlabeledFV( String id, String text ) {
    UnlabeledFeatureVector<E> ufv = new UnlabeledFeatureVector<E>(
        id );

    populateFV( text, ufv );

    return ufv;
  }

  /**
   * Populate the FeatureVector with Bag of Words.
   * @param c
   * @param fv
   */
  protected void populateFV( String text, FeatureVector<E> fv ) {
    List<String> unnormalized = tokenizer.tokenize( text );

    Multiset<String> terms = HashMultiset.create();
    for ( String token : unnormalized ) {
      String norm = Util.normalize( token );
      if ( !norm.isEmpty() ) {
        terms.add( norm );
      }
    }

    // sparse representation... no need to put in 0's
    for ( String term : terms.elementSet() ) {
      // rare words don't get included, so check first
      if ( !integerFeatureNames && train.getMetadata().containsKey( term ) ) {
          DoubleFeature bagFeat = new DoubleFeature( term, (double) terms.count( term ) );
          fv.put( term, bagFeat );
      } else if ( integerFeatureNames &&
          train.getMetadata().containsKey( String.valueOf( wordIndexMap.get( term ) ) ) ) {
          String featureName = String.valueOf( wordIndexMap.get( term ) );
          DoubleFeature bagFeat = new DoubleFeature( featureName, (double) terms.count( term ) );
          fv.put( featureName, bagFeat );
      }
    }
  }
}
