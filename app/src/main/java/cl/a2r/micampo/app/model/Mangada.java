
package cl.a2r.micampo.app.model;

import java.util.Date;
import java.util.List;

/**
 * Created by fmartin on 04-09-2016.
 */
public class Mangada {

    public Mangada() {
    }

    public Mangada(Integer _id, Integer _numero, Integer _predioid, Integer _actividadid, Date _fecha, Integer _estadoid) {
        id = _id;
        numero = _numero;
        predioid = _predioid;
        actividadid = _actividadid;
        fecha = _fecha;
        estadoid = _estadoid;
    }

    // ATRIBUTOS
    //----------------------------------------------------------------------------------------------
    private Integer id;
    private Integer numero = 0;
    private Integer predioid = 0;
    private Integer actividadid = 0;
    private Date fecha = new Date();
    private Integer estadoid = 0;

    private List<MangadaDetalle> detalles = null;

    // PROPIEDADES
    //----------------------------------------------------------------------------------------------
    public Integer getId() { return id; }
    public void setId(Integer _id) { id = _id; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer _numero) { numero = _numero; }

    public Integer getPredioId() { return predioid; }
    public void setPredioId(Integer _predioid) { predioid = _predioid; }

    public Integer getActividadId() { return actividadid; }
    public void setActividadId(Integer _actividadid) { actividadid = _actividadid; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date _fecha) { fecha = _fecha; }

    public Integer getEstadoId() { return estadoid; }
    public void setEstadoId(Integer _estadoid) { estadoid = _estadoid; }

    public List<MangadaDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<MangadaDetalle> _detalles) { detalles = _detalles; }
}
