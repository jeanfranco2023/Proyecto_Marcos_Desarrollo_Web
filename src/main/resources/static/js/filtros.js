const productosPorPagina = 12;
      let paginaActual = 1;
      let productosFiltrados = [...productosData];
      let currentSort = "popular";

      // Función para ordenar productos
      function sortProducts(products, sortBy) {
        const sortedProducts = [...products];
        
        switch(sortBy) {
          case "price-asc":
            return sortedProducts.sort((a, b) => a.precioProducto - b.precioProducto);
          case "price-desc":
            return sortedProducts.sort((a, b) => b.precioProducto - a.precioProducto);
          case "newest":
            return sortedProducts.sort((a, b) => b.idProducto - a.idProducto);
          case "popular":
          default:
            if (sortedProducts[0] && sortedProducts[0].rating !== undefined) {
              return sortedProducts.sort((a, b) => b.rating - a.rating);
            }
            return sortedProducts;
        }
      }

      // Función para actualizar el texto del botón de ordenación
      function updateSortButtonText(sortBy) {
        const sortTexts = {
          "popular": "Más populares",
          "price-asc": "Precio: menor a mayor",
          "price-desc": "Precio: mayor a menor",
          "newest": "Novedades"
        };
        
        const button = document.querySelector('#sortDropdown');
        if (button) {
          button.textContent = `Ordenar por: ${sortTexts[sortBy]}`;
        }
      }

      // Renderizar productos
      function renderizarProductos() {
        const inicio = (paginaActual - 1) * productosPorPagina;
        const fin = inicio + productosPorPagina;
        const productosPagina = productosFiltrados.slice(inicio, fin);
        const contenedor = document.querySelector(".row.g-2");
        contenedor.innerHTML = "";

        if (productosPagina.length === 0) {
          contenedor.innerHTML = `
            <div class="col-12 text-center py-5">
              <h4>No se encontraron productos con los filtros seleccionados</h4>
              <p>Intenta ajustar tus criterios de búsqueda</p>
            </div>
          `;
          return;
        }

        productosPagina.forEach((producto) => {
          contenedor.innerHTML += `
            <div class="col-sm-6 col-md-4 col-lg-3">
              <a href="/productos/producto/${producto.idProducto}" class="text-decoration-none text-dark">
                <div class="card h-100 mx-2 d-flex flex-column">
                  <div class="d-flex justify-content-center align-items-center p-2" style="height: 240px;">
                    <img src="/img/ropas/${producto.imagenProducto}" class="img-fluid rounded-3" alt="Producto" style="max-height: 100%; max-width: 100%; object-fit: contain;">
                  </div>
                  <div class="card-body d-flex flex-column">
                    <h5 class="card-title fs-6">${producto.nombreProducto}</h5>
                    <div class="d-flex text-warning fs-6">
                      <div class="d-flex align-items-center text-warning fs-6">
                        ${producto.generarEstrellas ? producto.generarEstrellas() : ''}
                      </div>
                    </div>
                    <div class="d-flex align-items-center mt-2">
                      <span class="fw-bold">S/.</span>
                      <span class="card-text fs-5 fw-bold">${producto.precioProducto}</span>
                    </div>
                    <div class="mt-auto">
                      <div class="d-flex justify-content-between">
                        <span class="badge bg-primary">Casual</span>
                        <span class="badge bg-secondary">${producto.categoriasText || ''}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </a>
            </div>
          `;
        });
      }

      // Renderizar paginación
      function renderizarPaginacion() {
        const totalPaginas = Math.ceil(
          productosFiltrados.length / productosPorPagina
        );
        const paginacion = document.getElementById("paginacion");
        paginacion.innerHTML = "";

        if (totalPaginas <= 1) return;

        // Botón anterior
        paginacion.innerHTML += `<button class="btn btn-outline-secondary mx-1" ${
          paginaActual === 1 ? "disabled" : ""
        } onclick="cambiarPagina(${paginaActual - 1})">Anterior</button>`;

        for (let i = 1; i <= totalPaginas; i++) {
          paginacion.innerHTML += `<button class="btn btn${
            i === paginaActual ? "" : "-outline"
          }-primary mx-1" onclick="cambiarPagina(${i})">${i}</button>`;
        }

        // Botón siguiente
        paginacion.innerHTML += `<button class="btn btn-outline-secondary mx-1" ${
          paginaActual === totalPaginas ? "disabled" : ""
        } onclick="cambiarPagina(${paginaActual + 1})">Siguiente</button>`;
      }

      // Cambiar página
      function cambiarPagina(nuevaPagina) {
        paginaActual = nuevaPagina;
        renderizarProductos();
        renderizarPaginacion();
      }

      // Aplicar filtros
      function aplicarFiltros() {
        const precioInput = document.getElementById("priceRange");
        const precioMax = precioInput ? parseFloat(precioInput.value) : 500;
        const precioMin = 20;

        const categoriaSelect = document.querySelector("select.form-select");
        const categoriaSeleccionada = categoriaSelect ? categoriaSelect.value : "";

        const tallasSeleccionadas = Array.from(
          document.querySelectorAll('input[name="tallas"]:checked')
        ).map((cb) => cb.value);

        const estilosSeleccionados = Array.from(
          document.querySelectorAll('input[name="estilo"]:checked')
        ).map((cb) =>
          cb.id.replace("style", "").replace("Mobile", "").toLowerCase()
        );

        productosFiltrados = productosData.filter((p) => {
          const precio = parseFloat(p.precioProducto);
          const cumplePrecio = precio >= precioMin && precio <= precioMax;

          const cumpleCategoria =
            !categoriaSeleccionada ||
            (p.categorias &&
              p.categorias.some((c) => c.idCategoria == categoriaSeleccionada));

          const cumpleTalla =
            tallasSeleccionadas.length === 0 ||
            (p.tallas &&
              p.tallas.some((t) =>
                tallasSeleccionadas.includes(t.id.toString())
              ));

          let cumpleEstilo = true;
          if (estilosSeleccionados.length > 0) {
            if (!p.estilos || p.estilos.length === 0) {
              cumpleEstilo = false;
            } else {
              const estilosProducto = p.estilos.map((e) =>
                e.nombre.toLowerCase()
              );
              cumpleEstilo = estilosSeleccionados.every((estilo) =>
                estilosProducto.includes(estilo.toLowerCase())
              );
            }
          }

          return cumplePrecio && cumpleCategoria && cumpleTalla && cumpleEstilo;
        });

        // Aplicar ordenamiento después de filtrar
        productosFiltrados = sortProducts(productosFiltrados, currentSort);
        
        paginaActual = 1;
        renderizarProductos();
        renderizarPaginacion();
      }

      // Inicializar al cargar el DOM
      document.addEventListener("DOMContentLoaded", () => {
        // Ordenar productos inicialmente
        productosFiltrados = sortProducts(productosData, currentSort);
        renderizarProductos();
        renderizarPaginacion();
        updateSortButtonText(currentSort);

        const priceRange = document.getElementById("priceRange");
        const currentPrice = document.getElementById("currentPrice");

        if (priceRange && currentPrice) {
          priceRange.addEventListener("input", function () {
            currentPrice.textContent = "S/" + this.value;
            document.getElementById(
              "priceValue"
            ).textContent = `S/20 - S/${this.value}`;
            aplicarFiltros();
          });
        }

        const categoriaSelect = document.querySelector("select.form-select");
        if (categoriaSelect) {
          categoriaSelect.addEventListener("change", aplicarFiltros);
        }

        document.querySelectorAll('input[name="tallas"]').forEach((cb) => {
          cb.addEventListener("change", aplicarFiltros);
        });

        document.querySelectorAll('input[name="estilo"]').forEach((cb) => {
          cb.addEventListener("change", aplicarFiltros);
        });

        // Event listeners para las opciones de ordenación
        document.querySelectorAll('.sort-option').forEach(option => {
          option.addEventListener('click', function(e) {
            e.preventDefault();
            const sortBy = this.getAttribute('data-sort');
            currentSort = sortBy;
            updateSortButtonText(sortBy);
            
            // Reordenar los productos filtrados actuales
            productosFiltrados = sortProducts(productosFiltrados, sortBy);
            renderizarProductos();
          });
        });
      });
