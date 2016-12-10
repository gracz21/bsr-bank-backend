package pl.poznan.put.bsr.bank.services.messages;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Kamil Walkowiak
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RegisterMessage {
    @XmlElement(required = true)
    private String userName;
    @XmlElement(required = true)
    private String password;
    @XmlElement(required = true)
    private String firstName;
    @XmlElement(required = true)
    private String lastName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
