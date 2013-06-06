package edu.tufts.cs.ml.classify;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.junit.Test;

import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.UnlabeledFeatureVector;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.reader.ArffReader;


public class ClassifierTest extends TestCase {

  /**
   * Test the Classifier.
   */
  @Test
  public void testClassifier() {
  }

  /**
   * Test the function that calculates distances.
   * @throws IOException
   * @throws IncomparableFeatureVectorException
   */
  @SuppressWarnings( "unchecked" )
  @Test
  public void testCalculateDistances() throws IOException,
    IncomparableFeatureVectorException {

    /* includes:
     *  6.1,2.9,4.7,1.4,versicolor,x4
        6.0,2.7,5.1,1.6,versicolor,x5
        6.0,2.7,5.1,1.6,virginica,x6 // an engineered tie with the one above
        ...
     */
    File trainFile = new File( "src/test/resources/train.arff" );
    ArffReader<String> reader = new ArffReader<String>();
    TrainRelation<String> train = (TrainRelation<String>) reader.read(
        trainFile );

    /* contents:
       6.7,3.1,4.4,1.4,x1
     */
    File testFile = new File( "src/test/resources/test.arff" );
    TestRelation<String> test = (TestRelation<String>) reader.read( testFile );

    KnnClassifier<String> c = new KnnClassifier<String>();
    c.train( train );
    TreeMap<Double, List<String>> dists = c.calculateDistances( test.get( 0 ) );

    int numItems = 0;
    for ( List<String> classif : dists.values() ) {
      numItems += classif.size();
    }

    assertEquals( train.size(), numItems );

    for ( Double dist : dists.keySet() ) {
      System.out.println( "Training: \t" + dist + "\t" + dists.get( dist ) );
    }
    // for x4, x5, and x6
    assert dists.containsKey( 0.7 );
    assert dists.containsKey( 1.1 );
  }

  /**
   * Test the function that gets the nearest neighbors.
   * @throws IOException
   * @throws IncomparableFeatureVectorException
   */
  @SuppressWarnings( "unchecked" )
  @Test
  public void testGetKNearetsNeighbors() throws IOException,
    IncomparableFeatureVectorException {

    File trainFile = new File( "src/test/resources/train.arff" );
    ArffReader<String> reader = new ArffReader<String>();
    TrainRelation<String> train = (TrainRelation<String>) reader.read(
        trainFile );

    File testFile = new File( "src/test/resources/test.arff" );
    TestRelation<String> test = (TestRelation<String>) reader.read( testFile );

    KnnClassifier<String> c = new KnnClassifier<String>();
    c.train( train );
    TreeMap<Double, List<String>> dists = c.calculateDistances( test.get( 0 ) );
    double lowest = dists.firstKey();
    List<String> classifications = dists.get( lowest );

    System.out.println( "Smallest dist: " + lowest + "\t" + classifications );

    TreeMap<Double, List<String>> neighbors = c.getNearestNeighbors( dists, 1 );
    assertEquals( neighbors.size(), 1 );
    assertEquals( neighbors.firstKey(), lowest );
    assertEquals( neighbors.get( lowest ), classifications );

    System.out.println( "K nearest neighbors [1]: " + neighbors );

    neighbors = c.getNearestNeighbors( dists, 3 );
    assertEquals( neighbors.size(), 3 );
    assertEquals( neighbors.firstKey(), lowest );
    assertEquals( neighbors.get( lowest ), classifications );

    System.out.println( "K nearest neighbors [3]: " + neighbors );

    neighbors = c.getNearestNeighbors( dists, 5 );
    assertEquals( neighbors.size(), 4 );  // because it includes a tie
    assertEquals( neighbors.firstKey(), lowest );
    assertEquals( neighbors.get( lowest ), classifications );

    System.out.println( "K nearest neighbors [5]: " + neighbors );

    neighbors = c.getNearestNeighbors( dists, 7 );
    assertEquals( neighbors.size(), 6 );  // because it includes a tie
    assertEquals( neighbors.firstKey(), lowest );
    assertEquals( neighbors.get( lowest ), classifications );

    System.out.println( "K nearest neighbors [7]: " + neighbors );

    neighbors = c.getNearestNeighbors( dists, 9 );
    assertEquals( neighbors.size(), 8 );  // because it includes a tie
    assertEquals( neighbors.firstKey(), lowest );
    assertEquals( neighbors.get( lowest ), classifications );

    System.out.println( "K nearest neighbors [9]: " + neighbors );
  }

