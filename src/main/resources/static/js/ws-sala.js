let stompClient = null;

addEventListener("DOMContentLoaded", () => {
  conectarWebSocket();
  const btnVolver = document.getElementById("formVolver");
  btnVolver.addEventListener("submit", VolverAlLobby);
  
  mostrarBotonEmpezarPartida(salaData.jugadores.length);
});

function conectarWebSocket() {
  const socket = new WebSocket("ws://localhost:8080/ws");
  stompClient = Stomp.over(socket);

  stompClient.connect(
    {},
    function (frame) {
      console.log("Conectado a WebSocket desde sala:", frame);

      stompClient.subscribe(
        "/topic/sala/" + salaData.codigo,
        function (mensaje) {
          const sala = JSON.parse(mensaje.body);
          actualizarSala(sala);
        }
      );

      stompClient.send("/app/actualizar-sala/" + salaData.codigo, {}, "{}");
    },
    function (error) {
      console.error("Error de conexión WebSocket:", error);
      alert("Error al conectar con el servidor");
    }
  );
}

function actualizarSala(sala) {
  alert("Actualizando sala " + salaData.codigo);
  
  salaData.creador = sala.creador;
  
  mostrarJugadores(sala.jugadores);
  mostrarBotonEmpezarPartida(sala.jugadores.length);
}

function mostrarJugadores(jugadores) {
  console.log("Mostrando jugadores:", jugadores);
  const contenedorJugadores = document.getElementById("jugadores");
  contenedorJugadores.innerHTML = "";

  if (jugadores && jugadores.length > 0) {
    jugadores.forEach((jugador) => {
      const divJugador = document.createElement("div");
      divJugador.style.background = "#999";
      divJugador.style.padding = "10px";
      divJugador.style.marginBottom = "5px";

      divJugador.innerHTML = `<p>${jugador.nombre}</p>`;
      
      contenedorJugadores.appendChild(divJugador);
    });
  } else {
    contenedorJugadores.innerHTML = "<p>No hay jugadores en la sala aún.</p>";
  }
}

function mostrarBotonEmpezarPartida(cantidadJugadores) {
  const divEmpezarPartida = document.getElementById("btnEmpezarPartida");
  
  if (!divEmpezarPartida) {
    console.error("No se encontró el elemento btnEmpezarPartida");
    return;
  }
  
  divEmpezarPartida.innerHTML = "";
  
  if (salaData.nombreJugador === salaData.creador) {
    const estaDeshabilitado = cantidadJugadores < 2;
    
    divEmpezarPartida.innerHTML = `
      <form action="/sala/empezar-partida" method="POST">
        <input type="hidden" name="codigoSala" value="${salaData.codigo}">
        <button type="submit" ${estaDeshabilitado ? 'disabled' : ''}>
          Empezar partida ${estaDeshabilitado ? '(mínimo 2 jugadores)' : ''}
        </button>
      </form>
    `;
    
    if (estaDeshabilitado) {
      alert("Se necesitan al menos 2 jugadores para empezar la partida.");
    } else {
      alert("Eres el creador de la sala, puedes empezar la partida.");
    }
  }
}

function VolverAlLobby() {
  stompClient.unsubscribe();
  alert("Desuscrito de " + salaData.codigo);
}