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
      console.log("Conectado con websockets desde lobby");

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

      const salaLlena = sala.cantidadJugadores >= sala.maxJugadores;
      const salaJugando = sala.estado === 'JUGANDO';
      const deshabilitado = salaLlena || salaJugando;
      let textoBoton = 'UNIRSE';
      if (salaJugando) textoBoton = 'EN PARTIDA';
      else if (salaLlena) textoBoton = 'SALA LLENA';

      // Usando componentes neo-sala-tarjeta
      divSala.className = 'neo-sala-tarjeta';

      const inicialCreador = sala.creador.nombre.charAt(0).toUpperCase();

      divSala.innerHTML = `
        <div class="neo-sala-titulo">
          ${sala.nombre}
        </div>
        <div class="neo-sala-contenido">
          <div class="neo-sala-item">
            <div class="neo-sala-etiqueta">Creador</div>
            <div class="neo-sala-creador">
              <div class="neo-sala-avatar">${inicialCreador}</div>
              <span class="neo-sala-nombre-creador">${sala.creador.nombre}</span>
            </div>
          </div>
          <div class="neo-sala-item">
            <div class="neo-sala-etiqueta">Categorías</div>
            <div style="font-size: 1.3rem; font-weight: 700;">${sala.cantidadCategorias}</div>
          </div>
          <div class="neo-sala-item">
            <div class="neo-sala-etiqueta">Duración</div>
            <div style="font-size: 1.3rem; font-weight: 700;">${sala.duracion}s</div>
          </div>
          <div class="neo-sala-item">
            <div class="neo-sala-etiqueta">Jugadores</div>
            <div style="font-size: 1.3rem; font-weight: 700;">${sala.cantidadJugadores}/${sala.maxJugadores}</div>
          </div>
          <div class="neo-sala-item">
            <div class="neo-sala-etiqueta">Estado</div>
            <div style="font-weight: 900;">${sala.estado}</div>
          </div>
          <form action="/sala/${sala.codigo}" method="POST" style="margin-top: 1rem;">
            <button type="submit" class="${deshabilitado ? 'neo-sala-boton-deshabilitado' : 'neo-sala-boton'}" ${deshabilitado ? 'disabled' : ''}>
              UNIRSE
            </button>
          </form>
        </div>
      `;

      listaSalas.appendChild(divSala);
    });
  }
}