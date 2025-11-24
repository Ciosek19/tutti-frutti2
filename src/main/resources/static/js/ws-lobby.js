let stompClient = null;

addEventListener("DOMContentLoaded", () => {
  conectarWebSocket();
  const btnCrearSala = document.getElementById("btnCrearSala");
  btnCrearSala.addEventListener("click", crearSala);
});

function conectarWebSocket() {
  const socket = new WebSocket("ws://localhost:8080/ws");
  stompClient = Stomp.over(socket);

  stompClient.connect(
    {},
    function (frame) {
      alert("Estas conectado con websockets desde lobby!");

      stompClient.subscribe("/topic/lobby", function (mensaje) {
        const salas = JSON.parse(mensaje.body);
        mostrarSalas(salas);
      });

      stompClient.send("/app/obtener-salas", {}, "{}");
    },
    function (error) {
      alert("ConectarWebSockets() -> Error de conexion!", error);
    }
  );
}

function mostrarSalas(salas) {
  const listaSalas = document.getElementById("listaSalas");
  alert("Mostrando salas...");
  listaSalas.innerHTML = "";

  if (salas && salas.length > 0) {
    salas.forEach((sala) => {
      const divSala = document.createElement("div");
      const divContenido = document.createElement("div");
      divContenido.innerHTML = `
        <strong>Codigo: ${sala.codigo}</strong><br>
        <span>${sala.nombre}</span><br>
        <small>Jugadores: ${sala.cantidadJugadores}</small><br>
        <small>Creador: ${sala.creador}</small><br>
        <div>
          <strong>Lista de jugadores:</strong>
          <ul>
            ${sala.jugadores.map((jugador) => `<li>${jugador.nombre}</li>`).join("")}
          </ul>
        </div>
        <form action="/sala/${sala.codigo}" method="POST">
          <button type="submit">UNIRSE</button>
        </form>
      `;

      divSala.appendChild(divContenido);
      listaSalas.appendChild(divSala);
    });
  } else {
    listaSalas.innerHTML =
      "<p>No hay salas disponibles. Crea la primera sala!</p>";
  }
}
