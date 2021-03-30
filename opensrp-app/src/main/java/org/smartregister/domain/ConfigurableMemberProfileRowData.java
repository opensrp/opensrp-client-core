package org.smartregister.domain;

public class ConfigurableMemberProfileRowData {

    private int rowIconId;
    private String rowTitle;
    private String rowDetail;
    private Class<?> rowClickedLaunchedClass;

    public String getRowTitle() {
        return rowTitle;
    }

    public void setRowTitle(String rowTitle) {
        this.rowTitle = rowTitle;
    }


    public String getRowDetail() {
        return rowDetail;
    }

    public void setRowDetail(String rowDetail) {
        this.rowDetail = rowDetail;
    }

    public int getRowIconId() {
        return rowIconId;
    }

    public void setRowIconId(int rowIconId) {
        this.rowIconId = rowIconId;
    }

    public Class<?> getRowClickedLaunchedClass() {
        return rowClickedLaunchedClass;
    }

    public void setRowClickedLaunchedClass(Class<?> rowClickedLaunchedClass) {
        this.rowClickedLaunchedClass = rowClickedLaunchedClass;
    }
}
