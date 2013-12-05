package edu.tufts.cs.ml.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.tufts.cs.ml.Feature;
import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.Metadata;
import edu.tufts.cs.ml.Relation;

public class SvmLightWriter extends Writer {

  @Override
  public void write( Relation<?> r, File f ) throws IOException {
    if ( f != null & r.size() > 0 ) {
      FileWriter fw = new FileWriter( f );
      BufferedWriter writer = new BufferedWriter( fw );

      for ( FeatureVector<?> fv : r ) {
        StringBuilder sb = new StringBuilder();
        if ( fv instanceof LabeledFeatureVector<?> ) {
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
        writer.append( sb.toString() );
      }

      // close the streams
      writer.close();
      fw.close();
    }
  }

  /**
   * Write one feature vector to an appendable form.
   * @param m
   * @param fv
   * @param a
   * @throws IOException
   */
  public void write( Metadata m, FeatureVector<?> fv, Appendable a ) throws IOException {
    StringBuilder sb = new StringBuilder();
    if ( fv instanceof LabeledFeatureVector<?> ) {
      sb.append( ( (LabeledFeatureVector<?>) fv ).getLabel().toString() + " " );
    } else { // no rank and no label -- test instance; set dummy label
      sb.append( "0 " );
    }
    for ( String feature : m.keySet() ) {
      Feature<?> value = fv.get( feature );
      if ( value != null ) {
        sb.append( feature + ":" + value.toString() + " " );
      }
    }
    //sb.append( " # " + fv.getId() + "\n" );
    sb.append( "\n" );
    
    a.append( sb.toString() );
  }

}
