let stompClient = null;

addEventListener("DOMContentLoaded", () => {
  conectarWebSocket();
  const btnVolver = document.getElementById("formVolver");
  btnVolver.addEventListener("submit", VolverAlLobby);
  
  mostrarBotonEmpezarPartida(salaData.jugadores.length);
  mostrarConfiguracion();
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
  salaData.cantidadCategorias = sala.cantidadCategorias || 5;
  salaData.maxJugadores = sala.maxJugadores || 4;
  
  mostrarJugadores(sala.jugadores);
  mostrarBotonEmpezarPartida(sala.jugadores.length);
  mostrarConfiguracion();
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

function mostrarConfiguracion() {
  const divConfiguracion = document.getElementById("configuracion");
  
  if (!divConfiguracion) {
    console.error("No se encontró el elemento configuracion");
    return;
  }
  
  divConfiguracion.innerHTML = "";
  
  // Solo mostrar configuración si eres el creador
  if (salaData.nombreJugador === salaData.creador) {
    divConfiguracion.innerHTML = `
      <h3>Configuración de la Partida</h3>
      <div style="background: #f0f0f0; padding: 15px; margin: 10px 0; border-radius: 5px;">
        <div style="margin-bottom: 15px;">
          <label for="cantidadCategorias">Cantidad de categorías (2-15):</label><br>
          <input 
            type="number" 
            id="cantidadCategorias" 
            min="2" 
            max="15" 
            value="${salaData.cantidadCategorias}"
            style="width: 100px; padding: 5px; margin-top: 5px;">
          <button onclick="actualizarCantidadCategorias()" style="margin-left: 10px;">Actualizar</button>
        </div>
        
        <div>
          <label for="maxJugadores">Máximo de jugadores (2-6):</label><br>
          <input 
            type="number" 
            id="maxJugadores" 
            min="2" 
            max="6" 
            value="${salaData.maxJugadores}"
            style="width: 100px; padding: 5px; margin-top: 5px;">
          <button onclick="actualizarMaxJugadores()" style="margin-left: 10px;">Actualizar</button>
        </div>
      </div>
    `;
  } else {
    // Mostrar configuración actual para jugadores no creadores
    divConfiguracion.innerHTML = `
      <h3>Configuración de la Partida</h3>
      <div style="background: #f0f0f0; padding: 15px; margin: 10px 0; border-radius: 5px;">
        <p><strong>Categorías:</strong> ${salaData.cantidadCategorias}</p>
        <p><strong>Máximo jugadores:</strong> ${salaData.maxJugadores}</p>
      </div>
    `;
  }
}

function actualizarCantidadCategorias() {
  const input = document.getElementById("cantidadCategorias");
  const cantidad = parseInt(input.value);
  
  if (cantidad < 2 || cantidad > 15) {
    alert("La cantidad de categorías debe estar entre 2 y 15");
    input.value = salaData.cantidadCategorias;
    return;
  }
  
  const datos = {
    codigoSala: salaData.codigo,
    cantidadCategorias: cantidad
  };
  
  stompClient.send("/app/configurar-categorias", {}, JSON.stringify(datos));
  alert(`Categorías actualizadas a ${cantidad}`);
}

function actualizarMaxJugadores() {
  const input = document.getElementById("maxJugadores");
  const cantidad = parseInt(input.value);
  
  if (cantidad < 2 || cantidad > 6) {
    alert("El máximo de jugadores debe estar entre 2 y 6");
    input.value = salaData.maxJugadores;
    return;
  }
  
  if (cantidad < salaData.jugadores.length) {
    alert(`No puedes reducir el máximo a ${cantidad} porque ya hay ${salaData.jugadores.length} jugadores en la sala`);
    input.value = salaData.maxJugadores;
    return;
  }
  
  const datos = {
    codigoSala: salaData.codigo,
    maxJugadores: cantidad
  };
  
  stompClient.send("/app/configurar-max-jugadores", {}, JSON.stringify(datos));
  alert(`Máximo de jugadores actualizado a ${cantidad}`);
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
        <input type="hidden" name="cantidadCategorias" value="${salaData.cantidadCategorias}">
        <input type="hidden" name="maxJugadores" value="${salaData.maxJugadores}">
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