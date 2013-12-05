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
import edu.tufts.cs.ml.writer.RankSvmWriter;
import edu.tufts.cs.ml.writer.SvmLightWriter;
import edu.tufts.cs.ml.writer.Writer;

public class RankSvmClassifier implements Classifier<Integer> {
  private static final Logger LOG =  Logger.getLogger(
      RankSvmClassifier.class.getName() );
  /** Train relation. */
  protected TrainRelation<Integer> train;
  /** The SVM model. */
  protected File model;
  /** The c tradeoff parameter. */
  protected double c;

  /**
   * Default constructor.
   * @param c
   */
  public RankSvmClassifier( double c ) {
    this.c = c;
  }

  public void train( TrainRelation<Integer> trainRelation ) {
    this.train = trainRelation;
    try {
      this.model = rankLearn( trainRelation );
    } catch ( Exception e  ) {
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
      LOG.log( Level.SEVERE, "Could not call svm-rank process to classify instances.",
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
    File trainFile =  new File( "train.dat" );
    w.write( trainData, trainFile );
    
    assert trainFile.exists();
    
    ProcessBuilder processBuilder = new ProcessBuilder(
        "src/main/resources/svm_rank/svm_rank_learn", "-c",
        String.valueOf( c ), "train.dat", "model" );
    processBuilder.redirectErrorStream( true );

    Process process = processBuilder.start();
    copy( process.getInputStream(), System.out );
    process.waitFor();
    
    File model = new File( "model" );

    assert model.exists();
    
    return model;
  }
  
  protected List<Double> rankClassify( Relation<?> testData, File model )
      throws IOException, InterruptedException {
    Writer w = new RankSvmWriter();
    w.write( testData, new File( "test.dat" ) );
    
    ProcessBuilder processBuilder = new ProcessBuilder(
        "src/main/resources/svm_rank/svm_rank_classify", "test.dat", "model", "predictions" );
    processBuilder.redirectErrorStream( true );

    Process process = processBuilder.start();
    copy( process.getInputStream(), System.out );
    process.waitFor();

    File predictions = new File( "predictions" );
    
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

}
