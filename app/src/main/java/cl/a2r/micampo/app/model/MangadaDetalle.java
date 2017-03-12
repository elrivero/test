
package cl.a2r.micampo.app.model;

/**
 * Created by fmartin on 04-09-2016.
 */
public class MangadaDetalle {

    public MangadaDetalle() {
    }

    public MangadaDetalle(Integer _id, Integer _mangadaid, Integer _ganadoid) {
        id = _id;
        mangadaid = _mangadaid;
        ganadoid = _ganadoid;
    }

    // ATRIBUTOS
    //----------------------------------------------------------------------------------------------
    private Integer id;
    private Integer mangadaid = 0;
    private Integer ganadoid = 0;

    // PROPIEDADES
    //----------------------------------------------------------------------------------------------
    public Integer getId() { return id; }
    public void setId(Integer _id) { id = _id; }

    public Integer getMangadaId() { return mangadaid; }
    public void setMangadaId(Integer _mangadaid) { mangadaid = _mangadaid; }

    public Integer getGanadoId() { return ganadoid; }
    public void setGanadoId(Integer _ganadoid) { ganadoid = _ganadoid; }

}
