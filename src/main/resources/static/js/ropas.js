document.addEventListener('DOMContentLoaded', function () {
    // === Selección de botones ===
    const tallaBtns = document.querySelectorAll('.size-btn');
    const colorBtns = document.querySelectorAll('.color-btn');
    const editarTallaBtns = document.querySelectorAll('.editar-talla-btn');
    const editarColorBtns = document.querySelectorAll('.editar-color-btn');

    // === Inputs ocultos ===
    const inputTallas = document.getElementById('tallasSeleccionadas');
    const inputColores = document.getElementById('coloresSeleccionados');
    const inputEditarTallas = document.getElementById('editar-tallasSeleccionadas');
    const inputEditarColores = document.getElementById('editar-coloresSeleccionados');

    // === Sets para mantener seleccionados ===
    let tallasSeleccionadas = new Set();
    let coloresSeleccionados = new Set();
    let editarTallasSeleccionadas = new Set();
    let editarColoresSeleccionados = new Set();

    // === Funciones de utilidad ===
    function toggleSeleccion(set, id, btn) {
        if (set.has(id)) {
            set.delete(id);
            btn.classList.remove('selected');
        } else {
            set.add(id);
            btn.classList.add('selected');
        }
    }

    function actualizarInput(input, set) {
        input.value = Array.from(set).join(',');
    }

    // === Manejo de selección en formulario principal ===
    tallaBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            toggleSeleccion(tallasSeleccionadas, id, btn);
            actualizarInput(inputTallas, tallasSeleccionadas);
        });
    });

    colorBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            toggleSeleccion(coloresSeleccionados, id, btn);
            actualizarInput(inputColores, coloresSeleccionados);
        });
    });

    // === Manejo de selección en formulario de edición ===
    editarTallaBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            toggleSeleccion(editarTallasSeleccionadas, id, btn);
            actualizarInput(inputEditarTallas, editarTallasSeleccionadas);
        });
    });

    editarColorBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const id = btn.getAttribute('data-id');
            toggleSeleccion(editarColoresSeleccionados, id, btn);
            actualizarInput(inputEditarColores, editarColoresSeleccionados);
        });
    });

    // === Al hacer click en botón de editar, cargar datos ===
    document.querySelectorAll('.btn-editar').forEach(boton => {
        boton.addEventListener('click', function () {
            const fila = boton.closest('tr');
            document.getElementById('editar-id').value = fila.querySelector('td:nth-child(1)').textContent.trim();
            document.getElementById('editar-nombre').value = fila.querySelector('td:nth-child(2)').textContent.trim();
            document.getElementById('editar-descripcion').value = fila.querySelector('td:nth-child(3)').textContent.trim();
            document.getElementById('editar-precio').value = fila.querySelector('td:nth-child(4)').textContent.trim();
            document.getElementById('editar-stock').value = fila.querySelector('td:nth-child(5)').textContent.trim();
            document.getElementById('editar-estrellas').value = fila.querySelector('td:nth-child(6)').textContent.trim();

            // Categoría
            const categoriaTexto = fila.querySelector('td:nth-child(7)').textContent.trim();
            const select = document.getElementById('editar-categoria');
            Array.from(select.options).forEach(opt => {
                opt.selected = opt.text === categoriaTexto;
            });

            // Tallas y colores
            const tallas = fila.querySelector('td:nth-child(8)').textContent.trim().split(',');
            const colores = fila.querySelector('td:nth-child(9)').textContent.trim().split(',');

            editarTallasSeleccionadas = new Set(tallas);
            editarColoresSeleccionados = new Set(colores);

            editarTallaBtns.forEach(btn => {
                const id = btn.getAttribute('data-id');
                btn.classList.toggle('selected', editarTallasSeleccionadas.has(id));
            });

            editarColorBtns.forEach(btn => {
                const id = btn.getAttribute('data-id');
                btn.classList.toggle('selected', editarColoresSeleccionados.has(id));
            });

            actualizarInput(inputEditarTallas, editarTallasSeleccionadas);
            actualizarInput(inputEditarColores, editarColoresSeleccionados);
        });
    });

    // === Enviar tallas y colores al enviar formulario de edición ===
    document.getElementById("form-editar").addEventListener("submit", function () {
        const inputsTalla = document.getElementById("inputs-tallas");
        const inputsColor = document.getElementById("inputs-colores");
        inputsTalla.innerHTML = "";
        inputsColor.innerHTML = "";

        Array.from(editarTallasSeleccionadas).forEach(id => {
            const input = document.createElement("input");
            input.type = "hidden";
            input.name = "tallasSeleccionadas";
            input.value = id;
            inputsTalla.appendChild(input);
        });

        Array.from(editarColoresSeleccionados).forEach(id => {
            const input = document.createElement("input");
            input.type = "hidden";
            input.name = "coloresSeleccionados";
            input.value = id;
            inputsColor.appendChild(input);
        });
    });
});
