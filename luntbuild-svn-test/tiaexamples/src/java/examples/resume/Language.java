/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.resume;

import org.apache.commons.lang.enum.Enum;

/**
 * Enumeration of different types of computer langugages that
 * a user may express an understanding of.
 * 
 * @author Howard Lewis Ship
 */
public class Language extends Enum
{
    public static final Language JAVA = new Language("JAVA");
    public static final Language C = new Language("C");
    public static final Language CSHARP = new Language("CSHARP");
    public static final Language CPLUSPLUS = new Language("CPLUSPLUS");
    public static final Language PYTHON = new Language("PYTHON");
    public static final Language PERL = new Language("PERL");
    public static final Language RUBY = new Language("RUBY");
    public static final Language VB = new Language("VB");
    public static final Language EIFFEL = new Language("EIFFEL");

    public static final Language[] ALL_VALUES =
        { C, CPLUSPLUS, CSHARP, EIFFEL, JAVA, PERL, PYTHON, RUBY, VB };

    protected Language(String name)
    {
        super(name);
    }

}
