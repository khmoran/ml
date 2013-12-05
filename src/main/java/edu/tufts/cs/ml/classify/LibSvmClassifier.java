package edu.tufts.cs.ml.classify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.TreeMultimap;

import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.Relation;
import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.UnlabeledFeatureVector;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.writer.SvmLightWriter;
import edu.tufts.cs.ml.writer.Writer;


public class LibSvmClassifier implements Classifier<Integer> {
  private static final Logger LOG =  Logger.getLogger(
      SvmLightClassifier.class.getName() );
  /** The prediction script. */
  protected static final String LIBSVM_PREDICT =
      "src/main/resources/libsvm/svm-predict";
  /** The training script. */
  protected static final String LIBSVM_TRAIN =
      "src/main/resources/libsvm/svm-train";
  /** The training file. */
  protected static final String TRAIN_FILE = "train.dat";
  /** The testing file. */
  protected static final String TEST_FILE = "test.dat";
  /** The model file. */
  protected static final String MODEL_FILE = "model";
  /** The predictions file. */
  protected static final String PREDICTIONS_FILE = "predictions";
  /** Train relation. */
  protected TrainRelation<Integer> train;
  /** The SVM model. */
  protected File model;

  public void train( TrainRelation<Integer> trainRelation ) {
    this.train = trainRelation;
    try {
      this.model = rankLearn( trainRelation );
    } catch ( Exception e  ) {
      e.printStackTrace();
      LOG.log( Level.SEVERE, "Could not call svm-rank process to train model.",
          e.getStackTrace() );
    }
  }

  public void classify( UnlabeledFeatureVector<Integer> testInstance )
      throws IncomparableFeatureVectorException {

  }

  public void classify( TestRelation<Integer> testRelation )
      throws IncomparableFeatureVectorException {

  }

  public TreeMultimap<Double, FeatureVector<Integer>> rank( TestRelation<Integer> testRelation )
      throws IncomparableFeatureVectorException {
    try {
      List<Double> predictions = rankClassify( testRelation, this.model );
      
      assert testRelation.size() == predictions.size();

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
  

  protected File rankLearn( TrainRelation<Integer> trainData ) throws IOException, InterruptedException {
    Writer w = new SvmLightWriter();
    File trainFile = new File( TRAIN_FILE );
    w.write( trainData, trainFile );
    
    assert trainFile.exists();

    ProcessBuilder processBuilder = new ProcessBuilder( LIBSVM_TRAIN, "-s", "0",
        "-t", "0", "-b", "1", TRAIN_FILE, MODEL_FILE );
    processBuilder.redirectErrorStream( true );

    Process process = processBuilder.start();
    copy( process.getInputStream(), System.out );
    process.waitFor();

    File model = new File( MODEL_FILE );

    assert model.exists();
    
    return model;
  }
  
  protected List<Double> rankClassify( Relation<?> testData, File model )
      throws IOException, InterruptedException {
    Writer w = new SvmLightWriter();
    File testFile = new File( TEST_FILE );
    w.write( testData, testFile );

    ProcessBuilder processBuilder = new ProcessBuilder(
        LIBSVM_PREDICT, "-b", "1", TEST_FILE, MODEL_FILE, PREDICTIONS_FILE );
    processBuilder.redirectErrorStream( true );

    Process process = processBuilder.start();
    copy( process.getInputStream(), System.out );
    process.waitFor();

    File predictions = new File( PREDICTIONS_FILE );
    
    assert predictions.exists();

    List<Double> probabilities = new ArrayList<Double>();
    BufferedReader br = new BufferedReader( new FileReader( predictions ) );
    String line = br.readLine(); // skip header
    while ( (line = br.readLine() ) != null ) {
       String[] parts = line.split( " " );
       double label = Double.valueOf( parts[0] );
       double prob = Double.valueOf( parts[1] );
       double total = label;
       if ( label > 0 ) {
         total += prob;
       } else {
         total -= prob;
       }
       probabilities.add( total );
    }
    br.close();

    new File( TRAIN_FILE ).delete();
    new File( MODEL_FILE ).delete();
    testFile.delete();
    predictions.delete();

    return probabilities;
  }
  
  private static void copy( InputStream in, OutputStream out ) throws IOException {
    int c = 0;
    while ( c != -1 ) {
        c = in.read();
        out.write( (char) c );
    }
  }
}
