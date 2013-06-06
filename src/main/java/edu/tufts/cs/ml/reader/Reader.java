package edu.tufts.cs.ml.reader;

import java.io.File;
import java.io.IOException;

import edu.tufts.cs.ml.Relation;

public abstract class Reader<E> {
  /** The cue to ignore the labels. */
  public static final boolean IGNORE_LABELS = true;

  /**
   * Read the file into a Relation of FeatureVectors.
   * @param f
   * @return
   * @throws IOException
   */
  public abstract Relation<?> read( File f ) throws IOException;
  /**
   * Read the file into a Relation of FeatureVectors.
   * @param f
   * @return
   * @throws IOException
   */
  public abstract Relation<?> read( File f, boolean ignoreLabels )
    throws IOException;
}
