package cl.a2r.micampo.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

import cl.a2r.micampo.model.revpostparto.GanRevPostParto;

/**
 * Created by fmartin on 04-09-2016.
 */
public class Diio implements Serializable {

    public Diio(Integer _ganadoid, Integer _diio, Integer _mangada, Date _fecha, String _descrpcion) {
        ganadoid = _ganadoid;
        diio = _diio;
        fecha = _fecha;
        descrpcion = _descrpcion;
        mangada = _mangada;
    }

    public Diio(Integer _diio, Integer _mangada) {
        diio = _diio;
        mangada = _mangada;
    }

    public Diio(Integer _diio) {
        diio = _diio;
    }

    // ATRIBUTOS
    //----------------------------------------------------------------------------------------------
    private Integer ganadoid = 0;
    private Integer diio = 0;
    private Date fecha = null;
    private String descrpcion = "";
    private Integer mangada = 0;

    protected Diio(Parcel in) {
        descrpcion = in.readString();
    }

    // PROPIEDADES
    //----------------------------------------------------------------------------------------------
    public Integer getGanadoId() { return ganadoid; }
    public void setId(Integer _ganadoid) { ganadoid = _ganadoid; }

    public Integer getDiio() { return diio; }
    public void setDiio(Integer _diio) { diio = _diio; }

    public String getDiioString() {
        if (getDiio() != null && getDiio() > 0) {
            String _diio = String.format("%010d", getDiio());
            return _diio.substring(0, 2) + " " + _diio.substring(2, 5) + " " + _diio.substring(5);
        }

        return "";
    }

    public Date getFecha() { return fecha; }
    public void setFecha(Date _fecha) { fecha = _fecha; }

    public String getDescripcion() { return descrpcion; }
    public void setDescripcion(String _descrpcion) { descrpcion = _descrpcion; }

    public Integer getMangada() { return mangada; }
    public void setMangada(Integer _mangada) { mangada = _mangada; }

}
