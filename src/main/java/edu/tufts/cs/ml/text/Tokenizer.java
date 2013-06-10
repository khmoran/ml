package edu.tufts.cs.ml.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Tokenizer {
  /** The list of stop words. */
  protected HashSet<String> stoplist;
  /** The default generated serial version UID. */
  static final long serialVersionUID = 1;

  /**
   * Default constructor using default EN-US stop words.
   */
  public Tokenizer() {
    stoplist = new HashSet<String>();

    // articles
    stop( "the" );
    stop( "a" );
    stop( "an" );

    // conjunctions
    stop( "and" );
    stop( "or" );

    // prepositions
    stop( "of" );
    stop( "for" );
    stop( "in" );
    stop( "on" );
    stop( "to" );
    stop( "with" );
    stop( "by" );

    // definite pronouns
    stop( "this" );
    stop( "that" );
    stop( "these" );
    stop( "those" );
    stop( "some" );
    stop( "other" );

    // personal pronouns
    stop( "it" );
    stop( "its" );
    stop( "we" );
    stop( "our" );

    // conjuctions
    stop( "as" );
    stop( "but" );
    stop( "not" );

    // verbs
    stop( "do" );
    stop( "does" );
    stop( "is" );
    stop( "be" );
    stop( "are" );
    stop( "can" );
    stop( "was" );
    stop( "were" );
  }

  /**
   * Constructor accepting a File containing stop words.
   * @param stopfile
   */
  public Tokenizer( File stopfile ) {
    stoplist = new HashSet<String>();

    try {
      BufferedReader in = new BufferedReader( new InputStreamReader(
          new FileInputStream( stopfile ), "UTF-8" ) );

      String word = null;
      while ( ( word = in.readLine() ) != null ) {
        stop( word );
      }

      in.close();

    } catch ( Exception e ) {
      System.err.println( "problem loading stoplist: " + e );
    }

  }

  /**
   * Constructor accepting a Collection of stop words.
   * @param stoplist
   */
  public Tokenizer( Collection<String> stoplist ) {
    this.stoplist = new HashSet<String>( stoplist );
  }

  /**
   * Clone the Tokenizer.
   * @return
   */
  @SuppressWarnings( "unchecked" )
  public Tokenizer deepClone() {
    return new Tokenizer( (HashSet<String>) stoplist.clone() );
  }

  /**
   * Tokenize the String.
   * @param s
   * @return
   */
  public List<String> tokenize( String s ) {
      ArrayList<String> tokens = new ArrayList<String>();

      int[] tokenBuffer = new int[1000];
      int length = -1;

      // Using code points instead of chars allows us
      // to support extended Unicode, and has no significant
      // efficiency costs.

      int totalCodePoints = Character.codePointCount( s, 0,
          s.length() );

      for ( int i = 0; i < totalCodePoints; i++ ) {

        int codePoint = Character.codePointAt( s, i );
        int codePointType = Character.getType( codePoint );

        if ( codePointType == Character.LOWERCASE_LETTER
            || codePointType == Character.UPPERCASE_LETTER ) {
          length++;
          tokenBuffer[length] = codePoint;
        } else if ( codePointType == Character.SPACE_SEPARATOR
            || codePointType == Character.LINE_SEPARATOR
            || codePointType == Character.PARAGRAPH_SEPARATOR
            || codePointType == Character.END_PUNCTUATION
            || codePointType == Character.DASH_PUNCTUATION
            || codePointType == Character.CONNECTOR_PUNCTUATION
            || codePointType == Character.START_PUNCTUATION
            || codePointType == Character.INITIAL_QUOTE_PUNCTUATION
            || codePointType == Character.FINAL_QUOTE_PUNCTUATION
            || codePointType == Character.OTHER_PUNCTUATION ) {

          // Things that delimit words
          if ( length != -1 ) {
            String token = new String( tokenBuffer, 0, length + 1 );
            if ( !stoplist.contains( token ) ) {
              tokens.add( token );
            }
            length = -1;
          }
        } else if ( codePointType == Character.COMBINING_SPACING_MARK
            || codePointType == Character.ENCLOSING_MARK
            || codePointType == Character.NON_SPACING_MARK
            || codePointType == Character.TITLECASE_LETTER
            || codePointType == Character.MODIFIER_LETTER
            || codePointType == Character.OTHER_LETTER ) {
          // Obscure things that are technically part of words.
          // Marks are especially useful for Indic scripts.

          length++;
          tokenBuffer[length] = codePoint;
        } else {
          // Character.DECIMAL_DIGIT_NUMBER
          // Character.CONTROL
          // Character.MATH_SYMBOL
          // System.out.println("type " + codePointType);
        }

      }

      if ( length != -1 ) {
        String token = new String( tokenBuffer, 0, length + 1 );
        if ( !stoplist.contains( token ) ) {
          tokens.add( token );
        }
      }

    return tokens;
  }

  /**
   * Add the String to the stop list.
   * @param word
   */
  protected void stop( String word ) {
    stoplist.add( word );
  }

}
