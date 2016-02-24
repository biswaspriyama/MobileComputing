package edu.asu.cse535assgn1.models;

/**
 * Created by Prameet Singh on 2/2/16.
 */
public class Patient {

    private String id;
    private String name;

    private int age;
    private boolean isMale;

    public Patient(String patientId, String patientName, int age, boolean isMale) {
        this.name = patientName;
        this.id = patientId;
        this.age = age;
        this.isMale = isMale;
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }

    public int getAge() {
        return this.age;
    }

    public boolean isMale() {
        return  this.isMale;
    }
}
