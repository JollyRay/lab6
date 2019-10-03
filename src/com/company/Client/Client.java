package com.company.Client;

import com.company.Tools.MessageAnswer;
import com.company.Tools.MessageRequest;
import com.company.Tools.SerialFactory;
import com.company.lab5.Human;
import com.company.lab5.HumanMaker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class Client {
    public static void main(String[] args) throws Exception{
        boolean readyMessege;
        MessageRequest messageRequest;
        String command="";
        String argument="";
        String IP;
        Human human = null;
        int ServerPort = 1707;
        try {
            IP = args[0].trim();
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            IP=InetAddress.getLocalHost().toString().substring(InetAddress.getLocalHost().toString().indexOf("/")+1);
        }
        System.out.println("Любая команда заканчивается \";\"");
        DatagramSocket serverSocket = new DatagramSocket(0);
        DatagramChannel channel = DatagramChannel.open ();
        String newData;
        while (true) {
            readyMessege = false;
            System.out.print("--->");
            newData = CommandReader().trim();
            if (newData == null)
                System.exit(0);
            int spaceIndex = newData.indexOf(" ");
            if (spaceIndex == -1){
                command = newData;
            }else
            {
                command=newData.substring(0, spaceIndex);
                argument=newData.substring(spaceIndex+1).trim();
            }
            switch (command){
                case "exit":
                    System.exit(0);
                case "setport":
                    System.out.println("Порт сервера сменён на "+argument);
                    ServerPort = Integer.parseInt(argument);
                    break;
                case "port":
                    System.out.println("port=" + serverSocket.getLocalPort());
                    break;
                case "add_if_max":
                case "add":
                case "remove":
                    if (spaceIndex==-1){
                        System.out.println("Вы не ввели аргумент");
                    }else{
                        try{
                            human = HumanMaker.makeHuman(argument);
//                            messageRequest = new MessageRequest(command, human, InetAddress.getLocalHost().toString().substring(InetAddress.getLocalHost().toString().indexOf("/") + 1), serverSocket.getLocalPort());
                            readyMessege= true;
                        }
                        catch (Exception e){
                            System.out.println(e.getMessage());
                        }
                    }
                    break;
                case "insert":
                    if (spaceIndex==-1){
                        System.out.println("Вы не ввели аргумент");
                    }else{
                        try{
                            int secondSpaceIndex = Integer.parseInt(argument.substring(0, argument.indexOf(' ')));
                            if (secondSpaceIndex!=-1) {
                                command +=" "+argument.substring(0, secondSpaceIndex);
                                argument = argument.substring(secondSpaceIndex+1).trim();
                                human = HumanMaker.makeHuman(argument);
//                                messageRequest = new MessageRequest(command, human, InetAddress.getLocalHost().toString().substring(InetAddress.getLocalHost().toString().indexOf("/") + 1), serverSocket.getLocalPort());
                                readyMessege= true;
                            }
                        }
                        catch (NumberFormatException e){
                            System.out.println("Вы ввели не число");
                        }
                        catch (Exception e){
                            System.out.println(e.getMessage());
                        }
                    }
                    break;
                case "show":
                case "info":
                case "help":
                    human=null;
//                    messageRequest = new MessageRequest(command, null, InetAddress.getLocalHost().toString().substring(InetAddress.getLocalHost().toString().indexOf("/") + 1), serverSocket.getLocalPort());
                    readyMessege= true;
                    break;
                case "save":
                    command=newData;
                    human=null;
//                    messageRequest = new MessageRequest(newData, null, InetAddress.getLocalHost().toString().substring(InetAddress.getLocalHost().toString().indexOf("/") + 1), serverSocket.getLocalPort());
                    readyMessege= true;
                    break;
                default:
                    System.out.println("Нет такой команды: \""+command+"\"\nПопробуйте снова или восользуйтесь командой \"help;\".");
            }
//            if (newData.trim().indexOf("exit") == 0)
//                System.exit(0);
//            if (newData.trim().indexOf("port") == 0 || newData.trim().indexOf("setport") == 0) {
//                if (newData.trim().indexOf("port") == 0)
//                    System.out.println("port=" + serverSocket.getLocalPort());
//                else
//                    System.out.println(newData.trim().substring(8).trim());
//                    ServerPort = Integer.parseInt(newData.trim().substring(8).trim());
//                }
//            else
            {
                if (readyMessege) {
                    Thread tr = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(9800);
                                System.err.print("Возникли технические проблемы. Сервер временно не доступен.");
                                System.exit(0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    tr.start();
                    ByteBuffer buf = ByteBuffer.wrap(new byte[1024]);
                    buf.clear();
                    messageRequest = new MessageRequest(command, human, InetAddress.getLocalHost().toString().substring(InetAddress.getLocalHost().toString().indexOf("/") + 1), serverSocket.getLocalPort());
                    buf.put(SerialFactory.serialize(messageRequest));
                    buf.flip();
                    channel.send(buf, new InetSocketAddress(IP, ServerPort));
                    poluchatel(serverSocket);
                    tr.stop();
                }
            }
        }

    }

    public static void poluchatel(DatagramSocket socket) throws Exception{
        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
        socket.receive(packet);
        int count = ((MessageAnswer) SerialFactory.unserialize(packet.getData())).getCount();
        byte[] anserByte = new byte[0];
        for (int i=0;count>i;i++){
            socket.receive(packet);
            anserByte=SerialFactory.ArrayComination(anserByte, packet.getData());
        }
        System.out.println(((MessageAnswer)SerialFactory.unserialize(anserByte)).getInformation());
    }


    static String CommandReader(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        char nextChar='j';
        int intChar;
        boolean kov = false;
        String strangeString="";
        while (nextChar!=';' || kov) {
            try {
                intChar= reader.read();
                if (intChar==-1)
                    System.exit(0);
                else
                    nextChar=(char) intChar;
                if (nextChar=='"')
                    kov=!kov;
                if (nextChar == ';' && !kov)
                    return strangeString;
                strangeString += nextChar;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
//
//    static String doComand(String newData){
//        Human human;
//        String command;
//        String argument;
//        int spaceIndex = newData.indexOf(' ');
//        if (spaceIndex != 0)
//        {
//            command = newData.substring(0, spaceIndex);
//            argument = newData.substring(spaceIndex+1);
//            argument.trim();
//        }
//        else {
//            command = newData;
//            argument="";
//        }
//        switch (command) {
//            case "add":
//                if (argument.length()==0){
//                    return "Не указали аргумент";
//                }else
//                    try {
//                        human = HumanMaker.makeHuman(argument);
//                    } catch (Exception e) {
//                        return e.getMessage();
//                    }
//            case "add_if_max":
//                if (argument.length()==0){
//                    return "Не указали аргумент";
//                }else
//                    try {
//                        human = HumanMaker.makeHuman(argument);
//                    } catch (Exception e) {
//                        return e.getMessage();
//                    }
//
//
//        }
//
//    }

}
