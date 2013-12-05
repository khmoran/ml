package edu.tufts.cs.ml.classify;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.TreeMultimap;

import edu.tufts.cs.ml.DoubleFeature;
import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.Metadata;
import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.UnlabeledFeatureVector;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.writer.SvmLightWriter;
import edu.tufts.cs.ml.writer.Writer;

public class SvmLightPairwiseTransformClassifier implements Classifier<Integer> {
  /** The Logger. */
  private static final Logger LOG =  Logger.getLogger(
      SvmLightPairwiseTransformClassifier.class.getName() );
  /** The training file. */
  protected static final String TRAIN_FILE = "train.dat";
  /** The testing file. */
  protected static final String TEST_FILE = "test.dat";
  /** The model file. */
  protected static final String MODEL_FILE = "model";
  /** The predictions file. */
  protected static final String PREDICTIONS_FILE = "predictions";
  /** Positive class label. */
  public static final int POS = 1;
  /** Negative class label. */
  public static final int NEG = -1;
  /** The SVM model. */
  protected File model;
  /** The c tradeoff parameter. */
  protected double c;

  /**
   * Default constructor.
   * @param c
   */
  public SvmLightPairwiseTransformClassifier( double c ) {
    this.c = c;

    File testFile = new File( TEST_FILE );

    // clean things up
    if ( testFile.exists() ) {
      testFile.delete();
    }
  }

  public void train( TrainRelation<Integer> trainRelation ) {
    try {
      File pairwiseFile = generatePairwiseTrainingSet(
          trainRelation );
      this.model = rankLearn( pairwiseFile );
    } catch ( Exception e  ) {
      e.printStackTrace();
      LOG.log( Level.SEVERE, "Could not call svm-rank process to train model.",
          e.getStackTrace() );
    }
  }

  public void classify( UnlabeledFeatureVector<Integer> testInstance )
      throws IncomparableFeatureVectorException {
    // TODO implement
  }

  public void classify( TestRelation<Integer> testRelation )
      throws IncomparableFeatureVectorException {
    rank( testRelation ); // ranks but doesn't return the resulting list
  }

  public TreeMultimap<Double, FeatureVector<Integer>> rank( TestRelation<Integer> testRelation )
      throws IncomparableFeatureVectorException {

    try {
      File test = new File( TEST_FILE );
      if ( !test.exists() ) {
        Writer w = new SvmLightWriter();
        w.write( testRelation, test );
      }

      List<Double> predictions = rankClassify( test, this.model );

      assert testRelation.size() == predictions.size();
      LOG.info( "Predictions size: " + predictions.size() +
          " (should be " + testRelation.size() + ")" );

      TreeMultimap<Double, FeatureVector<Integer>> map = TreeMultimap.create();
      for ( int i = 0; i < testRelation.size(); i++ ) {
        FeatureVector<Integer> fv = testRelation.get( i );
        Double prediction = predictions.get( i );
        map.put( prediction, fv );
      }

      int rank = 0;
      for ( Double prediction : map.keySet() ) {
        for ( FeatureVector<Integer> fv : map.get( prediction ) ) {
          fv.setRank( ++rank );
        }
      }

      return map;
    } catch ( Exception e ) {
      e.printStackTrace();
      LOG.log( Level.SEVERE, "Could not call svm-light process to classify instances.",
          e.getStackTrace() );
    }

    return null;
  }

  public double getCertainty( UnlabeledFeatureVector<Integer> testInstance )
      throws IncomparableFeatureVectorException {
    // TODO implement
    return 0.0;
  }
  
  protected File rankLearn( File trainFile ) throws IOException, InterruptedException {
    assert trainFile.exists();
    
    ProcessBuilder processBuilder = new ProcessBuilder(
        "src/main/resources/svm_light/svm_learn", "-c",
        String.valueOf( c ), TRAIN_FILE, MODEL_FILE );
    processBuilder.redirectErrorStream( true );

    Process process = processBuilder.start();
    copy( process.getInputStream(), System.out );
    process.waitFor();
    
    File model = new File( "model" );

    assert model.exists();
    
    return model;
  }
  
  protected List<Double> rankClassify( File testData, File model )
      throws IOException, InterruptedException {
    
    ProcessBuilder processBuilder = new ProcessBuilder(
        "src/main/resources/svm_light/svm_classify", TEST_FILE, MODEL_FILE, PREDICTIONS_FILE );
    processBuilder.redirectErrorStream( true );

    Process process = processBuilder.start();
    copy( process.getInputStream(), System.out );
    process.waitFor();

    File predictions = new File( PREDICTIONS_FILE );
    
    assert predictions.exists();

    List<Double> probabilities = new ArrayList<Double>();
    BufferedReader br = new BufferedReader( new FileReader( predictions ) );
    String line;
    while ( (line = br.readLine() ) != null ) {
       probabilities.add( Double.valueOf( line ) );
    }
    br.close();

    return probabilities;
  }
  
