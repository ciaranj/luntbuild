/* This work is hereby released into the Public Domain. 
 * To view a copy of the public domain dedication, visit 
 * http://creativecommons.org/licenses/publicdomain/ 
 * or send a letter to Creative Commons, 559 Nathan Abbott Way, 
 * Stanford, California 94305, USA.
 */
package examples.register;

import java.io.Serializable;

/**
 * Data object editted by the {@link examples.register.Register} page.
 * 
 * @author Howard Lewis Ship
 */
public class Address implements Serializable
{
	private String _firstName;
	private String _lastName;
	private String _address1;
	private String _address2;
	private String _city;
	private String _state;
	private String _zip;
	
    public String getAddress1()
    {
        return _address1;
    }

    public String getAddress2()
    {
        return _address2;
    }

    public String getCity()
    {
        return _city;
    }

    public String getFirstName()
    {
        return _firstName;
    }

    public String getLastName()
    {
        return _lastName;
    }

    public String getState()
    {
        return _state;
    }

    public String getZip()
    {
        return _zip;
    }

    public void setAddress1(String address1)
    {
        _address1 = address1;
    }

    public void setAddress2(String address2)
    {
        _address2 = address2;
    }

    public void setCity(String city)
    {
        _city = city;
    }

    public void setFirstName(String firstName)
    {
        _firstName = firstName;
    }

    public void setLastName(String lastName)
    {
        _lastName = lastName;
    }

    public void setState(String state)
    {
        _state = state;
    }

    public void setZip(String zip)
    {
        _zip = zip;
    }

}
