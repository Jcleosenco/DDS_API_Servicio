package APIServicio2;

import java.time.LocalDateTime;

public class IncidenteAPI {
  private LocalDateTime fechaApertura;
  private LocalDateTime fechaCierre;

  public IncidenteAPI(LocalDateTime fechaApertura, LocalDateTime fechaCierre) {
    this.fechaApertura = fechaApertura;
    this.fechaCierre = fechaCierre;
  }

  public LocalDateTime getFechaApertura() {
    return fechaApertura;
  }

  public LocalDateTime getFechaCierre() {
    return fechaCierre;
  }
}
