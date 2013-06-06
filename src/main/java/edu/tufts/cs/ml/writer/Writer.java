package edu.tufts.cs.ml.writer;

import java.io.File;
import java.io.IOException;

import edu.tufts.cs.ml.Relation;

public abstract class Writer {

  /**
   * Write the output to the provided File.
   * @param results
   * @param f
   * @throws IOException
   */
  public abstract void write( Relation<?> r, File f ) throws IOException;

}
