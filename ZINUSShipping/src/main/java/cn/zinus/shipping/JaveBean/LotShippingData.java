package cn.zinus.shipping.JaveBean;

import java.io.Serializable;

/**
 * Developer:Spring
 * DataTime :2017/9/26 13:48
 * Main Change:
 */

public class LotShippingData implements Serializable {
    private String LOTID;
    private String SHIPPINGPLANNO;
    private String VALIDSTATE;
    private String QTY;
    private String SHIPPINGPLANSEQ;
    private String CONTAINERSEQ;
    private String SHIPPINGDATE;
    private String ORDERTYPE;
    private String ORDERNO;
    private String PRODUCTDEFID;
    private String PRODUCTDEFVERSION;
    private String PRODUCTDEFNAME;
    private String LINENO;
    private String POID;
    private String TRACKOUTTIME;

    public String getTRACKOUTTIME() {
        return TRACKOUTTIME;
    }

    public void setTRACKOUTTIME(String TRACKOUTTIME) {
        this.TRACKOUTTIME = TRACKOUTTIME;
    }

    public String getPRODUCTDEFNAME() {
        return PRODUCTDEFNAME;
    }

    public void setPRODUCTDEFNAME(String PRODUCTDEFNAME) {
        this.PRODUCTDEFNAME = PRODUCTDEFNAME;
    }

    public String getPOID() {
        return POID;
    }

    public void setPOID(String POID) {
        this.POID = POID;
    }

    public String getORDERTYPE() {
        return ORDERTYPE;
    }

    public void setORDERTYPE(String ORDERTYPE) {
        this.ORDERTYPE = ORDERTYPE;
    }

    public String getORDERNO() {
        return ORDERNO;
    }

    public void setORDERNO(String ORDERNO) {
        this.ORDERNO = ORDERNO;
    }

    public String getPRODUCTDEFID() {
        return PRODUCTDEFID;
    }

    public void setPRODUCTDEFID(String PRODUCTDEFID) {
        this.PRODUCTDEFID = PRODUCTDEFID;
    }

    public String getPRODUCTDEFVERSION() {
        return PRODUCTDEFVERSION;
    }

    public void setPRODUCTDEFVERSION(String PRODUCTDEFVERSION) {
        this.PRODUCTDEFVERSION = PRODUCTDEFVERSION;
    }

    public String getLINENO() {
        return LINENO;
    }

    public void setLINENO(String LINENO) {
        this.LINENO = LINENO;
    }

    @Override
    public String toString() {
        return "LotShippingData{" +
                "LOTID='" + LOTID + '\'' +
                ", SHIPPINGPLANNO='" + SHIPPINGPLANNO + '\'' +
                ", VALIDSTATE='" + VALIDSTATE + '\'' +
                ", QTY='" + QTY + '\'' +
                ", SHIPPINGPLANSEQ='" + SHIPPINGPLANSEQ + '\'' +
                ", CONTAINERSEQ='" + CONTAINERSEQ + '\'' +
                ", SHIPPINGDATE='" + SHIPPINGDATE + '\'' +
                '}';
    }

    public String getSHIPPINGPLANSEQ() {
        return SHIPPINGPLANSEQ;
    }

    public void setSHIPPINGPLANSEQ(String SHIPPINGPLANSEQ) {
        this.SHIPPINGPLANSEQ = SHIPPINGPLANSEQ;
    }

    public String getCONTAINERSEQ() {
        return CONTAINERSEQ;
    }

    public void setCONTAINERSEQ(String CONTAINERSEQ) {
        this.CONTAINERSEQ = CONTAINERSEQ;
    }

    public String getSHIPPINGDATE() {
        return SHIPPINGDATE;
    }

    public void setSHIPPINGDATE(String SHIPPINGDATE) {
        this.SHIPPINGDATE = SHIPPINGDATE;
    }

    public String getQTY() {
        return QTY;
    }

    public void setQTY(String QTY) {
        this.QTY = QTY;
    }

    public String getLOTID() {
        return LOTID;
    }

    public void setLOTID(String LOTID) {
        this.LOTID = LOTID;
    }

    public String getSHIPPINGPLANNO() {
        return SHIPPINGPLANNO;
    }

    public void setSHIPPINGPLANNO(String SHIPPINGPLANNO) {
        this.SHIPPINGPLANNO = SHIPPINGPLANNO;
    }

    public String getVALIDSTATE() {
        return VALIDSTATE;
    }

    public void setVALIDSTATE(String VALIDSTATE) {
        this.VALIDSTATE = VALIDSTATE;
    }
}
