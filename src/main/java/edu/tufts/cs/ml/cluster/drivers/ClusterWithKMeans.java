package edu.tufts.cs.ml.cluster.drivers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.cluster.ClusterSet;
import edu.tufts.cs.ml.cluster.KMeans;
import edu.tufts.cs.ml.exception.CommandLineArgumentException;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.normalize.Normalizer;
import edu.tufts.cs.ml.normalize.ZScoreNormalizer;
import edu.tufts.cs.ml.reader.ArffReader;
import edu.tufts.cs.ml.reader.Reader;
import edu.tufts.cs.ml.util.MathUtil;
import edu.tufts.cs.ml.util.Util;
import edu.tufts.cs.ml.writer.ResultWriter;

/**
 * Classify the provided examples given a training set.
 *
 * @author Kelly Moran
 *
 */
public class ClusterWithKMeans implements Observer {
  /** The Logger. */
  private static final Logger LOG = Logger.getLogger( ClusterWithKMeans.class
      .getName() );
  /** The singleton instance. */
  protected static ClusterWithKMeans instance;
  /** Output from the random method. */
  protected static StringBuilder output = new StringBuilder();

  /**
   * Private constructor for utility class.
   */
  private ClusterWithKMeans() {
    // purposely not instantiable
  }

  /**
   * Print the configuration to the console.
   *
   * @param cmd
   */
  protected static void printInfo( ClusterArguments cmd ) {
    LOG.log( Level.INFO, "Running k-Means clustering algorithm with: "
        + "\n\tTraining file:\t" + cmd.getDataSet()
        + "\n\tOutput file:\t" + cmd.getOutputFile() );
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
    // ///////////
    // SETUP //
    // ///////////
    instance = new ClusterWithKMeans();
    ClusterArguments cmd = new ClusterArguments( args );
    printInfo( cmd );
    int[] indices = { 775, 1020, 200, 127, 329, 1626, 1515, 651, 658, 328,
      1160, 108, 422, 88, 105, 261, 212, 1941, 1724, 704, 1469, 635, 867,
      1187, 445, 222, 1283, 1288, 1766, 1168, 566, 1812, 214, 53, 423, 50,
      705, 1284, 1356, 996, 1084, 1956, 254, 711, 1997, 1378, 827, 1875, 424,
      1790, 633, 208, 1670, 1517, 1902, 1476, 1716, 1709, 264, 1, 371, 758,
      332, 542, 672, 483, 65, 92, 400, 1079, 1281, 145, 1410, 664, 155, 166,
      1900, 1134, 1462, 954, 1818, 1679, 832, 1627, 1760, 1330, 913, 234,
      1635, 1078, 640, 833, 392, 1425, 610, 1353, 1772, 908, 1964, 1260, 784,
      520, 1363, 544, 426, 1146, 987, 612, 1685, 1121, 1740, 287, 1383, 1923,
      1665, 19, 1239, 251, 309, 245, 384, 1306, 786, 1814, 7, 1203, 1068,
      1493, 859, 233, 1846, 1119, 469, 1869, 609, 385, 1182, 1949, 1622, 719,
      643, 1692, 1389, 120, 1034, 805, 266, 339, 826, 530, 1173, 802, 1495,
      504, 1241, 427, 1555, 1597, 692, 178, 774, 1623, 1641, 661, 1242, 1757,
      553, 1377, 1419, 306, 1838, 211, 356, 541, 1455, 741, 583, 1464, 209,
      1615, 475, 1903, 555, 1046, 379, 1938, 417, 1747, 342, 1148, 1697,
      1785, 298, 1485, 945, 1097, 207, 857, 1758, 1390, 172, 587, 455, 1690,
      1277, 345, 1166, 1367, 1858, 1427, 1434, 953, 1992, 1140, 137, 64,
      1448, 991, 1312, 1628, 167, 1042, 1887, 1825, 249, 240, 524, 1098, 311,
      337, 220, 1913, 727, 1659, 1321, 130, 1904, 561, 1270, 1250, 613, 152,
      1440, 473, 1834, 1387, 1656, 1028, 1106, 829, 1591, 1699, 1674, 947,
      77, 468, 997, 611, 1776, 123, 979, 1471, 1300, 1007, 1443, 164, 1881,
      1935, 280, 442, 1588, 1033, 79, 1686, 854, 257, 1460, 1380, 495, 1701,
      1611, 804, 1609, 975, 1181, 582, 816, 1770, 663, 737, 1810, 523, 1243,
      944, 1959, 78, 675, 135, 1381, 1472 };
    final int maxK = 12;
    final int maxI = 25;

    /*
     * Get the input data set.
     */
    File trainFile = cmd.getDataSet();
    Reader<String> reader = new ArffReader<String>();
    TestRelation<String> dataset = (TestRelation<String>) reader.read(
        trainFile, Reader.IGNORE_LABELS );

    /*
     * Normalize the data.
     */
    Normalizer<String> n = new ZScoreNormalizer<String>( dataset );
    n.normalize();

    /*
     * Run k-Means on the data.
     */
    KMeans<String> kMeans = new KMeans<String>( dataset );
    kMeans.addObserver( instance );

    // run maxI iterations for each k value
    output.append( "Random initialization method:\n\n" );
    output.append( "k, mean(k), mean(k)-2stdev(k), mean(k)+2stdev(k)\n" );
    TreeMap<Integer, List<Double>> sses =
        new TreeMap<Integer, List<Double>>();
    for ( int k = 1; k <= maxK; k++ ) {
      List<Double> sseList = new ArrayList<Double>();
      for ( int i = 0; i < maxI; i++ ) {
        // ex. k=12, i=24: start at 288; end at 299 (max)
        int start = k*i;  // ex. k=5, i=0: start at 0; k=5, i=1: start at 5
        int end = start+k; // ex. k=5, i=0: end at 4; k=5, i=1: end at 9
        int[] subset = Arrays.copyOfRange( indices, start, end );
        ClusterSet<String> clusters = kMeans.cluster( k, subset );
        double sse = clusters.calculateSSE();
        sseList.add( sse );
      }

      sses.put( k, sseList );

      double mean = Util.round( MathUtil.calcMean( sses.get( k ) ), 2 );
      double stdev = Util.round( MathUtil.calcStandardDeviation(
          sses.get( k ) ), 2 );
      double confLow = Util.round( mean-2*stdev, 2 );
      double confHigh = Util.round( mean+2*stdev, 2 );
      System.out.println( "k=" + k + ": \tmean sse: " + mean +
          ", stdev: " + stdev + ", conf-low: " + confLow +
          ", conf-high: " + confHigh );
      output.append( k + ", " + mean + ", " + confLow + ", " +
          confHigh + "\n" );
    }

    // now run 1 iteration per k with density initial centroid selection method
    output.append( "\nDensity initialization method:\n\n" );
    output.append( "k, sse\n" );
    for ( int k = 1; k <= maxK; k++ ) {
      ClusterSet<String> clusters = kMeans.cluster( k );
      double sse = clusters.calculateSSE();
      output.append( k + ", " + sse + "\n" );
      System.out.println( "k=" + k + ": \tsse=" + sse );
    }

    /*
     * Write the output.
     */
    File outFile = cmd.getOutputFile();
    ResultWriter writer = new ResultWriter();
    writer.write( output.toString(), outFile );

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
