package cl.a2r.micampo.postparto.model;

import java.util.Date;

import cl.a2r.micampo.app.model.Diio;
import cl.a2r.micampo.app.service.DiioInterface;
import cl.a2r.micampo.model.revpostparto.GanRevPostParto;

/**
 * Created by fmartin on 25-08-2016.
 */
public class Postparto implements DiioInterface {

    /**
     * Implementacion de DiioInterface para obtener objeto Diio
     * @return
     */
    @Override
    public Diio getDiioObject() {
        if (getMangada() >0) {
            return new Diio(getGanPostparto().getIdGanado(), getGanPostparto().getDiio(), getMangada(), new Date(), ganPostparto.getDiagnostico() + " / " + ganPostparto.getMedicamento() );
        }
        else {
            return new Diio(getGanPostparto().getDiio(), getMangada());
        }
    }

    // ATRIBUTOS
    //----------------------------------------------------------------------------------------------
    private Integer id;
    private Integer sincronizado = 0;
    private Integer mangada = 0;
    //private Integer modo = 0;
    private Integer candidato = 0;

    private GanRevPostParto ganPostparto;


    // PROPIEDADES
    //----------------------------------------------------------------------------------------------
    public Integer getId() { return id; }
    public void setId(Integer _id) { id = _id; }

    public Integer getSincronizado() { return sincronizado; }
    public void setSincronizado(Integer _sincronizado) { sincronizado = _sincronizado; }

    public Integer getMangada() { return mangada; }
    public void setMangada(Integer _mangada) { mangada = _mangada; }

    //public Integer getModo() { return modo; }
    //public void setModo(Integer _modo) { modo = _modo; }

    public Integer getCandidato() { return candidato == null ? 0 : candidato; }
    public void setCandidato(Integer _candidato) { candidato = _candidato; }

    public GanRevPostParto getGanPostparto() { return ganPostparto; }
    public void setGanPostparto(GanRevPostParto _ganPostparto) { ganPostparto = _ganPostparto; }

    public Integer getDiio() { return getGanPostparto().getDiio(); }
    public String getDiioString() {
        if (getGanPostparto().getDiio() != null && getGanPostparto().getDiio() > 0) {
            String _diio = String.format("%010d", getGanPostparto().getDiio());
            return _diio.substring(0, 2) + " " + _diio.substring(2, 5) + " " + _diio.substring(5);
        }

        return "";
    }

    public String getEid() { return getGanPostparto().getEid(); }

//    public boolean isCandidato() {
//        if (getGanPostparto() != null) {
//            return getGanPostparto().isCandidato();
//        }
//
//        return false;
//    }

    public String getMensaje() {
        if (getGanPostparto() != null && getGanPostparto().getMensaje() != null && getGanPostparto().getMensaje().length() > 0) {
            return getGanPostparto().getMensaje();
        }

        return "";
    }

    public String getTipoParto() {
        if (getGanPostparto().getIdTipoParto() == null) {
            getGanPostparto().setIdTipoParto(0);
        }
        switch (getGanPostparto().getIdTipoParto()) {
            case 1:
                return "Natural";
            case 2:
                return "Inducción";
            default:
                return "INDEFINIDO";
        }
    }


}