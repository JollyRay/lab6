package com.company.lab5;

import java.io.Serializable;
import java.util.Objects;

public abstract class Human implements HumanOpportunities, Comparable<Human>, Serializable {
    private String name = "Безымянный";
    private int clothes;

    Human(String name, int clothes) {
        this.name = name;
        this.clothes = clothes;
    }

    Human(String name) {
        this(name, 0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String see(String what){
        return "я видел " + what;
    }

    @Override
    public void takeOffClothes(int i){
        clothes -= i;
    }

    @Override
    public void putOnClothes(int i){
        clothes += i;
    }

    @Override
    public String degreeOfDress() throws ClothingException {
        if (clothes < 0)
            throw new ClothingException();
        return "я одето на " + clothes + "%";
    }

    @Override
    public String swear(){
        return "Совершишь богохульство.";
    }

    public abstract String Go();
    public abstract String Go(String str);

    @Override
    public String toString() {
        return String.format("Человечек, имя = %s, одежда = %s", name, clothes);
    }

    public int getClothes() {
        return clothes;
    }

    public void setClothes(int clothes) {
        this.clothes = clothes;
    }

    @Override
    public int compareTo(Human o) {
        return getClothes() - o.getClothes();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) return false;
        if (this == o) return true;
        Human c = (Human) o;
        return  c.getName().equals(getName()) &&
                c.getClothes() == getClothes();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, clothes);
    }
}
