package edu.tufts.cs.ml.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.tufts.cs.ml.DoubleFeature;
import edu.tufts.cs.ml.Feature;
import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.Metadata;
import edu.tufts.cs.ml.MissingFeature;
import edu.tufts.cs.ml.Relation;
import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.UnlabeledFeatureVector;

public class ArffReader<E> extends Reader<E> {
  /** The line text that marks the start of the data. */
  protected static final String DATA_MARKER = "@DATA";

  @Override
  public Relation<?> read( File f ) throws IOException {
    return read( f, !IGNORE_LABELS );
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public Relation<?> read( File f, boolean ignoreLabels ) throws IOException {
    FileInputStream fis = new FileInputStream( f );
    InputStreamReader isr = new InputStreamReader( fis );
    BufferedReader br = new BufferedReader( isr );

    boolean data = false;
    boolean relation = false;
    boolean usingIds = false;
    String line = br.readLine();
    Metadata m = new Metadata();
    Relation<?> r = null;
    String rName = null;
    int id = 1;
    while ( line != null ) {
      if ( line.toUpperCase().startsWith( Relation.RELATION_MARKER ) ) {
        relation = true;
        String[] content = line.split( " " );
        if ( content.length > 1 ) {
          rName = content[1].trim();
        }
      } else if ( line.toUpperCase().startsWith( DATA_MARKER ) ) {
        relation = false;
        data = true;
      } else if ( relation ) {
        if ( line.toUpperCase().startsWith( Metadata.ATTRIBUTE_MARKER )
            || line.startsWith( "%" ) || line.startsWith( "#" ) ) {
          String[] content = line.split( " " );
          if ( content.length > 2 ) {
            if ( content[1].trim().toUpperCase().endsWith( "ID" )
              || content[2].trim().toUpperCase().endsWith( "NAME" ) ) {
              //usingIds = true;
            }
            if ( content[1].trim().toUpperCase().startsWith( "CLASS" ) ) {
              if ( ignoreLabels ) {
                r = new TestRelation<E>( rName, m );
              } else {
                r = new TrainRelation<E>( rName, m );
                m.put( content[1].trim(), content[2].trim() );
              }
            } else if ( r == null ) {
              r = new TestRelation<E>( rName, m );
              m.put( content[1].trim(), content[2].trim() );
            } else {
              m.put( content[1].trim(), content[2].trim() );
            }
          } else if ( content.length == 2 ) {
            if ( content[1].trim().toUpperCase().endsWith( "ID" )
              || content[1].trim().toUpperCase().endsWith( "NAME" ) ) {
              //usingIds = true;
            }
          }
        }
      } else if ( data ) {
        String cleaned = line.replaceAll( "[^A-Za-z0-9 .,]", "" );
        String[] content = cleaned.split( "," );

        if ( content != null && content.length > 0 ) {
          // figure out which kind of feature vector it is and initialize it
          FeatureVector<E> fv;

          String name;
          if ( usingIds ) {
            name = content[content.length-1].trim();
          } else {
            name = "x" + String.valueOf( id++ );
          }
          if ( r instanceof TrainRelation ) {
            String label;
            if ( usingIds ) {
              label = content[content.length-2].trim();
            } else {
              label = content[content.length-1].trim();
            }
            fv = new LabeledFeatureVector<E>( (E) label, name );
          } else {
            fv = new UnlabeledFeatureVector<E>( name );
          }

          // add the features
          for ( int i = 0; i < content.length-1; i++ ) {
            String featureLabel = m.keySet().toArray( new String[0] )[i].trim();
            if ( !( featureLabel == null || featureLabel.equalsIgnoreCase(
                LabeledFeatureVector.CLASS_MARKER ) ) ) {
              try {
                Double featureVal = Double.parseDouble( content[i].trim() );
                Feature<?> ft = new DoubleFeature( featureLabel, featureVal );
                fv.put( featureLabel, ft );
              } catch ( NumberFormatException e ) {
                if ( content[i].trim().equals( "?" ) ) {
                  Feature<?> ft = new MissingFeature( featureLabel );
                  fv.put( featureLabel, ft );
                }
                // swallow this
              }
            }
          }
          if ( fv != null && !fv.isEmpty() ) {
            if ( r instanceof TestRelation ) {
              ( (TestRelation<E>) r ).add( (UnlabeledFeatureVector<E>) fv );
            } else {
              ( (TrainRelation<E>) r ).add( (LabeledFeatureVector<E>) fv );
            }
          }
        }
      }

      line = br.readLine();
    }

    // close the streams
    br.close();
    isr.close();
    fis.close();

    return r;
  }
}
