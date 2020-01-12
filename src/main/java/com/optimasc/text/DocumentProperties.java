package com.optimasc.text;

/** Additional properties for documents that
 *  can be used in {@link javax.swing.text.Document#getProperty(Object)}
 *  and {@link javax.swing.text.Document#putProperty(Object, Object)} 
 *  methods.
 *  
 *  It also contains utilities to very common <code>meta</code>
 *  elements.
 *
 **/
public class DocumentProperties
{
  /** Date property. Contains the date the document
   *  was last modified. The Date format is required
   *  to be an ISO 8601 date string.
   */
  public static final String DateProperty = "date";
  
  /** Generator property. Contains the name of the tool / generator
   *  used to create this document.
   */
  public static final String GeneratorProperty = "generator";
  
  
  /** Subject property. Contains the subject (keywords)
   *  associated with this document.
   *  
   *  This is of type <code>String</code>.
   *  Multiple terms can be separated by semi-colons or
   *  commas.
   */
  public static final String SubjectProperty = "keywords";

  /** Creator property. Contains the main creator/author 
   *  of this document. 
   *  
   *  This is of type <code>String</code>.
   *  Multiple creators can be separated by semi-colons or
   *  commas.
   */
  public static final String CreatorProperty = "author";
  
  /** Contributor property. Contains the contributors of
   *  this document. 
   *  
   *  This is of type <code>String</code>.
   *  Multiple contributes can be separated by semi-colons or
   *  commas.
   */
  public static final String ContributorProperty = "contributor";
  
  /** Contributor property. Contains the description of
   *  this document. 
   *  
   *  This is of type <code>String</code>.
   */
  public static final String DescriptionProperty = "description";
  
  
  
  /** Language property. Contains the main written language
   *  associated with this document. The Object is of type
   *  <code>LanguageSelector</code>
   */
  public static final String LanguageProperty = "language";
  
  /** Document class property. Contains the document class
   *  used for exporting the document. The Object is of type
   *  <code>DocumentClass</code>
   */
  public static final String DocumentClassProperty = "Document.class";  
  
  /** List of properties that represent a title. */
  protected static final String[] titlePropertyNames =
    {"title","dcterms.title","DC.title","dc:title"};
  
  /** List of properties that represent a description. */
  protected static final String[] descriptionPropertyNames =
    {"description","dcterms.descriptio","DC.description","dc:description"};
  
  /** List of properties that represent a creator/author. */
  protected static final String[] creatorPropertyNames =
    {"author","DC.creator","dcterms.creator","dc:creator"};
  
  /** List of properties that represent a subject/keywords. */
  protected static final String[] subjectPropertyNames =
    {"keywords","dcterms.contributor","DC.subject","dc:subject"};
  
  /** List of properties that represent contributors. */
  protected static final String[] contributorPropertyNames =
    {"dcterms.contributor","DC.contributor","dc:contributor","dcterms.contributor"};
  
  /** List of properties that represent a subject/keywords. */
  protected static final String[] languagePropertyNames =
    {"dcterms.language","DC.language","dc:language"};

  /** Checks for alias associated with the title property name
   *  and return true if this represents a title property. 
   *  
   * @param name The property name to check.
   * @return true if this represents a title property, otherwise
   *  false
   */
  public static boolean isTitle(String name)
  {
    for (int i = 0; i < titlePropertyNames.length; i++)
    {
      if (name.equalsIgnoreCase(titlePropertyNames[i]))
      {
        return true;
      }
    }
    return false;
  }
  
  /** Checks for alias associated with the creator property name
   *  and return true if this represents a creator property. 
   *  
   * @param name The property name to check.
   * @return true if this represents a creator property, otherwise
   *  false
   */
  public static boolean isCreator(String name)
  {
    for (int i = 0; i < creatorPropertyNames.length; i++)
    {
      if (name.equalsIgnoreCase(creatorPropertyNames[i]))
      {
        return true;
      }
    }
    return false;
  }
  
  
  /** Checks for alias associated with the subject property name
   *  and return true if this represents a subject property. 
   *  
   * @param name The property name to check.
   * @return true if this represents a subject property, otherwise
   *  false
   */
  public static boolean isSubject(String name)
  {
    for (int i = 0; i < subjectPropertyNames.length; i++)
    {
      if (name.equalsIgnoreCase(subjectPropertyNames[i]))
      {
        return true;
      }
    }
    return false;
  }
  
  
  
  /** Checks for alias associated with the description property name
   *  and return true if this represents a description property. 
   *  
   * @param name The property name to check.
   * @return true if this represents a description property, otherwise
   *  false
   */
  public static boolean isDescription(String name)
  {
    for (int i = 0; i < descriptionPropertyNames.length; i++)
    {
      if (name.equalsIgnoreCase(descriptionPropertyNames[i]))
      {
        return true;
      }
    }
    return false;
  }
  
  /** Checks for alias associated with the language property name
   *  and return true if this represents a language property. 
   *  
   * @param name The property name to check.
   * @return true if this represents a language property, otherwise
   *  false
   */
  public static boolean isLanguage(String name)
  {
    for (int i = 0; i < languagePropertyNames.length; i++)
    {
      if (name.equalsIgnoreCase(languagePropertyNames[i]))
      {
        return true;
      }
    }
    return false;
  }
  
  /** Checks for alias associated with the contributors property name
   *  and return true if this represents a contributor property. 
   *  
   * @param name The property name to check.
   * @return true if this represents a contributor property, otherwise
   *  false
   */
  public static boolean isContributors(String name)
  {
    for (int i = 0; i < contributorPropertyNames.length; i++)
    {
      if (name.equalsIgnoreCase(contributorPropertyNames[i]))
      {
        return true;
      }
    }
    return false;
  }
  
  
  
}
