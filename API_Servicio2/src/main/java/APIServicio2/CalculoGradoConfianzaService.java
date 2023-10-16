package APIServicio2;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CalculoGradoConfianzaService {

    public String gradoDeConfianza;
    public double puntosDeConfianza;

    public ApiDTO calcularGradoConfianzaUsuario(String username, List<IncidenteAPI> incidentesAbiertos, List<IncidenteAPI> incidentesCerrados) {

        puntosDeConfianza = calcularPuntosConfianzaUsuario(incidentesAbiertos,incidentesCerrados);
        gradoDeConfianza = asociarPuntosConGradoDeConfianza(puntosDeConfianza);
        return new ApiDTO(username,gradoDeConfianza);
    }

    public ApiDTO calcularGradoConfianzaComunidad(String nombre, List<ApiMain.UsuarioData> usuarios) {
        double puntosConfianzaComunidad = 0;
        double puntosAdescontar = 0;
        List<Double> listaPuntos = new ArrayList<>();

        for(ApiMain.UsuarioData usuario : usuarios) {
            List<IncidenteAPI> incidentesAbiertos = usuario.getIncidentesAbiertos();
            List<IncidenteAPI> incidentesCerrados = usuario.getIncidentesCerrados();
            Double puntosDeConfianza = calcularPuntosConfianzaUsuario(incidentesAbiertos,incidentesCerrados);
            listaPuntos.add(puntosDeConfianza);
        }

        for(Double puntos : listaPuntos) {
            puntosConfianzaComunidad += puntos;
            String grado = asociarPuntosConGradoDeConfianza(puntos);
            if(Objects.equals(grado, "Con reservas")) {
                puntosAdescontar += 0.2;
            }
        }
        double promedioPuntosConfianza = (puntosConfianzaComunidad / usuarios.size()) - puntosAdescontar;
        String grado = asociarPuntosConGradoDeConfianza(promedioPuntosConfianza);
        return new ApiDTO(nombre, grado);
    }

    public String asociarPuntosConGradoDeConfianza(double puntos) {
        if (puntos < 2) {
            gradoDeConfianza = "No confiable";
        } else if (puntos >= 2 && puntos <= 3) {
            gradoDeConfianza = "Con reservas";
        } else if (puntos > 3 && puntos <= 5) {
            gradoDeConfianza = "Confiable Nivel 1";
        } else {
            gradoDeConfianza = "Confiable Nivel 2";
        }
        return gradoDeConfianza;
    }

    public double calcularPuntosConfianzaUsuario(List<IncidenteAPI> incidentesAbiertos, List<IncidenteAPI> incidentesCerrados) {
        double puntosDeConfianza = 5;
        List<IncidenteAPI> aperturasFraudulentas = obtenerAperturasFraudulentasDelUsuario(incidentesAbiertos);
        List<IncidenteAPI> cierresFraudulentos = obtenerCierresFraudulentosDelUsuario(incidentesCerrados);

        puntosDeConfianza -= (aperturasFraudulentas.size() + cierresFraudulentos.size())* 0.2;
        if(huboAperturasYCierresDuranteLaSemana(incidentesAbiertos,incidentesCerrados,aperturasFraudulentas,cierresFraudulentos)){
            puntosDeConfianza += 0.5;
        }

        return puntosDeConfianza;
    }

    public List<IncidenteAPI> obtenerAperturasFraudulentasDelUsuario(List<IncidenteAPI> incidentesAbiertos) {
        LocalDateTime inicioSemana = LocalDateTime.now().minusDays(7);
        return incidentesAbiertos.stream().filter(i -> i.getFechaApertura().isAfter(inicioSemana) && ChronoUnit.MINUTES.between(i.getFechaApertura(), i.getFechaCierre()) < 3).collect(Collectors.toList());
    }

    public List<IncidenteAPI> obtenerCierresFraudulentosDelUsuario(List<IncidenteAPI> incidentesCerrados) {
        LocalDateTime inicioSemana = LocalDateTime.now().minusDays(7);
        return incidentesCerrados.stream().filter(i -> i.getFechaApertura().isAfter(inicioSemana) && ChronoUnit.MINUTES.between(i.getFechaApertura(), i.getFechaCierre()) < 3).collect(Collectors.toList());
    }
    public Boolean huboAperturasYCierresDuranteLaSemana(List<IncidenteAPI> incidentesAbiertos, List<IncidenteAPI> incidentesCerrados, List<IncidenteAPI> aperturasFraudulentas, List<IncidenteAPI> cierresFraudulentos) {
        incidentesAbiertos.removeAll(aperturasFraudulentas);
        incidentesCerrados.removeAll(cierresFraudulentos);

        LocalDateTime inicioSemana = LocalDateTime.now().minusDays(7);
        Integer cantidadAbiertos = incidentesAbiertos.stream().filter(i -> i.getFechaApertura().isAfter(inicioSemana)).toList().size();
        Integer cantidadCerrados = incidentesCerrados.stream().filter(i -> i.getFechaCierre().isAfter(inicioSemana)).toList().size();
        return (cantidadAbiertos + cantidadCerrados) > 0;
    }
}
