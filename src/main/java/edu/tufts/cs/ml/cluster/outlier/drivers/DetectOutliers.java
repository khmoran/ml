package edu.tufts.cs.ml.cluster.outlier.drivers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.cluster.Cluster;
import edu.tufts.cs.ml.cluster.ClusterSet;
import edu.tufts.cs.ml.cluster.KMeans;
import edu.tufts.cs.ml.cluster.KMedoids;
import edu.tufts.cs.ml.cluster.outlier.OutlierDetector;
import edu.tufts.cs.ml.exception.CommandLineArgumentException;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.normalize.Normalizer;
import edu.tufts.cs.ml.normalize.ZScoreNormalizer;
import edu.tufts.cs.ml.reader.ArffReader;
import edu.tufts.cs.ml.reader.Reader;
import edu.tufts.cs.ml.writer.ResultWriter;

/**
 * Detect the outliers in a given data set.
 *
 * @author Kelly Moran
 *
 */
public class DetectOutliers implements Observer {
  /** The Logger. */
  private static final Logger LOG = Logger.getLogger( DetectOutliers.class
      .getName() );
  /** The singleton instance. */
  protected static DetectOutliers instance;
  /** Output from the run. */
  protected static StringBuilder output;

  /**
   * Private constructor for utility class.
   */
  private DetectOutliers() {
    // purposely not instantiable
  }

  /**
   * Print the configuration to the console.
   *
   * @param cmd
   */
  protected static void printInfo( OutlierArguments cmd ) {
    LOG.log( Level.INFO, "Running outlier detection with: "
        + "\n\tDataset 1:\t" + cmd.getDataSet1() + "\n\tDataset 2:\t"
        + cmd.getDataSet2() + "\n\tConfidence threshold:\t"
        + cmd.getThreshold() );
  }

  /**
   * @param args
   * @throws CommandLineArgumentException
   * @throws IOException
   * @throws IncomparableFeatureVectorException
   */
  @SuppressWarnings( "unchecked" )
  public static void main( String[] args ) throws CommandLineArgumentException,
      IOException, IncomparableFeatureVectorException {
    /////////////
    //  SETUP  //
    /////////////
    instance = new DetectOutliers();
    OutlierArguments cmd = new OutlierArguments( args );
    printInfo( cmd );
    final int min_k = 2;
    final int max_k = 12;
    final int min_outliers = 50;
    final double pSizeCluster = .05;
    final double tFuzzy = cmd.getThreshold();

    /*
     * Run this once on each data set.
     */

    List<File> datasets = new ArrayList<File>();
    datasets.add( cmd.getDataSet1() );
    datasets.add( cmd.getDataSet2() );

    Reader<String> reader;
    TestRelation<String> dataset;
    Normalizer<String> n;
    int i = 1;
    for ( File f : datasets ) {
      /*
       * Get the input data set.
       */
      reader = new ArffReader<String>();
      dataset = (TestRelation<String>) reader
          .read( f, Reader.IGNORE_LABELS );

      /*
       * Normalize the data.
       */
      n = new ZScoreNormalizer<String>( dataset );
      n.normalize();

      /*
       * Create the OutlierDetector.
       */
      OutlierDetector<String> detector = new OutlierDetector<String>();

      /*
       * Run k-Means on the data.
       */
      int numOutliers = 0;
      KMeans<String> kMeans = new KMeans<String>( dataset );
      kMeans.addObserver( instance );
      output = new StringBuilder( "k-Means method:\n" );
      output.append( "\tproportional threshold: " + pSizeCluster + "\n\n" );
      for ( int k = min_k; k < max_k && numOutliers < min_outliers; k++ ) {
        ClusterSet<String> clusters = kMeans.cluster( k );
        ClusterSet<String> outliers = detector.detectSmallClusters( clusters,
            pSizeCluster );
        numOutliers = outliers.size();

        output.append( "k: " + k );
        for ( Cluster<?> c : outliers.values() ) {
          for ( FeatureVector<?> outlier : c ) {
            output.append( "\n\t" + outlier.getId() );
          }
        }
        output.append( "\n\n" );
      }

      /*
       * Write the output.
       */
      String filename = "k-means-dataset" + i + ".dat";
      File outFile = new File( filename );
      ResultWriter writer = new ResultWriter();
      writer.write( output.toString(), outFile );
      LOG.log( Level.INFO, "Output written to " + filename );

      /*
       * Now the original method (k-medoids-based).
       */
      KMedoids<String> kMedoids = new KMedoids<String>( dataset );
      kMedoids.addObserver( instance );
      int k = kMedoids.detectK( min_k, max_k );
      output = new StringBuilder( "COD method:\n" );
      ClusterSet<String> clusters = kMedoids.cluster( k );
      Map<FeatureVector<?>, Double> outliers = detector.detectAllFuzzy(
          clusters, tFuzzy );
      numOutliers = outliers.size();

      for ( FeatureVector<?> outlier : outliers.keySet() ) {
        output.append( "\n\t" + outlier.getId() + "\tconfidence: " +
            outliers.get( outlier ) );
      }
      output.append( "\n\n" );

      /*
       * Write the output.
       */
      filename = "cod-dataset" + i + ".dat";
      outFile = new File( filename );
      writer = new ResultWriter();
      writer.write( output.toString(), outFile );
      LOG.log( Level.INFO, "Output written to " + filename );
      i++;
    }

    LOG.log( Level.INFO, "Complete!" );
  }

  /**
   * Update the outputs.
   *
   * @param source
   * @param msg
   */
  public void update( Observable source, Object msg ) {
    if ( source instanceof KMeans ) {
      output.append( msg.toString() + "\n" );
    }
  }

}
