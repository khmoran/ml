package edu.tufts.cs.ml.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ResultWriter {

  /**
   * Write the results to the given file.
   * @param s
   * @param f
   * @throws IOException
   */
  public void write( String s, File f ) throws IOException {
    FileOutputStream fos = new FileOutputStream( f );
    OutputStreamWriter out = new OutputStreamWriter( fos, "UTF-8" );

    out.write( s );

    // close the streams
    out.close();
    fos.close();
  }

}