  /**
   * Test the function that breaks ties.
   * @throws IOException
   * @throws IncomparableFeatureVectorException
   */
  @SuppressWarnings( "unchecked" )
  @Test
  public void testTieBreaker() throws IOException,
    IncomparableFeatureVectorException {

    File trainFile = new File( "src/test/resources/train.arff" );
    ArffReader<String> reader = new ArffReader<String>();
    TrainRelation<String> train = (TrainRelation<String>) reader.read(
        trainFile );

    File testFile = new File( "src/test/resources/test.arff" );
    TestRelation<String> test = (TestRelation<String>) reader.read( testFile );

    KnnClassifier<String> c = new KnnClassifier<String>();
    c.train( train );
    TreeMap<Double, List<String>> dists = c.calculateDistances( test.get( 0 ) );
    double lowest = dists.firstKey();
    List<String> classifications = dists.get( lowest );

    // 3 contains a tie between versicolor and setosa; versicolor should win
    TreeMap<Double, List<String>> neighbors = c.getNearestNeighbors( dists, 3 );
    assertEquals( neighbors.size(), 3 );
    assertEquals( neighbors.firstKey(), lowest );
    assertEquals( neighbors.get( lowest ), classifications );

    Set<String> labels = new HashSet<String>();
    labels.add( "setosa" );
    labels.add( "versicolor" );
    String winner = c.breakTie( labels, neighbors );

    assertEquals( winner, "versicolor" );
  }

  /**
   * Test the function that classifies the test vector.
   * @throws IOException
   * @throws IncomparableFeatureVectorException
   */
  @SuppressWarnings( "unchecked" )
  @Test
  public void testClassify() throws IOException,
    IncomparableFeatureVectorException {

    File trainFile = new File( "src/test/resources/train.arff" );
    ArffReader<String> reader = new ArffReader<String>();
    TrainRelation<String> train = (TrainRelation<String>) reader.read(
        trainFile );

    File testFile = new File( "src/test/resources/test.arff" );
    TestRelation<String> test = (TestRelation<String>) reader.read( testFile );

    KnnClassifier<String> c = new KnnClassifier<String>();
    c.train( train );

    UnlabeledFeatureVector<String> ufv = test.get( 0 );

    int[] ks1 = {1};
    c.classify( ufv, ks1 );
    assertEquals( ufv.getClassification( 1 ), "versicolor" );

    int[] ks3 = {3};
    c.classify( ufv, ks3 );
    assertEquals( ufv.getClassification( 3 ), "versicolor" );

    int[] ks5 = {5};
    c.classify( ufv, ks5 );
    assertEquals( ufv.getClassification( 5 ), "versicolor" );

    int[] ks7 = {7};
    c.classify( ufv, ks7 );
    assertEquals( ufv.getClassification( 7 ), "versicolor" );

    int[] ks9 = {9};
    c.classify( ufv, ks9 );
    assertEquals( ufv.getClassification( 1 ), "versicolor" );
    assertEquals( ufv.getClassification( 3 ), "versicolor" );
    assertEquals( ufv.getClassification( 5 ), "versicolor" );
    assertEquals( ufv.getClassification( 7 ), "versicolor" );
    assertEquals( ufv.getClassification( 9 ), "virginica" );
  }

  /**
   * Test the function that classifies the test vector.
   * @throws IOException
   * @throws IncomparableFeatureVectorException
   */
  @SuppressWarnings( "unchecked" )
  @Test
  public void testClassifyBatch() throws IOException,
    IncomparableFeatureVectorException {

    File trainFile = new File( "src/test/resources/train.arff" );
    ArffReader<String> reader = new ArffReader<String>();
    TrainRelation<String> train = (TrainRelation<String>) reader.read(
        trainFile );

    File testFile = new File( "src/test/resources/test.arff" );
    TestRelation<String> test = (TestRelation<String>) reader.read( testFile );

    KnnClassifier<String> c = new KnnClassifier<String>();
    c.train( train );

    UnlabeledFeatureVector<String> ufv = test.get( 0 );

    int[] ks = {1, 3, 5, 7, 9};
    c.classify( ufv, ks );
    assertEquals( ufv.getClassification( 1 ), "versicolor" );
    assertEquals( ufv.getClassification( 3 ), "versicolor" );
    assertEquals( ufv.getClassification( 5 ), "versicolor" );
    assertEquals( ufv.getClassification( 7 ), "versicolor" );
    assertEquals( ufv.getClassification( 9 ), "virginica" );
  }


}
