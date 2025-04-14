package com.example.genessystem.objects;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class Patient {
    private final SimpleLongProperty nationalId;
    private final SimpleStringProperty medicalRecord;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty dob;
    private final SimpleStringProperty age;
    private final SimpleStringProperty email;
    private final SimpleStringProperty phone;
    private final SimpleStringProperty gender;


    public Patient(long nationalId, String medicalRecord, String firstName, String lastName, String dob,
                   String age, String email, String phone, String gender) {
        this.nationalId = new SimpleLongProperty(nationalId);
        this.medicalRecord = new SimpleStringProperty(medicalRecord);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.dob = new SimpleStringProperty(dob);
        this.age = new SimpleStringProperty(age);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.gender = new SimpleStringProperty(gender);
    }


    public long getNationalId() {
        return nationalId.get();
    }

    public String getMedicalRecord() {
        return medicalRecord.get();
    }

    public String getFirstName() {
        return firstName.get();
    }

    public String getLastName() {
        return lastName.get();
    }

    public String getAge() {
        return age.get();
    }

    public String getEmail() {
        return email.get();
    }

    public String getPhone() {
        return phone.get();
    }

    public String getGender() {
        return gender.get();
    }
}

