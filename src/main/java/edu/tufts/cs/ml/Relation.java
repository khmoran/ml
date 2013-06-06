package edu.tufts.cs.ml;

import java.util.ArrayList;


public class Relation<F extends FeatureVector<?>> extends ArrayList<F> {
  /** Default generated serial version UID. */
  private static final long serialVersionUID = -7990337660622504080L;
  /** The line text that marks the start of the relation information. */
  public static final String RELATION_MARKER = "@RELATION";
  /** The line text that marks the start of the data. */
  protected static final String DATA_MARKER = "@DATA";
  /** The relation's metadata (attribute information). */
  private Metadata metadata;
  /** The name of the relation. */
  private String name;
  /** The standard deviation for the weighted distance based voting scheme. */
  private Double sigma;

  /**
   * Get the metadata.
   * @return
   */
  public Metadata getMetadata() {
    return this.metadata;
  }

  /**
   * Get the sigma value.
   * @return
   */
  public Double getSigma() {
    return this.sigma;
  }

  /**
   * Set the sigma value.
   * @param sigma
   */
  public void setSigma( Double sigma ) {
    this.sigma = sigma;
  }

  /**
   * Set the metadata.
   * @param metadata
   */
  public void setMetadata( Metadata metadata ) {
    this.metadata = metadata;
  }

  /**
   * Default constructor.
   * @param metadata
   */
  public Relation( String name, Metadata metadata ) {
    this.name = name;
    this.metadata = metadata;
  }

  /**
   * Get the name of the Relation.
   * @return
   */
  public String getName() {
    return this.name;
  }

  /**
   * Set the name of the Relation.
   * @param name
   * @return
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * Normalize the dataset.
   */
  public void normalize() {
    for ( FeatureVector<?> fv : this ) {
      fv.normalize();
    }
  }

  /**
   * Get the FeatureVector with this id.
   * @param fvId
   * @return
   */
  public FeatureVector<?> get( String fvId ) {
    for ( FeatureVector<?> fv : this ) {
      if ( fv.getId().equals( fvId ) ) {
        return fv;
      }
    }

    return null;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append( RELATION_MARKER );
    sb.append( " " );
    sb.append( name );
    sb.append( "\n\n" );
    sb.append( metadata.toString() );
    sb.append( "\n" );

    sb.append( DATA_MARKER );
    sb.append( "\n" );

    for ( FeatureVector<?> fv : this ) {
      sb.append( fv.toString() );
    }

    sb.replace( sb.lastIndexOf( "\n" ), sb.lastIndexOf( "\n" )+1, "" );
    return sb.toString();
  }
}
