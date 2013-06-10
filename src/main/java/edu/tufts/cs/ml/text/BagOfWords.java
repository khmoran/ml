package edu.tufts.cs.ml.text;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
  /** The minimum times a word can occur to be included in the bag of words. */
  protected static final int MIN_OCCURS = 6;
  /** Set of Citations already used to train the Classifier. */
  protected Set<String> trained = new HashSet<String>();
  /** The training data. */
  protected TrainRelation<E> train;
  /** The tokenizer to use. */
  protected Tokenizer tokenizer;

  /**
   * Default constructor.
   * @param classifier
   */
  public BagOfWords( File stopWordsFile ) {
    this.train = new TrainRelation<E>( "BoW", new Metadata() );
    this.tokenizer = new Tokenizer( stopWordsFile );
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
      for ( String u : unnormalized ) {
        String norm = Util.normalize( u );
        if ( !norm.isEmpty() ) {
          terms.add( norm );
        }
      }
    }

    for ( String term : terms.elementSet() ) {
      if ( terms.count( term ) >= MIN_OCCURS ) { // don't count infreq. words
        train.getMetadata().put( term, "boolean" );
      }
    }
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
      train.add( createLabeledFV( id, text, clazz ) );
      trained.add( id );
    }
  }

  /**
   * Get the training data.
   * @return
   */
  public TrainRelation<E> getTrainingData() {
    return this.train;
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

    createFV( text, lfv );

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

    createFV( text, ufv );

    return ufv;
  }

  /**
   * Populate the FeatureVector with Bag of Words.
   * @param c
   * @param fv
   */
  protected void createFV( String text, FeatureVector<E> fv ) {
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
      if ( train.getMetadata().containsKey( term ) ) {
        DoubleFeature bagFeat = new DoubleFeature(
            term, (double) terms.count( term ) );
        fv.put( term, bagFeat );
      }
    }
  }
}
