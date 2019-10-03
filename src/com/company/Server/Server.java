package com.company.Server;

import com.company.lab5.*;
import com.company.Tools.MessageAnswer;
import com.company.Tools.MessageRequest;
import com.company.Tools.SerialFactory;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class Server {
    static Cave cave = new Cave(Weather.NORMAL, "???");
    static String infoFile = "test.csv";
    static String autoSave = "test.csv";
    static DatagramChannel channel;

    public static void main(String[] args){
        try {
            System.out.println(InetAddress.getLocalHost().toString().substring(InetAddress.getLocalHost().toString().indexOf("/")+1));
            cave = SaverLoadCol.load(infoFile);
            waitPacket();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    static void waitPacket() throws Exception{
        try {
            channel = DatagramChannel.open();
            channel.socket().bind(new InetSocketAddress(1707));
        }
        catch (Exception e){
            channel = DatagramChannel.open();
            channel.socket().bind(new InetSocketAddress(0));
            System.out.println("Предпологаймый порт занят, так что наш новый: "+channel.socket().getLocalPort());
        }
        try{
            while(true) {
                ByteBuffer buf = ByteBuffer.wrap(new byte[1024]);
                buf.clear();
                channel.receive(buf);
                new Thread(() -> answers(buf)).start();
            }
        }catch (IOException e)
        {e.printStackTrace();}
    }

    static void answers(ByteBuffer buffer){
        try {
            MessageRequest messageRequest = (MessageRequest) SerialFactory.unserialize(buffer.array());
            String answer = doCommand(messageRequest.getCommand(), messageRequest.getHuman());
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);
            int count;
            MessageAnswer messageAnswer = new MessageAnswer(1, answer);
            if (SerialFactory.serialize(messageAnswer).length % 1024 ==0)
                count=SerialFactory.serialize(messageAnswer).length/1024;
            else
                count=(SerialFactory.serialize(messageAnswer).length/1024)+1;
            messageAnswer.setCount(count).setInformation(null);
            datagramPacket = new DatagramPacket(SerialFactory.serialize(messageAnswer), SerialFactory.serialize(messageAnswer).length, InetAddress.getByName(messageRequest.getAddress()), messageRequest.getPort()); //datagramPacket = new DatagramPacket(SerialFactory.serialize(messageAnswer), SerialFactory.serialize(messageAnswer).length, InetAddress.getByName("localhost"), messageRequest.getPort());
            socket.send(datagramPacket);
            byte[] bytesAnswer= SerialFactory.serialize(new MessageAnswer(count, answer));
            for (int i=0;count>i;i++){

                datagramPacket = new DatagramPacket(SerialFactory.getByteInfo1024(bytesAnswer, i), SerialFactory.getByteInfo1024(bytesAnswer, i).length, InetAddress.getByName(messageRequest.getAddress()), messageRequest.getPort());//datagramPacket = new DatagramPacket(SerialFactory.serialize(messageAnswer), SerialFactory.serialize(messageAnswer).length, messageRequest.getAddress(), messageRequest.getPort());
                socket.send(datagramPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    static String doCommand(String comman, Human human) {
        String commanda;
        String argum = null;
        if (comman == null)
            return "Пришёл null";

        comman = comman.trim();
        int spaceIndex = comman.indexOf(' ');
        if (spaceIndex == -1) {
            commanda = comman;
            argum=null;
        }
        else {
            commanda = comman.substring(0, spaceIndex);
            argum = comman.substring(spaceIndex + 1).trim();
        }
        if (commanda.length() == 0)
            return "Введите команду";

        switch (commanda) {
//            case "exit":
//                if (!cave.isRemarkMark())
//                    new File(autoSave).delete();
//                System.exit(0);

            case "add":
                if (human == null)
                    return "Не указали аргумент";

                try {
                    cave.add(human);
                    SaverLoadCol.save(autoSave, cave);
                    cave.setRemarkMark(true);
                    return "Создан человечек";
                } catch (Exception e) {
                    return e.getMessage();
                }

            case "add_if_max":
                if (human == null)
                    return "Не указан аргумент";
                try {
                    boolean make = cave.add_if_max(human);
                    if (make) {
                        SaverLoadCol.save(autoSave, cave);
                        cave.setRemarkMark(true);
                        return "Создан человечек";
                    }
                    return "Не создан человек";
                } catch (Exception e) {
                    return e.getMessage();
                }

            case "show":
                return cave.show();

            case "info":
                return cave.getCollectionInfo();

            case "remove":
                if (human == null) {
                    return "Нет аргумента";
                }
                try {
                    if (cave.remove(human)) {
                        SaverLoadCol.save(autoSave, cave);
                        cave.setRemarkMark(true);
                        return "Человек ликвидирован";
                    }
                    return "Человек не найден";
                }
                catch(Exception e){
                    return e.getMessage();
                }

            case "insert":
                if (human==null)
                    return "Нет аргумента";
                if (argum!=null) {
                    try {
                        cave.insert(Integer.parseInt(argum), human);
                        SaverLoadCol.save(autoSave, cave);
                        cave.setRemarkMark(true);
                        return "Человек добавлен по индексу (функции не работает, из-за условий на сортировку)";
                    }
                    catch (NumberFormatException e){
                        return "Вы ввели не число";
                    }
                    catch (Exception e){
                        return e.getMessage();
                    }
                }
                else
                    return "Вы ввели неверный аргумент";
            case "save":
                try {
                    if (argum==null){
                        return "Я не знаю куда сохранять Т.Т, введите нормальное имя файла";
                    }
                    if (SaverLoadCol.save(argum, cave)){
                    cave.setRemarkMark(false);
                    return "Йа усё сохраниль";
                    }
                    return "По этому адресу происходит другое сохранение, выебрите другое место";
                } catch (Exception e) {
                    return e.getMessage();
                }
            case "help":
                return "Вы можете использовать команды:\n\n" +
                        "show - вывести все элементы коллекции\n" +
                        "insert {int index} {element} - добавить элемент по индексу\n" +
                        "add_if_max {element} - добавить элемент если его значение больше любого из имеющихся\n" +
                        "add {element} - добавить элемент\n" +
                        "remove {element} - удалить первое вхождение подобного элемента\n" +
                        "info - вывести информацию о коллекции\n" +
                        "save {link} - сохранить коллекцию\n" +
                        "port - вывод порта пользователя" +
                        "help - вывести спосок команд и подсказок\n\n" +
                        "Подсказки:\n" +
                        "Любая команда заканчивается на ';'\n" +
                        "{element} - это элемент записанный в формате JSON\n" +
                        "{int index} - индекс аргумента"+
                        "\n\nПримеры аргументов: {\"name\": \"Jack\", \"clothes\": 22}\n" +
                        "{\"name\": \"Gab\", \"clothes\": 75, \"width\": 100, \"height\": 200}";

            default: return "Нет такой команды: \""+comman+"\"\nПопробуйте снова или восользуйтесь командой \"help;\".";
        }
    }

}
