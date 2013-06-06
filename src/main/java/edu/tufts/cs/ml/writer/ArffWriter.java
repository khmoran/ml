package edu.tufts.cs.ml.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.tufts.cs.ml.Relation;

public class ArffWriter extends Writer {

  @Override
  public void write( Relation<?> r, File f ) throws IOException {
    if ( f != null & r.size() > 0 ) {
      FileWriter fw = new FileWriter( f );
      BufferedWriter writer = new BufferedWriter( fw );

      // first write the header
      writer.write( r.toString() );

      // close the streams
      writer.close();
      fw.close();
    }
  }

}
