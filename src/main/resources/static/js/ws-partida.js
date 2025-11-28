let stompClient = null;

addEventListener("DOMContentLoaded", () => {
  console.log("ID de partida:", idPartida);
  console.log("Nombre jugador:", nombreJugador);
  conectarWebSocket();
});

function conectarWebSocket() {
  const socket = new WebSocket("ws://localhost:8080/ws");
  stompClient = Stomp.over(socket);
  stompClient.connect(
    {},
    function (frame) {
      console.log("Conectado a WebSocket");

      stompClient.subscribe("/topic/partida/" + idPartida, function (mensaje) {
        console.log("Mensaje recibido:", mensaje.body);
        const data = JSON.parse(mensaje.body);

        if (data.accion === "INICIAR_CRONOMETRO") {
          mostrarCronometro(data.tiempoRestante);
        } else if (data.accion === "ACTUALIZAR_CRONOMETRO") {
          mostrarCronometro(data.tiempoRestante);
        } else if (data.accion === "TIEMPO_AGOTADO") {
          finalizarCronometro();
        } else if (data.accion === "FINALIZAR") {
          window.location.href = data.urlResultados;
        } else {
          renderizarPartida(data);
        }
      });

      // Solicitar estado inicial
      stompClient.send("/app/obtener-partida/" + idPartida, {}, "{}");
    },
    function (error) {
      console.error("Error de conexión WebSocket:", error);
      alert("Error al conectar con el servidor");
    }
  );
}

function mostrarCronometro(segundos) {
  const minutos = Math.floor(segundos / 60);
  const segs = segundos % 60;
  const display = `${minutos.toString().padStart(2, "0")}:${segs
    .toString()
    .padStart(2, "0")}`;

  let cronometroDiv = document.getElementById("cronometro");
  if (!cronometroDiv) {
    cronometroDiv = document.createElement("div");
    cronometroDiv.id = "cronometro";

    const infoPartida = document.getElementById("infoPartida");
    if (infoPartida) {
      infoPartida.insertAdjacentElement("afterend", cronometroDiv);
    }
  }

  cronometroDiv.innerHTML = `<h2>⏰ ${display}</h2>`;
}

function finalizarCronometro() {
  mostrarCronometro(0);

  const form = document.getElementById("formCategorias");
  if (form) {
    form.querySelectorAll("input").forEach((input) => (input.disabled = true));
    const btnSubmit = form.querySelector("button[type='submit']");
    if (btnSubmit) {
      btnSubmit.disabled = true;
      btnSubmit.textContent = "TIEMPO AGOTADO";
    }
    alert("Tiempo agotado");
  }
}

function renderizarPartida(partida) {
  console.log("Renderizando partida:", partida);
  
  const infoPartida = document.getElementById("infoPartida");
  infoPartida.innerHTML = `
    <h2>Partida #${partida.id}</h2>
    <p><strong>Duración:</strong> ${partida.duracion} segundos</p>
    <p><strong>Letra:</strong> <span style="font-size: 24px; font-weight: bold;">${partida.letra || "No asignada"}</span></p>
    <p><strong>Estado:</strong> ${partida.estado}</p>
  `;

  const jugadoresDiv = document.getElementById("jugadores");
  jugadoresDiv.innerHTML = "<h3>Jugadores:</h3>";

  if (partida.jugadores && partida.jugadores.length > 0) {
    partida.jugadores.forEach((jugador) => {
      const nombreJug = jugador.nombre || jugador;
      jugadoresDiv.innerHTML += `<p>${nombreJug}</p>`;
    });
  } else {
    jugadoresDiv.innerHTML += "<p>No hay jugadores</p>";
  }

  const categoriasDiv = document.getElementById("categorias");
  categoriasDiv.innerHTML = "<h3>Completa las categorías:</h3>";

  if (partida.categorias && partida.categorias.length > 0) {
    let formularioHTML = '<form id="formCategorias">';

    partida.categorias.forEach((categoria, index) => {
      formularioHTML += `
        <div>
          <label for="categoria-${index}"><strong>${categoria}:</strong></label><br>
          <input 
            type="text" 
            id="categoria-${index}" 
            name="${categoria}"
            placeholder="Escribe aquí..."
            ${partida.estado === "FINALIZADA" ? "disabled" : ""}
            required>
        </div>
        <br>
      `;
    });

    if (partida.estado === "FINALIZADA") {
      formularioHTML += "<p><strong>La partida ha finalizado</strong></p>";
    } else {
      formularioHTML += '<button type="submit">TUTTI FRUTTI</button>';
    }

    formularioHTML += "</form>";
    categoriasDiv.innerHTML += formularioHTML;

    const form = document.getElementById("formCategorias");
    if (form && partida.estado !== "FINALIZADA") {
      form.addEventListener("submit", enviarRespuestas);
    }
  } else {
    categoriasDiv.innerHTML += "<p>No hay categorías</p>";
  }
}

function enviarRespuestas(event) {
  event.preventDefault();

  const formData = new FormData(event.target);
  const respuestas = {};

  for (let [categoria, respuesta] of formData.entries()) {
    respuestas[categoria] = respuesta.trim();
  }

  console.log("Enviando respuestas:", respuestas);

  const payload = {
    nombreJugador: nombreJugador,
    respuestas: respuestas,
  };

  stompClient.send(
    "/app/enviar-respuestas/" + idPartida,
    {},
    JSON.stringify(payload)
  );

  alert("Respuestas enviadas");

  event.target
    .querySelectorAll("input, button")
    .forEach((el) => (el.disabled = true));

  const btnSubmit = event.target.querySelector("button[type='submit']");
  if (btnSubmit) {
    btnSubmit.textContent = "ENVIADO";
  }
}