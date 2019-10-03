package com.company.lab5;

import java.io.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

public class SaverLoadCol {

    private static LinkedList<String> arrayOfWay= new LinkedList<>();
    /**
     * Сохраняет коллекции в файл
     * @param way путь к файлу
     * @param cave сама коллекция
     * @throws Exception если при сохранение возникли ошибки. Смотри сообщения.
     */
    public static boolean save(String way, Cave cave) throws Exception{
        if (addInList(way)) {
            File file = new File(way);

            if (file.isDirectory()) {
                throw new IllegalArgumentException("Это директория, а не файл");
            }
            file.createNewFile();
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
            writer.append((cave.getName())).append(", ").append(cave.getWeather().toString()).append(", ").append(Long.toString(cave.getDate().getTime())).append('\n');
            for (Human human : cave.getList()) {
                writer.append(human.getName()).append(", ").append(Integer.toString(human.getClothes()));
                if (human instanceof Forwarder) {
                    writer.append(", ").append(((Forwarder) human).getFind().getHeightWidth());
                }
                writer.append("\n");
            }
            cave.setRemarkMark(false);
            writer.close();
            removeInList(way);
            return true;
        }
        return false;
    }

    /**
     * Загрузка коллекции из файла
     * @param way путь к файлу
     * @return возвращает коллецию
     * @throws Exception выкидывает исключение, если что-то идёт не так. Смотри сообщения.
     */
    public static Cave load(String way) throws Exception{
        File file = new File(way);
        file.createNewFile();
        Scanner scan = new Scanner(file);
        String hat;
        if (scan.hasNextLine()) {
            hat = scan.nextLine();
            if (!hat.contains(","))
                throw new Exception("Шапка указана не верно");
            String name = hat.substring(0, hat.indexOf(",")).trim();
            Weather weather;
            Date date;
            hat = hat.substring(hat.indexOf(",") + 1);
            if (!hat.contains(","))
                throw new Exception("Шапка указана не верно");
            try {
                weather = Weather.valueOf((hat.substring(0, hat.indexOf(",")).trim()));
                date = new Date(Long.parseLong(hat.substring(hat.indexOf(",") + 1).trim()));
            } catch (IllegalStateException e) {
                throw new IllegalStateException("Загрузка файла не удалась, погода в шапке указана не верно");
            } catch (Exception e) {
                throw new Exception("Загрузка файла не удалась, дата в шапке указана неверно");
            }
            Cave cave = new Cave(weather, name);
            cave.setDate(date);
            int clothes, hieght, width;
            while (scan.hasNextLine()) {
                hat = scan.nextLine().trim();
                if (hat.isEmpty())
                    continue;
                String[] masStats = hat.split(",");
                if (masStats.length == 2) {
                    name = masStats[0].trim();
                    try {
                        clothes = Integer.parseInt(masStats[1].trim());
                    } catch (Exception e) {
                        throw new Exception("Неверно задана одетость");
                    }
                    cave.add(HumanMaker.makeHuman("{\"name\": \""+name+"\", \"clothes\": "+clothes+"}"));
                } else if (masStats.length == 4) {
                    name = masStats[0].trim();
                    try {
                        clothes = Integer.parseInt(masStats[1].trim());
                        width = Integer.parseInt(masStats[3].trim());
                        hieght = Integer.parseInt(masStats[2].trim());
                    } catch (Exception e) {
                        throw new Exception("Неверно заданы характеристики экспедитора");
                    }
                    Forwarder forw = new Forwarder(name, clothes);
                    forw.setFind(new Picture(hieght, width));
                    cave.add(forw);
                } else
                    throw new Exception("Просто всё не правельно, укажите нормальный файл, где нормальные объекты");

            }
            scan.close();
            cave.setRemarkMark(false);
            return cave;
        }
        return new Cave(Weather.NORMAL, "Desperation");
    }
    static String createheAutoSavePath(String autoSave){
        int number = 2;
        if (new File(autoSave).exists() || new File(autoSave).isDirectory()) {
            while (new File(autoSave + number).exists() || new File(autoSave + number).isDirectory())
                number++;
            autoSave = autoSave + number + ".csv";
        }
        else
            autoSave = autoSave+".csv";
        try {
            File autoSaveFile = new File(autoSave);
            autoSaveFile.createNewFile();
            if (!autoSaveFile.canRead()) {
                System.out.println("Нет доступа к дирекктории, я отказываюсь рабоать в таких условиях");
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return autoSave;
    }

    static String createInfoFilePath(String infoFile){
        String nodoTak;
        if (infoFile.lastIndexOf(".csv")!=4){
            System.out.println("Ваш файл не csv, но мы это сейчас подправим");
            if (infoFile.lastIndexOf(".")==-1)
                nodoTak=infoFile+infoFile+".csv";
            else
                nodoTak=infoFile.substring(0, infoFile.lastIndexOf("."))+".csv";
            if (new File(infoFile).exists())
            try{
                new File(nodoTak).createNewFile();
                FileReader fileReader = new FileReader(infoFile);
                FileWriter fileWriter = new FileWriter(nodoTak);
                int ch = fileReader.read();
                while(ch != -1) {
                    fileWriter.append((char) ch);
                    ch = fileReader.read();
                }
                fileWriter.close();
                fileReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            infoFile=nodoTak;
        }
        return infoFile;
    }
    static synchronized boolean addInList(String way){
        if (arrayOfWay.contains(way))
            return false;
            arrayOfWay.add(way);
            return true;
    }
    static synchronized void removeInList(String way){
        arrayOfWay.remove(way);
    }

}
