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
}
