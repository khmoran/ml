package edu.tufts.cs.ml.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.tufts.cs.ml.DoubleFeature;
import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.Metadata;
import edu.tufts.cs.ml.Relation;
import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.UnlabeledFeatureVector;

public class SvmLightReader<E> extends Reader<E> {

  @Override
  public Relation<?> read( File f ) throws IOException {
    return read( f, !IGNORE_LABELS );
  }

  @Override
  public Relation<?> read( File f, boolean ignoreLabels ) throws IOException {
    FileInputStream fis = new FileInputStream( f );

    return read( fis, ignoreLabels );
  }

  @Override
  public Relation<?> read( InputStream s ) throws IOException {
    return read( s, !IGNORE_LABELS );
  }

  @Override
  public Relation<?> read( InputStream s, boolean ignoreLabels )
      throws IOException {

    InputStreamReader isr = new InputStreamReader( s );
    BufferedReader br = new BufferedReader( isr );

    Metadata m = new Metadata();

    Relation<?> r;
    if ( ignoreLabels ) {
      r = new TestRelation<Integer>( "r", m );
    } else {
      r = new TrainRelation<Integer>( "r", m );
    }
    String line = br.readLine();
    int j = 0;
    while ( line != null ) {
      // don't process comment lines
      if ( line.trim().startsWith( "#" ) ) {
        line = br.readLine(); // read the next line
        continue;
      }

      // example: 344 -1 ? 387:1.0 389:1.0 498:1.0 606:1.0 660:1.0...
      String[] cols = line.split( " " );

      String id;
      int label;
      Integer rank = null;
      Integer qid = null;
      if ( cols[1].startsWith( "qid:" ) ) { // svm-rank: have rank and quid
        id = String.valueOf( j );
        label = Integer.parseInt( cols[0] );
        rank = label;
        qid = Integer.valueOf( cols[1].split( "qid:" )[1] );
      } else if ( cols[1].contains( ":" ) ) { // we don't have ids
        id = String.valueOf( j );
        label = Integer.parseInt( cols[0] );
      } else { // we have ids and a label
        id = cols[0];
        label = Integer.parseInt( cols[1] );
      }

      FeatureVector<Integer> fv;
      if ( ignoreLabels ) {
        fv = new UnlabeledFeatureVector<Integer>( id );
      } else {
        fv = new LabeledFeatureVector<Integer>( label, id ); 
      }
      fv.setRank( rank );
      fv.setQid( qid );
      
      for ( int i = 2; i < cols.length; i++ ) {
        if ( cols[i].trim().startsWith( "#" ) ) break;
        String[] parts = cols[i].split( ":" );
        String featName = parts[0];
        if ( !m.containsKey( featName ) ) m.put( featName, "" );
        // remove comment errata if no space between last feature and comment
        double featVal = Double.parseDouble( parts[1].split( "#" )[0] );
        DoubleFeature val = new DoubleFeature( featName, featVal );
        fv.put( featName, val );
        if ( cols[i].contains( "#" ) ) break; // signals comment
      }

      if ( ignoreLabels ) {
        ( (TestRelation<Integer> ) r ).add( (UnlabeledFeatureVector<Integer>) fv );
      } else {
        ( (TrainRelation<Integer> ) r ).add( (LabeledFeatureVector<Integer>) fv );
      }
      line = br.readLine(); // read the next line
      j++;
    }

    br.close();
    isr.close();
    s.close();

    return r;
  }
}
