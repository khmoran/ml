package edu.tufts.cs.ml.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import edu.tufts.cs.ml.DoubleFeature;
import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.Metadata;
import edu.tufts.cs.ml.Relation;
import edu.tufts.cs.ml.TrainRelation;

public class SvmLightReader<E> extends Reader<E> {
  /** The Logger. */
  private static final Logger LOG =  Logger.getLogger(
      SvmLightReader.class.getName() );

  @Override
  public Relation<?> read( File f ) throws IOException {
    FileInputStream fis = new FileInputStream( f );
    InputStreamReader isr = new InputStreamReader( fis );
    BufferedReader br = new BufferedReader( isr );

    TrainRelation<Integer> r =
        new TrainRelation<Integer>( "r", new Metadata() );
    String line = br.readLine();
    while ( line != null ) {
      // example: 344 -1 ? 387:1.0 389:1.0 498:1.0 606:1.0 660:1.0...
      String[] cols = line.split( " " );
      String id = cols[0];
      int label = Integer.parseInt( cols[1] );
      LabeledFeatureVector<Integer> fv =
          new LabeledFeatureVector<Integer>( label, id );

      // ignore cols[2] because it's an unspecified L2 label
      for ( int i = 3; i < cols.length; i++ ) {
        String[] parts = cols[i].split( ":" );
        String featName = parts[0];
        double featVal = Double.parseDouble( parts[1] );
        DoubleFeature val = new DoubleFeature( featName, featVal );
        fv.put( featName, val );
      }

      r.add( fv );
      line = br.readLine(); // read the next line
    }

    br.close();
    isr.close();
    fis.close();

    return r;
  }

  @Override
  public Relation<?> read( File f, boolean ignoreLabels ) throws IOException {
    // TODO Auto-generated method stub
    return null;
  }
}
