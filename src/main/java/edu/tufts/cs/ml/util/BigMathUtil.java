package edu.tufts.cs.ml.util;

import java.math.BigDecimal;

public class BigMathUtil {
  /** Epsilon. */
  protected BigDecimal epsilon; // based on desired precision
  /** Natural e. */
  protected BigDecimal natural_e;
  /** Pi. */
  protected BigDecimal pi;
  /** Precision in digits. */
  protected int prec = 30; // precision in digits
  /** Precision in bits. */
  protected int bits = 100; // precision in bits (approx 3.32 prec. in digits)

  /**
   * Default constructor.
   */
  public BigMathUtil() {
    // "constants" needed by other functions
    natural_e = naturalE( prec ); /* precision */
    epsilon = new BigDecimal( "1" );
    for ( int i = 0; i < bits; i++ ) {
      epsilon = epsilon.multiply( new BigDecimal( "0.5" ) );
    }
  }

  /**
   * Exp series.
   * @param x
   * @return
   */
  public BigDecimal expSeries( BigDecimal x ) { // abs(x)<=0.5, prec digits
    // prec digits returned
    BigDecimal fact = new BigDecimal( "1" ); // factorial
    BigDecimal xp = new BigDecimal( "1" ); // power of x
    BigDecimal y = new BigDecimal( "1" ); // sum of series on x
    int n;
    n = ( 2 * prec ) / 3;
    for ( int i = 1; i < n; i++ ) {
      fact = fact.multiply( new BigDecimal( i ) );
      fact = fact.setScale( prec, BigDecimal.ROUND_DOWN );
      xp = xp.multiply( x );
      xp = xp.setScale( prec, BigDecimal.ROUND_DOWN );
      y = y.add( xp.divide( fact, BigDecimal.ROUND_DOWN ) );
    }
    y = y.setScale( prec, BigDecimal.ROUND_DOWN );
    return y;
  }

  /**
   * Exp.
   * @param x
   * @return
   */
  public BigDecimal exp( BigDecimal x ) {
    BigDecimal y = new BigDecimal( "1.0" ); // sum of series on xc
    BigDecimal xc; // x - j
    BigDecimal ep = natural_e; // e^j
    BigDecimal j = new BigDecimal( "1" );
    BigDecimal one = new BigDecimal( "1.0" );
    BigDecimal half = new BigDecimal( "0.5" );
    BigDecimal xp; // positive, then invert
    if ( x.abs().compareTo( half ) < 0 )
      return expSeries( x );
    if ( x.compareTo( new BigDecimal( "0" ) ) > 0 ) { // positive
      while ( x.compareTo( j.add( one ) ) > 0 ) {
        ep = ep.multiply( natural_e );
        j = j.add( one );
      }
      xc = x.subtract( j );
      y = ep.multiply( expSeries( xc ) );
      y = y.setScale( prec, BigDecimal.ROUND_DOWN );
      return y;
    } else { // negative
      xp = x.negate();
      while ( xp.compareTo( j.add( one ) ) > 0 ) {
        ep = ep.multiply( natural_e );
        j = j.add( one );
      }
      xc = xp.subtract( j );
      y = ep.multiply( expSeries( xc ) );
      y = y.setScale( prec, BigDecimal.ROUND_DOWN );
      return ( one.add( epsilon ) ).divide( y, BigDecimal.ROUND_DOWN );
    }
  } // end exp

  /**
   * Natural e.
   * @param prec_dig
   * @return
   */
  public BigDecimal naturalE( int prec_dig ) {
    BigDecimal sum = new BigDecimal( "1" );
    BigDecimal fact = new BigDecimal( "1" );
    BigDecimal del = new BigDecimal( "1" );
    BigDecimal one = new BigDecimal( "1" );
    BigDecimal ten = new BigDecimal( "10" );
    int prec_bits = ( prec_dig * 332 ) / 100;
    one = one.setScale( prec_dig, BigDecimal.ROUND_DOWN );
    for ( int i = 0; i < prec_dig; i++ )
      del = del.multiply( ten );
    for ( int i = 1; i < prec_bits; i++ ) {
      fact = fact.multiply( new BigDecimal( i ) );
      fact = fact.setScale( prec_dig, BigDecimal.ROUND_DOWN );
      sum = sum.add( one.divide( fact, BigDecimal.ROUND_DOWN ) );
      if ( del.compareTo( fact ) < 0 )
        break;
    }
    return sum.setScale( prec_dig, BigDecimal.ROUND_DOWN );
  }

  /**
   * Calculate the standard Gaussian pdf.
   * @param x
   * @return
   */
  public static BigDecimal calcPdf( double x ) {
    if ( Double.isNaN( x ) ) {
      return new BigDecimal( Double.MIN_NORMAL );
    }
    BigDecimal num = new BigDecimal( Math.exp( -x*x / 2 ) );
    BigDecimal denom = new BigDecimal( Math.sqrt( 2 * Math.PI ) );

    if ( denom.doubleValue() == 0 ) {
      denom = new BigDecimal( Double.MIN_NORMAL );
    }

    return num.divide( denom, BigDecimal.ROUND_HALF_UP );
  }

  /**
   * Calculate the Gaussian pdf with mean mu and stddev sigma.
   * @param x Value
   * @param mu Mean
   * @param sigma Std dev
   * @return
   */
  public static BigDecimal calcPdf( double x, double mu, double sigma ) {
    BigDecimal num = calcPdf( ( x - mu ) / sigma );
    BigDecimal denom = new BigDecimal( sigma );
    if ( denom.doubleValue() == 0 ) {
      denom = new BigDecimal( Double.MIN_NORMAL );
    }
    return num.divide( denom, BigDecimal.ROUND_HALF_UP );
  }

  /**
   * Calculate the standard Gaussian cdf using Taylor approximation.
   * @param z
   * @return
   */
  public static BigDecimal calcCdf( double z ) {
    if ( z < -8.0 ) return new BigDecimal( 0.0 );
    if ( z >  8.0 ) return new BigDecimal( 1.0 );
    double sum = 0.0, term = z;
    for ( int i = 3; sum + term != sum; i += 2 ) {
      sum  = sum + term;
      term = term * z * z / i;
    }

    return new BigDecimal( 0.5 + sum ).multiply( calcPdf( z ) );
  }

  /**
   * Calculate the Gaussian cdf with mean mu and stddev sigma.
   * @param z Value
   * @param mu Mean
   * @param sigma Std dev
   * @return
   */
  public static BigDecimal calcCdf( double z, double mu, double sigma ) {
    return calcCdf( ( z - mu ) / sigma );
  }

}
