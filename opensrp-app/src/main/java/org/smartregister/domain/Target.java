package org.smartregister.domain;

import com.google.gson.annotations.SerializedName;

import java.util.AbstractMap.SimpleEntry;
import java.util.Date;
import java.util.List;

/**
 * Created by samuelgithengi on 4/29/19.
 */
public class Target {

    private String measure;

    private Date due;

    @SerializedName("detail")
    private List<Detail> details;

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public Date getDue() {
        return due;
    }

    public void setDue(Date due) {
        this.due = due;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }


    class Detail {

        private Measure detailQuantity;

        private MeasureRange detailRange;

        private SimpleEntry<String, String> detailCodableConcept;

        public Measure getDetailQuantity() {
            return detailQuantity;
        }

        public void setDetailQuantity(Measure detailQuantity) {
            this.detailQuantity = detailQuantity;
        }

        public MeasureRange getDetailRange() {
            return detailRange;
        }

        public void setDetailRange(MeasureRange detailRange) {
            this.detailRange = detailRange;
        }

        public SimpleEntry<String, String> getDetailCodableConcept() {
            return detailCodableConcept;
        }

        public void setDetailCodableConcept(SimpleEntry<String, String> detailCodableConcept) {
            this.detailCodableConcept = detailCodableConcept;
        }
    }

    class Measure {
        private String value;

        private String comparator;

        private float unit;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getComparator() {
            return comparator;
        }

        public void setComparator(String comparator) {
            this.comparator = comparator;
        }

        public float getUnit() {
            return unit;
        }

        public void setUnit(float unit) {
            this.unit = unit;
        }
    }

    class MeasureRange {
        private Measure high;

        private Measure low;

        public Measure getHigh() {
            return high;
        }

        public void setHigh(Measure high) {
            this.high = high;
        }

        public Measure getLow() {
            return low;
        }

        public void setLow(Measure low) {
            this.low = low;
        }
    }

}


