package edu.tufts.cs.ml.classify;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.testng.annotations.Test;

import com.google.common.collect.TreeMultimap;

import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.Relation;
import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.reader.Reader;
import edu.tufts.cs.ml.reader.SvmLightReader;

public class RankSVMClassifierTest {

  /**
   * Test the RankSVMClassifier.
   * @throws IncomparableFeatureVectorException 
   */
  @Test
  public void testRankSvmClassifier() throws IOException, IncomparableFeatureVectorException {
    String trainInput = "# query 1\n" +
        "3 qid:1 1:1 2:1 3:0 4:0.2 5:0\n" +
        "2 qid:1 1:0 2:0 3:1 4:0.1 5:1\n" +
        "1 qid:1 1:0 2:1 3:0 4:0.4 5:0\n" +
        "1 qid:1 1:0 2:0 3:1 4:0.3 5:0\n" +
        "# query 2\n" +
        "1 qid:2 1:0 2:0 3:1 4:0.2 5:0\n" +
        "2 qid:2 1:1 2:0 3:1 4:0.4 5:0\n" +
        "1 qid:2 1:0 2:0 3:1 4:0.1 5:0\n" +
        "1 qid:2 1:0 2:0 3:1 4:0.2 5:0\n" +
        "# query 3\n" +
        "2 qid:3 1:0 2:0 3:1 4:0.1 5:1\n" +
        "3 qid:3 1:1 2:1 3:0 4:0.3 5:0\n" +
        "4 qid:3 1:1 2:0 3:0 4:0.4 5:1\n" +
        "1 qid:3 1:0 2:1 3:1 4:0.5 5:0";

    Reader<String> reader = new SvmLightReader<String>();
    Relation<?> train = reader.read(
        new ByteArrayInputStream( trainInput.getBytes( "UTF-8" ) ) );
    
    assert train.size() == 12;

    String testInput = "4 qid:4 1:1 2:0 3:0 4:0.2 5:1\n" +
        "3 qid:4 1:1 2:1 3:0 4:0.3 5:0\n" +
        "2 qid:4 1:0 2:0 3:0 4:0.2 5:1\n" +
        "1 qid:4 1:0 2:0 3:1 4:0.2 5:0";

    Relation<?> test = reader.read(
        new ByteArrayInputStream( testInput.getBytes( "UTF-8" ) ), true );

    assert test.size() == 4;

    RankSvmClassifier c = new RankSvmClassifier( 3.0 );
    c.train( (TrainRelation<Integer>) train );

    TreeMultimap<Double, FeatureVector<Integer>> ranks = c.rank( (TestRelation<Integer>) test );
    assert ranks.size() == test.size();
    
    for ( FeatureVector<Integer> fv : ranks.values() ) {
      System.out.println( "rank " + fv.getRank() + ": " + fv );
    }
  }
}

