package edu.tufts.cs.ml;

public abstract class Feature<E> {
  /** The name of the dimension. */
  protected String name;
  /** The value of the feature. */
  protected E value;

  /**
   * Default constructor.
   * @param value
   * @param idx
   */
  public Feature( String name, E value ) {
    this.name = name;
    this.value = value;
  }

  /**
   * Get the value.
   * @return
   */
  public E getValue() {
    return this.value;
  }

  /**
   * Get the name.
   * @return
   */
  public String getName() {
    return this.name;
  }

  /**
   * Set the name of the dimension.
   * @param name
   */
  public void setName( String name ) {
    this.name = name;
  }

  /**
   * Set the value.
   * @param value
   */
  public void setValue( E value ) {
    this.value = value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (
        ( this.name == null ) ? 0 : this.name.hashCode() );
    result = prime * result
        + ( ( this.value == null ) ? 0 : this.value.hashCode() );
    return result;
  }

  @Override
  public boolean equals( Object obj ) {
    if ( this == obj )
      return true;
    if ( obj == null )
      return false;
    if ( getClass() != obj.getClass() )
      return false;
    Feature<?> other = (Feature<?>) obj;
    if ( this.name == null ) {
      if ( other.name != null )
        return false;
    } else if ( !this.name.equals( other.name ) )
      return false;
    if ( this.value == null ) {
      if ( other.value != null )
        return false;
    } else if ( !this.value.equals( other.value ) )
      return false;
    return true;
  }

  /**
   * Get the Euclidean distance between the two features.
   * @param f
   */
  abstract Double getDifference( Feature<E> f );

}
