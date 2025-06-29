package com.example.gateway.model;

import java.util.List;

public class ForecastData {
    private List<String> timeSeries;
    private List<Elem> values;

    public static class Elem{
        private String elem;
        private float lat;
        private float lng;
        private List<Float> data;

        public String getElem() {
            return elem;
        }

        public void setElem(String elem) {
            this.elem = elem;
        }

        public float getLat() {
            return lat;
        }

        public void setLat(float lat) {
            this.lat = lat;
        }

        public float getLng() {
            return lng;
        }

        public void setLng(float lng) {
            this.lng = lng;
        }

        public List<Float> getData() {
            return data;
        }

        public void setData(List<Float> data) {
            this.data = data;
        }
    }

    public List<String> getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(List<String> timeSeries) {
        this.timeSeries = timeSeries;
    }

    public List<Elem> getValues() {
        return values;
    }

    public void setValues(List<Elem> values) {
        this.values = values;
    }
}
