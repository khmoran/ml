package edu.tufts.cs.ml.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.tufts.cs.ml.Feature;
import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.Relation;

public class RankSvmWriter extends Writer {

  @Override
  public void write( Relation<?> r, File f ) throws IOException {
    if ( f != null & r.size() > 0 ) {
      FileWriter fw = new FileWriter( f );
      BufferedWriter writer = new BufferedWriter( fw );

      for ( FeatureVector<?> fv : r ) {
        StringBuilder sb = new StringBuilder();
        if ( (fv.getRank() != null || fv.getQid() != null ) ) {
          String rank = ( fv.getRank() == null ) ? "0" : String.valueOf( fv.getRank() );
          String qid = ( fv.getQid() == null ) ? "1" : String.valueOf( fv.getQid() );
          sb.append( rank + " qid:" + qid + " " );
        } else if ( fv instanceof LabeledFeatureVector<?> ) {
          sb.append( ( (LabeledFeatureVector<?>) fv ).getLabel().toString() + " " );
        } else { // no rank and no label -- test instance; set dummy label
          sb.append( "0 " );
        }
        for ( String feature : r.getMetadata().keySet() ) {
          Feature<?> value = fv.get( feature );
          if ( value != null ) {
            sb.append( feature + ":" + value.toString() + " " );
          }
        }
        //sb.append( " # " + fv.getId() + "\n" );
        sb.append( "\n" );
        writer.append( sb );
      }

      // close the streams
      writer.close();
      fw.close();
    }
  }

}
