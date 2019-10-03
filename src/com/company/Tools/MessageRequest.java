package com.company.Tools;

import java.io.Serializable;
import com.company.lab5.*;

public class MessageRequest implements Serializable {

    public MessageRequest(String command, String address, int port) {
        this.command = command;
        this.address = address;
        this.port = port;
        this.human = null;
    }
    public MessageRequest(String command, Human human, String address, int port) {
        this.command = command;
        this.address = address;
        this.port = port;
        this.human = human;
    }

    private static final long serialVersionUID=1234567876;
    private String command;
    private Human human;
    private String address;
    private int port;

    public String getCommand() {return command;}

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void setcommand(String command) {
        this.command = command;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Human getHuman() {return human;}

    public void setHuman(Human human) {this.human = human;}
}


