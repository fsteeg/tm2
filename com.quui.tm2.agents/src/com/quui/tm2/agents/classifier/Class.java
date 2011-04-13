package com.quui.tm2.agents.classifier;
//package de.uni_koeln.spinfo.tesla.component.wsd.classifier;
//
//import java.io.Serializable;
//
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.Table;
//
//import de.uni_koeln.spinfo.tesla.rolesystem.presets.dataobjects.Category;
//
//
///**
// * A Tesla Data Object (http://www.spinfo.uni-koeln.de/Forschung/Tesla)
// * containing an int as the actual data.
// * 
// * @author Fabian Steeg (fsteeg@spinfo.uni-koeln.de, Sprachliche Informationsverarbeitung)
// * @see http://www.spinfo.uni-koeln.de/tesla
// * 
// */
//@Entity
//@Table(name = "an_class")
//public class Class implements Category, Serializable {
//
//    private static final long serialVersionUID = 1182544471110L;
//
//    private long id;
//
//    /**
//     * The actual data: an int
//     */
//    private String clazz;
//
//    public Class() {
//
//    }
//
//    public Class(String result) {
//        this.clazz = result;
//    }
//
//    public String getValue() {
//        return clazz;
//    }
//
//    public void setValue(String number) {
//        this.clazz = number;
//    }
//
//    @Id
//    @JoinColumn(table = "an_annotation", name = "id", referencedColumnName = "id", unique = true)
//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//}