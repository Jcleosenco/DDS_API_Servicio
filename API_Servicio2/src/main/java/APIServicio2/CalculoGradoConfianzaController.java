package APIServicio2;

import java.util.List;


public class CalculoGradoConfianzaController {

    private static CalculoGradoConfianzaService calculoGradoConfianzaService;

    public CalculoGradoConfianzaController(){
        calculoGradoConfianzaService = new CalculoGradoConfianzaService();
    }

    public ApiDTO calcularGradoConfianzaUsuario(String username, List<IncidenteAPI> incidentesAbiertos, List<IncidenteAPI> incidentesCerrados) {
        return calculoGradoConfianzaService.calcularGradoConfianzaUsuario(username,incidentesAbiertos,incidentesCerrados);
    }

    public ApiDTO calcularGradoConfianzaComunidad(String nombre, List<ApiMain.UsuarioData> usuarios) {
        return calculoGradoConfianzaService.calcularGradoConfianzaComunidad(nombre,usuarios);
    }
}
