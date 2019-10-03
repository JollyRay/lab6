package com.company.Tools;

import java.io.*;

public class SerialFactory {
    static byte byteMass[];
    public static byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(object);
        byteMass = baos.toByteArray();
        oos.close();
        return byteMass;
    }
    public static Object unserialize(byte[] bytes) throws IOException, ClassNotFoundException{
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object object = ois.readObject();
        ois.close();
        return object;
    }
    public static byte[] ArrayComination(final byte[] ...arrays ) {
        int size = 0;
        for ( byte[] a: arrays )
            size += a.length;

        byte[] res = new byte[size];

        int destPos = 0;
        for ( int i = 0; i < arrays.length; i++ ) {
            if ( i > 0 ) destPos += arrays[i-1].length;
            int length = arrays[i].length;
            System.arraycopy(arrays[i], 0, res, destPos, length);
        }

        return res;
    }
    public static void readmass(byte[] b)
    {   int i = 0;
        for (byte byt:b) {
            if (byt==1)
                i++;
        }
        System.out.println(i);
    }
    public static byte[] getByteInfo1024(byte[] byteMass, int number){
        int start, stop;
        if (byteMass.length>(number+1)*1024){
            start=number*1024;
            stop=(number+1)*1024;
        }
        else{
            start=number*1024;
            stop=byteMass.length;
        }
        byte[] bytes=new byte[stop-start];
        for (int counter=start;counter<stop;counter++){
            bytes[counter-start]=byteMass[counter];
        }
        return bytes;
    }
}