  private static void copy( InputStream in, OutputStream out ) throws IOException {
    int c = 0;
    while ( c != -1 ) {
        c = in.read();
        out.write( (char) c );
    }
  }

  /**
   * 
   * @param toSort
   * @param pivot
   * @param pmidRankings
   * @return
   */
  protected static int compare( String toSort, String pivot,
      List<FeatureVector<Integer>> pmidRankings ) {
    String jointId = pivot.toString() + "_" + toSort.toString();
    for ( FeatureVector<Integer> relativeFv : pmidRankings ) {
      if ( relativeFv.getId().equals( jointId ) ) {
        return relativeFv.getRank();
      }
    }

    throw new RuntimeException( "Joint id " + jointId + " not found in rankings!" );
  }

  /**
   * 
   * @param bowTrain
   * @return
   * @throws IOException 
   */
  protected File generatePairwiseTrainingSet( TrainRelation<Integer> train ) throws IOException {
    TrainRelation<Integer> posBowTrain = getTrainingData( train, POS );
    TrainRelation<Integer> negBowTrain = getTrainingData( train, NEG );

    SvmLightWriter w = new SvmLightWriter();
    File pairwiseFile = new File( TRAIN_FILE );
    FileWriter fw = new FileWriter( pairwiseFile );
    BufferedWriter bw = new BufferedWriter( fw );

    // create pairwise vectors in both directions so that not all labels
    // are from the same class
    long numPairs = 0;
    numPairs += addPairwiseVectors( w, bw, posBowTrain, negBowTrain, POS );
    numPairs += addPairwiseVectors( w, bw, negBowTrain, posBowTrain, NEG );
    
    bw.close();
    
    LOG.log( Level.INFO, "Standard training set size: " + train.size() );
    LOG.log( Level.INFO, "Pairwise training set size: " + numPairs +
        " (should be " + ( posBowTrain.size()*negBowTrain.size()*2 ) + ")" );

    return pairwiseFile;
  }

  /**
   * 
   * @param pairwiseTrain
   * @param set1
   * @param set2
   * @param label
   * @throws IOException 
   */
  protected long addPairwiseVectors( SvmLightWriter w, BufferedWriter bw,
      TrainRelation<Integer> set1, TrainRelation<Integer> set2, Integer label )
      throws IOException {

    long numPairs = 0;
    for ( LabeledFeatureVector<Integer> outerLfv : set1 ) {
      for ( LabeledFeatureVector<Integer> innerLfv : set2 ) {
        LabeledFeatureVector<Integer> pairwiseLfv = new LabeledFeatureVector<Integer>(
            label, outerLfv.getId() + "_" + innerLfv.getId() );
        for ( String featName : set1.getMetadata().keySet() ) {
          DoubleFeature pairwiseFeat = subtractFeatures( featName, outerLfv, innerLfv );
          if ( pairwiseFeat != null ) { // sparse matrix; don't need 0 features
            pairwiseLfv.put( featName, pairwiseFeat );
          }
        }

        w.write( set1.getMetadata(), pairwiseLfv, bw );
        numPairs++;
      }
    }

    return numPairs;
  }

  /**
   * 
   * @param featName
   * @param fv1
   * @param fv2
   * @return
   */
  protected DoubleFeature subtractFeatures( String featName,
      FeatureVector<Integer> fv1, FeatureVector<Integer> fv2 ) {
    Double posLfvValue = 0.0;
    Double negLfvValue = 0.0;
    if ( fv1.containsKey( featName ) ) {
      posLfvValue = (Double) fv1.get( featName ).getValue();
    }
    if ( fv2.containsKey( featName ) ) {
      negLfvValue = (Double) fv2.get( featName ).getValue();
    }
    double diff = posLfvValue - negLfvValue;
    if ( diff != 0 ) {
      return new DoubleFeature( featName, diff );
    } else {
      return null; // sparse matrix, don't need 0 features
    }
  }

  /**
   * Get the training data.
   * @return
   */
  protected TrainRelation<Integer> getTrainingData( TrainRelation<Integer> train, Integer clazz ) {
    TrainRelation<Integer> subRelation = new TrainRelation<Integer>( "sub-relation",
        (Metadata) train.getMetadata().clone() );
    for ( LabeledFeatureVector<Integer> lfv : train ) {
      if ( lfv.getLabel().equals( clazz ) ) {
        subRelation.add( lfv );
      }
    }

    return subRelation;
  }
  
}
