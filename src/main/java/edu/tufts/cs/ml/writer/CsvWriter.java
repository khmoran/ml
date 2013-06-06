package edu.tufts.cs.ml.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.Relation;

public class CsvWriter extends Writer {

  @Override
  public void write( Relation<?> r, File f ) throws IOException {
    if ( f != null & r.size() > 0 ) {
      FileWriter fw = new FileWriter( f );
      BufferedWriter writer = new BufferedWriter( fw );

      // first write the header
      StringBuilder sb = new StringBuilder();
      for ( FeatureVector<?> fv : r ) {
        sb.append( fv.toString() );
      }

      writer.write( sb.toString() );

      // close the streams
      writer.close();
      fw.close();
    }
  }

}
