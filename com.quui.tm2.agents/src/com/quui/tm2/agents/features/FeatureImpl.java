package com.quui.tm2.agents.features;
//package de.uni_koeln.spinfo.tesla.component.wsd.features;
//
//import java.io.Serializable;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Vector;
//
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.OneToMany;
//import javax.persistence.Table;
//
//import org.hibernate.annotations.CollectionOfElements;
//import org.hibernate.annotations.IndexColumn;
//
//import de.uni_koeln.spinfo.tesla.rolesystem.presets.dataobjects.Features;
//
//
///**
// * A Tesla Data Object (http://www.spinfo.uni-koeln.de/Forschung/Tesla)
// * wrapping a double as the actual data.
// * 
// * @author Fabian Steeg (fsteeg@spinfo.uni-koeln.de, Sprachliche Informationsverarbeitung)
// * @see http://www.spinfo.uni-koeln.de/tesla
// * 
// */
//@Entity
//@Table(name = "an_feature")
//public class FeatureImpl implements Features, Serializable {
//
//    private static final long serialVersionUID = 1182047993762L;
//
//    private long id;
//
//    /**
//     * The actual data: a float between 0 and 1
//     */
//    private float number;
//
//    public FeatureImpl() {
//
//    }
//
//    public FeatureImpl(float number) {
//        this.number = number;
//    }
//
////    public float getValue() {
////        return number;
////    }
////
////    public void setValue(float number) {
////        this.number = number;
////    }
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
//    
//    @CollectionOfElements
//	public List<Number> getFeatures() {
//		return new Vector<Number>(Arrays.asList(new Float[]{number}));
//	}
//	
//	public void setFeatures(List<Number> list){
//		this.number = (Float) list.get(0);
//	}
//
//}