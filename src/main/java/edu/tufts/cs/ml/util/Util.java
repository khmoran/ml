package edu.tufts.cs.ml.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.tufts.cs.ml.Feature;
import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.Relation;
import edu.tufts.cs.ml.TrainRelation;

public class Util {

  /**
   * Private constructor for utility class.
   */
  protected Util() {
    // purposely not instantiable
  }

  /**
   * Round the double to the provided number of places.
   *
   * @param value
   * @param numPlaces
   * @return
   */
  public static double round( double value, int numPlaces ) {
    double multFactor = Math.pow( 10, numPlaces );
    double zeroDPs = value * multFactor;
    return Math.round( zeroDPs ) / multFactor;
  }

  /**
   * Convert the collection of features to a list of its double values.
   *
   * @param features
   * @return
   */
  public static List<Double> featuresToDoubles(
      Collection<Feature<?>> features ) {
    List<Double> vals = new ArrayList<Double>();

    for ( Feature<?> f : features ) {
      double val = Double.parseDouble( f.getValue().toString() );
      vals.add( val );
    }

    return vals;
  }

  /**
   * Convert the collection of features to a list of its double values.
   *
   * @param features
   * @return
   */
  public static List<Double> labelsToDoubles( TrainRelation<?> r ) {
    List<Double> vals = new ArrayList<Double>();

    for ( LabeledFeatureVector<?> fv : r ) {
      Object value = fv.getLabel();
      try {
        double dValue = Double.parseDouble( value.toString() );
        vals.add( dValue );
      } catch ( NumberFormatException e ) {
        // TODO handle this
      }
    }

    return vals;
  }

  /**
   * Convert the collection of features to a list of its double values.
   *
   * @param features
   * @return
   */
  public static List<Double> featuresToDoubles( String featureName,
      Relation<?> r ) {
    List<Double> vals = new ArrayList<Double>();

    for ( FeatureVector<?> fv : r ) {
      Feature<?> f = fv.get( featureName );
      if ( f != null ) {
        Object value = f.getValue();
        try {
          double dValue = Double.parseDouble( value.toString() );
          vals.add( dValue );
        } catch ( NumberFormatException e ) {
          // TODO handle this
        }
      }
    }

    return vals;
  }

  /**
   * Sort the map by its values.
   *
   * @param map
   * @return
   */
  public static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>>
  sortByEntries( Map<K, V> map ) {
    SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
        new Comparator<Map.Entry<K, V>>() {
          public int compare( Map.Entry<K, V> e1, Map.Entry<K, V> e2 ) {
            int res = e2.getValue().compareTo( e1.getValue() );
            return res != 0 ? res : 1; // Special fix to preserve items with
                                       // equal values
          }
        } );
    sortedEntries.addAll( map.entrySet() );
    return sortedEntries;
  }

  /**
   * Strip the punctuation, capitalization from the given String.
   * @param s
   * @return
   */
  public static String normalize( String s ) {
    return s.trim().replaceAll( "[^a-zA-Z0-9]", "" ).toLowerCase();
  }
}
