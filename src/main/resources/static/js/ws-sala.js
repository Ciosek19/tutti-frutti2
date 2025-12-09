let stompClient = null;

addEventListener("DOMContentLoaded", () => {
  mostrarJugadores(salaData.jugadores);
  mostrarBotonEmpezarPartida(salaData.jugadores.length);
  mostrarConfiguracion();

  conectarWebSocket();
  const btnVolver = document.getElementById("formVolver");
  btnVolver.addEventListener("submit", VolverAlLobby);
});

function conectarWebSocket() {
  const socket = new WebSocket("ws://localhost:8080/ws");
  stompClient = Stomp.over(socket);

  stompClient.connect(
    {},
    function (frame) {

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
      alert("Error al conectar con el servidor");
    }
  );
}

function actualizarSala(sala) {
  if (sala.accion === 'INICIAR_PARTIDA') {
    window.location.href = '/partida/' + sala.partidaId;
    return;
  }

  salaData.creador = sala.creador ? sala.creador.nombre : sala.creador;
  salaData.jugadores = sala.jugadores || [];
  salaData.cantidadCategorias = sala.cantidadCategorias || 5;
  salaData.maxJugadores = sala.maxJugadores || 4;
  salaData.duracion = sala.duracion || 60;

  mostrarJugadores(sala.jugadores);
  mostrarBotonEmpezarPartida(sala.jugadores.length);
  mostrarConfiguracion();
}

function mostrarJugadores(jugadores) {
  const contenedorJugadores = document.getElementById("jugadores");
  contenedorJugadores.innerHTML = "";

  if (jugadores && jugadores.length > 0) {
    jugadores.forEach((jugador) => {
      const divJugador = document.createElement("div");
      divJugador.className = "jugador-card";

      const inicial = jugador.nombre.charAt(0).toUpperCase();

      divJugador.innerHTML = `
        <div class="jugador-avatar">${inicial}</div>
        <span>${jugador.nombre}</span>
      `;

      contenedorJugadores.appendChild(divJugador);
    });
  } else {
    contenedorJugadores.innerHTML = "<p>No hay jugadores en la sala aún.</p>";
  }
}

function mostrarConfiguracion() {
  const divConfiguracion = document.getElementById("configuracion");

  if (!divConfiguracion) {
    return;
  }

  divConfiguracion.innerHTML = "";

  // Solo mostrar configuración si eres el creador
  if (salaData.nombreJugador === salaData.creador) {
    divConfiguracion.innerHTML = `
      <div class="config-item">
        <label for="cantidadCategorias">Cantidad de categorías (2-15)</label>
        <div class="config-input-group">
          <input type="number" id="cantidadCategorias" min="2" max="15" value="${salaData.cantidadCategorias}">
          <button onclick="actualizarCantidadCategorias()" class="neo-boton-universal btn-actualizar">Actualizar</button>
        </div>
      </div>
      <div class="config-item">
        <label for="maxJugadores">Máximo de jugadores (2-6)</label>
        <div class="config-input-group">
          <input type="number" id="maxJugadores" min="2" max="6" value="${salaData.maxJugadores}">
          <button onclick="actualizarMaxJugadores()" class="neo-boton-universal btn-actualizar">Actualizar</button>
        </div>
      </div>
      <div class="config-item">
        <label for="duracion">Duración (30-120 segundos)</label>
        <div class="config-input-group">
          <input type="number" id="duracion" min="30" max="120" value="${salaData.duracion}">
          <button onclick="actualizarDuracion()" class="neo-boton-universal btn-actualizar">Actualizar</button>
        </div>
      </div>
    `;
  } else {
    divConfiguracion.innerHTML = `
      <div class="config-readonly">
        <p><strong>Categorías:</strong> ${salaData.cantidadCategorias}</p>
        <p><strong>Máximo jugadores:</strong> ${salaData.maxJugadores}</p>
        <p><strong>Duración:</strong> ${salaData.duracion} segundos</p>
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
}

function actualizarMaxJugadores() {
  const input = document.getElementById("maxJugadores");
  const cantidad = parseInt(input.value);
  
  if (cantidad < 2 || cantidad > 6) {
    alert("El maximo de jugadores debe estar entre 2 y 6");
    input.value = salaData.maxJugadores;
    return;
  }
  
  if (cantidad < salaData.jugadores.length) {
    alert(`No puedes reducir el maximo a ${cantidad} porque ya hay ${salaData.jugadores.length} jugadores en la sala`);
    input.value = salaData.maxJugadores;
    return;
  }
  
  const datos = {
    codigoSala: salaData.codigo,
    maxJugadores: cantidad
  };
  
  stompClient.send("/app/configurar-max-jugadores", {}, JSON.stringify(datos));
}

function actualizarDuracion() {
  const input = document.getElementById("duracion");
  const duracion = parseInt(input.value);

  if (duracion < 30 || duracion > 120) {
    alert("La duracion debe estar entre 30 y 120 segundos");
    input.value = salaData.duracion;
    return;
  }

  const datos = {
    codigoSala: salaData.codigo,
    duracion: duracion
  };

  stompClient.send("/app/configurar-duracion", {}, JSON.stringify(datos));
}

function mostrarBotonEmpezarPartida(cantidadJugadores) {
  const divEmpezarPartida = document.getElementById("btnEmpezarPartida");

  if (!divEmpezarPartida) {
    return;
  }

  divEmpezarPartida.innerHTML = "";

  const creadorNombre = typeof salaData.creador === 'object' ? salaData.creador.nombre : salaData.creador;

  if (salaData.nombreJugador === creadorNombre) {
    const estaDeshabilitado = cantidadJugadores < 2;

    divEmpezarPartida.innerHTML = `
      <form action="/sala/empezar-partida" method="POST">
        <input type="hidden" name="codigoSala" value="${salaData.codigo}">
        <input type="hidden" name="cantidadCategorias" value="${salaData.cantidadCategorias}">
        <input type="hidden" name="maxJugadores" value="${salaData.maxJugadores}">
        <button type="submit" class="neo-boton-universal" ${estaDeshabilitado ? 'disabled' : ''}>
          Empezar partida ${estaDeshabilitado ? '(mínimo 2 jugadores)' : ''}
        </button>
      </form>
    `;
  }
}

function VolverAlLobby() {
  if (stompClient && stompClient.connected) {
    stompClient.disconnect();
  }
}