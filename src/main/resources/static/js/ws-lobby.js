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
      console.log("Conectado con websockets desde lobby!");

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
  console.log("Mostrando salas...");
  listaSalas.innerHTML = "";

  if (salas && salas.length > 0) {
    salas.forEach((sala) => {
      const divSala = document.createElement("div");
      const divContenido = document.createElement("div");

      const salaLlena = sala.cantidadJugadores >= sala.maxJugadores;
      const salaJugando = sala.estado === 'JUGANDO';
      const deshabilitado = salaLlena || salaJugando;
      let textoBoton = 'UNIRSE';
      if (salaJugando) textoBoton = 'EN PARTIDA';
      else if (salaLlena) textoBoton = 'SALA LLENA';

      divContenido.innerHTML = `
        <strong>Codigo: ${sala.codigo}</strong><br>
        <span>${sala.nombre}</span><br>
        <small>Jugadores: ${sala.cantidadJugadores}/${sala.maxJugadores}</small><br>
        <small>Creador: ${sala.creador.nombre}</small><br>
        <small>Estado: ${sala.estado}</small><br>
        <div>
          <strong>Lista de jugadores:</strong>
          <ul>
            ${sala.jugadores.map((jugador) => `<li>${jugador.nombre}</li>`).join("")}
          </ul>
        </div>
        <form action="/sala/${sala.codigo}" method="POST">
          <button type="submit" ${deshabilitado ? 'disabled' : ''}>
            ${textoBoton}
          </button>
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