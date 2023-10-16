package APIServicio2;

import com.google.gson.Gson;

import java.util.List;

import static spark.Spark.*;

public class ApiMain {
    public static void main(String[] args) {
        // Defino servidor
        port(8081);

        // Defino controllers
        CalculoGradoConfianzaController calculoGradoConfianzaController = new CalculoGradoConfianzaController();

        // Crea una instancia de Gson
        Gson gson = new Gson();

        // Defino Rutas
        get("/status", (req, res) -> {
            return gson.toJson("API OK");
        });
        post("/usuarios/gradoConfianza", (req, res) -> {
            String jsonBody = req.body();
            UsuarioData reqData = gson.fromJson(jsonBody, UsuarioData.class);

            String username = reqData.getUsername();
            List<IncidenteAPI> incidentesAbiertos = reqData.getIncidentesAbiertos();
            List<IncidenteAPI> incidentesCerrados = reqData.getIncidentesCerrados();

            ApiDTO result = calculoGradoConfianzaController.calcularGradoConfianzaUsuario(username,incidentesAbiertos,incidentesCerrados);
            return gson.toJson(result);
        });
        post("/comunidades/gradoConfianza", (req, res) -> {
            String jsonBody = req.body();
            ComunidadData reqData = gson.fromJson(jsonBody, ComunidadData.class);

            String nombre = reqData.getNombre();
            List<UsuarioData> usuarios = reqData.getUsuarios();

            ApiDTO result = calculoGradoConfianzaController.calcularGradoConfianzaComunidad(nombre,usuarios);
            return gson.toJson(result);
        });
    }

    static class UsuarioData {
        private String username;
        private List<IncidenteAPI> incidentesAbiertos;
        private List<IncidenteAPI> incidentesCerrados;

        public String getUsername() {
            return username;
        }

        public List<IncidenteAPI> getIncidentesAbiertos() {
            return incidentesAbiertos;
        }

        public List<IncidenteAPI> getIncidentesCerrados() {
            return incidentesCerrados;
        }
    }

    static class ComunidadData {
        private String nombre;
        private List<UsuarioData> usuarios;

        public String getNombre() {
            return nombre;
        }

        public List<UsuarioData> getUsuarios() {
            return usuarios;
        }
    }
}
