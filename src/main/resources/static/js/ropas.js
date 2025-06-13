document.addEventListener("DOMContentLoaded", () => {
  const tallasSeleccionadas = new Set();
  const coloresSeleccionados = new Set();

  const inputTallas = document.getElementById("tallasSeleccionadas");
  const inputColores = document.getElementById("coloresSeleccionados");

  // Inicializar desde los valores cargados (útil para edición)
  if (inputTallas.value) {
    inputTallas.value.split(",").forEach(id => {
      if (id.trim()) {
        tallasSeleccionadas.add(id.trim());
      }
    });
  }

  if (inputColores.value) {
    inputColores.value.split(",").forEach(id => {
      if (id.trim()) {
        coloresSeleccionados.add(id.trim());
      }
    });
  }

  // Marcar botones ya seleccionados visualmente (si estamos editando)
  document.querySelectorAll(".size-btn").forEach(btn => {
    const id = btn.getAttribute("data-id");
    if (tallasSeleccionadas.has(id)) {
      btn.classList.add("selected");
    }
  });

  document.querySelectorAll(".color-btn").forEach(btn => {
    const id = btn.getAttribute("data-id");
    if (coloresSeleccionados.has(id)) {
      btn.classList.add("selected");
    }
  });

  // Actualizar valores ocultos en el formulario
  function actualizarInputs() {
    inputTallas.value = Array.from(tallasSeleccionadas).join(",");
    inputColores.value = Array.from(coloresSeleccionados).join(",");
  }

  // Limpiar y validar que solo haya números
  function filtrarSoloNumeros(valor) {
    return valor
      .split(",")
      .map(v => v.replace(/\D/g, ""))
      .filter(v => v.length > 0)
      .join(",");
  }

  // Antes de enviar el formulario
  const form = document.querySelector("form");
  if (form) {
    form.addEventListener("submit", () => {
      actualizarInputs();
      inputTallas.value = filtrarSoloNumeros(inputTallas.value);
      inputColores.value = filtrarSoloNumeros(inputColores.value);
    });
  }

  // Manejar clics en tallas
  document.querySelectorAll(".size-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      const id = btn.getAttribute("data-id");
      if (tallasSeleccionadas.has(id)) {
        tallasSeleccionadas.delete(id);
        btn.classList.remove("selected");
      } else {
        tallasSeleccionadas.add(id);
        btn.classList.add("selected");
      }
      actualizarInputs();
    });
  });

  // Manejar clics en colores
  document.querySelectorAll(".color-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      const id = btn.getAttribute("data-id");
      if (coloresSeleccionados.has(id)) {
        coloresSeleccionados.delete(id);
        btn.classList.remove("selected");
      } else {
        coloresSeleccionados.add(id);
        btn.classList.add("selected");
      }
      actualizarInputs();
    });
  });
});
