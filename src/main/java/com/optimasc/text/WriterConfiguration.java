package com.optimasc.text;

/** Interface that can be implemented by interfaces
 *  to configure some features in a generic way.
 * 
 * @author Carl Eric Codere
 *
 */
public interface WriterConfiguration
{
  /** Set configuration parameters */
  public void setParameter(String name, Object value);

  /** Get configuration parameter. */
  public Object getParameter(String name);

}
